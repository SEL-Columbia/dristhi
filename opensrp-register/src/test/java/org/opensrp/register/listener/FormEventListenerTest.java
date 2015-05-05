package org.opensrp.register.listener;

import static java.util.Arrays.asList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.opensrp.common.util.EasyMap.mapOf;

import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.scheduler.domain.MotechEvent;
import org.opensrp.domain.FormExportToken;
import org.opensrp.dto.form.FormSubmissionDTO;
import org.opensrp.form.domain.FormSubmission;
import org.opensrp.form.service.FormSubmissionService;
import org.opensrp.register.DrishtiScheduleConstants.OpenSRPEvent;
import org.opensrp.register.listener.FormEventListener;
import org.opensrp.repository.AllFormExportTokens;
import org.opensrp.service.formSubmission.FormEntityService;

import com.google.gson.Gson;

public class FormEventListenerTest {
    @Mock
    private FormSubmissionService formSubmissionService;
    @Mock
    private FormEntityService formEntityService;
    @Mock
    private AllFormExportTokens formExportTokens;

    private FormEventListener listener;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        listener = new FormEventListener(formSubmissionService, formEntityService, formExportTokens);
    }

    @Test
    public void shouldDelegateFormSubmissionToSubmissionService() throws Exception {
        List<FormSubmissionDTO> formSubmissions = asList(new FormSubmissionDTO("anm id 1", "instance id 1", "entity id 1", "form name", null, "0", "1"),
                new FormSubmissionDTO("anm id 2", "instance id 2", "entity id 2", "form name", null, "0", "1"));

        listener.submitForms(new MotechEvent(OpenSRPEvent.FORM_SUBMISSION, mapOf("data", (Object) new Gson().toJson(formSubmissions))));

        verify(formSubmissionService).submit(formSubmissions);
    }

    @Test
    public void shouldFetchFormSubmissionsFromSubmissionService() throws Exception {
        List<FormSubmissionDTO> formSubmissions = asList(new FormSubmissionDTO("anm id 1", "instance id 1", "entity id 1", "form name", null, "0", "1").withServerVersion("0"),
                new FormSubmissionDTO("anm id 2", "instance id 2", "entity id 2", "form name", null, "0", "1").withServerVersion("0"));
        when(formExportTokens.getAll()).thenReturn(asList(new FormExportToken(1L)));
        when(formSubmissionService.fetch(1L)).thenReturn(formSubmissions);

        listener.fetchForms(new MotechEvent("SUBJECT", null));

        verify(formEntityService).process(asList(new FormSubmission("anm id 1", "instance id 1", "form name", "entity id 1", 0L, "1", null, 0L),
                new FormSubmission("anm id 2", "instance id 2", "form name", "entity id 2", 0L, "1", null, 0L)));
    }

    @Test
    public void shouldCreateFormExportTokenIfItDoesNotExists() throws Exception {
        List<FormSubmissionDTO> formSubmissions = asList(new FormSubmissionDTO("anm id 1", "instance id 1", "entity id 1", "form name", null, "0", "1").withServerVersion("0"),
                new FormSubmissionDTO("anm id 2", "instance id 2", "entity id 2", "form name", null, "0", "1").withServerVersion("0"));
        when(formExportTokens.getAll()).thenReturn(Collections.EMPTY_LIST);
        when(formSubmissionService.fetch(1L)).thenReturn(formSubmissions);

        listener.fetchForms(new MotechEvent("SUBJECT", null));

        verify(formExportTokens).add(new FormExportToken(0L));
    }

    @Test
    public void shouldNotDoAnythingIfFetchFromSubmissionServiceReturnsEmptyList() throws Exception {
        when(formExportTokens.getAll()).thenReturn(asList(new FormExportToken(1L)));
        when(formSubmissionService.fetch(1L)).thenReturn(Collections.EMPTY_LIST);

        listener.fetchForms(new MotechEvent("SUBJECT", null));

        verifyZeroInteractions(formEntityService);
    }
}
