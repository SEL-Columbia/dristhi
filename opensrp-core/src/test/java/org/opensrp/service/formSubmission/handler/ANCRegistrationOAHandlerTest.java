package org.opensrp.service.formSubmission.handler;

import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.opensrp.form.domain.FormSubmission;
import org.opensrp.service.ANCService;

public class ANCRegistrationOAHandlerTest {
    @Mock
    private ANCService ancService;

    private ANCRegistrationOAHandler handler;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        handler = new ANCRegistrationOAHandler(ancService);
    }

    @Test
    public void shouldDelegateFormSubmissionHandlingToECService() throws Exception {
        FormSubmission submission = new FormSubmission("anm id 1", "instance id 1", "anc_registration_oa", "entity id 1", 0L, "1", null, 0L);

        handler.handle(submission);

        verify(ancService).registerOutOfAreaANC(submission);
    }
}
