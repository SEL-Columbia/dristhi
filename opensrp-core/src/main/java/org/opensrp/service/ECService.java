package org.opensrp.service;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.opensrp.common.AllConstants.CommonFormFields.SUBMISSION_DATE_FIELD_NAME;
import static org.opensrp.common.AllConstants.ECCloseFields.IS_EC_CLOSE_CONFIRMED_FIELD_NAME;
import static org.opensrp.common.AllConstants.ECRegistrationFields.CASTE;
import static org.opensrp.common.AllConstants.ECRegistrationFields.ECONOMIC_STATUS;
import static org.opensrp.common.AllConstants.EntityCloseFormFields.CLOSE_REASON_FIELD_NAME;
import static org.opensrp.common.AllConstants.EntityCloseFormFields.WRONG_ENTRY_VALUE;
import static org.opensrp.common.AllConstants.FamilyPlanningFormFields.COMPLICATION_DATE_FIELD_NAME;
import static org.opensrp.common.AllConstants.FamilyPlanningFormFields.CONDOM_FP_METHOD_VALUE;
import static org.opensrp.common.AllConstants.FamilyPlanningFormFields.CURRENT_FP_METHOD_FIELD_NAME;
import static org.opensrp.common.AllConstants.FamilyPlanningFormFields.DMPA_INJECTION_DATE_FIELD_NAME;
import static org.opensrp.common.AllConstants.FamilyPlanningFormFields.FEMALE_STERILIZATION_FP_METHOD_VALUE;
import static org.opensrp.common.AllConstants.FamilyPlanningFormFields.FEMALE_STERILIZATION_TYPE;
import static org.opensrp.common.AllConstants.FamilyPlanningFormFields.FP_FOLLOWUP_DATE_FIELD_NAME;
import static org.opensrp.common.AllConstants.FamilyPlanningFormFields.FP_METHOD_CHANGE_DATE_FIELD_NAME;
import static org.opensrp.common.AllConstants.FamilyPlanningFormFields.FP_RENEW_METHOD_VISIT_DATE;
import static org.opensrp.common.AllConstants.FamilyPlanningFormFields.IUD_FP_METHOD_VALUE;
import static org.opensrp.common.AllConstants.FamilyPlanningFormFields.IUD_PLACE;
import static org.opensrp.common.AllConstants.FamilyPlanningFormFields.LMP_DATE;
import static org.opensrp.common.AllConstants.FamilyPlanningFormFields.MALE_STERILIZATION_FP_METHOD_VALUE;
import static org.opensrp.common.AllConstants.FamilyPlanningFormFields.MALE_STERILIZATION_TYPE;
import static org.opensrp.common.AllConstants.FamilyPlanningFormFields.NEEDS_FOLLOWUP_FIELD_NAME;
import static org.opensrp.common.AllConstants.FamilyPlanningFormFields.NEEDS_REFERRAL_FOLLOWUP_FIELD_NAME;
import static org.opensrp.common.AllConstants.FamilyPlanningFormFields.NEW_FP_METHOD_FIELD_NAME;
import static org.opensrp.common.AllConstants.FamilyPlanningFormFields.NUMBER_OF_CONDOMS_SUPPLIED_FIELD_NAME;
import static org.opensrp.common.AllConstants.FamilyPlanningFormFields.NUMBER_OF_OCP_STRIPS_SUPPLIED_FIELD_NAME;
import static org.opensrp.common.AllConstants.FamilyPlanningFormFields.OCP_FP_METHOD_VALUE;
import static org.opensrp.common.AllConstants.FamilyPlanningFormFields.OCP_REFILL_DATE_FIELD_NAME;
import static org.opensrp.common.AllConstants.FamilyPlanningFormFields.PREVIOUS_FP_METHOD_FIELD_NAME;
import static org.opensrp.common.AllConstants.FamilyPlanningFormFields.REFERRAL_FOLLOW_UP_DATE_FIELD_NAME;
import static org.opensrp.common.AllConstants.FamilyPlanningFormFields.UPT_RESULT;
import static org.opensrp.common.AllConstants.Form.BOOLEAN_TRUE_VALUE;
import static org.opensrp.common.AllConstants.ReportDataParameters.QUANTITY;
import static org.opensrp.common.AllConstants.ReportDataParameters.SERVICE_PROVIDED_DATE;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joda.time.LocalDate;
import org.opensrp.domain.EligibleCouple;
import org.opensrp.domain.FPProductInformation;
import org.opensrp.domain.register.CondomFPDetails;
import org.opensrp.domain.register.FemaleSterilizationFPDetails;
import org.opensrp.domain.register.IUDFPDetails;
import org.opensrp.domain.register.MaleSterilizationFPDetails;
import org.opensrp.domain.register.OCPFPDetails;
import org.opensrp.form.domain.FormSubmission;
import org.opensrp.repository.AllEligibleCouples;
import org.opensrp.scheduler.service.ActionService;
import org.opensrp.service.formSubmission.handler.ReportFieldsDefinition;
import org.opensrp.service.reporting.ECReportingService;
import org.opensrp.service.scheduling.ECSchedulingService;
import org.opensrp.util.SafeMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

        initializeECWithEmptyFPDetails(eligibleCouple);
        updateECWithFPDetails(eligibleCouple, submission, fpMethod);

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

    private void initializeECWithEmptyFPDetails(EligibleCouple eligibleCouple) {
        List<IUDFPDetails> iudFPDetails = new ArrayList<>();
        List<CondomFPDetails> condomFPDetails = new ArrayList<>();
        List<OCPFPDetails> ocpFPDetails = new ArrayList<>();
        List<FemaleSterilizationFPDetails> femaleSterilizationFPDetails = new ArrayList<>();
        List<MaleSterilizationFPDetails> maleSterilizationFPDetails = new ArrayList<>();
        eligibleCouple
                .withCondomFPDetails(condomFPDetails)
                .withIUDFPDetails(iudFPDetails)
                .withOCPFPDetails(ocpFPDetails)
                .withFemaleSterilizationFPDetails(femaleSterilizationFPDetails)
                .withMaleSterilizationFPDetails(maleSterilizationFPDetails);
    }

    private void updateECWithFPDetails(EligibleCouple eligibleCouple, FormSubmission submission,
                                       String fpMethod) {
        List<IUDFPDetails> iudFPDetails = eligibleCouple.iudFPDetails();
        List<CondomFPDetails> condomFPDetails = eligibleCouple.condomFPDetails();
        List<OCPFPDetails> ocpFPDetails = eligibleCouple.ocpFPDetails();
        List<FemaleSterilizationFPDetails> femaleSterilizationFPDetails = eligibleCouple.femaleSterilizationFPDetails();
        List<MaleSterilizationFPDetails> maleSterilizationFPDetails = eligibleCouple.maleSterilizationFPDetails();
        String fpAcceptanceDate = submission.getField(FP_METHOD_CHANGE_DATE_FIELD_NAME);
        if (IUD_FP_METHOD_VALUE.equalsIgnoreCase(fpMethod)) {
            iudFPDetails.add(new IUDFPDetails(fpAcceptanceDate, submission.getField(IUD_PLACE), submission.getField(LMP_DATE), submission.getField(UPT_RESULT)));
        }
        if (CONDOM_FP_METHOD_VALUE.equalsIgnoreCase(fpMethod)) {
            condomFPDetails.add(getCondomFPDetails(submission, fpAcceptanceDate));
        }
        if (OCP_FP_METHOD_VALUE.equalsIgnoreCase(fpMethod)) {
            ocpFPDetails.add(getOCPFPDetails(submission, fpAcceptanceDate));
        }
        if (FEMALE_STERILIZATION_FP_METHOD_VALUE.equalsIgnoreCase(fpMethod)) {
            femaleSterilizationFPDetails.add(
                    new FemaleSterilizationFPDetails(
                            submission.getField(FEMALE_STERILIZATION_TYPE),
                            fpAcceptanceDate,
                            null)
            );
        }
        if (MALE_STERILIZATION_FP_METHOD_VALUE.equalsIgnoreCase(fpMethod)) {
            maleSterilizationFPDetails.add(
                    new MaleSterilizationFPDetails(
                            submission.getField(MALE_STERILIZATION_TYPE),
                            fpAcceptanceDate,
                            null)
            );
        }
        eligibleCouple
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
        updateECWithFPDetails(couple, submission, newFPMethod);
        allEligibleCouples.update(couple);

        List<String> reportFields = reportFieldsDefinition.get(submission.formName());
        Map<String, String> reportFieldsMap = submission.getFields(reportFields);
        reportFieldsMap.put(CASTE, couple.details().get(CASTE));
        reportFieldsMap.put(ECONOMIC_STATUS, couple.details().get(ECONOMIC_STATUS));
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

        allEligibleCouples.update(updateECWithRefills(submission, couple));

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

    private EligibleCouple updateECWithRefills(FormSubmission submission, EligibleCouple couple) {
        String fpMethod = submission.getField(CURRENT_FP_METHOD_FIELD_NAME);
        String date = submission.getField(FP_RENEW_METHOD_VISIT_DATE);
        if (OCP_FP_METHOD_VALUE.equalsIgnoreCase(fpMethod)) {
            return updateOCPDetailsWithRefills(couple, submission.getField(NUMBER_OF_OCP_STRIPS_SUPPLIED_FIELD_NAME), date);
        }
        if (CONDOM_FP_METHOD_VALUE.equalsIgnoreCase(fpMethod)) {
            return updateCondomDetailsWithRefills(couple, submission.getField(NUMBER_OF_CONDOMS_SUPPLIED_FIELD_NAME), date);
        }
        return null;
    }

    private EligibleCouple updateOCPDetailsWithRefills(EligibleCouple couple, String quantity, String date) {
        List<OCPFPDetails> ocpFPDetails = couple.ocpFPDetails();
        sortOCPFPDetails(ocpFPDetails);
        OCPFPDetails requiredOCPFPDetail = getRequiredOCPFPDetails(ocpFPDetails, date);
        int indexOfFPDetailToBeUpdated = ocpFPDetails.indexOf(requiredOCPFPDetail);
        Map<String, String> refill = new HashMap<>();
        refill.put(SERVICE_PROVIDED_DATE, date);
        refill.put(QUANTITY, quantity);
        requiredOCPFPDetail.refills().add(refill);
        couple.ocpFPDetails().set(indexOfFPDetailToBeUpdated, requiredOCPFPDetail);
        return couple;
    }

    private EligibleCouple updateCondomDetailsWithRefills(EligibleCouple couple, String quantity, String date) {
        List<CondomFPDetails> condomFPDetails = couple.condomFPDetails();
        sortCondomFPDetails(condomFPDetails);
        CondomFPDetails requiredCondomFPDetail = getRequiredCondomFPDetails(date, condomFPDetails);
        int indexOfFPDetailToBeUpdated = condomFPDetails.indexOf(requiredCondomFPDetail);
        Map<String, String> refill = new HashMap<>();
        refill.put(SERVICE_PROVIDED_DATE, date);
        refill.put(QUANTITY, quantity);
        requiredCondomFPDetail.refills().add(refill);
        couple.condomFPDetails().set(indexOfFPDetailToBeUpdated, requiredCondomFPDetail);
        return couple;
    }

    public void followupOnFPMethod(FormSubmission submission) {
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
        if (FEMALE_STERILIZATION_FP_METHOD_VALUE.equalsIgnoreCase(fpMethod)) {
            return updateFemaleSterilizationDetailsWithFollowUpDate(date, couple);
        }
        if (MALE_STERILIZATION_FP_METHOD_VALUE.equalsIgnoreCase(fpMethod)) {
            return updateMaleSterilizationDetailsWithFollowUpDate(date, couple);
        }
        return couple;
    }

    private EligibleCouple updateMaleSterilizationDetailsWithFollowUpDate(String followUpDate, EligibleCouple couple) {
        List<MaleSterilizationFPDetails> maleSterilizationFPDetails = couple.maleSterilizationFPDetails();
        sortMaleSterilizationDetails(maleSterilizationFPDetails);
        MaleSterilizationFPDetails requiredMaleSterilizationFPDetail = getRequiredMaleSterilizationFPDetails(followUpDate, maleSterilizationFPDetails);
        int indexOfFPDetailToBeUpdated = maleSterilizationFPDetails.indexOf(requiredMaleSterilizationFPDetail);
        requiredMaleSterilizationFPDetail.followupVisitDates().add(followUpDate);
        couple.maleSterilizationFPDetails().set(indexOfFPDetailToBeUpdated, requiredMaleSterilizationFPDetail);
        return couple;
    }

    private EligibleCouple updateFemaleSterilizationDetailsWithFollowUpDate(String followUpDate, EligibleCouple couple) {
        List<FemaleSterilizationFPDetails> femaleSterilizationFPDetails = couple.femaleSterilizationFPDetails();
        sortFemaleSterilizationDetails(femaleSterilizationFPDetails);
        FemaleSterilizationFPDetails requiredFemaleSterilizationFPDetail = getRequiredFemaleSterilizationFPDetails(followUpDate, femaleSterilizationFPDetails);
        int indexOfFPDetailToBeUpdated = femaleSterilizationFPDetails.indexOf(requiredFemaleSterilizationFPDetail);
        requiredFemaleSterilizationFPDetail.followupVisitDates().add(followUpDate);
        couple.femaleSterilizationFPDetails().set(indexOfFPDetailToBeUpdated, requiredFemaleSterilizationFPDetail);
        return couple;
    }

    private void sortOCPFPDetails(List<OCPFPDetails> ocpFPDetails) {
        Collections.sort(ocpFPDetails, new Comparator<OCPFPDetails>() {
            @Override
            public int compare(OCPFPDetails detail, OCPFPDetails anotherDetail) {
                return LocalDate.parse(detail.fpAcceptanceDate()).compareTo(LocalDate.parse(anotherDetail.fpAcceptanceDate()));
            }
        });
    }

    private void sortCondomFPDetails(List<CondomFPDetails> condomFPDetails) {
        Collections.sort(condomFPDetails, new Comparator<CondomFPDetails>() {
            @Override
            public int compare(CondomFPDetails detail, CondomFPDetails anotherDetail) {
                return LocalDate.parse(detail.fpAcceptanceDate()).compareTo(LocalDate.parse(anotherDetail.fpAcceptanceDate()));
            }
        });
    }

    private void sortFemaleSterilizationDetails(List<FemaleSterilizationFPDetails> femaleSterilizationDetails) {
        Collections.sort(femaleSterilizationDetails, new Comparator<FemaleSterilizationFPDetails>() {
            @Override
            public int compare(FemaleSterilizationFPDetails detail, FemaleSterilizationFPDetails anotherDetail) {
                return LocalDate.parse(detail.sterilizationDate()).compareTo(LocalDate.parse(anotherDetail.sterilizationDate()));
            }
        });
    }

    private void sortMaleSterilizationDetails(List<MaleSterilizationFPDetails> maleSterilizationDetails) {
        Collections.sort(maleSterilizationDetails, new Comparator<MaleSterilizationFPDetails>() {
            @Override
            public int compare(MaleSterilizationFPDetails detail, MaleSterilizationFPDetails anotherDetail) {
                return LocalDate.parse(detail.sterilizationDate()).compareTo(LocalDate.parse(anotherDetail.sterilizationDate()));
            }
        });
    }

    private OCPFPDetails getRequiredOCPFPDetails(List<OCPFPDetails> ocpFPDetails, String refillDate) {
        List<OCPFPDetails> requiredOCPFPDetails = new ArrayList<>();
        for (OCPFPDetails ocpFPDetail : ocpFPDetails) {
            LocalDate date = LocalDate.parse(ocpFPDetail.fpAcceptanceDate());
            if (!LocalDate.parse(refillDate).isBefore(date)) {
                requiredOCPFPDetails.add(ocpFPDetail);
            }
        }
        return requiredOCPFPDetails.get(requiredOCPFPDetails.size() - 1);
    }

    private CondomFPDetails getRequiredCondomFPDetails(String refillDate, List<CondomFPDetails> condomFPDetails) {
        List<CondomFPDetails> requiredCondomFPDetails = new ArrayList<>();
        for (CondomFPDetails condomFPDetail : condomFPDetails) {
            LocalDate date = LocalDate.parse(condomFPDetail.fpAcceptanceDate());
            if (!LocalDate.parse(refillDate).isBefore(date)) {
                requiredCondomFPDetails.add(condomFPDetail);
            }
        }
        return requiredCondomFPDetails.get(requiredCondomFPDetails.size() - 1);
    }

    private FemaleSterilizationFPDetails getRequiredFemaleSterilizationFPDetails(String followUpDate, List<FemaleSterilizationFPDetails> femaleSterilizationFPDetails) {
        List<FemaleSterilizationFPDetails> requiredFemaleSterilizationFPDetails = new ArrayList<>();
        for (FemaleSterilizationFPDetails femaleSterilizationFPDetail : femaleSterilizationFPDetails) {
            LocalDate date = LocalDate.parse(femaleSterilizationFPDetail.sterilizationDate());
            if (!LocalDate.parse(followUpDate).isBefore(date)) {
                requiredFemaleSterilizationFPDetails.add(femaleSterilizationFPDetail);
            }
        }
        return requiredFemaleSterilizationFPDetails.get(requiredFemaleSterilizationFPDetails.size() - 1);
    }

    private MaleSterilizationFPDetails getRequiredMaleSterilizationFPDetails(String followUpDate, List<MaleSterilizationFPDetails> sterilizationFPDetails) {
        List<MaleSterilizationFPDetails> requiredMaleSterilizationFPDetails = new ArrayList<>();
        for (MaleSterilizationFPDetails maleSterilizationFPDetail : sterilizationFPDetails) {
            LocalDate date = LocalDate.parse(maleSterilizationFPDetail.sterilizationDate());
            if (!LocalDate.parse(followUpDate).isBefore(date)) {
                requiredMaleSterilizationFPDetails.add(maleSterilizationFPDetail);
            }
        }
        return requiredMaleSterilizationFPDetails.get(requiredMaleSterilizationFPDetails.size() - 1);
    }

    public void handleReferralFollowup(FormSubmission submission) {
        EligibleCouple couple = allEligibleCouples.findByCaseId(submission.entityId());
        if (couple == null) {
            logger.warn("Tried to report FP Referral follow-up of a non-existing EC, with submission: " + submission);
            return;
        }
        couple = updateECWithFPFollowUp(couple, submission, submission.getField(REFERRAL_FOLLOW_UP_DATE_FIELD_NAME));
        allEligibleCouples.update(couple);

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

        if (WRONG_ENTRY_VALUE.equalsIgnoreCase(submission.getField(CLOSE_REASON_FIELD_NAME))) {
            logger.info("Deleting Reports for EC with Entity Id: " + submission.entityId());
            reportingService.deleteReportsForEC(submission.entityId());
        }

        logger.info("Closing EC : " + submission);

        allEligibleCouples.close(submission.entityId());
        //#TODO: actionService.markAllAlertsAsInactive(submission.entityId());
    }
}
