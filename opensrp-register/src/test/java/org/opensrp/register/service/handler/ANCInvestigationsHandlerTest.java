package org.opensrp.register.service.handler;

import org.opensrp.form.domain.FormSubmission;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

import org.opensrp.register.service.handler.ANCInvestigationsHandler;
import org.opensrp.register.ziggy.service.ANCService;

public class ANCInvestigationsHandlerTest {
    @Mock
    private ANCService ancService;

    private ANCInvestigationsHandler handler;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        handler = new ANCInvestigationsHandler(ancService);
    }

    @Test
    public void shouldDelegateFormSubmissionHandlingToANCService() throws Exception {
        FormSubmission submission = new FormSubmission("anm id 1", "instance id 1", "anc_investigations", "entity id 1", 0L, "1", null, 0L);

        handler.handle(submission);

        verify(ancService).ancInvestigations(submission);
    }
}
