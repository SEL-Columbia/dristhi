package org.ei.drishti.service;

import org.ei.drishti.common.AllConstants;
import org.ei.drishti.domain.EligibleCouple;
import org.ei.drishti.domain.FPProductInformation;
import org.ei.drishti.domain.register.*;
import org.ei.drishti.form.domain.FormSubmission;
import org.ei.drishti.repository.AllEligibleCouples;
import org.ei.drishti.service.formSubmission.handler.ReportFieldsDefinition;
import org.ei.drishti.service.reporting.ECReportingService;
import org.ei.drishti.service.scheduling.ECSchedulingService;
import org.ei.drishti.util.SafeMap;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.ei.drishti.common.AllConstants.CommonFormFields.SUBMISSION_DATE_FIELD_NAME;
import static org.ei.drishti.common.AllConstants.ECCloseFields.IS_EC_CLOSE_CONFIRMED_FIELD_NAME;
import static org.ei.drishti.common.AllConstants.FamilyPlanningFormFields.*;
import static org.ei.drishti.common.AllConstants.Form.BOOLEAN_TRUE_VALUE;
import static org.ei.drishti.common.AllConstants.ReportDataParameters.QUANTITY;
import static org.ei.drishti.common.AllConstants.ReportDataParameters.SERVICE_PROVIDED_DATE;

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
        String fpMethod = submission.getField(CURRENT_FP_METHOD_FIELD_NAME);

        eligibleCouple = getEligibleCoupleWithFPDetails(eligibleCouple, submission, fpMethod);
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

    private EligibleCouple getEligibleCoupleWithUpdatedFPDetails(EligibleCouple eligibleCouple, FormSubmission submission, String fpMethod) {
        List<IUDFPDetails> iudFPDetails = eligibleCouple.iudFPDetails();
        List<CondomFPDetails> condomFPDetails = eligibleCouple.condomFPDetails();
        List<OCPFPDetails> ocpFPDetails = eligibleCouple.ocpFPDetails();
        List<SterilizationFPDetails> femaleSterilizationFPDetails = eligibleCouple.femaleSterilizationFPDetails();
        List<SterilizationFPDetails> maleSterilizationFPDetails = eligibleCouple.maleSterilizationFPDetails();
        return getEligibleCoupleWithFPDetailsUpdated(eligibleCouple, submission, fpMethod, iudFPDetails, condomFPDetails, ocpFPDetails, femaleSterilizationFPDetails, maleSterilizationFPDetails);
    }

    private EligibleCouple getEligibleCoupleWithFPDetails(EligibleCouple eligibleCouple, FormSubmission submission, String fpMethod) {
        List<IUDFPDetails> iudFPDetails = new ArrayList<>();
        List<CondomFPDetails> condomFPDetails = new ArrayList<>();
        List<OCPFPDetails> ocpFPDetails = new ArrayList<>();
        List<SterilizationFPDetails> femaleSterilizationFPDetails = new ArrayList<>();
        List<SterilizationFPDetails> maleSterilizationFPDetails = new ArrayList<>();
        return getEligibleCoupleWithFPDetailsUpdated(eligibleCouple, submission, fpMethod,
                iudFPDetails, condomFPDetails, ocpFPDetails, femaleSterilizationFPDetails, maleSterilizationFPDetails);
    }

    private EligibleCouple getEligibleCoupleWithFPDetailsUpdated(EligibleCouple eligibleCouple, FormSubmission submission,
                                                                 String fpMethod,
                                                                 List<IUDFPDetails> iudFPDetails,
                                                                 List<CondomFPDetails> condomFPDetails,
                                                                 List<OCPFPDetails> ocpFPDetails, List<SterilizationFPDetails> femaleSterilizationFPDetails,
                                                                 List<SterilizationFPDetails> maleSterilizationFPDetails) {
        String fpAcceptanceDate = submission.getField(FP_METHOD_CHANGE_DATE_FIELD_NAME);
        if (fpMethod.equalsIgnoreCase(IUD_FP_METHOD_VALUE)) {
            iudFPDetails.add(new IUDFPDetails(fpAcceptanceDate, submission.getField(IUD_PLACE), submission.getField(LMP_DATE), submission.getField(UPT_RESULT)));
        }
        if (fpMethod.equalsIgnoreCase(CONDOM_FP_METHOD_VALUE)) {
            condomFPDetails.add(getCondomFPDetails(submission, fpAcceptanceDate));
        }
        if (fpMethod.equalsIgnoreCase(OCP_FP_METHOD_VALUE)) {
            ocpFPDetails.add(getOCPFPDetails(submission, fpAcceptanceDate));
        }
        if (fpMethod.equalsIgnoreCase(FEMALE_STERILIZATION_FP_METHOD_VALUE)) {
            femaleSterilizationFPDetails.add(
                    new FemaleSterilizationFPDetails(
                            submission.getField(FEMALE_STERILIZATION_TYPE),
                            fpAcceptanceDate,
                            null)
            );
        }
        if (fpMethod.equalsIgnoreCase(MALE_STERILIZATION_FP_METHOD_VALUE)) {
            maleSterilizationFPDetails.add(
                    new MaleSterilizationFPDetails(
                            submission.getField(MALE_STERILIZATION_TYPE),
                            fpAcceptanceDate,
                            null)
            );
        }
        return eligibleCouple
                .withIUDFPDetails(iudFPDetails)
                .withCondomFPDetails(condomFPDetails)
                .withOCPFPDetails(ocpFPDetails)
                .withFemaleSterilizationFPDetails(femaleSterilizationFPDetails)
                .withMaleSterilizationFPDetails(maleSterilizationFPDetails);
    }

    private OCPFPDetails getOCPFPDetails(FormSubmission submission, String fpAcceptanceDate) {
        Map<String, String> refill = new HashMap<>();
        refill.put(SERVICE_PROVIDED_DATE, fpAcceptanceDate);
        refill.put(QUANTITY, submission.getField(NUMBER_OF_OCP_STRIPS_SUPPLIED_FIELD_NAME));
        List<Map<String, String>> refills = new ArrayList<>();
        refills.add(refill);
        return new OCPFPDetails(fpAcceptanceDate, refills, submission.getField(LMP_DATE), submission.getField(UPT_RESULT));
    }

    private CondomFPDetails getCondomFPDetails(FormSubmission submission, String fpAcceptanceDate) {
        Map<String, String> refill = new HashMap<>();
        refill.put(SERVICE_PROVIDED_DATE, fpAcceptanceDate);
        refill.put(QUANTITY, submission.getField(NUMBER_OF_CONDOMS_SUPPLIED_FIELD_NAME));
        List<Map<String, String>> refills = new ArrayList<>();
        refills.add(refill);
        return new CondomFPDetails(fpAcceptanceDate, refills);
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

        String newFPMethod = submission.getField(NEW_FP_METHOD_FIELD_NAME);
        couple.details().put(CURRENT_FP_METHOD_FIELD_NAME, newFPMethod);
        couple = getEligibleCoupleWithUpdatedFPDetails(couple, submission, newFPMethod);
        allEligibleCouples.update(couple);

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
                newFPMethod,
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

    public void handleFPFollowup(FormSubmission submission) {
        EligibleCouple couple = allEligibleCouples.findByCaseId(submission.entityId());
        if (couple == null) {
            logger.warn("Tried to report FP follow up of a non-existing EC, with submission: " + submission);
            return;
        }
        couple = updateECWithFPFollowUp(couple, submission, submission.getField(FP_FOLLOWUP_DATE_FIELD_NAME));
        allEligibleCouples.update(couple);

        FPProductInformation fpProductInformation = new FPProductInformation(
                submission.entityId(), submission.anmId(),
                submission.getField(CURRENT_FP_METHOD_FIELD_NAME),
                null, null, null, null, null,
                submission.getField(SUBMISSION_DATE_FIELD_NAME),
                null, submission.getField(FP_FOLLOWUP_DATE_FIELD_NAME), submission.getField(NEEDS_FOLLOWUP_FIELD_NAME), submission.getField(NEEDS_REFERRAL_FOLLOWUP_FIELD_NAME));
        schedulingService.fpFollowup(fpProductInformation);
    }

    private EligibleCouple updateECWithFPFollowUp(EligibleCouple couple, FormSubmission submission, String date) {
        String fpMethod = submission.getField(CURRENT_FP_METHOD_FIELD_NAME);
        if (fpMethod.equalsIgnoreCase(FEMALE_STERILIZATION_FP_METHOD_VALUE)) {
            return updateFemaleSterilizationDetailsWithFollowUpDate(date, couple);
        }
        if (fpMethod.equalsIgnoreCase(MALE_STERILIZATION_FP_METHOD_VALUE)) {
            return updateMaleSterilizationDetailsWithFollowUpDate(date, couple);
        }
        return couple;
    }

    private EligibleCouple updateMaleSterilizationDetailsWithFollowUpDate(String followUpDate, EligibleCouple couple) {
        List<SterilizationFPDetails> maleSterilizationFPDetails = couple.maleSterilizationFPDetails();
        sortSterilizationDetails(maleSterilizationFPDetails);
        SterilizationFPDetails requiredMaleSterilizationFPDetail = getRequiredSterilizationFPDetails(followUpDate, maleSterilizationFPDetails);
        int indexOfFPDetailToBeUpdated = maleSterilizationFPDetails.indexOf(requiredMaleSterilizationFPDetail);
        requiredMaleSterilizationFPDetail.followupVisitDates().add(followUpDate);
        couple.maleSterilizationFPDetails().set(indexOfFPDetailToBeUpdated, requiredMaleSterilizationFPDetail);
        return couple;
    }

    private void sortSterilizationDetails(List<SterilizationFPDetails> sterilizationDetails) {
        Collections.sort(sterilizationDetails, new Comparator<SterilizationFPDetails>() {
            @Override
            public int compare(SterilizationFPDetails detail1, SterilizationFPDetails detail2) {
                return LocalDate.parse(detail1.sterilizationDate()).compareTo(LocalDate.parse(detail2.sterilizationDate()));
            }
        });
    }

    private SterilizationFPDetails getRequiredSterilizationFPDetails(String followUpDate, List<SterilizationFPDetails> sterilizationFPDetails) {
        List<SterilizationFPDetails> requiredSterilizationFPDetails = new ArrayList<>();
        for (SterilizationFPDetails sterilizationFPDetail : sterilizationFPDetails) {
            LocalDate date = LocalDate.parse(sterilizationFPDetail.sterilizationDate());
            if (LocalDate.parse(followUpDate).isAfter(date)) {
                requiredSterilizationFPDetails.add(sterilizationFPDetail);
            }
        }
        return requiredSterilizationFPDetails.get(requiredSterilizationFPDetails.size() - 1);
    }

    private EligibleCouple updateFemaleSterilizationDetailsWithFollowUpDate(String followUpDate, EligibleCouple couple) {
        List<SterilizationFPDetails> femaleSterilizationFPDetails = couple.femaleSterilizationFPDetails();
        sortSterilizationDetails(femaleSterilizationFPDetails);
        SterilizationFPDetails requiredFemaleSterilizationFPDetail = getRequiredSterilizationFPDetails(followUpDate, femaleSterilizationFPDetails);
        int indexOfFPDetailToBeUpdated = femaleSterilizationFPDetails.indexOf(requiredFemaleSterilizationFPDetail);
        requiredFemaleSterilizationFPDetail.followupVisitDates().add(followUpDate);
        couple.femaleSterilizationFPDetails().set(indexOfFPDetailToBeUpdated, requiredFemaleSterilizationFPDetail);
        return couple;
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
