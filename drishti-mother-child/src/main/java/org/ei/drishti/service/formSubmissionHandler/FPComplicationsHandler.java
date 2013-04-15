package org.ei.drishti.service.formSubmissionHandler;

import org.ei.drishti.dto.form.FormSubmission;
import org.springframework.stereotype.Component;

@Component
public class FPComplicationsHandler implements FormSubmissionHandler {
    @Override
    public void handle(FormSubmission submission) {
        throw new RuntimeException("Not implemented");
    }
}
