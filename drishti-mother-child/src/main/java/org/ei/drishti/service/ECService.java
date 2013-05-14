package org.ei.drishti.service;

import org.ei.drishti.contract.EligibleCoupleCloseRequest;
import org.ei.drishti.contract.OutOfAreaANCRegistrationRequest;
import org.ei.drishti.domain.EligibleCouple;
import org.ei.drishti.domain.FPProductInformation;
import org.ei.drishti.form.domain.FormSubmission;
import org.ei.drishti.repository.AllEligibleCouples;
import org.ei.drishti.service.formSubmissionHandler.ReportFieldsDefinition;
import org.ei.drishti.service.reporting.ECReportingService;
import org.ei.drishti.service.scheduling.ECSchedulingService;
import org.ei.drishti.util.IdGenerator;
import org.ei.drishti.util.SafeMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import static org.ei.drishti.common.AllConstants.ChangeFamilyPlanningMethodCommCareFields.NEW_FP_METHOD_FIELD_NAME;
import static org.ei.drishti.common.AllConstants.ChangeFamilyPlanningMethodCommCareFields.PREVIOUS_FP_METHOD_FIELD_NAME;
import static org.ei.drishti.common.AllConstants.CommonCommCareFields.SUBMISSION_DATE_COMMCARE_FIELD_NAME;
import static org.ei.drishti.common.AllConstants.DETAILS_EXTRA_DATA_KEY_NAME;
import static org.ei.drishti.common.AllConstants.FamilyPlanningCommCareFields.*;

@Service
public class ECService {
    private static Logger logger = LoggerFactory.getLogger(ActionService.class.toString());
    private AllEligibleCouples allEligibleCouples;
    private ActionService actionService;
    private ECReportingService reportingService;
    private ECSchedulingService schedulingService;
    private IdGenerator idGenerator;
    private ReportFieldsDefinition reportFieldsDefinition;

    @Autowired
    public ECService(AllEligibleCouples allEligibleCouples, ActionService actionService, ECReportingService reportingService,
                     IdGenerator idGenerator, ECSchedulingService schedulingService, ReportFieldsDefinition reportFieldsDefinition) {
        this.allEligibleCouples = allEligibleCouples;
        this.actionService = actionService;
        this.reportingService = reportingService;
        this.idGenerator = idGenerator;
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
                submission.getField(SUBMISSION_DATE_COMMCARE_FIELD_NAME), null, null, null, null);

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
                null, null, null, null, null, null, submission.getField(SUBMISSION_DATE_COMMCARE_FIELD_NAME), null,
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

        reportingService.fpChange(new SafeMap(submission.getFields(reportFields)));
        FPProductInformation fpProductInformation = new FPProductInformation(
                submission.entityId(), submission.anmId(),
                submission.getField(NEW_FP_METHOD_FIELD_NAME),
                submission.getField(PREVIOUS_FP_METHOD_FIELD_NAME), null,
                submission.getField(NUMBER_OF_OCP_STRIPS_SUPPLIED_FIELD_NAME),
                null,
                submission.getField(NUMBER_OF_CONDOMS_SUPPLIED_FIELD_NAME),
                submission.getField(SUBMISSION_DATE_COMMCARE_FIELD_NAME),
                submission.getField(FP_METHOD_CHANGE_DATE_FIELD_NAME),
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
                submission.getField(SUBMISSION_DATE_COMMCARE_FIELD_NAME), null, null, null, null);
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
                submission.getField(SUBMISSION_DATE_COMMCARE_FIELD_NAME),
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
                null, null, null, null, null, null, submission.getField(SUBMISSION_DATE_COMMCARE_FIELD_NAME), null,
                submission.getField(REFERRAL_FOLLOW_UP_DATE_FIELD_NAME), submission.getField(NEEDS_FOLLOWUP_FIELD_NAME), submission.getField(NEEDS_REFERRAL_FOLLOWUP_FIELD_NAME));
        schedulingService.reportReferralFollowup(fpProductInformation);
    }

    public EligibleCouple registerEligibleCoupleForOutOfAreaANC(OutOfAreaANCRegistrationRequest request, Map<String, Map<String, String>> extraData) {
        EligibleCouple couple = new EligibleCouple(idGenerator.generateUUID().toString(), "0")
                .withCouple(request.wife(), request.husband()).withANMIdentifier(request.anmIdentifier())
                .withLocation(request.village(), request.subCenter(), request.phc()).withDetails(extraData.get(DETAILS_EXTRA_DATA_KEY_NAME))
                .asOutOfArea();

        allEligibleCouples.register(couple);
        return couple;
    }

    public void closeEligibleCouple(EligibleCoupleCloseRequest request) {
        if (!allEligibleCouples.exists(request.caseId())) {
            logger.warn("Cannot close EC as it does not exist! Details: " + request);
            return;
        }
        logger.info("Closing EC : " + request);

        allEligibleCouples.close(request.caseId());
        actionService.closeEligibleCouple(request.caseId(), request.anmIdentifier());
    }
}
