package org.opensrp.service.formSubmission.handler;

import org.opensrp.form.domain.FormSubmission;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

import org.opensrp.service.ANCService;
import org.opensrp.service.formSubmission.handler.TTHandler;

public class TTHandlerTest {
    @Mock
    private ANCService ancService;

    private TTHandler handler;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        handler = new TTHandler(ancService);
    }

    @Test
    public void shouldDelegateFormSubmissionHandlingToANCService() throws Exception {
        FormSubmission submission = new FormSubmission("anm id 1", "instance id 1", "tt_booster", "entity id 1", 0L, "1", null, 0L);

        handler.handle(submission);

        verify(ancService).ttProvided(submission);
    }
}
