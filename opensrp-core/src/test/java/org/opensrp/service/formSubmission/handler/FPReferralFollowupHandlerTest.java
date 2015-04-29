package org.opensrp.service.formSubmission.handler;

import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.opensrp.form.domain.FormSubmission;
import org.opensrp.service.ECService;

public class FPReferralFollowupHandlerTest {
    @Mock
    private ECService ecService;

    private FPReferralFollowupHandler handler;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        handler = new FPReferralFollowupHandler(ecService);
    }

    @Test
    public void shouldDelegateHandlingToECService() throws Exception {
        FormSubmission submission = new FormSubmission("anm id 1", "instance id 1", "fp_referral_followup", "entity id 1", 0L, "1", null, 0L);

        handler.handle(submission);

        verify(ecService).handleReferralFollowup(submission);
    }
}
