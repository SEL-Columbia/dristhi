package org.ei.drishti.service.formSubmission.handler;

import org.ei.drishti.form.domain.FormSubmission;
import org.ei.drishti.form.repository.AllFormSubmissions;
import org.ei.drishti.util.EasyMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

import static java.text.MessageFormat.format;
import static org.ei.drishti.common.AllConstants.Form.*;

@Component
public class FormSubmissionRouter {
    private static Logger logger = LoggerFactory.getLogger(FormSubmissionRouter.class.toString());
    private AllFormSubmissions formSubmissionsRepository;
    private final Map<String, FormSubmissionHandler> handlerMap;

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
                                ChildRegistrationECHandler childRegistrationECHandler, ChildImmunizationsHandler childImmunizationsHandler, ChildCloseHandler childCloseHandler) {
        this.formSubmissionsRepository = formSubmissionsRepository;
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
                .put(TT_BOOSTER, ttHandler)
                .put(TT_1, ttHandler)
                .put(TT_2, ttHandler)
                .put(IFA, ifaHandler)
                .put(HB_TEST, hbTestHandler)
                .put(DELIVERY_OUTCOME, deliveryOutcomeHandler)
                .put(PNC_REGISTRATION_OA, pncRegistrationOAHandler)
                .put(PNC_CLOSE, pncCloseHandler)
                .put(PNC_VISIT, pncVisitHandler)
                .put(CHILD_REGISTRATION_EC, childRegistrationECHandler)
                .put(CHILD_IMMUNIZATIONS, childImmunizationsHandler)
                .put(CHILD_CLOSE, childCloseHandler)
                .map();
    }

    public void route(String instanceId) {
        FormSubmission submission = formSubmissionsRepository.findByInstanceId(instanceId);
        FormSubmissionHandler handler = handlerMap.get(submission.formName());
        if (handler == null) {
            logger.warn("Could not find a handler due to unknown form submission: " + submission);
            return;
        }
        logger.info(format("Handling {0} form submission with instance Id: {1} for entity: {2}", submission.formName(),
                submission.instanceId(), submission.entityId()));
        handler.handle(submission);
    }
}

