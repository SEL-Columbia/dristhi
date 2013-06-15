package org.ei.drishti.service.formSubmission.handler;

import org.ei.drishti.form.domain.FormSubmission;
import org.ei.drishti.service.ECService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

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
        FormSubmission submission = new FormSubmission("anm id 1", "instance id 1", "fp_referral_followup", "entity id 1", null, 0L, 0L);

        handler.handle(submission);

        verify(ecService).reportReferralFollowup(submission);
    }
}
