package org.opensrp.register.service.handler;

import java.util.Map;

import org.opensrp.common.util.EasyMap;
import org.opensrp.form.repository.AllFormSubmissions;
import org.opensrp.register.service.reporting.MCTSReportService;
import org.opensrp.service.formSubmission.handler.FormSubmissionHandler;
import org.opensrp.service.formSubmission.handler.IHandlerMapper;
import org.opensrp.service.reporting.FormSubmissionReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import static org.opensrp.common.AllConstants.Form.*;

@Service
public class HandlerMapper implements IHandlerMapper {

	private final Map<String, FormSubmissionHandler> handlerMap;
	
	@Autowired
	public HandlerMapper(  
			AllFormSubmissions formSubmissionsRepository,
			FormSubmissionReportService formSubmissionReportService,
			ECRegistrationHandler ecRegistrationHandler,
            FPComplicationsHandler fpComplicationsHandler,
            FPChangeHandler fpChangeHandler,
            RenewFPProductHandler renewFPProductHandler,
            FPFollowupHandler fpFollowupHandler,
            FPReferralFollowupHandler fpReferralFollowupHandler,
            ECCloseHandler ecCloseHandler,
            ANCRegistrationHandler ancRegistrationHandler,
            ANCRegistrationOAHandler ancRegistrationOAHandler,
            ANCVisitHandler ancVisitHandler,
            ANCCloseHandler ancCloseHandler,
            TTHandler ttHandler,
            IFAHandler ifaHandler,
            HbTestHandler hbTestHandler,
            DeliveryOutcomeHandler deliveryOutcomeHandler,
            PNCRegistrationOAHandler pncRegistrationOAHandler,
            PNCCloseHandler pncCloseHandler,
            PNCVisitHandler pncVisitHandler,
            ChildRegistrationECHandler childRegistrationECHandler,
            ChildRegistrationOAHandler childRegistrationOAHandler,
            VitaminAHandler vitaminAHandler,
            ChildImmunizationsHandler childImmunizationsHandler,
            ChildIllnessHandler childIllnessHandler,
            ChildCloseHandler childCloseHandler,
            DeliveryPlanHandler deliveryPlanHandler,
            PostpartumFamilyPlanningHandler postpartumFamilyPlanningHandler,
            RecordECPsHandler recordECPsHandler,
            ECEditHandler ecEditHandler,
            ANCInvestigationsHandler ancInvestigationsHandler,
            MCTSReportService mctsReportService)
	{
		
		
		handlerMap = EasyMap.create(EC_REGISTRATION, (FormSubmissionHandler) ecRegistrationHandler)
                .put(FP_COMPLICATIONS, fpComplicationsHandler)
                .put(FP_CHANGE, fpChangeHandler)
                .put(RENEW_FP_PRODUCT, renewFPProductHandler)
                .put(FP_FOLLOWUP_PRODUCT, fpFollowupHandler)
                .put(FP_REFERRAL_FOLLOWUP, fpReferralFollowupHandler)
                .put(EC_CLOSE, ecCloseHandler)
                .put(ANC_REGISTRATION, ancRegistrationHandler)
                .put(ANC_REGISTRATION_OA, ancRegistrationOAHandler)
                .put(ANC_VISIT, ancVisitHandler)
                .put(ANC_CLOSE, ancCloseHandler)
                .put(TT, ttHandler)
                .put(TT_BOOSTER, ttHandler)
                .put(TT_1, ttHandler)
                .put(TT_2, ttHandler)
                .put(IFA, ifaHandler)
                .put(HB_TEST, hbTestHandler)
                .put(DELIVERY_OUTCOME, deliveryOutcomeHandler)
                .put(DELIVERY_PLAN, deliveryPlanHandler)
                .put(PNC_REGISTRATION_OA, pncRegistrationOAHandler)
                .put(PNC_CLOSE, pncCloseHandler)
                .put(PNC_VISIT, pncVisitHandler)
                .put(CHILD_REGISTRATION_EC, childRegistrationECHandler)
                .put(CHILD_REGISTRATION_OA, childRegistrationOAHandler)
                .put(CHILD_IMMUNIZATIONS, childImmunizationsHandler)
                .put(CHILD_ILLNESS, childIllnessHandler)
                .put(CHILD_CLOSE, childCloseHandler)
                .put(VITAMIN_A, vitaminAHandler)
                .put(PPFP, postpartumFamilyPlanningHandler)
                .put(RECORD_ECPS, recordECPsHandler)
                .put(EC_EDIT, ecEditHandler)
                .put(ANC_INVESTIGATIONS, ancInvestigationsHandler)
                .map();

	}

	@Override
	public Map<String, FormSubmissionHandler> handlerMapper() {
		return handlerMap;
	}

}
