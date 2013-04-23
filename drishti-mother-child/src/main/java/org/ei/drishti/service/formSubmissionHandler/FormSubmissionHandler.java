package org.ei.drishti.service.formSubmissionHandler;

import org.ei.drishti.domain.form.FormSubmission;

public interface FormSubmissionHandler {
    public void handle(FormSubmission submission);
}
