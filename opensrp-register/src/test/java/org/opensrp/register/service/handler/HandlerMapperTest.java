package org.opensrp.register.service.handler;

import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.opensrp.form.domain.FormSubmission;
import org.opensrp.form.repository.AllFormSubmissions;
import org.opensrp.register.service.reporting.MCTSReportService;
import org.opensrp.service.formSubmission.handler.FormSubmissionHandler;
import org.opensrp.service.reporting.FormSubmissionReportService;
import static org.opensrp.common.AllConstants.Form.*;

public class HandlerMapperTest {
	
	@Mock
	AllFormSubmissions formSubmissionsRepository;
	@Mock
	FormSubmissionReportService formSubmissionReportService;
	@Mock
	ECRegistrationHandler ecRegistrationHandler;
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
    MCTSReportService mctsReportService;

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
	             mctsReportService);
	}
	
	 @Test
	 public void shouldDelegateHandlerMapper() throws Exception {
		 
		 Map<String, FormSubmissionHandler> handler =   handlerMapper.handlerMapper();
	       FormSubmission submission = new FormSubmission("anm id 1", "instance id 1", "anc_visit", "entity id 1", 0L, "1", null, 0L);

	       FormSubmissionHandler ancVisitHandler = (FormSubmissionHandler) handler.get(ANC_VISIT);
	       ancVisitHandler.handle(submission);
	      // verify(ancVisitHandler).handle(submission);
	 }
}
