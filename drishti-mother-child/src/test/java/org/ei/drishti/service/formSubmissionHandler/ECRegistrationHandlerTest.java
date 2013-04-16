package org.ei.drishti.service.formSubmissionHandler;

import org.ei.drishti.dto.form.FormSubmission;
import org.ei.drishti.service.ECService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class ECRegistrationHandlerTest {
    @Mock
    private ECService ecService;

    private ECRegistrationHandler handler;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        handler = new ECRegistrationHandler(ecService);
    }

    @Test
    public void shouldDelegateFormSubmissionHandlingToECService() throws Exception {
        FormSubmission submission = new FormSubmission("anm id 1", "instance id 1", "entity id 1", "ec_registration", null, "0");

        handler.handle(submission);

        verify(ecService).registerEligibleCouple(submission);
    }
}
