package org.ei.drishti.listener;

import org.ei.drishti.dto.form.FormSubmission;
import org.ei.drishti.event.FormSubmissionEvent;
import org.ei.drishti.service.FormSubmissionService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.scheduler.domain.MotechEvent;

import java.util.List;

import static java.util.Arrays.asList;
import static org.ei.drishti.util.EasyMap.mapOf;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class FormSubmissionEventListenerTest {
    @Mock
    private FormSubmissionService formSubmissionService;

    private FormSubmissionEventListener listener;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        listener = new FormSubmissionEventListener(formSubmissionService);
    }

    @Test
    public void shouldDelegateToFormSubmissionService() throws Exception {
        List<FormSubmission> formSubmissions = asList(new FormSubmission("anm id 1", "instance id 1", "entity id 1", "form name", null, 0),
                new FormSubmission("anm id 2", "instance id 2", "entity id 2", "form name", null, 0));

        listener.submitForms(new MotechEvent(FormSubmissionEvent.SUBJECT, mapOf("data", (Object) formSubmissions)));

        verify(formSubmissionService).processSubmissions(formSubmissions);
    }
}
