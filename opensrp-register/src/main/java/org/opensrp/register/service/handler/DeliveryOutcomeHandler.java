package org.opensrp.register.service.handler;

import org.opensrp.form.domain.FormSubmission;
import org.opensrp.register.ziggy.service.ANCService;
import org.opensrp.register.ziggy.service.ChildService;
import org.opensrp.service.formSubmission.handler.CustomFormSubmissionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DeliveryOutcomeHandler implements CustomFormSubmissionHandler {
    private ANCService ancService;
    private ChildService childService;

    @Autowired
    public DeliveryOutcomeHandler(ANCService ancService, ChildService childService) {
        this.ancService = ancService;
        this.childService = childService;
    }

    @Override
    public void handle(FormSubmission submission) {
        ancService.deliveryOutcome(submission);
        childService.registerChildren(submission);
    }
}
