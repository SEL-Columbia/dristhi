package org.ei.drishti.service.formSubmission.handler;

import org.ei.drishti.form.domain.FormSubmission;
import org.ei.drishti.service.PNCService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class PNCRegistrationOAHandlerTest {
    @Mock
    private PNCService pncService;

    private PNCRegistrationOAHandler handler;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        handler = new PNCRegistrationOAHandler(pncService);
    }

    @Test
    public void shouldDelegateFormSubmissionHandlingToPNCService() throws Exception {
        FormSubmission submission = new FormSubmission("anm id 1", "instance id 1", "pnc_registration_oa", "entity id 1", null, 0L, 0L);

        handler.handle(submission);

        verify(pncService).pncRegistration(submission);
        verify(pncService).pncOAChildRegistration(submission);
    }
}
