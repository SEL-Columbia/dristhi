package org.opensrp.service.formSubmission.handler;

import org.opensrp.form.domain.FormSubmission;

public interface CustomFormSubmissionHandler {
    public void handle(FormSubmission submission);
}
