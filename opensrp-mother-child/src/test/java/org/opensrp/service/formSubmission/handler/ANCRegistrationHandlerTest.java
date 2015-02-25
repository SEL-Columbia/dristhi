package org.opensrp.service.formSubmission.handler;

import org.opensrp.form.domain.FormSubmission;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

import org.opensrp.service.ANCService;
import org.opensrp.service.formSubmission.handler.ANCRegistrationHandler;

public class ANCRegistrationHandlerTest {
    @Mock
    private ANCService ancService;

    private ANCRegistrationHandler handler;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        handler = new ANCRegistrationHandler(ancService);
    }

    @Test
    public void shouldDelegateFormSubmissionHandlingToECService() throws Exception {
        FormSubmission submission = new FormSubmission("anm id 1", "instance id 1", "anc_registration", "entity id 1", 0L, "1", null, 0L);

        handler.handle(submission);

        verify(ancService).registerANC(submission);
    }
}
