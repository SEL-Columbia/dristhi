package org.opensrp.register.service.handler;

import org.opensrp.form.domain.FormSubmission;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

import org.opensrp.register.service.ECService;
import org.opensrp.register.service.handler.FPComplicationsHandler;

public class FPComplicationsHandlerTest {
    @Mock
    private ECService ecService;

    private FPComplicationsHandler handler;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        handler = new FPComplicationsHandler(ecService);
    }

    @Test
    public void shouldDelegateFormSubmissionHandlingToECService() throws Exception {
        FormSubmission submission = new FormSubmission("anm id 1", "instance id 1", "ec_registration", "entity id 1", 0L, "1", null, 0L);

        handler.handle(submission);

        verify(ecService).reportFPComplications(submission);
    }
}
