package org.opensrp.register.service.handler;

import static org.opensrp.common.AllConstants.Form.ANC_CLOSE;
import static org.opensrp.common.AllConstants.Form.ANC_INVESTIGATIONS;
import static org.opensrp.common.AllConstants.Form.ANC_REGISTRATION;
import static org.opensrp.common.AllConstants.Form.ANC_REGISTRATION_OA;
import static org.opensrp.common.AllConstants.Form.ANC_VISIT;
import static org.opensrp.common.AllConstants.Form.CHILD_CLOSE;
import static org.opensrp.common.AllConstants.Form.CHILD_ILLNESS;
import static org.opensrp.common.AllConstants.Form.CHILD_IMMUNIZATIONS;
import static org.opensrp.common.AllConstants.Form.CHILD_REGISTRATION_EC;
import static org.opensrp.common.AllConstants.Form.CHILD_REGISTRATION_OA;
import static org.opensrp.common.AllConstants.Form.DELIVERY_OUTCOME;
import static org.opensrp.common.AllConstants.Form.DELIVERY_PLAN;
import static org.opensrp.common.AllConstants.Form.EC_CLOSE;
import static org.opensrp.common.AllConstants.Form.EC_EDIT;
import static org.opensrp.common.AllConstants.Form.EC_REGISTRATION;
import static org.opensrp.common.AllConstants.Form.FP_CHANGE;
import static org.opensrp.common.AllConstants.Form.FP_COMPLICATIONS;
import static org.opensrp.common.AllConstants.Form.FP_FOLLOWUP_PRODUCT;
import static org.opensrp.common.AllConstants.Form.FP_REFERRAL_FOLLOWUP;
import static org.opensrp.common.AllConstants.Form.HB_TEST;
import static org.opensrp.common.AllConstants.Form.IFA;
import static org.opensrp.common.AllConstants.Form.PNC_CLOSE;
import static org.opensrp.common.AllConstants.Form.PNC_REGISTRATION_OA;
import static org.opensrp.common.AllConstants.Form.PNC_VISIT;
import static org.opensrp.common.AllConstants.Form.PPFP;
import static org.opensrp.common.AllConstants.Form.RECORD_ECPS;
import static org.opensrp.common.AllConstants.Form.RENEW_FP_PRODUCT;
import static org.opensrp.common.AllConstants.Form.TT;
import static org.opensrp.common.AllConstants.Form.TT_1;
import static org.opensrp.common.AllConstants.Form.TT_2;
import static org.opensrp.common.AllConstants.Form.TT_BOOSTER;
import static org.opensrp.common.AllConstants.Form.VITAMIN_A;

import org.opensrp.form.repository.AllFormSubmissions;
import org.opensrp.register.service.reporting.MCTSReportService;
import org.opensrp.service.formSubmission.handler.HandlerMapper;
import org.opensrp.service.reporting.FormSubmissionReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FormHandlerMapper {

	@Autowired
	public FormHandlerMapper(HandlerMapper handlerMapper,
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
		handlerMapper.addHandler(EC_REGISTRATION, ecRegistrationHandler)
                .addHandler(FP_COMPLICATIONS, fpComplicationsHandler)
                .addHandler(FP_CHANGE, fpChangeHandler)
                .addHandler(RENEW_FP_PRODUCT, renewFPProductHandler)
                .addHandler(FP_FOLLOWUP_PRODUCT, fpFollowupHandler)
                .addHandler(FP_REFERRAL_FOLLOWUP, fpReferralFollowupHandler)
                .addHandler(EC_CLOSE, ecCloseHandler)
                .addHandler(ANC_REGISTRATION, ancRegistrationHandler)
                .addHandler(ANC_REGISTRATION_OA, ancRegistrationOAHandler)
                .addHandler(ANC_VISIT, ancVisitHandler)
                .addHandler(ANC_CLOSE, ancCloseHandler)
                .addHandler(TT, ttHandler)
                .addHandler(TT_BOOSTER, ttHandler)
                .addHandler(TT_1, ttHandler)
                .addHandler(TT_2, ttHandler)
                .addHandler(IFA, ifaHandler)
                .addHandler(HB_TEST, hbTestHandler)
                .addHandler(DELIVERY_OUTCOME, deliveryOutcomeHandler)
                .addHandler(DELIVERY_PLAN, deliveryPlanHandler)
                .addHandler(PNC_REGISTRATION_OA, pncRegistrationOAHandler)
                .addHandler(PNC_CLOSE, pncCloseHandler)
                .addHandler(PNC_VISIT, pncVisitHandler)
                .addHandler(CHILD_REGISTRATION_EC, childRegistrationECHandler)
                .addHandler(CHILD_REGISTRATION_OA, childRegistrationOAHandler)
                .addHandler(CHILD_IMMUNIZATIONS, childImmunizationsHandler)
                .addHandler(CHILD_ILLNESS, childIllnessHandler)
                .addHandler(CHILD_CLOSE, childCloseHandler)
                .addHandler(VITAMIN_A, vitaminAHandler)
                .addHandler(PPFP, postpartumFamilyPlanningHandler)
                .addHandler(RECORD_ECPS, recordECPsHandler)
                .addHandler(EC_EDIT, ecEditHandler)
                .addHandler(ANC_INVESTIGATIONS, ancInvestigationsHandler);
	}
}
