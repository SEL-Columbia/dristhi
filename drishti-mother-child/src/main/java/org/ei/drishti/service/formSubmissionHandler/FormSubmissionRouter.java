package org.ei.drishti.service.formSubmissionHandler;

import org.ei.drishti.domain.form.FormSubmission;
import org.ei.drishti.repository.AllFormSubmissions;
import org.ei.drishti.util.EasyMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

import static java.text.MessageFormat.format;
import static org.ei.drishti.common.AllConstants.Form.EC_REGISTRATION;
import static org.ei.drishti.common.AllConstants.Form.FP_COMPLICATIONS;

@Component
public class FormSubmissionRouter {
    private static Logger logger = LoggerFactory.getLogger(FormSubmissionRouter.class.toString());
    private AllFormSubmissions formSubmissionsRepository;
    private final Map<String, FormSubmissionHandler> handlerMap;

    @Autowired
    public FormSubmissionRouter(AllFormSubmissions formSubmissionsRepository, ECRegistrationHandler ecRegistrationHandler, FPComplicationsHandler fpComplicationsHandler) {
        this.formSubmissionsRepository = formSubmissionsRepository;
        handlerMap = EasyMap.create(EC_REGISTRATION, (FormSubmissionHandler) ecRegistrationHandler)
                .put(FP_COMPLICATIONS, fpComplicationsHandler)
                .map();
    }

    public void route(String instanceId) {
        FormSubmission submission = formSubmissionsRepository.findByInstanceId(instanceId);
        FormSubmissionHandler handler = handlerMap.get(submission.formName());
        if (handler == null) {
            logger.warn("Could not find a handler due to unknown form submission: " + submission);
            return;
        }
        logger.info(format("Handling {0} form submission with instance Id: {1} for entity: {2}", submission.formName(),
                submission.instanceId(), submission.entityId()));
        handler.handle(submission);
    }
}

