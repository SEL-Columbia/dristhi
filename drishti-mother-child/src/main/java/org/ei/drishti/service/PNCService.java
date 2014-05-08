package org.ei.drishti.service;

import org.ei.drishti.common.AllConstants;
import org.ei.drishti.common.util.EasyMap;
import org.ei.drishti.domain.EligibleCouple;
import org.ei.drishti.domain.Mother;
import org.ei.drishti.domain.PNCVisit;
import org.ei.drishti.domain.register.*;
import org.ei.drishti.form.domain.FormSubmission;
import org.ei.drishti.form.domain.SubFormData;
import org.ei.drishti.repository.AllChildren;
import org.ei.drishti.repository.AllEligibleCouples;
import org.ei.drishti.repository.AllMothers;
import org.ei.drishti.service.formSubmission.handler.ReportFieldsDefinition;
import org.ei.drishti.service.reporting.MotherReportingService;
import org.ei.drishti.service.scheduling.PNCSchedulesService;
import org.ei.drishti.util.SafeMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.text.MessageFormat.format;
import static org.ei.drishti.common.AllConstants.ANCVisitFormFields.WEIGHT;
import static org.ei.drishti.common.AllConstants.ChildImmunizationFields.IMMUNIZATIONS_GIVEN_FIELD_NAME;
import static org.ei.drishti.common.AllConstants.CommonChildFormFields.GENDER;
import static org.ei.drishti.common.AllConstants.CommonFormFields.ID;
import static org.ei.drishti.common.AllConstants.CommonFormFields.REFERENCE_DATE;
import static org.ei.drishti.common.AllConstants.PNCRegistrationFields.*;
import static org.ei.drishti.common.AllConstants.EntityCloseFormFields.CLOSE_REASON_FIELD_NAME;
import static org.ei.drishti.common.AllConstants.FamilyPlanningFormFields.*;
import static org.ei.drishti.common.AllConstants.Form.*;
import static org.ei.drishti.common.AllConstants.PNCCloseFields.DEATH_OF_MOTHER_VALUE;
import static org.ei.drishti.common.AllConstants.PNCCloseFields.PERMANENT_RELOCATION_VALUE;
import static org.ei.drishti.common.AllConstants.PNCVisitFormFields.*;
import static org.ei.drishti.common.AllConstants.ReportDataParameters.QUANTITY;
import static org.ei.drishti.common.AllConstants.ReportDataParameters.SERVICE_PROVIDED_DATE;
import static org.ei.drishti.common.util.EasyMap.create;

@Service
public class PNCService {
    private static Logger logger = LoggerFactory.getLogger(PNCService.class.toString());
    private ActionService actionService;
    private PNCSchedulesService pncSchedulesService;
    private AllEligibleCouples allEligibleCouples;
    private AllMothers allMothers;
    private AllChildren allChildren;
    private MotherReportingService motherReportingService;
    private ReportFieldsDefinition reportFieldsDefinition;

    @Autowired
    public PNCService(ActionService actionService,
                      PNCSchedulesService pncSchedulesService,
                      AllEligibleCouples allEligibleCouples,
                      AllMothers allMothers,
                      AllChildren allChildren,
                      MotherReportingService motherReportingService,
                      ReportFieldsDefinition reportFieldsDefinition) {
        this.actionService = actionService;
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
        ArrayList<Map<String, String>> childrenDetails = new ArrayList<>();
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
        actionService.markAllAlertsAsInactive(mother.caseId());
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
        if (BOOLEAN_TRUE_VALUE.equals(submission.getField(DID_WOMAN_SURVIVE))) {
            pncSchedulesService.deliveryOutcome(mother.caseId(), submission.getField(REFERENCE_DATE));
        }
        List<String> reportFields = reportFieldsDefinition.get(submission.formName());
        motherReportingService.pncRegistrationOA(new SafeMap(submission.getFields(reportFields)));
    }

    public void close(FormSubmission submission) {
        Mother mother = allMothers.findByCaseId(submission.entityId());
        if (mother == null) {
            logger.warn(format("Failed to close PNC as there is no mother registered with ID: {0}", submission.entityId()));
            return;
        }

        logger.info("Closing PNC case. Entity Id: " + submission.entityId());
        allMothers.close(submission.entityId());
        actionService.markAllAlertsAsInactive(submission.entityId());
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
        actionService.markAllAlertsAsInactive(entityId);
        actionService.closeMother(entityId, mother.anmIdentifier(), AllConstants.AUTO_CLOSE_PNC_CLOSE_REASON);
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
        return new OCPFPDetails(fpAcceptanceDate, refills, null,null);
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
            allChildren.remove(childId);
        }
        return true;
    }

    private boolean isDeliveryOutcomeStillBirth(FormSubmission submission) {
        return AllConstants.PNCRegistrationFields.STILL_BIRTH_VALUE.equalsIgnoreCase(submission.getField(DELIVERY_OUTCOME));
    }
}
