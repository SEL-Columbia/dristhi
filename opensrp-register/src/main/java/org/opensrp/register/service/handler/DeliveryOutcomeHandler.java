package org.opensrp.register.service.handler;

import org.opensrp.form.domain.FormSubmission;
import org.opensrp.register.service.ANCService;
import org.opensrp.register.service.ChildService;
import org.opensrp.register.service.PNCService;
import org.opensrp.service.formSubmission.handler.CustomFormSubmissionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DeliveryOutcomeHandler implements CustomFormSubmissionHandler {
    private ANCService ancService;
    private PNCService pncService;
    private ChildService childService;

    @Autowired
    public DeliveryOutcomeHandler(ANCService ancService, PNCService pncService, ChildService childService) {
        this.ancService = ancService;
        this.pncService = pncService;
        this.childService = childService;
    }

    @Override
    public void handle(FormSubmission submission) {
        ancService.deliveryOutcome(submission);
        pncService.deliveryOutcome(submission);
        childService.registerChildren(submission);
    }
}
