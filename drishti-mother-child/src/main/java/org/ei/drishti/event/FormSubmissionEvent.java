package org.ei.drishti.event;

import org.ei.drishti.dto.form.FormSubmission;
import org.motechproject.scheduler.domain.MotechEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FormSubmissionEvent {
    public static final String SUBJECT = "FORM-SUBMISSION";

    private List<FormSubmission> formSubmissions;

    public FormSubmissionEvent(List<FormSubmission> formSubmissions) {
        this.formSubmissions = formSubmissions;
    }

    public MotechEvent toEvent() {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("data", formSubmissions);
        return new MotechEvent(SUBJECT, parameters);
    }
}
