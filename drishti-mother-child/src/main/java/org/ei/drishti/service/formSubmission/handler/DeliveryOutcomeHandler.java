package org.ei.drishti.service.formSubmission.handler;

import org.ei.drishti.form.domain.FormSubmission;
import org.ei.drishti.service.ANCService;
import org.ei.drishti.service.ChildService;
import org.ei.drishti.service.PNCService;
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
