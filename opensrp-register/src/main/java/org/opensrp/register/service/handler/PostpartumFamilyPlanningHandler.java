package org.opensrp.register.service.handler;

import org.opensrp.form.domain.FormSubmission;
import org.opensrp.register.service.PNCService;
import org.opensrp.service.formSubmission.handler.FormSubmissionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PostpartumFamilyPlanningHandler implements FormSubmissionHandler {
    private PNCService pncService;

    @Autowired
    public PostpartumFamilyPlanningHandler(PNCService pncService) {
        this.pncService = pncService;
    }

    @Override
    public void handle(FormSubmission submission) {
        pncService.reportPPFamilyPlanning(submission);
    }
}