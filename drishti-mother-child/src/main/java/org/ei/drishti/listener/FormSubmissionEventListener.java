package org.ei.drishti.listener;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.ei.drishti.dto.form.FormSubmissionDTO;
import org.ei.drishti.event.FormSubmissionEvent;
import org.ei.drishti.form.service.SubmissionService;
import org.motechproject.scheduler.domain.MotechEvent;
import org.motechproject.server.event.annotations.MotechListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class FormSubmissionEventListener {
    private SubmissionService submissionService;

    @Autowired
    public FormSubmissionEventListener(SubmissionService submissionService) {
        this.submissionService = submissionService;
    }

    @MotechListener(subjects = FormSubmissionEvent.SUBJECT)
    public void submitForms(MotechEvent event) {
        List<FormSubmissionDTO> formSubmissions = new Gson().fromJson((String) event.getParameters().get("data"), new TypeToken<List<FormSubmissionDTO>>() {
        }.getType());
        submissionService.processSubmissions(formSubmissions);
    }
}
