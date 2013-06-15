package org.ei.drishti.service.formSubmission.handler;

import org.ei.drishti.form.domain.FormSubmission;
import org.ei.drishti.service.ANCService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

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
        FormSubmission submission = new FormSubmission("anm id 1", "instance id 1", "anc_visit", "entity id 1", null, 0L, 0L);

        handler.handle(submission);

        verify(ancService).ancVisit(submission);
    }
}
