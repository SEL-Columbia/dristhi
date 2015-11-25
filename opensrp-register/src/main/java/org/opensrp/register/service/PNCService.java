package org.opensrp.register.service;

import static java.text.MessageFormat.format;
import static org.opensrp.common.AllConstants.ANCVisitFormFields.WEIGHT;
import static org.opensrp.common.AllConstants.ChildImmunizationFields.IMMUNIZATIONS_GIVEN_FIELD_NAME;
import static org.opensrp.common.AllConstants.CommonChildFormFields.GENDER;
import static org.opensrp.common.AllConstants.CommonFormFields.ID;
import static org.opensrp.common.AllConstants.CommonFormFields.REFERENCE_DATE;
import static org.opensrp.common.AllConstants.DeliveryOutcomeFields.CHILD_REGISTRATION_SUB_FORM_NAME;
import static org.opensrp.common.AllConstants.DeliveryOutcomeFields.DID_MOTHER_SURVIVE;
import static org.opensrp.common.AllConstants.DeliveryOutcomeFields.DID_WOMAN_SURVIVE;
import static org.opensrp.common.AllConstants.DeliveryOutcomeFields.IMMUNIZATIONS_AT_BIRTH;
import static org.opensrp.common.AllConstants.EntityCloseFormFields.CLOSE_REASON_FIELD_NAME;
import static org.opensrp.common.AllConstants.FamilyPlanningFormFields.CONDOM_FP_METHOD_VALUE;
import static org.opensrp.common.AllConstants.FamilyPlanningFormFields.CURRENT_FP_METHOD_FIELD_NAME;
import static org.opensrp.common.AllConstants.FamilyPlanningFormFields.FEMALE_STERILIZATION_FP_METHOD_VALUE;
import static org.opensrp.common.AllConstants.FamilyPlanningFormFields.FEMALE_STERILIZATION_TYPE;
import static org.opensrp.common.AllConstants.FamilyPlanningFormFields.FP_METHOD_CHANGE_DATE_FIELD_NAME;
import static org.opensrp.common.AllConstants.FamilyPlanningFormFields.IUD_FP_METHOD_VALUE;
import static org.opensrp.common.AllConstants.FamilyPlanningFormFields.IUD_PLACE;
import static org.opensrp.common.AllConstants.FamilyPlanningFormFields.MALE_STERILIZATION_FP_METHOD_VALUE;
import static org.opensrp.common.AllConstants.FamilyPlanningFormFields.MALE_STERILIZATION_TYPE;
import static org.opensrp.common.AllConstants.FamilyPlanningFormFields.NUMBER_OF_CONDOMS_SUPPLIED_FIELD_NAME;
import static org.opensrp.common.AllConstants.FamilyPlanningFormFields.NUMBER_OF_OCP_STRIPS_SUPPLIED_FIELD_NAME;
import static org.opensrp.common.AllConstants.FamilyPlanningFormFields.OCP_FP_METHOD_VALUE;
import static org.opensrp.common.AllConstants.Form.BOOLEAN_FALSE_VALUE;
import static org.opensrp.common.AllConstants.Form.BOOLEAN_TRUE_VALUE;
import static org.opensrp.common.AllConstants.Form.CHILD_REGISTRATION_OA;
import static org.opensrp.common.AllConstants.Form.PNC_VISIT_CHILD_SUB_FORM_NAME;
import static org.opensrp.common.AllConstants.PNCCloseFields.DEATH_OF_MOTHER_VALUE;
import static org.opensrp.common.AllConstants.PNCCloseFields.PERMANENT_RELOCATION_VALUE;
import static org.opensrp.common.AllConstants.PNCVisitFormFields.ABDOMINAL_PROBLEMS_FIELD_NAME;
import static org.opensrp.common.AllConstants.PNCVisitFormFields.ACTIVITY_PROBLEMS;
import static org.opensrp.common.AllConstants.PNCVisitFormFields.BREAST_PROBLEMS;
import static org.opensrp.common.AllConstants.PNCVisitFormFields.BREATHING_PROBLEMS;
import static org.opensrp.common.AllConstants.PNCVisitFormFields.DIFFICULTIES_FIELD_NAME;
import static org.opensrp.common.AllConstants.PNCVisitFormFields.SKIN_PROBLEMS;
import static org.opensrp.common.AllConstants.PNCVisitFormFields.URINAL_PROBLEMS_FIELD_NAME;
import static org.opensrp.common.AllConstants.PNCVisitFormFields.URINE_STOOL_PROBLEMS;
import static org.opensrp.common.AllConstants.PNCVisitFormFields.VAGINAL_PROBLEMS_FIELD_NAME;
import static org.opensrp.common.AllConstants.PNCVisitFormFields.VISIT_DATES_FIELD_NAME;
import static org.opensrp.common.AllConstants.PNCVisitFormFields.VISIT_DATE_FIELD_NAME;
import static org.opensrp.common.AllConstants.PNCVisitFormFields.VISIT_PERSON_FIELD_NAME;
import static org.opensrp.common.AllConstants.PNCVisitFormFields.VISIT_PLACE_FIELD_NAME;
import static org.opensrp.common.AllConstants.ReportDataParameters.QUANTITY;
import static org.opensrp.common.AllConstants.ReportDataParameters.SERVICE_PROVIDED_DATE;
import static org.opensrp.common.util.EasyMap.create;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.opensrp.common.AllConstants;
import org.opensrp.common.util.EasyMap;
import org.opensrp.form.domain.FormSubmission;
import org.opensrp.form.domain.SubFormData;
import org.opensrp.register.CondomFPDetails;
import org.opensrp.register.FemaleSterilizationFPDetails;
import org.opensrp.register.IUDFPDetails;
import org.opensrp.register.MaleSterilizationFPDetails;
import org.opensrp.register.OCPFPDetails;
import org.opensrp.register.domain.EligibleCouple;
import org.opensrp.register.domain.Mother;
import org.opensrp.register.domain.PNCVisit;
import org.opensrp.register.repository.AllChildren;
import org.opensrp.register.repository.AllEligibleCouples;
import org.opensrp.register.repository.AllMothers;
import org.opensrp.register.service.reporting.MotherReportingService;
import org.opensrp.register.service.scheduling.PNCSchedulesService;
import org.opensrp.util.ReportFieldsDefinition;
import org.opensrp.util.SafeMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PNCService {
    private static Logger logger = LoggerFactory.getLogger(PNCService.class.toString());
    private PNCSchedulesService pncSchedulesService;
    private AllEligibleCouples allEligibleCouples;
    private AllMothers allMothers;
    private AllChildren allChildren;
    private MotherReportingService motherReportingService;
    private ReportFieldsDefinition reportFieldsDefinition;

    @Autowired
    public PNCService(PNCSchedulesService pncSchedulesService,
                      AllEligibleCouples allEligibleCouples,
                      AllMothers allMothers,
                      AllChildren allChildren,
                      MotherReportingService motherReportingService,
                      ReportFieldsDefinition reportFieldsDefinition) {
        this.pncSchedulesService = pncSchedulesService;
        this.allEligibleCouples = allEligibleCouples;
        this.allMothers = allMothers;
        this.allChildren = allChildren;
        this.motherReportingService = motherReportingService;
        this.reportFieldsDefinition = reportFieldsDefinition;
    }

    public void deliveryOutcome(FormSubmission submission) {
        Mother mother = allMothers.findByCaseId(submission.entityId());
        if (mother == null) {
            logger.warn(format("Failed to handle delivery outcome as there is no mother registered with ID: {0}", submission.entityId()));
            return;
        }
        if (BOOLEAN_FALSE_VALUE.equals(submission.getField(DID_WOMAN_SURVIVE))
                || BOOLEAN_FALSE_VALUE.equals(submission.getField(DID_MOTHER_SURVIVE))) {
            logger.info("Closing Mother as the mother died during delivery. Mother Id: " + mother.caseId());
            closeMother(mother);
        } else if (BOOLEAN_TRUE_VALUE.equals(submission.getField(DID_WOMAN_SURVIVE))
                || BOOLEAN_TRUE_VALUE.equals(submission.getField(DID_MOTHER_SURVIVE))) {
            pncSchedulesService.deliveryOutcome(submission.entityId(), submission.getField(REFERENCE_DATE));
        }
        SubFormData subFormData = submission.getSubFormByName(CHILD_REGISTRATION_SUB_FORM_NAME);

        addChildrenDetailsToMother(submission, subFormData, mother);
        allMothers.update(mother);
    }

    private void addChildrenDetailsToMother(FormSubmission submission, SubFormData subFormData, Mother mother) {
        List<Map<String, String>> childrenDetails = new ArrayList<>(mother.childrenDetails());
        if (!handleStillBirth(submission, subFormData)) {
            for (Map<String, String> childFields : subFormData.instances()) {
                Map<String, String> child = create(ID, childFields.get(ID))
                        .put(GENDER, childFields.get(GENDER))
                        .put(WEIGHT, childFields.get(WEIGHT))
                        .put(IMMUNIZATIONS_AT_BIRTH, childFields.get(IMMUNIZATIONS_GIVEN_FIELD_NAME))
                        .map();
                childrenDetails.add(child);
            }
        }
        mother.withChildrenDetails(childrenDetails);
    }


    private void closeMother(Mother mother) {
        mother.setIsClosed(true);
        pncSchedulesService.unEnrollFromSchedules(mother.caseId());

        EligibleCouple eligibleCouple = allEligibleCouples.findByCaseId(mother.ecCaseId());
        logger.info("Closing EC case along with PNC case. Ec Id: " + eligibleCouple.caseId());
        eligibleCouple.setIsClosed(true);
        allEligibleCouples.update(eligibleCouple);
    }

    public void pncRegistrationOA(FormSubmission submission) {
        List<Mother> mothers = allMothers.findByEcCaseId(submission.entityId());
        if (mothers.size() <= 0) {
            logger.warn("Failed to handle PNC registration as there is no mother registered with ec id: " + submission.entityId());
            return;
        }

        Mother mother = mothers.get(0);
        mother.withAnm(submission.anmId());
        addChildrenDetailsToMother(submission, submission.getSubFormByName(CHILD_REGISTRATION_OA), mother);
        allMothers.update(mother);

        updateEligibleCouple(submission, mother);

        if (BOOLEAN_TRUE_VALUE.equals(submission.getField(DID_WOMAN_SURVIVE))) {
            pncSchedulesService.deliveryOutcome(mother.caseId(), submission.getField(REFERENCE_DATE));
        }
        List<String> reportFields = reportFieldsDefinition.get(submission.formName());
        motherReportingService.pncRegistrationOA(new SafeMap(submission.getFields(reportFields)));
    }

    private void updateEligibleCouple(FormSubmission submission, Mother mother) {
        EligibleCouple eligibleCouple = allEligibleCouples.findByCaseId(mother.ecCaseId());
        eligibleCouple.withANMIdentifier(submission.anmId());
        allEligibleCouples.update(eligibleCouple);
    }

    public void close(FormSubmission submission) {
        Mother mother = allMothers.findByCaseId(submission.entityId());
        if (mother == null) {
            logger.warn(format("Failed to close PNC as there is no mother registered with ID: {0}", submission.entityId()));
            return;
        }

        logger.info("Closing PNC case. Entity Id: " + submission.entityId());
        allMothers.close(submission.entityId());
        pncSchedulesService.unEnrollFromSchedules(submission.entityId());
        List<String> reportFields = reportFieldsDefinition.get(submission.formName());
        motherReportingService.closePNC(new SafeMap(submission.getFields(reportFields)));

        if (DEATH_OF_MOTHER_VALUE.equalsIgnoreCase(submission.getField(CLOSE_REASON_FIELD_NAME))
                || PERMANENT_RELOCATION_VALUE.equalsIgnoreCase(submission.getField(CLOSE_REASON_FIELD_NAME))) {
            logger.info("Closing EC case along with PNC case. Submission: " + submission);
            allEligibleCouples.close(mother.ecCaseId());
        }
    }

    public void autoClosePNCCase(String entityId) {
        Mother mother = allMothers.findByCaseId(entityId);
        if (mother == null) {
            logger.warn(format("Failed to auto close PNC as there is no mother registered with ID: {0}", entityId));
            return;
        }

        logger.info(format("Auto closing mother case with entity id {0} as the Post-pregnancy period has elapsed.", entityId));
        allMothers.close(entityId);
        pncSchedulesService.fulfillPNCAutoCloseMilestone(entityId, mother.anmIdentifier());
        pncSchedulesService.generateMotherClosedAlert(entityId, mother.anmIdentifier());
    }

    public void pncVisitHappened(FormSubmission submission) {
        Mother mother = allMothers.findByCaseId(submission.entityId());
        if (mother == null) {
            logger.warn("Found PNC visit without registered mother for entity ID: " + submission.entityId());
            return;
        }

        updatePNCVisitDatesOfMother(submission, mother);
        updatePNCVisitDetails(submission, mother);
        allMothers.update(mother);

        List<String> reportFields = reportFieldsDefinition.get(submission.formName());
        motherReportingService.pncVisitHappened(new SafeMap(submission.getFields(reportFields)));
    }

    private void updatePNCVisitDetails(FormSubmission submission, Mother mother) {
        mother.addPNCVisit(new PNCVisit()
                .withDate(submission.getField(VISIT_DATE_FIELD_NAME))
                .withPerson(submission.getField(VISIT_PERSON_FIELD_NAME))
                .withPlace(submission.getField(VISIT_PLACE_FIELD_NAME))
                .withDifficulties(submission.getField(DIFFICULTIES_FIELD_NAME))
                .withAbdominalProblems(submission.getField(ABDOMINAL_PROBLEMS_FIELD_NAME))
                .withVaginalProblems(submission.getField(VAGINAL_PROBLEMS_FIELD_NAME))
                .withUrinalProblems(submission.getField(URINAL_PROBLEMS_FIELD_NAME))
                .withBreastProblems(submission.getField(BREAST_PROBLEMS))
                .withChildrenDetails(getChildVisitDetails(submission.getSubFormByName(PNC_VISIT_CHILD_SUB_FORM_NAME))));
    }

    private List<Map<String, String>> getChildVisitDetails(SubFormData subFormData) {
        List<Map<String, String>> childVisitDetails = new ArrayList<>();
        for (Map<String, String> childFields : subFormData.instances()) {
            Map<String, String> child = EasyMap.create(ID, childFields.get(ID))
                    .put(ID, childFields.get(ID))
                    .put(URINE_STOOL_PROBLEMS, childFields.get(URINE_STOOL_PROBLEMS))
                    .put(ACTIVITY_PROBLEMS, childFields.get(ACTIVITY_PROBLEMS))
                    .put(BREATHING_PROBLEMS, childFields.get(BREATHING_PROBLEMS))
                    .put(SKIN_PROBLEMS, childFields.get(SKIN_PROBLEMS))
                    .map();
            childVisitDetails.add(child);
        }
        return childVisitDetails;
    }

    private void updatePNCVisitDatesOfMother(FormSubmission submission, Mother mother) {
        String visitDate = submission.getField(VISIT_DATE_FIELD_NAME);
        String pncVisitDates = mother.getDetail(VISIT_DATES_FIELD_NAME) == null
                ? visitDate
                : mother.getDetail(VISIT_DATES_FIELD_NAME) + " " + visitDate;
        mother.details().put(VISIT_DATES_FIELD_NAME, pncVisitDates);
    }

    public void reportPPFamilyPlanning(FormSubmission submission) {
        Mother mother = allMothers.findByCaseId(submission.entityId());
        EligibleCouple eligibleCouple = allEligibleCouples.findByCaseId(mother.ecCaseId());

        EligibleCouple updatedEligibleCouple = updateECWithFPMethod(submission, eligibleCouple);
        allEligibleCouples.update(updatedEligibleCouple);
    }

    private EligibleCouple updateECWithFPMethod(FormSubmission submission, EligibleCouple eligibleCouple) {
        String fpMethod = submission.getField(CURRENT_FP_METHOD_FIELD_NAME);
        String date = submission.getField(FP_METHOD_CHANGE_DATE_FIELD_NAME);
        if (FEMALE_STERILIZATION_FP_METHOD_VALUE.equalsIgnoreCase(fpMethod)) {
            String type = submission.getField(FEMALE_STERILIZATION_TYPE);
            eligibleCouple = updateECWithFemaleSterilizationFPDetails(eligibleCouple, type, date);
            return eligibleCouple;
        }
        if (IUD_FP_METHOD_VALUE.equalsIgnoreCase(fpMethod)) {
            String place = submission.getField(IUD_PLACE);
            eligibleCouple = updateECWithIUDFPDetails(eligibleCouple, place, date);
            return eligibleCouple;
        }
        if (MALE_STERILIZATION_FP_METHOD_VALUE.equalsIgnoreCase(fpMethod)) {
            String type = submission.getField(MALE_STERILIZATION_TYPE);
            List<MaleSterilizationFPDetails> maleSterilizationFPDetails = eligibleCouple.maleSterilizationFPDetails();
            maleSterilizationFPDetails.add(new MaleSterilizationFPDetails(type, date));
            return eligibleCouple.withMaleSterilizationFPDetails(maleSterilizationFPDetails);
        }
        if (OCP_FP_METHOD_VALUE.equalsIgnoreCase(fpMethod)) {
            List<OCPFPDetails> ocpFPDetails = eligibleCouple.ocpFPDetails();
            ocpFPDetails.add(getOCPFPDetails(submission, date));
            eligibleCouple.withOCPFPDetails(ocpFPDetails);
        }
        if (CONDOM_FP_METHOD_VALUE.equalsIgnoreCase(fpMethod)) {
            List<CondomFPDetails> condomFPDetails = eligibleCouple.condomFPDetails();
            condomFPDetails.add(getCondomFPDetails(submission, date));
            eligibleCouple.withCondomFPDetails(condomFPDetails);
        }
        return eligibleCouple;
    }

    private EligibleCouple updateECWithIUDFPDetails(EligibleCouple eligibleCouple, String place, String date) {
        List<IUDFPDetails> iudFPDetails = eligibleCouple.iudFPDetails();
        iudFPDetails.add(new IUDFPDetails(date, place, null, null));
        return eligibleCouple.withIUDFPDetails(iudFPDetails);
    }

    private EligibleCouple updateECWithFemaleSterilizationFPDetails(EligibleCouple eligibleCouple, String type, String date) {
        List<FemaleSterilizationFPDetails> femaleSterilizationFPDetails = eligibleCouple.femaleSterilizationFPDetails();
        femaleSterilizationFPDetails.add(new FemaleSterilizationFPDetails(type, date));
        return eligibleCouple.withFemaleSterilizationFPDetails(femaleSterilizationFPDetails);
    }

    private OCPFPDetails getOCPFPDetails(FormSubmission submission, String fpAcceptanceDate) {
        Map<String, String> refill = new HashMap<>();
        refill.put(SERVICE_PROVIDED_DATE, fpAcceptanceDate);
        refill.put(QUANTITY, submission.getField(NUMBER_OF_OCP_STRIPS_SUPPLIED_FIELD_NAME));
        List<Map<String, String>> refills = new ArrayList<>();
        refills.add(refill);
        return new OCPFPDetails(fpAcceptanceDate, refills, null, null);
    }

    private CondomFPDetails getCondomFPDetails(FormSubmission submission, String fpAcceptanceDate) {
        Map<String, String> refill = new HashMap<>();
        refill.put(SERVICE_PROVIDED_DATE, fpAcceptanceDate);
        refill.put(QUANTITY, submission.getField(NUMBER_OF_CONDOMS_SUPPLIED_FIELD_NAME));
        List<Map<String, String>> refills = new ArrayList<>();
        refills.add(refill);
        return new CondomFPDetails(fpAcceptanceDate, refills);
    }

    private boolean handleStillBirth(FormSubmission submission, SubFormData subFormData) {
        if (!isDeliveryOutcomeStillBirth(submission)) {
            return false;
        }
        if (!subFormData.instances().isEmpty()) {
            String childId = subFormData.instances().get(0).get(ID);
            if(!StringUtils.isEmpty(childId)){
                allChildren.remove(childId);
            }
        }
        return true;
    }

    private boolean isDeliveryOutcomeStillBirth(FormSubmission submission) {
        return AllConstants.DeliveryOutcomeFields.STILL_BIRTH_VALUE.equalsIgnoreCase(submission.getField(AllConstants.DeliveryOutcomeFields.DELIVERY_OUTCOME));
    }
}
