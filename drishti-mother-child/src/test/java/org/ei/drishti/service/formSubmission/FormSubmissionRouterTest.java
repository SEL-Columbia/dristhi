package org.ei.drishti.service.formSubmission;

import org.ei.drishti.form.domain.FormSubmission;
import org.ei.drishti.form.repository.AllFormSubmissions;
import org.ei.drishti.service.formSubmission.handler.*;
import org.ei.drishti.service.reporting.FormSubmissionReportService;
import org.ei.drishti.service.reporting.MCTSReportService;
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
    @Mock
    private ANCRegistrationHandler ancRegistrationHandler;
    @Mock
    private ANCRegistrationOAHandler ancRegistrationOAHandler;
    @Mock
    private ANCVisitHandler ancVisitHandler;
    @Mock
    private ANCCloseHandler ancCloseHandler;
    @Mock
    private TTHandler ttHandler;
    @Mock
    private IFAHandler ifaHandler;
    @Mock
    private HbTestHandler hbTestHandler;
    @Mock
    private DeliveryOutcomeHandler deliveryOutcomeHandler;
    @Mock
    private PNCRegistrationOAHandler pncRegistrationOAHandler;
    @Mock
    private PNCCloseHandler pncCloseHandler;
    @Mock
    private PNCVisitHandler pncVisitHandler;
    @Mock
    private ChildRegistrationECHandler childRegistrationECHandler;
    @Mock
    private ChildImmunizationsHandler childImmunizationsHandler;
    @Mock
    private ChildCloseHandler childCloseHandler;
    @Mock
    private ChildRegistrationOAHandler childRegistrationOAHandler;
    @Mock
    private ChildIllnessHandler childIllnessHandler;
    @Mock
    private VitaminAHandler vitaminAHandler;
    @Mock
    private DeliveryPlanHandler deliveryPlanHandler;
    @Mock
    private PostpartumFamilyPlanningHandler postpartumFamilyPlanningHandler;
    @Mock
    private RecordECPsHandler recordECPsHandler;
    @Mock
    private ECEditHandler ecEditHandler;
    @Mock
    private ANCInvestigationsHandler ancInvestigationsHandler;
    @Mock
    private FormSubmissionReportService formSubmissionReportService;
    @Mock
    private MCTSReportService mctsReportService;

    private FormSubmissionRouter router;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        router = new FormSubmissionRouter(formSubmissionsRepository,
                ecRegistrationHandler,
                fpComplicationsHandler,
                fpChangeHandler,
                renewFPProductHandler,
                fpFollowupHandler,
                fpReferralFollowupHandler,
                ecCloseHandler,
                ancRegistrationHandler,
                ancRegistrationOAHandler,
                ancVisitHandler,
                ancCloseHandler,
                ttHandler,
                ifaHandler,
                hbTestHandler,
                deliveryOutcomeHandler,
                pncRegistrationOAHandler,
                pncCloseHandler,
                pncVisitHandler,
                childRegistrationECHandler,
                childRegistrationOAHandler,
                vitaminAHandler,
                childImmunizationsHandler,
                childIllnessHandler,
                childCloseHandler,
                deliveryPlanHandler,
                postpartumFamilyPlanningHandler,
                recordECPsHandler,
                ecEditHandler,
                ancInvestigationsHandler,
                formSubmissionReportService, mctsReportService);
    }

    @Test
    public void shouldDelegateFormSubmissionHandlingToHandlerBasedOnFormName() throws Exception {
        FormSubmission formSubmission = new FormSubmission("anm id 1", "instance id 1", "ec_registration", "entity id 1", 0L, "1", null, 0L);
        when(formSubmissionsRepository.findByInstanceId("instance id 1")).thenReturn(formSubmission);

        router.route("instance id 1");

        verify(ecRegistrationHandler).handle(formSubmission);
        verify(formSubmissionReportService).reportFor(formSubmission);

        formSubmission = new FormSubmission("anm id 1", "instance id 2", "fp_complications", "entity id 2", 0L, "1", null, 0L);
        when(formSubmissionsRepository.findByInstanceId("instance id 2")).thenReturn(formSubmission);

        router.route("instance id 2");

        verify(fpComplicationsHandler).handle(formSubmission);
        verify(formSubmissionReportService).reportFor(formSubmission);
    }

    @Test
    public void shouldDelegateRenewFPProductFormSubmissionHandlingToRenewFPProductHandler() throws Exception {
        FormSubmission formSubmission = new FormSubmission("anm id 1", "instance id 1", "renew_fp_product", "entity id 1", 0L, "1", null, 0L);
        when(formSubmissionsRepository.findByInstanceId("instance id 1")).thenReturn(formSubmission);

        router.route("instance id 1");

        verify(formSubmissionsRepository).findByInstanceId("instance id 1");
        verify(renewFPProductHandler).handle(formSubmission);
        verify(formSubmissionReportService).reportFor(formSubmission);
    }

    @Test
    public void shouldDelegateFPReferralFollowupFormSubmissionHandlingToFPReferralFollowupHandler() throws Exception {
        FormSubmission formSubmission = new FormSubmission("anm id 1", "instance id 1", "fp_referral_followup", "entity id 1", 0L, "1", null, 0L);
        when(formSubmissionsRepository.findByInstanceId("instance id 1")).thenReturn(formSubmission);

        router.route("instance id 1");

        verify(formSubmissionsRepository).findByInstanceId("instance id 1");
        verify(fpReferralFollowupHandler).handle(formSubmission);
        verify(formSubmissionReportService).reportFor(formSubmission);
    }

    @Test
    public void shouldDelegateECCloseFormSubmissionHandlingToECCloseHandler() throws Exception {
        FormSubmission formSubmission = new FormSubmission("anm id 1", "instance id 1", "ec_close", "entity id 1", 0L, "1", null, 0L);
        when(formSubmissionsRepository.findByInstanceId("instance id 1")).thenReturn(formSubmission);

        router.route("instance id 1");

        verify(formSubmissionsRepository).findByInstanceId("instance id 1");
        verify(ecCloseHandler).handle(formSubmission);
        verify(formSubmissionReportService).reportFor(formSubmission);
    }

    @Test
    public void shouldDelegateANCRegistrationFormSubmissionHandlingToANCRegistrationHandler() throws Exception {
        FormSubmission formSubmission = new FormSubmission("anm id 1", "instance id 1", "anc_registration", "entity id 1", 0L, "1", null, 0L);
        when(formSubmissionsRepository.findByInstanceId("instance id 1")).thenReturn(formSubmission);

        router.route("instance id 1");

        verify(formSubmissionsRepository).findByInstanceId("instance id 1");
        verify(ancRegistrationHandler).handle(formSubmission);
        verify(formSubmissionReportService).reportFor(formSubmission);
    }

    @Test
    public void shouldDelegateANCRegistrationOAFormSubmissionHandlingToANCRegistrationOAHandler() throws Exception {
        FormSubmission formSubmission = new FormSubmission("anm id 1", "instance id 1", "anc_registration_oa", "entity id 1", 0L, "1", null, 0L);
        when(formSubmissionsRepository.findByInstanceId("instance id 1")).thenReturn(formSubmission);

        router.route("instance id 1");

        verify(formSubmissionsRepository).findByInstanceId("instance id 1");
        verify(ancRegistrationOAHandler).handle(formSubmission);
        verify(formSubmissionReportService).reportFor(formSubmission);
    }

    @Test
    public void shouldDelegateANCVisitFormSubmissionHandlingToANCVisitHandler() throws Exception {
        FormSubmission formSubmission = new FormSubmission("anm id 1", "instance id 1", "anc_visit", "entity id 1", 0L, "1", null, 0L);
        when(formSubmissionsRepository.findByInstanceId("instance id 1")).thenReturn(formSubmission);

        router.route("instance id 1");

        verify(formSubmissionsRepository).findByInstanceId("instance id 1");
        verify(ancVisitHandler).handle(formSubmission);
        verify(formSubmissionReportService).reportFor(formSubmission);
    }

    @Test
    public void shouldDelegateANCCloseFormSubmissionHandlingToANCCloseHandler() throws Exception {
        FormSubmission formSubmission = new FormSubmission("anm id 1", "instance id 1", "anc_close", "entity id 1", 0L, "1", null, 0L);
        when(formSubmissionsRepository.findByInstanceId("instance id 1")).thenReturn(formSubmission);

        router.route("instance id 1");

        verify(formSubmissionsRepository).findByInstanceId("instance id 1");
        verify(ancCloseHandler).handle(formSubmission);
        verify(formSubmissionReportService).reportFor(formSubmission);
    }

    @Test
    public void shouldDelegateTTBoosterFormSubmissionHandlingToTTHandler() throws Exception {
        FormSubmission formSubmission = new FormSubmission("anm id 1", "instance id 1", "tt_booster", "entity id 1", 0L, "1", null, 0L);
        when(formSubmissionsRepository.findByInstanceId("instance id 1")).thenReturn(formSubmission);

        router.route("instance id 1");

        verify(formSubmissionsRepository).findByInstanceId("instance id 1");
        verify(ttHandler).handle(formSubmission);
        verify(formSubmissionReportService).reportFor(formSubmission);
    }

    @Test
    public void shouldDelegateTT1FormSubmissionHandlingToTTHandler() throws Exception {
        FormSubmission formSubmission = new FormSubmission("anm id 1", "instance id 1", "tt_1", "entity id 1", 0L, "1", null, 0L);
        when(formSubmissionsRepository.findByInstanceId("instance id 1")).thenReturn(formSubmission);

        router.route("instance id 1");

        verify(formSubmissionsRepository).findByInstanceId("instance id 1");
        verify(ttHandler).handle(formSubmission);
        verify(formSubmissionReportService).reportFor(formSubmission);
    }

    @Test
    public void shouldDelegateTT2FormSubmissionHandlingToTTHandler() throws Exception {
        FormSubmission formSubmission = new FormSubmission("anm id 1", "instance id 1", "tt_2", "entity id 1", 0L, "1", null, 0L);
        when(formSubmissionsRepository.findByInstanceId("instance id 1")).thenReturn(formSubmission);

        router.route("instance id 1");

        verify(formSubmissionsRepository).findByInstanceId("instance id 1");
        verify(ttHandler).handle(formSubmission);
        verify(formSubmissionReportService).reportFor(formSubmission);
    }

    @Test
    public void shouldDelegateIFAFormSubmissionHandlingToIFAHandler() throws Exception {
        FormSubmission formSubmission = new FormSubmission("anm id 1", "instance id 1", "ifa", "entity id 1", 0L, "1", null, 0L);
        when(formSubmissionsRepository.findByInstanceId("instance id 1")).thenReturn(formSubmission);

        router.route("instance id 1");

        verify(formSubmissionsRepository).findByInstanceId("instance id 1");
        verify(ifaHandler).handle(formSubmission);
        verify(formSubmissionReportService).reportFor(formSubmission);
    }

    @Test
    public void shouldDelegateHbTestFormSubmissionHandlingToHBTestHandler() throws Exception {
        FormSubmission formSubmission = new FormSubmission("anm id 1", "instance id 1", "hb_test", "entity id 1", 0L, "1", null, 0L);
        when(formSubmissionsRepository.findByInstanceId("instance id 1")).thenReturn(formSubmission);

        router.route("instance id 1");

        verify(formSubmissionsRepository).findByInstanceId("instance id 1");
        verify(hbTestHandler).handle(formSubmission);
        verify(formSubmissionReportService).reportFor(formSubmission);
    }

    @Test
    public void shouldDelegateDeliveryOutcomeTestFormSubmissionHandlingToDeliveryOutcomeHandler() throws Exception {
        FormSubmission formSubmission = new FormSubmission("anm id 1", "instance id 1", "delivery_outcome", "entity id 1", 0L, "1", null, 0L);
        when(formSubmissionsRepository.findByInstanceId("instance id 1")).thenReturn(formSubmission);

        router.route("instance id 1");

        verify(formSubmissionsRepository).findByInstanceId("instance id 1");
        verify(deliveryOutcomeHandler).handle(formSubmission);
        verify(formSubmissionReportService).reportFor(formSubmission);
    }

    @Test
    public void shouldDelegatePNCRegistrationOAFormSubmissionHandlingToPNCRegistrationOAHandler() throws Exception {
        FormSubmission formSubmission = new FormSubmission("anm id 1", "instance id 1", "pnc_registration_oa", "entity id 1", 0L, "1", null, 0L);
        when(formSubmissionsRepository.findByInstanceId("instance id 1")).thenReturn(formSubmission);

        router.route("instance id 1");

        verify(formSubmissionsRepository).findByInstanceId("instance id 1");
        verify(pncRegistrationOAHandler).handle(formSubmission);
        verify(formSubmissionReportService).reportFor(formSubmission);
    }

    @Test
    public void shouldDelegatePNCCloseFormSubmissionHandlingToPNCCloseHandler() throws Exception {
        FormSubmission formSubmission = new FormSubmission("anm id 1", "instance id 1", "pnc_close", "entity id 1", 0L, "1", null, 0L);
        when(formSubmissionsRepository.findByInstanceId("instance id 1")).thenReturn(formSubmission);

        router.route("instance id 1");

        verify(formSubmissionsRepository).findByInstanceId("instance id 1");
        verify(pncCloseHandler).handle(formSubmission);
        verify(formSubmissionReportService).reportFor(formSubmission);
    }

    @Test
    public void shouldDelegatePNCVisitFormSubmissionHandlingToPNCVisitHandler() throws Exception {
        FormSubmission formSubmission = new FormSubmission("anm id 1", "instance id 1", "pnc_visit", "entity id 1", 0L, "1", null, 0L);
        when(formSubmissionsRepository.findByInstanceId("instance id 1")).thenReturn(formSubmission);

        router.route("instance id 1");

        verify(formSubmissionsRepository).findByInstanceId("instance id 1");
        verify(pncVisitHandler).handle(formSubmission);
        verify(formSubmissionReportService).reportFor(formSubmission);
    }

    @Test
    public void shouldDelegateChildRegistrationECFormSubmissionHandlingToChildRegistrationECHandler() throws Exception {
        FormSubmission formSubmission = new FormSubmission("anm id 1", "instance id 1", "child_registration_ec", "entity id 1", 0L, "1", null, 0L);
        when(formSubmissionsRepository.findByInstanceId("instance id 1")).thenReturn(formSubmission);

        router.route("instance id 1");

        verify(formSubmissionsRepository).findByInstanceId("instance id 1");
        verify(childRegistrationECHandler).handle(formSubmission);
        verify(formSubmissionReportService).reportFor(formSubmission);
    }

    @Test
    public void shouldDelegateChildImmunizationsFormSubmissionHandlingToChildImmunizationsHandler() throws Exception {
        FormSubmission formSubmission = new FormSubmission("anm id 1", "instance id 1", "child_immunizations", "entity id 1", 0L, "1", null, 0L);
        when(formSubmissionsRepository.findByInstanceId("instance id 1")).thenReturn(formSubmission);

        router.route("instance id 1");

        verify(formSubmissionsRepository).findByInstanceId("instance id 1");
        verify(childImmunizationsHandler).handle(formSubmission);
        verify(formSubmissionReportService).reportFor(formSubmission);
    }

    @Test
    public void shouldDelegateChildCloseFormSubmissionHandlingToChildCloseHandler() throws Exception {
        FormSubmission formSubmission = new FormSubmission("anm id 1", "instance id 1", "child_close", "entity id 1", 0L, "1", null, 0L);
        when(formSubmissionsRepository.findByInstanceId("instance id 1")).thenReturn(formSubmission);

        router.route("instance id 1");

        verify(formSubmissionsRepository).findByInstanceId("instance id 1");
        verify(childCloseHandler).handle(formSubmission);
        verify(formSubmissionReportService).reportFor(formSubmission);
    }

    @Test
    public void shouldDelegateChildRegistrationOAFormSubmissionHandlingToChildRegistrationOAHandler() throws Exception {
        FormSubmission formSubmission = new FormSubmission("anm id 1", "instance id 1", "child_registration_oa", "entity id 1", 0L, "1", null, 0L);
        when(formSubmissionsRepository.findByInstanceId("instance id 1")).thenReturn(formSubmission);

        router.route("instance id 1");

        verify(formSubmissionsRepository).findByInstanceId("instance id 1");
        verify(childRegistrationOAHandler).handle(formSubmission);
        verify(formSubmissionReportService).reportFor(formSubmission);
    }

    @Test
    public void shouldDelegateVitaminAFormSubmissionHandlingToVitaminAHandler() throws Exception {
        FormSubmission formSubmission = new FormSubmission("anm id 1", "instance id 1", "vitamin_a", "entity id 1", 0L, "1", null, 0L);
        when(formSubmissionsRepository.findByInstanceId("instance id 1")).thenReturn(formSubmission);

        router.route("instance id 1");

        verify(formSubmissionsRepository).findByInstanceId("instance id 1");
        verify(vitaminAHandler).handle(formSubmission);
        verify(formSubmissionReportService).reportFor(formSubmission);
    }

    @Test
    public void shouldDelegateDeliveryPlanFormSubmissionHandlingToDeliveryPlanHandler() throws Exception {
        FormSubmission formSubmission = new FormSubmission("anm id 1", "instance id 1", "delivery_plan", "entity id 1", 0L, "1", null, 0L);
        when(formSubmissionsRepository.findByInstanceId("instance id 1")).thenReturn(formSubmission);

        router.route("instance id 1");

        verify(formSubmissionsRepository).findByInstanceId("instance id 1");
        verify(deliveryPlanHandler).handle(formSubmission);
        verify(formSubmissionReportService).reportFor(formSubmission);
    }

    @Test
    public void shouldDelegatePostpartumFamilyPlanningFormSubmissionHandlingToPostpartumFamilyPlanningHandler() throws Exception {
        FormSubmission formSubmission = new FormSubmission("anm id 1", "instance id 1", "postpartum_family_planning", "entity id 1", 0L, "1", null, 0L);
        when(formSubmissionsRepository.findByInstanceId("instance id 1")).thenReturn(formSubmission);

        router.route("instance id 1");

        verify(formSubmissionsRepository).findByInstanceId("instance id 1");
        verify(postpartumFamilyPlanningHandler).handle(formSubmission);
        verify(formSubmissionReportService).reportFor(formSubmission);
    }

    @Test
    public void shouldDelegateRecordECPsFormSubmissionHandlingToRecordECPsHandler() throws Exception {
        FormSubmission formSubmission = new FormSubmission("anm id 1", "instance id 1", "record_ecps", "entity id 1", 0L, "1", null, 0L);
        when(formSubmissionsRepository.findByInstanceId("instance id 1")).thenReturn(formSubmission);
        router.route("instance id 1");

        verify(formSubmissionsRepository).findByInstanceId("instance id 1");
        verify(recordECPsHandler).handle(formSubmission);
    }

    @Test
    public void shouldDelegateECEditFormSubmissionHandlingToECEditHandler() throws Exception {
        FormSubmission formSubmission = new FormSubmission("anm id 1", "instance id 1", "ec_edit", "entity id 1", 0L, "1", null, 0L);
        when(formSubmissionsRepository.findByInstanceId("instance id 1")).thenReturn(formSubmission);

        router.route("instance id 1");

        verify(ecEditHandler).handle(formSubmission);
        verify(formSubmissionReportService).reportFor(formSubmission);
    }

    @Test
    public void shouldDelegateANCInvestigationsFormSubmissionHandlingToANCInvestigationswHandler() throws Exception {
        FormSubmission formSubmission = new FormSubmission("anm id 1", "instance id 1", "anc_investigations", "entity id 1", 0L, "1", null, 0L);
        when(formSubmissionsRepository.findByInstanceId("instance id 1")).thenReturn(formSubmission);

        router.route("instance id 1");

        verify(formSubmissionsRepository).findByInstanceId("instance id 1");
        verify(ancInvestigationsHandler).handle(formSubmission);
        verify(formSubmissionReportService).reportFor(formSubmission);
    }
}
