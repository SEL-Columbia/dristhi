package org.opensrp.service.formSubmission.handler;

import org.opensrp.form.domain.FormSubmission;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

import org.opensrp.service.ChildService;
import org.opensrp.service.PNCService;
import org.opensrp.service.formSubmission.handler.PNCVisitHandler;

public class PNCVisitHandlerTest {
    @Mock
    private PNCService pncService;
    @Mock
    private ChildService childService;

    private PNCVisitHandler handler;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        handler = new PNCVisitHandler(pncService, childService);
    }

    @Test
    public void shouldDelegateFormSubmissionHandleToPNCService() throws Exception {
        FormSubmission submission = new FormSubmission("anm id 1", "instance id 1", "pnc_visit", "entity id 1", 0L, "1", null, 0L);

        handler.handle(submission);

        verify(pncService).pncVisitHappened(submission);
        verify(childService).pncVisitHappened(submission);
    }
}
