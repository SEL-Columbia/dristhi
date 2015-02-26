package org.opensrp.service.formSubmission.handler;

import org.opensrp.form.domain.FormSubmission;
import org.opensrp.service.ECService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class FPReferralFollowupHandler implements FormSubmissionHandler {
    private ECService ecService;

    @Autowired
    public FPReferralFollowupHandler(ECService ecService) {
        this.ecService = ecService;
    }

    @Override
    public void handle(FormSubmission submission) {
        ecService.handleReferralFollowup(submission);
    }
}