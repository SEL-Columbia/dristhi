package org.ei.drishti.service.formSubmission.handler;

import org.ei.drishti.form.domain.FormSubmission;
import org.ei.drishti.service.PNCService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class PostpartumFamilyPlanningHandlerTest {
    @Mock
    private PNCService pncService;

    private PostpartumFamilyPlanningHandler handler;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        handler = new PostpartumFamilyPlanningHandler(pncService);
    }

    @Test
    public void shouldDelegateFormSubmissionHandlingToPNCService() throws Exception {
        FormSubmission submission = new FormSubmission("anm id 1", "instance id 1", "postpartum_family_planning", "entity id 1", 0L, "1", null, 0L);

        handler.handle(submission);

        verify(pncService).reportPPFamilyPlanning(submission);
    }
}
