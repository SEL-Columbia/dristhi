package org.opensrp.service.formSubmission.handler;

import org.opensrp.form.domain.FormSubmission;
import org.opensrp.service.ANCService;
import org.opensrp.service.ChildService;
import org.opensrp.service.PNCService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DeliveryOutcomeHandler implements FormSubmissionHandler {
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
