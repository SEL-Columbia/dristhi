package org.ei.drishti.service.formSubmission.handler;

import org.ei.drishti.form.domain.FormSubmission;
import org.ei.drishti.service.ChildService;
import org.ei.drishti.service.PNCService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PNCVisitHandler implements FormSubmissionHandler {
    private PNCService pncService;
    private ChildService childService;

    @Autowired
    public PNCVisitHandler(PNCService pncService, ChildService childService) {
        this.pncService = pncService;
        this.childService = childService;
    }

    @Override
    public void handle(FormSubmission submission) {
        pncService.pncVisitHappened(submission);
        childService.pncVisitHappened(submission);
    }
}
