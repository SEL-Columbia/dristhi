package org.ei.drishti.service.formSubmission.handler;

import org.ei.drishti.form.domain.FormSubmission;
import org.ei.drishti.service.PNCService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PNCCloseHandler implements FormSubmissionHandler {
    private PNCService pncService;

    @Autowired
    public PNCCloseHandler(PNCService pncService) {
        this.pncService = pncService;
    }

    @Override
    public void handle(FormSubmission submission) {
        pncService.close(submission);
    }
}
