package org.opensrp.register.service.handler;

import org.opensrp.form.domain.FormSubmission;
import org.opensrp.register.service.ChildService;
import org.opensrp.register.service.PNCService;
import org.opensrp.service.formSubmission.handler.CustomFormSubmissionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PNCRegistrationOAHandler implements CustomFormSubmissionHandler {
    private PNCService pncService;
    private ChildService childService;

    @Autowired
    public PNCRegistrationOAHandler(PNCService pncService, ChildService childService) {
        this.pncService = pncService;
        this.childService = childService;
    }

    @Override
    public void handle(FormSubmission submission) {
        pncService.pncRegistrationOA(submission);
        childService.pncOAChildRegistration(submission);
    }
}
