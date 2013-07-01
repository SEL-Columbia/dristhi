package org.ei.drishti.service.formSubmission.handler;

import org.ei.drishti.form.domain.FormSubmission;
import org.ei.drishti.service.ANCService;
import org.ei.drishti.service.ChildService;
import org.ei.drishti.service.PNCService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class DeliveryOutcomeHandlerTest {
    @Mock
    private ANCService ancService;
    @Mock
    private PNCService pncService;
    @Mock
    private ChildService childService;

    private DeliveryOutcomeHandler handler;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        handler = new DeliveryOutcomeHandler(ancService, pncService, childService);
    }

    @Test
    public void shouldDelegateFormSubmissionHandlingToBothANCAndPNCService() throws Exception {
        FormSubmission submission = new FormSubmission("anm id 1", "instance id 1", "delivery_outcome", "entity id 1", null, 0L, 0L);

        handler.handle(submission);

        verify(ancService).deliveryOutcome(submission);
        verify(pncService).deliveryOutcome(submission);
        verify(childService).registerChildren(submission);
    }
}
