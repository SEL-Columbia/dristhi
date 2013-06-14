package org.ei.drishti.service.formSubmissionHandler;

import org.ei.drishti.form.domain.FormSubmission;
import org.ei.drishti.service.PNCService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

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
        FormSubmission submission = new FormSubmission("anm id 1", "instance id 1", "pnc_close", "entity id 1", null, 0L, 0L);

        handler.handle(submission);

        verify(pncService).close(submission);
    }
}
