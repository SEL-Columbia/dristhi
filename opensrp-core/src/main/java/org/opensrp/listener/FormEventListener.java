package org.opensrp.listener;

import ch.lambdaj.function.convert.Converter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.opensrp.domain.FormExportToken;
import org.opensrp.form.domain.FormSubmission;
import org.opensrp.form.service.FormSubmissionConverter;
import org.opensrp.form.service.FormSubmissionService;
import org.opensrp.scheduler.DrishtiFormScheduler;
import org.motechproject.scheduler.domain.MotechEvent;
import org.motechproject.server.event.annotations.MotechListener;
import org.opensrp.dto.form.FormSubmissionDTO;
import org.opensrp.event.FormSubmissionEvent;
import org.opensrp.repository.AllFormExportTokens;
import org.opensrp.service.formSubmission.FormEntityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import static ch.lambdaj.collection.LambdaCollections.with;
import static java.text.MessageFormat.format;
import static org.apache.commons.lang.exception.ExceptionUtils.getFullStackTrace;

@Component
public class FormEventListener {
    private static Logger logger = LoggerFactory.getLogger(FormEventListener.class.toString());
    private FormSubmissionService formSubmissionService;
    private FormEntityService formEntityService;
    private AllFormExportTokens allFormExportTokens;
    private static final ReentrantLock lock = new ReentrantLock();

    @Autowired
    public FormEventListener(FormSubmissionService formSubmissionService, FormEntityService formEntityService, AllFormExportTokens allFormExportTokens) {
        this.formSubmissionService = formSubmissionService;
        this.formEntityService = formEntityService;
        this.allFormExportTokens = allFormExportTokens;
    }

    @MotechListener(subjects = FormSubmissionEvent.SUBJECT)
    public void submitForms(MotechEvent event) {
        List<FormSubmissionDTO> formSubmissions = new Gson().fromJson((String) event.getParameters().get("data"), new TypeToken<List<FormSubmissionDTO>>() {
        }.getType());
        formSubmissionService.submit(formSubmissions);
    }

    @MotechListener(subjects = DrishtiFormScheduler.SUBJECT)
    public void fetchForms(MotechEvent event) {
        if (!lock.tryLock()) {
            logger.warn("Not fetching forms from Message Queue. It is already in progress.");
            return;
        }
        try {
            logger.info("Fetching Forms");
            long version = getVersion();

            List<FormSubmissionDTO> formSubmissionDTOs = formSubmissionService.fetch(version);
            if (formSubmissionDTOs.isEmpty()) {
                logger.info("No new forms found. Export token: " + version);
                return;
            }

            logger.info(format("Fetched {0} new forms found. Export token: {1}", formSubmissionDTOs.size(), version));
            List<FormSubmission> formSubmissions = with(formSubmissionDTOs)
                    .convert(new Converter<FormSubmissionDTO, FormSubmission>() {
                        @Override
                        public FormSubmission convert(FormSubmissionDTO submission) {
                            return FormSubmissionConverter.toFormSubmissionWithVersion(submission);
                        }
                    });
            formEntityService.process(formSubmissions);
        } catch (Exception e) {
            logger.error(MessageFormat.format("{0} occurred while trying to fetch forms. Message: {1} with stack trace {2}",
                    e.toString(), e.getMessage(), getFullStackTrace(e)));
        } finally {
            lock.unlock();
        }
    }

    private long getVersion() {
        List<FormExportToken> exportTokens = allFormExportTokens.getAll();
        if (exportTokens.isEmpty()) {
            allFormExportTokens.add(new FormExportToken(0L));
            return 0L;
        }
        return exportTokens.get(0).getVersion();
    }
}
