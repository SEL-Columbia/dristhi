package org.ei.drishti.service;

import org.ei.drishti.common.AllConstants;
import org.ei.drishti.domain.EligibleCouple;
import org.ei.drishti.domain.FPProductInformation;
import org.ei.drishti.form.domain.FormSubmission;
import org.ei.drishti.repository.AllEligibleCouples;
import org.ei.drishti.service.formSubmission.handler.ReportFieldsDefinition;
import org.ei.drishti.service.reporting.ECReportingService;
import org.ei.drishti.service.scheduling.ECSchedulingService;
import org.ei.drishti.util.SafeMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.ei.drishti.common.AllConstants.CommonFormFields.SUBMISSION_DATE_FIELD_NAME;
import static org.ei.drishti.common.AllConstants.ECCloseFields.IS_EC_CLOSE_CONFIRMED_FIELD_NAME;
import static org.ei.drishti.common.AllConstants.FamilyPlanningFormFields.*;
import static org.ei.drishti.common.AllConstants.Form.BOOLEAN_TRUE_VALUE;

@Service
public class ECService {
    private static Logger logger = LoggerFactory.getLogger(ActionService.class.toString());
    private AllEligibleCouples allEligibleCouples;
    private ECReportingService reportingService;
    private ECSchedulingService schedulingService;
    private ReportFieldsDefinition reportFieldsDefinition;

    @Autowired
    public ECService(AllEligibleCouples allEligibleCouples, ECSchedulingService schedulingService, ECReportingService reportingService,
                     ReportFieldsDefinition reportFieldsDefinition) {
        this.allEligibleCouples = allEligibleCouples;
        this.reportingService = reportingService;
        this.schedulingService = schedulingService;
        this.reportFieldsDefinition = reportFieldsDefinition;
    }

    public void registerEligibleCouple(FormSubmission submission) {
        EligibleCouple eligibleCouple = allEligibleCouples.findByCaseId(submission.entityId());
        allEligibleCouples.update(eligibleCouple.withANMIdentifier(submission.anmId()));

        List<String> reportFields = reportFieldsDefinition.get(submission.formName());
        reportingService.registerEC(new SafeMap(submission.getFields(reportFields)));

        FPProductInformation fpProductInformation = new FPProductInformation(
                submission.entityId(), submission.anmId(),
                submission.getField(CURRENT_FP_METHOD_FIELD_NAME),
                null, submission.getField(DMPA_INJECTION_DATE_FIELD_NAME),
                submission.getField(NUMBER_OF_OCP_STRIPS_SUPPLIED_FIELD_NAME),
                submission.getField(OCP_REFILL_DATE_FIELD_NAME),
                submission.getField(NUMBER_OF_CONDOMS_SUPPLIED_FIELD_NAME),
                submission.getField(SUBMISSION_DATE_FIELD_NAME),
                submission.getField(FP_METHOD_CHANGE_DATE_FIELD_NAME), null, null, null);

        schedulingService.registerEC(fpProductInformation);
    }

    public void reportFPComplications(FormSubmission submission) {
        EligibleCouple couple = allEligibleCouples.findByCaseId(submission.entityId());
        if (couple == null) {
            logger.warn("Tried to report FP Complications of a non-existing EC, with submission: " + submission);
            return;
        }

        FPProductInformation fpProductInformation = new FPProductInformation(
                submission.entityId(), submission.anmId(),
                null, null, null, null, null, null, submission.getField(SUBMISSION_DATE_FIELD_NAME), null,
                submission.getField(COMPLICATION_DATE_FIELD_NAME), submission.getField(NEEDS_FOLLOWUP_FIELD_NAME), submission.getField(NEEDS_REFERRAL_FOLLOWUP_FIELD_NAME));
        schedulingService.reportFPComplications(fpProductInformation);
    }

    public void reportFPChange(FormSubmission submission) {
        EligibleCouple couple = allEligibleCouples.findByCaseId(submission.entityId());
        if (couple == null) {
            logger.warn("Tried to report FP Change of a non-existing EC, with submission: " + submission);
            return;
        }

        List<String> reportFields = reportFieldsDefinition.get(submission.formName());
        Map<String, String> reportFieldsMap = submission.getFields(reportFields);
        reportFieldsMap.put(AllConstants.ECRegistrationFields.CASTE, couple.details().get(AllConstants.ECRegistrationFields.CASTE));
        reportFieldsMap.put(AllConstants.ECRegistrationFields.ECONOMIC_STATUS, couple.details().get(AllConstants.ECRegistrationFields.ECONOMIC_STATUS));
        reportingService.fpChange(new SafeMap(reportFieldsMap));

        String fpMethodChangeDate = submission.getField(FP_METHOD_CHANGE_DATE_FIELD_NAME);
        if (isBlank(fpMethodChangeDate)) {
            fpMethodChangeDate = submission.getField(SUBMISSION_DATE_FIELD_NAME);
        }
        FPProductInformation fpProductInformation = new FPProductInformation(
                submission.entityId(), submission.anmId(),
                submission.getField(NEW_FP_METHOD_FIELD_NAME),
                submission.getField(PREVIOUS_FP_METHOD_FIELD_NAME), null,
                submission.getField(NUMBER_OF_OCP_STRIPS_SUPPLIED_FIELD_NAME),
                null,
                submission.getField(NUMBER_OF_CONDOMS_SUPPLIED_FIELD_NAME),
                submission.getField(SUBMISSION_DATE_FIELD_NAME),
                fpMethodChangeDate,
                null, null, null);
        schedulingService.fpChange(fpProductInformation);
    }

    public void renewFPProduct(FormSubmission submission) {
        EligibleCouple couple = allEligibleCouples.findByCaseId(submission.entityId());
        if (couple == null) {
            logger.warn("Tried to report FP Renew of a non-existing EC, with submission: " + submission);
            return;
        }

        FPProductInformation fpProductInformation = new FPProductInformation(
                submission.entityId(), submission.anmId(),
                submission.getField(CURRENT_FP_METHOD_FIELD_NAME),
                null, submission.getField(DMPA_INJECTION_DATE_FIELD_NAME),
                submission.getField(NUMBER_OF_OCP_STRIPS_SUPPLIED_FIELD_NAME),
                submission.getField(OCP_REFILL_DATE_FIELD_NAME),
                submission.getField(NUMBER_OF_CONDOMS_SUPPLIED_FIELD_NAME),
                submission.getField(SUBMISSION_DATE_FIELD_NAME), null, null, null, null);
        schedulingService.renewFPProduct(fpProductInformation);
    }

    public void reportFPFollowup(FormSubmission submission) {
        EligibleCouple couple = allEligibleCouples.findByCaseId(submission.entityId());
        if (couple == null) {
            logger.warn("Tried to report FP follow up of a non-existing EC, with submission: " + submission);
            return;
        }

        FPProductInformation fpProductInformation = new FPProductInformation(
                submission.entityId(), submission.anmId(),
                submission.getField(CURRENT_FP_METHOD_FIELD_NAME),
                null, null, null, null, null,
                submission.getField(SUBMISSION_DATE_FIELD_NAME),
                null, submission.getField(FP_FOLLOWUP_DATE_FIELD_NAME), submission.getField(NEEDS_FOLLOWUP_FIELD_NAME), submission.getField(NEEDS_REFERRAL_FOLLOWUP_FIELD_NAME));
        schedulingService.fpFollowup(fpProductInformation);
    }

    public void reportReferralFollowup(FormSubmission submission) {
        EligibleCouple couple = allEligibleCouples.findByCaseId(submission.entityId());
        if (couple == null) {
            logger.warn("Tried to report FP Referral follow-up of a non-existing EC, with submission: " + submission);
            return;
        }

        FPProductInformation fpProductInformation = new FPProductInformation(
                submission.entityId(), submission.anmId(),
                null, null, null, null, null, null, submission.getField(SUBMISSION_DATE_FIELD_NAME), null,
                submission.getField(REFERRAL_FOLLOW_UP_DATE_FIELD_NAME), submission.getField(NEEDS_FOLLOWUP_FIELD_NAME), submission.getField(NEEDS_REFERRAL_FOLLOWUP_FIELD_NAME));
        schedulingService.reportReferralFollowup(fpProductInformation);
    }

    public void closeEligibleCouple(FormSubmission submission) {
        if (!BOOLEAN_TRUE_VALUE.equalsIgnoreCase(submission.getField(IS_EC_CLOSE_CONFIRMED_FIELD_NAME))) {
            logger.warn("ANM has not confirmed the close so not closing EC! Form Submission: " + submission);
            return;
        }
        if (!allEligibleCouples.exists(submission.entityId())) {
            logger.warn("Cannot close EC as it does not exist! Form Submission: " + submission);
            return;
        }

        logger.info("Closing EC : " + submission);

        allEligibleCouples.close(submission.entityId());
        //#TODO: actionService.markAllAlertsAsInactive(submission.entityId());
    }
}
