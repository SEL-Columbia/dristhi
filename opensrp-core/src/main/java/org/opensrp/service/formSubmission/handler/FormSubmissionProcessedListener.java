package org.opensrp.service.formSubmission.handler;

import java.util.List;

import org.opensrp.form.domain.FormSubmission;

public interface FormSubmissionProcessedListener {
    public void onFormSubmissionProcessed(String client, List<String> dependents, FormSubmission submission);
}
