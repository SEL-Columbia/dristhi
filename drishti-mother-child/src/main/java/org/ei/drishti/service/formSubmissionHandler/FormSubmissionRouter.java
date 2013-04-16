package org.ei.drishti.service.formSubmissionHandler;

import org.ei.drishti.dto.form.FormSubmission;
import org.ei.drishti.util.EasyMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

import static org.ei.drishti.common.AllConstants.Form.EC_REGISTRATION;
import static org.ei.drishti.common.AllConstants.Form.FP_COMPLICATIONS;

@Component
public class FormSubmissionRouter {
    private static Logger logger = LoggerFactory.getLogger(FormSubmissionRouter.class.toString());
    private final Map<String, FormSubmissionHandler> handlerMap;

    @Autowired
    public FormSubmissionRouter(ECRegistrationHandler ecRegistrationHandler, FPComplicationsHandler fpComplicationsHandler) {
        handlerMap = EasyMap.create(EC_REGISTRATION, (FormSubmissionHandler) ecRegistrationHandler)
                .put(FP_COMPLICATIONS, fpComplicationsHandler)
                .map();
    }

    public void route(FormSubmission submission) {
        FormSubmissionHandler handler = handlerMap.get(submission.formName());
        if (handler == null) {
            logger.warn("Could not find a handler due to unknown form submission: " + submission);
            return;
        }
        handler.handle(submission);
    }
}

