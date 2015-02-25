package org.opensrp.service.formSubmission.handler;

import org.opensrp.form.domain.FormSubmission;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

import org.opensrp.service.PNCService;
import org.opensrp.service.formSubmission.handler.PNCCloseHandler;

public class PNCCloseHandlerTest {
    @Mock
    private PNCService pncService;

    private PNCCloseHandler handler;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        handler = new PNCCloseHandler(pncService);
    }

    @Test
    public void shouldDelegateFormSubmissionHandlingToPNCService() throws Exception {
        FormSubmission submission = new FormSubmission("anm id 1", "instance id 1", "pnc_close", "entity id 1", 0L, "1", null, 0L);

        handler.handle(submission);

        verify(pncService).close(submission);
    }
}
