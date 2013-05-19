package org.ei.drishti.service.formSubmissionHandler;

import org.ei.drishti.form.domain.FormSubmission;
import org.ei.drishti.form.repository.AllFormSubmissions;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class FormSubmissionRouterTest {
    @Mock
    private AllFormSubmissions formSubmissionsRepository;
    @Mock
    private ECRegistrationHandler ecRegistrationHandler;
    @Mock
    private FPComplicationsHandler fpComplicationsHandler;
    @Mock
    private FPChangeHandler fpChangeHandler;
    @Mock
    private RenewFPProductHandler renewFPProductHandler;
    @Mock
    private FPFollowupHandler fpFollowupHandler;
    @Mock
    private FPReferralFollowupHandler fpReferralFollowupHandler;
    @Mock
    private ECCloseHandler ecCloseHandler;

    private FormSubmissionRouter router;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        router = new FormSubmissionRouter(formSubmissionsRepository, ecRegistrationHandler, fpComplicationsHandler,
                fpChangeHandler, renewFPProductHandler, fpFollowupHandler, fpReferralFollowupHandler, ecCloseHandler);
    }

    @Test
    public void shouldDelegateFormSubmissionHandlingToHandlerBasedOnFormName() throws Exception {
        FormSubmission formSubmission = new FormSubmission("anm id 1", "instance id 1", "ec_registration", "entity id 1", null, 0L, 0L);
        when(formSubmissionsRepository.findByInstanceId("instance id 1")).thenReturn(formSubmission);

        router.route("instance id 1");

        verify(ecRegistrationHandler).handle(formSubmission);

        formSubmission = new FormSubmission("anm id 1", "instance id 2", "fp_complications", "entity id 2", null, 0L, 0L);
        when(formSubmissionsRepository.findByInstanceId("instance id 2")).thenReturn(formSubmission);

        router.route("instance id 2");

        verify(fpComplicationsHandler).handle(formSubmission);
    }

    @Test
    public void shouldDelegateRenewFPProductFormSubmissionHandlingToRenewFPProductHandler() throws Exception {
        FormSubmission formSubmission = new FormSubmission("anm id 1", "instance id 1", "renew_fp_product", "entity id 1", null, 0L, 0L);
        when(formSubmissionsRepository.findByInstanceId("instance id 1")).thenReturn(formSubmission);

        router.route("instance id 1");

        verify(formSubmissionsRepository).findByInstanceId("instance id 1");
        verify(renewFPProductHandler).handle(formSubmission);
    }

    @Test
    public void shouldDelegateFPReferralFollowupFormSubmissionHandlingToFPReferralFollowupHandler() throws Exception {
        FormSubmission formSubmission = new FormSubmission("anm id 1", "instance id 1", "fp_referral_followup", "entity id 1", null, 0L, 0L);
        when(formSubmissionsRepository.findByInstanceId("instance id 1")).thenReturn(formSubmission);

        router.route("instance id 1");

        verify(formSubmissionsRepository).findByInstanceId("instance id 1");
        verify(fpReferralFollowupHandler).handle(formSubmission);
    }

    @Test
    public void shouldDelegateECCloseFormSubmissionHandlingToECCloseHandler() throws Exception {
        FormSubmission formSubmission = new FormSubmission("anm id 1", "instance id 1", "ec_close", "entity id 1", null, 0L, 0L);
        when(formSubmissionsRepository.findByInstanceId("instance id 1")).thenReturn(formSubmission);

        router.route("instance id 1");

        verify(formSubmissionsRepository).findByInstanceId("instance id 1");
        verify(ecCloseHandler).handle(formSubmission);
    }
}
