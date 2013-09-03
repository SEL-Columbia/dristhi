package org.ei.drishti.service.formSubmission.handler;

import org.ei.drishti.form.domain.FormSubmission;
import org.ei.drishti.service.ChildService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class ChildIllnessHandlerTest {
    @Mock
    private ChildService childService;

    private ChildIllnessHandler handler;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        handler = new ChildIllnessHandler(childService);
    }

    @Test
    public void shouldDelegateChildIllnessFormSubmissionHandleToChildService() throws Exception {
        FormSubmission submission = new FormSubmission("anm id 1", "instance id 1", "pnc_visit", "entity id 1", 0L, "1", null, 0L);

        handler.handle(submission);

        verify(childService).sickVisitHappened(submission);
    }
}
