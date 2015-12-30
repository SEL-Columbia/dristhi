package org.opensrp.register.service.handler;

import org.opensrp.form.domain.FormSubmission;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

import org.opensrp.register.service.handler.ANCVisitHandler;
import org.opensrp.register.ziggy.service.ANCService;

public class ANCVisitHandlerTest {
    @Mock
    private ANCService ancService;

    private ANCVisitHandler handler;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        handler = new ANCVisitHandler(ancService);
    }

    @Test
    public void shouldDelegateFormSubmissionHandlingToANCService() throws Exception {
        FormSubmission submission = new FormSubmission("anm id 1", "instance id 1", "anc_visit", "entity id 1", 0L, "1", null, 0L);

        handler.handle(submission);

        verify(ancService).ancVisit(submission);
    }
}
