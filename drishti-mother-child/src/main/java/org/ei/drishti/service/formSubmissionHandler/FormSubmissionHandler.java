package org.ei.drishti.service.formSubmissionHandler;

import org.ei.drishti.dto.form.FormSubmission;

public interface FormSubmissionHandler {
    public void handle(FormSubmission submission);
}
