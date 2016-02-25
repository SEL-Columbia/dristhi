package org.opensrp.service.formSubmission;

import static java.text.MessageFormat.format;
import static java.util.Collections.sort;
import static org.apache.commons.lang.exception.ExceptionUtils.getFullStackTrace;

import java.text.MessageFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import org.motechproject.scheduler.domain.MotechEvent;
import org.motechproject.server.event.annotations.MotechListener;
import org.opensrp.common.AllConstants;
import org.opensrp.domain.AppStateToken;
import org.opensrp.domain.ErrorTrace;
import org.opensrp.dto.form.FormSubmissionDTO;
import org.opensrp.form.domain.FormSubmission;
import org.opensrp.form.service.FormSubmissionService;
import org.opensrp.service.ConfigService;
import org.opensrp.service.ErrorTraceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

@Component
public class FormSubmissionListener {
    private static Logger logger = LoggerFactory.getLogger(FormSubmissionListener.class.toString());
    private static final ReentrantLock lock = new ReentrantLock();
    private FormSubmissionService formSubmissionService;
    private ConfigService configService;
    private FormSubmissionProcessor fsp;
    private ErrorTraceService errorTraceService;

    @Autowired
    public FormSubmissionListener(FormSubmissionService formSubmissionService, FormSubmissionProcessor fsp,
    		ConfigService configService, ErrorTraceService errorTraceService) {
        this.formSubmissionService = formSubmissionService;
        this.configService = configService;
        this.errorTraceService = errorTraceService;
        this.fsp = fsp;
		this.configService.registerAppStateToken(AllConstants.Config.FORM_ENTITY_PARSER_LAST_SYNCED_FORM_SUBMISSION, 
			0, "Token to keep track of forms processed for client n event parsing and schedule handling", true);
    }

    @MotechListener(subjects = AllConstants.OpenSRPEvent.FORM_SUBMISSION)
    public void submitForms(MotechEvent event) {
        List<FormSubmissionDTO> formSubmissions = new Gson().fromJson((String) event.getParameters().get("data"), new TypeToken<List<FormSubmissionDTO>>() {
        }.getType());
        formSubmissionService.submit(formSubmissions);
    }

    @MotechListener(subjects = AllConstants.FORM_SCHEDULE_SUBJECT)
    public void parseForms(MotechEvent event) {
        if (!lock.tryLock()) {
            logger.warn("Not fetching forms from Message Queue. It is already in progress.");
            return;
        }
        try {
            logger.info("Fetching Forms");
            long version = getVersion();

            List<FormSubmission> formSubmissions = formSubmissionService.getAllSubmissions(version, null);
            if (formSubmissions.isEmpty()) {
                logger.info("No new forms found. Export token: " + version);
                return;
            }

            logger.info(format("Fetched {0} new forms found. Export token: {1}", formSubmissions.size(), version));
            
            sort(formSubmissions, serverVersionComparator());

            for (FormSubmission submission : formSubmissions) {
            	try{
	            	logger.info(format("Invoking save for form with instance Id: {0} and for entity Id: {1}", submission.instanceId(), submission.entityId()));
	
	            	fsp.processFormSubmission(submission);
	            	
	            	configService.updateAppStateToken(AllConstants.Config.FORM_ENTITY_PARSER_LAST_SYNCED_FORM_SUBMISSION, submission.serverVersion());
            	}
            	catch(Exception e){
            		e.printStackTrace();
            		errorTraceService.addError(new ErrorTrace(new Date(), "FormSubmissionProcessor", this.getClass().getName(), e.getStackTrace().toString(), "unsolved", FormSubmission.class.getName()));
            	}
            }
        } catch (Exception e) {
            logger.error(MessageFormat.format("{0} occurred while trying to fetch forms. Message: {1} with stack trace {2}",
                    e.toString(), e.getMessage(), getFullStackTrace(e)));
        } finally {
            lock.unlock();
        }
    }

    private long getVersion() {
        AppStateToken token = configService.getAppStateTokenByName(AllConstants.Config.FORM_ENTITY_PARSER_LAST_SYNCED_FORM_SUBMISSION);
        return token==null?0L:token.longValue();
    }
    
    private Comparator<FormSubmission> serverVersionComparator() {
        return new Comparator<FormSubmission>() {
            public int compare(FormSubmission firstSubmission, FormSubmission secondSubmission) {
                long firstTimestamp = firstSubmission.serverVersion();
                long secondTimestamp = secondSubmission.serverVersion();
                return firstTimestamp == secondTimestamp ? 0 : firstTimestamp < secondTimestamp ? -1 : 1;
            }
        };
    }
}
