package org.opensrp.register.service.handler;

import org.opensrp.form.domain.FormSubmission;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

import org.opensrp.register.service.ANCService;
import org.opensrp.register.service.ChildService;
import org.opensrp.register.service.PNCService;
import org.opensrp.register.service.handler.DeliveryOutcomeHandler;

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
        FormSubmission submission = new FormSubmission("anm id 1", "instance id 1", "delivery_outcome", "entity id 1", 0L, "1", null, 0L);

        handler.handle(submission);

        verify(ancService).deliveryOutcome(submission);
        verify(pncService).deliveryOutcome(submission);
        verify(childService).registerChildren(submission);
    }
}
