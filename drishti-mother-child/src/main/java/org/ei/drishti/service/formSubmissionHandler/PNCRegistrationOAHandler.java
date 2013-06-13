package org.ei.drishti.service.formSubmissionHandler;

import org.ei.drishti.form.domain.FormSubmission;
import org.ei.drishti.service.PNCService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PNCRegistrationOAHandler implements FormSubmissionHandler {
    private PNCService pncService;

    @Autowired
    public PNCRegistrationOAHandler(PNCService pncService) {
        this.pncService = pncService;
    }

    @Override
    public void handle(FormSubmission submission) {
        pncService.pncRegistration(submission);
        pncService.pncOAChildRegistration(submission);
    }
}
