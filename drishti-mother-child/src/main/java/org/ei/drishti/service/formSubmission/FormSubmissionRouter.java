package org.ei.drishti.service.formSubmission;

import org.ei.drishti.form.domain.FormSubmission;
import org.ei.drishti.form.repository.AllFormSubmissions;
import org.ei.drishti.service.formSubmission.handler.*;
import org.ei.drishti.service.reporting.FormSubmissionReportService;
import org.ei.drishti.common.util.EasyMap;
import org.ei.drishti.service.reporting.MCTSReportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

import static java.text.MessageFormat.format;
import static org.apache.commons.lang.exception.ExceptionUtils.getFullStackTrace;
import static org.ei.drishti.common.AllConstants.Form.*;

@Component
public class FormSubmissionRouter {
    private static Logger logger = LoggerFactory.getLogger(FormSubmissionRouter.class.toString());
    private AllFormSubmissions formSubmissionsRepository;
    private final Map<String, FormSubmissionHandler> handlerMap;
    private FormSubmissionReportService formSubmissionReportService;
    private MCTSReportService mctsReportService;

    @Autowired
    public FormSubmissionRouter(AllFormSubmissions formSubmissionsRepository,
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
                                FormSubmissionReportService formSubmissionReportService,
                                MCTSReportService mctsReportService) {
        this.formSubmissionsRepository = formSubmissionsRepository;
        this.formSubmissionReportService = formSubmissionReportService;
        this.mctsReportService = mctsReportService;
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

    public void route(String instanceId) throws Exception {
        FormSubmission submission = formSubmissionsRepository.findByInstanceId(instanceId);
        FormSubmissionHandler handler = handlerMap.get(submission.formName());
        if (handler == null) {
            logger.warn("Could not find a handler due to unknown form submission: " + submission);
            return;
        }
        logger.info(format("Handling {0} form submission with instance Id: {1} for entity: {2}", submission.formName(),
                submission.instanceId(), submission.entityId()));
        try {
            handler.handle(submission);
            formSubmissionReportService.reportFor(submission);
            mctsReportService.reportFor(submission);
        } catch (Exception e) {
            logger.error(format("Handling {0} form submission with instance Id: {1} for entity: {2} failed with exception : {3}",
                    submission.formName(), submission.instanceId(), submission.entityId(), getFullStackTrace(e)));
            throw e;
        }
    }
}

