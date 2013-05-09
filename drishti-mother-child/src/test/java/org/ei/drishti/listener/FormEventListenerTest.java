package org.ei.drishti.listener;

import com.google.gson.Gson;
import org.ei.drishti.domain.form.FormExportToken;
import org.ei.drishti.domain.form.FormSubmission;
import org.ei.drishti.dto.form.FormSubmissionDTO;
import org.ei.drishti.event.FormSubmissionEvent;
import org.ei.drishti.form.service.SubmissionService;
import org.ei.drishti.repository.AllFormExportTokens;
import org.ei.drishti.service.FormSubmissionService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.scheduler.domain.MotechEvent;

import java.util.Collections;
import java.util.List;

import static java.util.Arrays.asList;
import static org.ei.drishti.util.EasyMap.mapOf;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class FormEventListenerTest {
    @Mock
    private SubmissionService submissionService;
    @Mock
    private FormSubmissionService formSubmissionService;
    @Mock
    private AllFormExportTokens formExportTokens;

    private FormEventListener listener;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        listener = new FormEventListener(submissionService, formSubmissionService, formExportTokens);
    }

    @Test
    public void shouldDelegateFormSubmissionToSubmissionService() throws Exception {
        List<FormSubmissionDTO> formSubmissions = asList(new FormSubmissionDTO("anm id 1", "instance id 1", "entity id 1", "form name", null, "0"),
                new FormSubmissionDTO("anm id 2", "instance id 2", "entity id 2", "form name", null, "0"));

        listener.submitForms(new MotechEvent(FormSubmissionEvent.SUBJECT, mapOf("data", (Object) new Gson().toJson(formSubmissions))));

        verify(submissionService).processSubmissions(formSubmissions);
    }

    @Test
    public void shouldFetchFormSubmissionsFromSubmissionService() throws Exception {
        List<FormSubmissionDTO> formSubmissions = asList(new FormSubmissionDTO("anm id 1", "instance id 1", "entity id 1", "form name", null, "0").withServerVersion("0"),
                new FormSubmissionDTO("anm id 2", "instance id 2", "entity id 2", "form name", null, "0").withServerVersion("0"));
        when(formExportTokens.getAll()).thenReturn(asList(new FormExportToken(1L)));
        when(submissionService.fetchSubmission(1L)).thenReturn(formSubmissions);

        listener.fetchForms(new MotechEvent("SUBJECT", null));

        verify(formSubmissionService).processSubmissions(asList(new FormSubmission("anm id 1", "instance id 1", "form name", "entity id 1", null, 0L),
                new FormSubmission("anm id 2", "instance id 2", "form name", "entity id 2", null, 0L)));
    }

    @Test
    public void shouldCreateFormExportTokenIfItDoesNotExists() throws Exception {
        List<FormSubmissionDTO> formSubmissions = asList(new FormSubmissionDTO("anm id 1", "instance id 1", "entity id 1", "form name", null, "0").withServerVersion("0"),
                new FormSubmissionDTO("anm id 2", "instance id 2", "entity id 2", "form name", null, "0").withServerVersion("0"));
        when(formExportTokens.getAll()).thenReturn(Collections.EMPTY_LIST);
        when(submissionService.fetchSubmission(1L)).thenReturn(formSubmissions);

        listener.fetchForms(new MotechEvent("SUBJECT", null));

        verify(formExportTokens).add(new FormExportToken(0L));
    }

    @Test
    public void shouldNotDoAnythingIfFetchFromSubmissionServiceReturnsEmptyList() throws Exception {
        when(formExportTokens.getAll()).thenReturn(asList(new FormExportToken(1L)));
        when(submissionService.fetchSubmission(1L)).thenReturn(Collections.EMPTY_LIST);

        listener.fetchForms(new MotechEvent("SUBJECT", null));

        verifyZeroInteractions(formSubmissionService);
    }
}
