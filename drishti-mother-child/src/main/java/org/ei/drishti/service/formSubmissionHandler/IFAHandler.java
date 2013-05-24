package org.ei.drishti.service.formSubmissionHandler;

import org.ei.drishti.form.domain.FormSubmission;
import org.ei.drishti.service.ANCService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class IFAHandler implements FormSubmissionHandler {
    private ANCService ancService;

    @Autowired
    public IFAHandler(ANCService ancService) {
        this.ancService = ancService;
    }

    @Override
    public void handle(FormSubmission submission) {
        ancService.ifaTabletsGiven(submission);
    }
}
