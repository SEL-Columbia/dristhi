package org.opensrp.register.service.formSubmission;

import org.opensrp.form.domain.FormSubmission;
import org.opensrp.form.repository.AllFormSubmissions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import org.opensrp.register.service.handler.ANCCloseHandler;
import org.opensrp.register.service.handler.ANCInvestigationsHandler;
import org.opensrp.register.service.handler.ANCRegistrationHandler;
import org.opensrp.register.service.handler.ANCRegistrationOAHandler;
import org.opensrp.register.service.handler.ANCVisitHandler;
import org.opensrp.register.service.handler.ChildCloseHandler;
import org.opensrp.register.service.handler.ChildIllnessHandler;
import org.opensrp.register.service.handler.ChildImmunizationsHandler;
import org.opensrp.register.service.handler.ChildRegistrationECHandler;
import org.opensrp.register.service.handler.ChildRegistrationOAHandler;
import org.opensrp.register.service.handler.DeliveryOutcomeHandler;
import org.opensrp.register.service.handler.DeliveryPlanHandler;
import org.opensrp.register.service.handler.ECCloseHandler;
import org.opensrp.register.service.handler.ECEditHandler;
import org.opensrp.register.service.handler.ECRegistrationHandler;
import org.opensrp.register.service.handler.FPChangeHandler;
import org.opensrp.register.service.handler.FPComplicationsHandler;
import org.opensrp.register.service.handler.FPFollowupHandler;
import org.opensrp.register.service.handler.FPReferralFollowupHandler;
import org.opensrp.register.service.handler.HandlerMapper;
import org.opensrp.register.service.handler.HbTestHandler;
import org.opensrp.register.service.handler.IFAHandler;
import org.opensrp.register.service.handler.PNCCloseHandler;
import org.opensrp.register.service.handler.PNCRegistrationOAHandler;
import org.opensrp.register.service.handler.PNCVisitHandler;
import org.opensrp.register.service.handler.PostpartumFamilyPlanningHandler;
import org.opensrp.register.service.handler.RecordECPsHandler;
import org.opensrp.register.service.handler.RenewFPProductHandler;
import org.opensrp.register.service.handler.TTHandler;
import org.opensrp.register.service.handler.VitaminAHandler;
import org.opensrp.register.service.reporting.MCTSReportService;
import org.opensrp.service.formSubmission.FormSubmissionRouter;
import org.opensrp.service.formSubmission.handler.FormSubmissionHandler;
import org.opensrp.service.formSubmission.handler.IHandlerMapper;
import org.opensrp.service.reporting.FormSubmissionReportService;
import org.opensrp.service.reporting.IMCTSReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/*@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:test-applicationContext-opensrp-register.xml")*/
public class FormSubmissionRouterTest {
	
	@Mock
    FPComplicationsHandler fpComplicationsHandler;
	@Mock
    FPChangeHandler fpChangeHandler;
	@Mock
    RenewFPProductHandler renewFPProductHandler;
	@Mock
    FPFollowupHandler fpFollowupHandler;
	@Mock
    FPReferralFollowupHandler fpReferralFollowupHandler;
	@Mock
    ECCloseHandler ecCloseHandler;
	@Mock
    ANCRegistrationHandler ancRegistrationHandler;
	@Mock
    ANCRegistrationOAHandler ancRegistrationOAHandler;
	@Mock
    ANCVisitHandler ancVisitHandler;
	@Mock
    ANCCloseHandler ancCloseHandler;
	@Mock
    TTHandler ttHandler;
	@Mock
    IFAHandler ifaHandler;
	@Mock
    HbTestHandler hbTestHandler;
	@Mock
    DeliveryOutcomeHandler deliveryOutcomeHandler;
	@Mock
    PNCRegistrationOAHandler pncRegistrationOAHandler;
	@Mock
    PNCCloseHandler pncCloseHandler;
	@Mock
    PNCVisitHandler pncVisitHandler;
	@Mock
    ChildRegistrationECHandler childRegistrationECHandler;
	@Mock
    ChildRegistrationOAHandler childRegistrationOAHandler;
	@Mock
    VitaminAHandler vitaminAHandler;
	@Mock
    ChildImmunizationsHandler childImmunizationsHandler;
	@Mock
    ChildIllnessHandler childIllnessHandler;
	@Mock
    ChildCloseHandler childCloseHandler;
	@Mock
    DeliveryPlanHandler deliveryPlanHandler;
	@Mock
    PostpartumFamilyPlanningHandler postpartumFamilyPlanningHandler;
	@Mock
    RecordECPsHandler recordECPsHandler;
	@Mock
    ECEditHandler ecEditHandler;
	@Mock
    ANCInvestigationsHandler ancInvestigationsHandler;
    @Mock
    private AllFormSubmissions formSubmissionsRepository;
    @Mock
    private ECRegistrationHandler ecRegistrationHandler;
    @Mock
    private FormSubmissionReportService formSubmissionReportService;
    @Mock
    private IMCTSReportService mctsReportService;
    @Mock
    private MCTSReportService mReportService;
    @Mock
    private FormSubmissionRouter router;
	@Mock
	private HandlerMapper handlerMapper;
	@Before
    public void setUp() throws Exception {
    	
        initMocks(this);
        
        handlerMapper = new HandlerMapper( 
				 formSubmissionsRepository,
				 formSubmissionReportService,
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
	             mReportService);
	

        
        
        router = new FormSubmissionRouter(
	        		formSubmissionsRepository,
	        		formSubmissionReportService,
	        		mctsReportService,
	        		handlerMapper
        		);
    }

    @Test
    public void shouldDelegateFormSubmissionHandlingToHandlerBasedOnFormName() throws Exception {
        FormSubmission formSubmission = new FormSubmission("anm id 1", "instance id 1", "ec_registration", "entity id 1", 0L, "1", null, 0L);
        when(formSubmissionsRepository.findByInstanceId("instance id 1")).thenReturn(formSubmission);

        router.route("instance id 1");
        FormSubmissionHandler ecRegistrationHandler = (FormSubmissionHandler)handlerMapper.handlerMapper().get(
        		formSubmission.formName());

        verify(ecRegistrationHandler).handle(formSubmission);
        verify(formSubmissionReportService).reportFor(formSubmission);

        formSubmission = new FormSubmission("anm id 1", "instance id 2", "fp_complications", "entity id 2", 0L, "1", null, 0L);
        when(formSubmissionsRepository.findByInstanceId("instance id 2")).thenReturn(formSubmission);

        router.route("instance id 2");
        
        FormSubmissionHandler fpComplicationsHandler = handlerMapper.handlerMapper().get(
        		formSubmission.formName());
        
        verify(fpComplicationsHandler).handle(formSubmission);
        verify(formSubmissionReportService).reportFor(formSubmission);
    }

    @Test
    public void shouldDelegateRenewFPProductFormSubmissionHandlingToRenewFPProductHandler() throws Exception {
        FormSubmission formSubmission = new FormSubmission("anm id 1", "instance id 1", "renew_fp_product", "entity id 1", 0L, "1", null, 0L);
        when(formSubmissionsRepository.findByInstanceId("instance id 1")).thenReturn(formSubmission);

        router.route("instance id 1");

        verify(formSubmissionsRepository).findByInstanceId("instance id 1");
        

        FormSubmissionHandler renewFPProductHandler = handlerMapper.handlerMapper().get(
        		formSubmission.formName());
        
        verify(renewFPProductHandler).handle(formSubmission);
        verify(formSubmissionReportService).reportFor(formSubmission);
    }

    @Test
    public void shouldDelegateFPReferralFollowupFormSubmissionHandlingToFPReferralFollowupHandler() throws Exception {
        FormSubmission formSubmission = new FormSubmission("anm id 1", "instance id 1", "fp_referral_followup", "entity id 1", 0L, "1", null, 0L);
        when(formSubmissionsRepository.findByInstanceId("instance id 1")).thenReturn(formSubmission);

        router.route("instance id 1");

        verify(formSubmissionsRepository).findByInstanceId("instance id 1");
        
        FormSubmissionHandler fpReferralFollowupHandler = handlerMapper.handlerMapper().get(
        		formSubmission.formName());
        verify(fpReferralFollowupHandler).handle(formSubmission);
        verify(formSubmissionReportService).reportFor(formSubmission);
    }

    @Test
    public void shouldDelegateECCloseFormSubmissionHandlingToECCloseHandler() throws Exception {
        FormSubmission formSubmission = new FormSubmission("anm id 1", "instance id 1", "ec_close", "entity id 1", 0L, "1", null, 0L);
        when(formSubmissionsRepository.findByInstanceId("instance id 1")).thenReturn(formSubmission);

        router.route("instance id 1");

        verify(formSubmissionsRepository).findByInstanceId("instance id 1");
        
        FormSubmissionHandler ecCloseHandler = handlerMapper.handlerMapper().get(
        		formSubmission.formName());
        verify(ecCloseHandler).handle(formSubmission);
        verify(formSubmissionReportService).reportFor(formSubmission);
    }

    @Test
    public void shouldDelegateANCRegistrationFormSubmissionHandlingToANCRegistrationHandler() throws Exception {
        FormSubmission formSubmission = new FormSubmission("anm id 1", "instance id 1", "anc_registration", "entity id 1", 0L, "1", null, 0L);
        when(formSubmissionsRepository.findByInstanceId("instance id 1")).thenReturn(formSubmission);

        router.route("instance id 1");

        verify(formSubmissionsRepository).findByInstanceId("instance id 1");
        
        FormSubmissionHandler ancRegistrationHandler = handlerMapper.handlerMapper().get(
        		formSubmission.formName());
        
        verify(ancRegistrationHandler).handle(formSubmission);
        verify(formSubmissionReportService).reportFor(formSubmission);
    }

    @Test
    public void shouldDelegateANCRegistrationOAFormSubmissionHandlingToANCRegistrationOAHandler() throws Exception {
        FormSubmission formSubmission = new FormSubmission("anm id 1", "instance id 1", "anc_registration_oa", "entity id 1", 0L, "1", null, 0L);
        when(formSubmissionsRepository.findByInstanceId("instance id 1")).thenReturn(formSubmission);

        router.route("instance id 1");

        verify(formSubmissionsRepository).findByInstanceId("instance id 1");
        
        FormSubmissionHandler ancRegistrationOAHandler = handlerMapper.handlerMapper().get(
        		formSubmission.formName());
        verify(ancRegistrationOAHandler).handle(formSubmission);
        verify(formSubmissionReportService).reportFor(formSubmission);
    }

    @Test
    public void shouldDelegateANCVisitFormSubmissionHandlingToANCVisitHandler() throws Exception {
        FormSubmission formSubmission = new FormSubmission("anm id 1", "instance id 1", "anc_visit", "entity id 1", 0L, "1", null, 0L);
        when(formSubmissionsRepository.findByInstanceId("instance id 1")).thenReturn(formSubmission);

        router.route("instance id 1");

        verify(formSubmissionsRepository).findByInstanceId("instance id 1");
        
        FormSubmissionHandler ancVisitHandler = handlerMapper.handlerMapper().get(
        		formSubmission.formName());
        verify(ancVisitHandler).handle(formSubmission);
        verify(formSubmissionReportService).reportFor(formSubmission);
    }

    @Test
    public void shouldDelegateANCCloseFormSubmissionHandlingToANCCloseHandler() throws Exception {
        FormSubmission formSubmission = new FormSubmission("anm id 1", "instance id 1", "anc_close", "entity id 1", 0L, "1", null, 0L);
        when(formSubmissionsRepository.findByInstanceId("instance id 1")).thenReturn(formSubmission);

        router.route("instance id 1");

        verify(formSubmissionsRepository).findByInstanceId("instance id 1");
        
        FormSubmissionHandler ancCloseHandler = handlerMapper.handlerMapper().get(
        		formSubmission.formName());
        verify(ancCloseHandler).handle(formSubmission);
        verify(formSubmissionReportService).reportFor(formSubmission);
    }

    @Test
    public void shouldDelegateTTBoosterFormSubmissionHandlingToTTHandler() throws Exception {
        FormSubmission formSubmission = new FormSubmission("anm id 1", "instance id 1", "tt_booster", "entity id 1", 0L, "1", null, 0L);
        when(formSubmissionsRepository.findByInstanceId("instance id 1")).thenReturn(formSubmission);

        router.route("instance id 1");

        verify(formSubmissionsRepository).findByInstanceId("instance id 1");
        FormSubmissionHandler ttHandler = handlerMapper.handlerMapper().get(
        		formSubmission.formName());
        verify(ttHandler).handle(formSubmission);
        verify(formSubmissionReportService).reportFor(formSubmission);
    }

    @Test
    public void shouldDelegateTT1FormSubmissionHandlingToTTHandler() throws Exception {
        FormSubmission formSubmission = new FormSubmission("anm id 1", "instance id 1", "tt_1", "entity id 1", 0L, "1", null, 0L);
        when(formSubmissionsRepository.findByInstanceId("instance id 1")).thenReturn(formSubmission);

        router.route("instance id 1");

        verify(formSubmissionsRepository).findByInstanceId("instance id 1");
        FormSubmissionHandler ttHandler = handlerMapper.handlerMapper().get(
        		formSubmission.formName());
        verify(ttHandler).handle(formSubmission);
        verify(formSubmissionReportService).reportFor(formSubmission);
    }

    @Test
    public void shouldDelegateTT2FormSubmissionHandlingToTTHandler() throws Exception {
        FormSubmission formSubmission = new FormSubmission("anm id 1", "instance id 1", "tt_2", "entity id 1", 0L, "1", null, 0L);
        when(formSubmissionsRepository.findByInstanceId("instance id 1")).thenReturn(formSubmission);

        router.route("instance id 1");

        verify(formSubmissionsRepository).findByInstanceId("instance id 1");
        FormSubmissionHandler ttHandler = handlerMapper.handlerMapper().get(
        		formSubmission.formName());
        verify(ttHandler).handle(formSubmission);
        verify(formSubmissionReportService).reportFor(formSubmission);
    }

    @Test
    public void shouldDelegateIFAFormSubmissionHandlingToIFAHandler() throws Exception {
        FormSubmission formSubmission = new FormSubmission("anm id 1", "instance id 1", "ifa", "entity id 1", 0L, "1", null, 0L);
        when(formSubmissionsRepository.findByInstanceId("instance id 1")).thenReturn(formSubmission);

        router.route("instance id 1");

        verify(formSubmissionsRepository).findByInstanceId("instance id 1");
        
        FormSubmissionHandler ifaHandler = handlerMapper.handlerMapper().get(
        		formSubmission.formName());
        verify(ifaHandler).handle(formSubmission);
        verify(formSubmissionReportService).reportFor(formSubmission);
    }

    @Test
    public void shouldDelegateHbTestFormSubmissionHandlingToHBTestHandler() throws Exception {
        FormSubmission formSubmission = new FormSubmission("anm id 1", "instance id 1", "hb_test", "entity id 1", 0L, "1", null, 0L);
        when(formSubmissionsRepository.findByInstanceId("instance id 1")).thenReturn(formSubmission);

        router.route("instance id 1");

        verify(formSubmissionsRepository).findByInstanceId("instance id 1");
        FormSubmissionHandler hbTestHandler = handlerMapper.handlerMapper().get(
        		formSubmission.formName());
        verify(hbTestHandler).handle(formSubmission);
        verify(formSubmissionReportService).reportFor(formSubmission);
    }

    @Test
    public void shouldDelegateDeliveryOutcomeTestFormSubmissionHandlingToDeliveryOutcomeHandler() throws Exception {
        FormSubmission formSubmission = new FormSubmission("anm id 1", "instance id 1", "delivery_outcome", "entity id 1", 0L, "1", null, 0L);
        when(formSubmissionsRepository.findByInstanceId("instance id 1")).thenReturn(formSubmission);

        router.route("instance id 1");

        verify(formSubmissionsRepository).findByInstanceId("instance id 1");
        
        FormSubmissionHandler deliveryOutcomeHandler = handlerMapper.handlerMapper().get(
        		formSubmission.formName());
        verify(deliveryOutcomeHandler).handle(formSubmission);
        verify(formSubmissionReportService).reportFor(formSubmission);
    }

    @Test
    public void shouldDelegatePNCRegistrationOAFormSubmissionHandlingToPNCRegistrationOAHandler() throws Exception {
        FormSubmission formSubmission = new FormSubmission("anm id 1", "instance id 1", "pnc_registration_oa", "entity id 1", 0L, "1", null, 0L);
        when(formSubmissionsRepository.findByInstanceId("instance id 1")).thenReturn(formSubmission);

        router.route("instance id 1");

        verify(formSubmissionsRepository).findByInstanceId("instance id 1");
        
        FormSubmissionHandler pncRegistrationOAHandler = handlerMapper.handlerMapper().get(
        		formSubmission.formName());
        verify(pncRegistrationOAHandler).handle(formSubmission);
        verify(formSubmissionReportService).reportFor(formSubmission);
    }

    @Test
    public void shouldDelegatePNCCloseFormSubmissionHandlingToPNCCloseHandler() throws Exception {
        FormSubmission formSubmission = new FormSubmission("anm id 1", "instance id 1", "pnc_close", "entity id 1", 0L, "1", null, 0L);
        when(formSubmissionsRepository.findByInstanceId("instance id 1")).thenReturn(formSubmission);

        router.route("instance id 1");

        verify(formSubmissionsRepository).findByInstanceId("instance id 1");
        
        FormSubmissionHandler pncCloseHandler = handlerMapper.handlerMapper().get(
        		formSubmission.formName());
        verify(pncCloseHandler).handle(formSubmission);
        verify(formSubmissionReportService).reportFor(formSubmission);
    }

    @Test
    public void shouldDelegatePNCVisitFormSubmissionHandlingToPNCVisitHandler() throws Exception {
        FormSubmission formSubmission = new FormSubmission("anm id 1", "instance id 1", "pnc_visit", "entity id 1", 0L, "1", null, 0L);
        when(formSubmissionsRepository.findByInstanceId("instance id 1")).thenReturn(formSubmission);

        router.route("instance id 1");

        verify(formSubmissionsRepository).findByInstanceId("instance id 1");
        
        FormSubmissionHandler pncVisitHandler = handlerMapper.handlerMapper().get(
        		formSubmission.formName());
        verify(pncVisitHandler).handle(formSubmission);
        verify(formSubmissionReportService).reportFor(formSubmission);
    }

    @Test
    public void shouldDelegateChildRegistrationECFormSubmissionHandlingToChildRegistrationECHandler() throws Exception {
        FormSubmission formSubmission = new FormSubmission("anm id 1", "instance id 1", "child_registration_ec", "entity id 1", 0L, "1", null, 0L);
        when(formSubmissionsRepository.findByInstanceId("instance id 1")).thenReturn(formSubmission);

        router.route("instance id 1");

        verify(formSubmissionsRepository).findByInstanceId("instance id 1");
        
        FormSubmissionHandler childRegistrationECHandler = handlerMapper.handlerMapper().get(
        		formSubmission.formName());
        verify(childRegistrationECHandler).handle(formSubmission);
        verify(formSubmissionReportService).reportFor(formSubmission);
    }

    @Test
    public void shouldDelegateChildImmunizationsFormSubmissionHandlingToChildImmunizationsHandler() throws Exception {
        FormSubmission formSubmission = new FormSubmission("anm id 1", "instance id 1", "child_immunizations", "entity id 1", 0L, "1", null, 0L);
        when(formSubmissionsRepository.findByInstanceId("instance id 1")).thenReturn(formSubmission);

        router.route("instance id 1");

        verify(formSubmissionsRepository).findByInstanceId("instance id 1");
        
        FormSubmissionHandler childImmunizationsHandler = handlerMapper.handlerMapper().get(
        		formSubmission.formName());
        verify(childImmunizationsHandler).handle(formSubmission);
        verify(formSubmissionReportService).reportFor(formSubmission);
    }

    @Test
    public void shouldDelegateChildCloseFormSubmissionHandlingToChildCloseHandler() throws Exception {
        FormSubmission formSubmission = new FormSubmission("anm id 1", "instance id 1", "child_close", "entity id 1", 0L, "1", null, 0L);
        when(formSubmissionsRepository.findByInstanceId("instance id 1")).thenReturn(formSubmission);

        router.route("instance id 1");

        verify(formSubmissionsRepository).findByInstanceId("instance id 1");
        
        FormSubmissionHandler childCloseHandler = handlerMapper.handlerMapper().get(
        		formSubmission.formName());
        verify(childCloseHandler).handle(formSubmission);
        verify(formSubmissionReportService).reportFor(formSubmission);
    }

    @Test
    public void shouldDelegateChildRegistrationOAFormSubmissionHandlingToChildRegistrationOAHandler() throws Exception {
        FormSubmission formSubmission = new FormSubmission("anm id 1", "instance id 1", "child_registration_oa", "entity id 1", 0L, "1", null, 0L);
        when(formSubmissionsRepository.findByInstanceId("instance id 1")).thenReturn(formSubmission);

        router.route("instance id 1");

        verify(formSubmissionsRepository).findByInstanceId("instance id 1");
        
        FormSubmissionHandler childRegistrationOAHandler = handlerMapper.handlerMapper().get(
        		formSubmission.formName());
        verify(childRegistrationOAHandler).handle(formSubmission);
        verify(formSubmissionReportService).reportFor(formSubmission);
    }

    @Test
    public void shouldDelegateVitaminAFormSubmissionHandlingToVitaminAHandler() throws Exception {
        FormSubmission formSubmission = new FormSubmission("anm id 1", "instance id 1", "vitamin_a", "entity id 1", 0L, "1", null, 0L);
        when(formSubmissionsRepository.findByInstanceId("instance id 1")).thenReturn(formSubmission);

        router.route("instance id 1");

        verify(formSubmissionsRepository).findByInstanceId("instance id 1");
        
        FormSubmissionHandler vitaminAHandler = handlerMapper.handlerMapper().get(
        		formSubmission.formName());
        verify(vitaminAHandler).handle(formSubmission);
        verify(formSubmissionReportService).reportFor(formSubmission);
    }

    @Test
    public void shouldDelegateDeliveryPlanFormSubmissionHandlingToDeliveryPlanHandler() throws Exception {
        FormSubmission formSubmission = new FormSubmission("anm id 1", "instance id 1", "delivery_plan", "entity id 1", 0L, "1", null, 0L);
        when(formSubmissionsRepository.findByInstanceId("instance id 1")).thenReturn(formSubmission);

        router.route("instance id 1");

        verify(formSubmissionsRepository).findByInstanceId("instance id 1");
        
        FormSubmissionHandler deliveryPlanHandler = handlerMapper.handlerMapper().get(
        		formSubmission.formName());
        verify(deliveryPlanHandler).handle(formSubmission);
        verify(formSubmissionReportService).reportFor(formSubmission);
    }

    @Test
    public void shouldDelegatePostpartumFamilyPlanningFormSubmissionHandlingToPostpartumFamilyPlanningHandler() throws Exception {
        FormSubmission formSubmission = new FormSubmission("anm id 1", "instance id 1", "postpartum_family_planning", "entity id 1", 0L, "1", null, 0L);
        when(formSubmissionsRepository.findByInstanceId("instance id 1")).thenReturn(formSubmission);

        router.route("instance id 1");

        verify(formSubmissionsRepository).findByInstanceId("instance id 1");
        
        FormSubmissionHandler postpartumFamilyPlanningHandler = handlerMapper.handlerMapper().get(
        		formSubmission.formName());
        verify(postpartumFamilyPlanningHandler).handle(formSubmission);
        verify(formSubmissionReportService).reportFor(formSubmission);
    }

    @Test
    public void shouldDelegateRecordECPsFormSubmissionHandlingToRecordECPsHandler() throws Exception {
        FormSubmission formSubmission = new FormSubmission("anm id 1", "instance id 1", "record_ecps", "entity id 1", 0L, "1", null, 0L);
        when(formSubmissionsRepository.findByInstanceId("instance id 1")).thenReturn(formSubmission);
        router.route("instance id 1");

        verify(formSubmissionsRepository).findByInstanceId("instance id 1");
        
        FormSubmissionHandler recordECPsHandler = handlerMapper.handlerMapper().get(
        		formSubmission.formName());
        verify(recordECPsHandler).handle(formSubmission);
    }

    @Test
    public void shouldDelegateECEditFormSubmissionHandlingToECEditHandler() throws Exception {
        FormSubmission formSubmission = new FormSubmission("anm id 1", "instance id 1", "ec_edit", "entity id 1", 0L, "1", null, 0L);
        when(formSubmissionsRepository.findByInstanceId("instance id 1")).thenReturn(formSubmission);

        router.route("instance id 1");

        FormSubmissionHandler ecEditHandler = handlerMapper.handlerMapper().get(
        		formSubmission.formName());
        verify(ecEditHandler).handle(formSubmission);
        verify(formSubmissionReportService).reportFor(formSubmission);
    }

    @Test
    public void shouldDelegateANCInvestigationsFormSubmissionHandlingToANCInvestigationswHandler() throws Exception {
        FormSubmission formSubmission = new FormSubmission("anm id 1", "instance id 1", "anc_investigations", "entity id 1", 0L, "1", null, 0L);
        when(formSubmissionsRepository.findByInstanceId("instance id 1")).thenReturn(formSubmission);

        router.route("instance id 1");

        verify(formSubmissionsRepository).findByInstanceId("instance id 1");
        
        FormSubmissionHandler ancInvestigationsHandler = handlerMapper.handlerMapper().get(
        		formSubmission.formName());
        verify(ancInvestigationsHandler).handle(formSubmission);
        verify(formSubmissionReportService).reportFor(formSubmission);
    }
}
