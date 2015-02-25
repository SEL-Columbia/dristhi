package org.opensrp.service.formSubmission.handler;

import org.opensrp.form.domain.FormSubmission;
import org.opensrp.service.PNCService;
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