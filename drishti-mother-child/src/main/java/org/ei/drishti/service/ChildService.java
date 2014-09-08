package org.ei.drishti.service;

import org.ei.drishti.common.AllConstants;
import org.ei.drishti.domain.Child;
import org.ei.drishti.domain.Mother;
import org.ei.drishti.form.domain.FormSubmission;
import org.ei.drishti.form.domain.SubFormData;
import org.ei.drishti.repository.AllChildren;
import org.ei.drishti.repository.AllMothers;
import org.ei.drishti.service.formSubmission.handler.ReportFieldsDefinition;
import org.ei.drishti.service.reporting.ChildReportingService;
import org.ei.drishti.service.scheduling.ChildSchedulesService;
import org.ei.drishti.util.SafeMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.ei.drishti.common.AllConstants.ANCFormFields.*;
import static org.ei.drishti.common.AllConstants.ChildImmunizationFields.*;
import static org.ei.drishti.common.AllConstants.ChildRegistrationFormFields.*;
import static org.ei.drishti.common.AllConstants.CommonFormFields.*;
import static org.ei.drishti.common.AllConstants.DeliveryOutcomeFields.DELIVERY_PLACE;
import static org.ei.drishti.common.AllConstants.DeliveryOutcomeFields.DID_BREAST_FEEDING_START;
import static org.ei.drishti.common.AllConstants.PNCVisitFormFields.URINE_STOOL_PROBLEMS;
import static org.ei.drishti.common.AllConstants.VitaminAFields.VITAMIN_A_DOSE_PREFIX;

@Service
public class ChildService {
    public static final String IMMUNIZATIONS_SEPARATOR = " ";
    private static Logger logger = LoggerFactory.getLogger(ChildService.class.toString());
    private ChildSchedulesService childSchedulesService;
    private AllMothers allMothers;
    private AllChildren allChildren;
    private ChildReportingService childReportingService;
    private ActionService actionService;
    private ReportFieldsDefinition reportFieldsDefinition;

    @Autowired
    public ChildService(ChildSchedulesService childSchedulesService,
                        AllMothers allMothers,
                        AllChildren allChildren,
                        ChildReportingService childReportingService, ActionService actionService, ReportFieldsDefinition reportFieldsDefinition) {
        this.childSchedulesService = childSchedulesService;
        this.allMothers = allMothers;
        this.allChildren = allChildren;
        this.childReportingService = childReportingService;
        this.actionService = actionService;
        this.reportFieldsDefinition = reportFieldsDefinition;
    }

    public void registerChildren(FormSubmission submission) {
        Mother mother = allMothers.findByCaseId(submission.entityId());
        if (mother == null) {
            logger.warn("Failed to handle children registration as there is no mother registered with id: " + submission.entityId());
            return;
        }

        SubFormData subFormData = submission.getSubFormByName(AllConstants.DeliveryOutcomeFields.CHILD_REGISTRATION_SUB_FORM_NAME);
        if (handleStillBirth(submission, subFormData)) return;

        String referenceDate = submission.getField(REFERENCE_DATE);
        for (Map<String, String> childFields : subFormData.instances()) {
            Child child = allChildren.findByCaseId(childFields.get(ID));

            List<String> immunizationsGiven = getNamesOfImmunizationsGiven(childFields.get(IMMUNIZATIONS_GIVEN_FIELD_NAME));
            child = child.withAnm(submission.anmId()).withDateOfBirth(referenceDate)
                    .withThayiCard(mother.thayiCardNumber()).setIsClosed(false)
                    .withImmunizations(getChildImmunizationDetails(submission, child, immunizationsGiven));
            allChildren.update(child);

            SafeMap reportingData = new SafeMap();
            reportingData.put(ChildReportingService.CHILD_ID_FIELD, child.caseId());
            reportingData.put(DELIVERY_PLACE, submission.getField(DELIVERY_PLACE));
            reportingData.put(BF_POSTBIRTH, submission.getField(DID_BREAST_FEEDING_START));
            reportingData.put(REGISTRATION_DATE, referenceDate);
            childReportingService.registerChild(reportingData);

            childSchedulesService.enrollChild(child);
        }
    }

    private ArrayList<String> split(String immunizationsGivenField) {
        return new ArrayList<>(asList(immunizationsGivenField.split(IMMUNIZATIONS_SEPARATOR)));
    }

    private boolean isDeliveryOutcomeStillBirth(FormSubmission submission) {
        return AllConstants.DeliveryOutcomeFields.STILL_BIRTH_VALUE.equalsIgnoreCase(submission.getField(AllConstants.DeliveryOutcomeFields.DELIVERY_OUTCOME));
    }

    public void registerChildrenForEC(FormSubmission submission) {
        if (shouldCloseMother(submission.getField(SHOULD_CLOSE_MOTHER))) {
            closeMother(submission.getField(MOTHER_ID));
        }
        Child child = allChildren.findByCaseId(submission.getField(ChildReportingService.CHILD_ID_FIELD));
        child.withAnm(submission.anmId()).withThayiCard(submission.getField(THAYI_CARD_NUMBER)).setIsClosed(false);

        List<String> immunizationsGiven = getNamesOfImmunizationsGiven(submission.getField(IMMUNIZATIONS_GIVEN_FIELD_NAME));

        List<String> vitaminHistory = getVitaminDoses(submission.getField("childVitaminAHistory"));

        child.withImmunizations(getChildImmunizationDetails(submission, child, immunizationsGiven));
        child.withVitaminADoses(getVitaminDoseDetails(submission, child, vitaminHistory));
        allChildren.update(child);
    }

    private void closeMother(String field) {
        Mother mother = allMothers.findByCaseId(field);
        mother.setIsClosed(true);
        allMothers.update(mother);
    }

    private boolean shouldCloseMother(String shouldCloseMother) {
        return isBlank(shouldCloseMother) || Boolean.parseBoolean(shouldCloseMother);
    }

    private Map<String, String> getVitaminDoseDetails(FormSubmission submission, Child child, List<String> vitaminHistory) {
        Map<String, String> vitaminDoses = child.vitaminADoses() == null
                ? new HashMap<String, String>()
                : child.vitaminADoses();
        for (String vitamin : vitaminHistory) {
            vitaminDoses.put(VITAMIN_A_DOSE_PREFIX + vitamin, getVitaminDate(submission, vitamin));
        }
        return vitaminDoses;
    }

    private ArrayList<String> getVitaminDoses(String vitaminDoses) {
        return isBlank(vitaminDoses)
                ? new ArrayList<String>()
                : split(vitaminDoses);
    }

    private String getVitaminDate(FormSubmission submission, String vitaminDose) {
        return submission.getField(VITAMIN + vitaminDose + DATE);
    }

    public void registerChildrenForOA(FormSubmission submission) {
        Child child = allChildren.findByCaseId(submission.getField(ID));
        child.withAnm(submission.anmId()).withThayiCard(submission.getField(THAYI_CARD_NUMBER));

        List<String> immunizationsGiven = getNamesOfImmunizationsGiven(submission.getField(IMMUNIZATIONS_GIVEN_FIELD_NAME));
        List<String> vitaminHistory = getVitaminDoses(submission.getField(CHILD_VITAMIN_A_HISTORY));

        child.withImmunizations(getChildImmunizationDetails(submission, child, immunizationsGiven));
        child.withVitaminADoses(getVitaminDoseDetails(submission, child, vitaminHistory));

        allChildren.update(child);
    }

    public void updateChildImmunization(FormSubmission submission) {
        Child child = allChildren.findByCaseId(submission.entityId());
        if (child == null) {
            logger.warn("Found immunization update without registered child for entity ID: " + submission.entityId());
            return;
        }

        String previousImmunizationsField = isBlank(submission.getField(PREVIOUS_IMMUNIZATIONS_FIELD_NAME))
                ? "" : submission.getField(PREVIOUS_IMMUNIZATIONS_FIELD_NAME);
        List<String> previousImmunizations = asList(previousImmunizationsField.split(IMMUNIZATIONS_SEPARATOR));

        List<String> immunizationsGiven = getNamesOfImmunizationsGiven(submission.getField(IMMUNIZATIONS_GIVEN_FIELD_NAME));
        immunizationsGiven.removeAll(previousImmunizations);
        child.withImmunizations(getChildImmunizationDetails(submission, child, immunizationsGiven));

        allChildren.update(child);
        SafeMap reportFieldsMap = new SafeMap(submission.getFields(reportFieldsDefinition.get(submission.formName())));
        childReportingService.immunizationProvided(reportFieldsMap, previousImmunizations);

        childSchedulesService.updateEnrollments(submission.entityId(), previousImmunizations);
    }

    private Map<String, String> getChildImmunizationDetails(FormSubmission submission, Child child, List<String> immunizationsGiven) {
        Map<String, String> immunizations = child.immunizations() == null
                ? new HashMap<String, String>()
                : child.immunizations();
        for (String immunization : immunizationsGiven) {
            immunizations.put(immunization, getImmunizationDate(submission, immunization));
        }
        return immunizations;
    }

    private String getImmunizationDate(FormSubmission submission, String immunization) {
        String immunizationDateField = immunizationDateForImmunizationFromChildRegistrationEC(submission, immunization);

        //For Child OA and Child registration EC
        if (immunizationDateField != null)
            return immunizationDateField;
        if (submission.getField(IMMUNIZATION_DATE_FIELD_NAME) != null)
            return submission.getField(IMMUNIZATION_DATE_FIELD_NAME);
        return submission.getField(SUBMISSION_DATE_FIELD_NAME);
    }

    private String immunizationDateForImmunizationFromChildRegistrationEC(FormSubmission submission, String immunization) {
        String immunizationDateField = immunization.replace("_", "") + "Date";
        return submission.getField(immunizationDateField);
    }

    private List<String> getNamesOfImmunizationsGiven(String immunizationsGiven) {
        return isBlank(immunizationsGiven)
                ? new ArrayList<String>()
                : split(immunizationsGiven);
    }

    public void vitaminAProvided(FormSubmission submission) {
        Child child = allChildren.findByCaseId(submission.entityId());
        if (child == null) {
            logger.warn("Found that Vitamin A was provided to a not registered child with entity ID: " + submission.entityId());
            return;
        }
        updateVitaminDetailsToChildEntity(submission, child);
        SafeMap reportFieldsMap = new SafeMap(submission.getFields(reportFieldsDefinition.get(submission.formName())));
        childReportingService.vitaminAProvided(reportFieldsMap);
    }

    private void updateVitaminDetailsToChildEntity(FormSubmission submission, Child child) {
        String vitaminADose = submission.getField("vitaminADose");
        String vitaminADate = submission.getField("vitaminADate");
        Map<String, String> vitaminDoses = child.vitaminADoses() == null
                ? new HashMap<String, String>()
                : child.vitaminADoses();
        vitaminDoses.put(VITAMIN_A_DOSE_PREFIX + vitaminADose, vitaminADate);
        allChildren.update(child.withVitaminADoses(vitaminDoses));
    }

    public void closeChild(FormSubmission submission) {
        if (!allChildren.childExists(submission.entityId())) {
            logger.warn("Found close child request without registered child for entity id: " + submission.entityId());
            return;
        }

        allChildren.close(submission.entityId());
        actionService.markAllAlertsAsInactive(submission.entityId());
        SafeMap reportFieldsMap = new SafeMap(submission.getFields(reportFieldsDefinition.get(submission.formName())));
        childReportingService.closeChild(reportFieldsMap);
        childSchedulesService.unenrollChild(submission.entityId());
    }

    public void pncOAChildRegistration(FormSubmission submission) {
        List<Mother> mothers = allMothers.findByEcCaseId(submission.entityId());
        if (mothers.size() <= 0) {
            logger.warn("Failed to handle PNC OA children registration as there is no mother registered with id: " + submission.entityId());
            return;
        }
        Mother mother = mothers.get(0);
        allMothers.update(mother.withAnm(submission.anmId()));

        SubFormData subFormData = submission.getSubFormByName(AllConstants.Form.PNC_REGISTRATION_OA_SUB_FORM_NAME);
        if (handleStillBirth(submission, subFormData)) return;

        String referenceDate = submission.getField(REFERENCE_DATE);
        for (Map<String, String> childFields : subFormData.instances()) {
            Child child = allChildren.findByCaseId(childFields.get(ID));
            List<String> immunizationsGiven = getNamesOfImmunizationsGiven(childFields.get(IMMUNIZATIONS_GIVEN_FIELD_NAME));
            child = child.withAnm(submission.anmId())
                    .withDateOfBirth(referenceDate)
                    .withThayiCard(mother.thayiCardNumber())
                    .setIsClosed(false)
                    .withImmunizations(getChildImmunizationDetails(submission, child, immunizationsGiven));
            allChildren.update(child);

            SafeMap reportingData = new SafeMap();
            reportingData.put(ChildReportingService.CHILD_ID_FIELD, child.caseId());
            reportingData.put(DELIVERY_PLACE, submission.getField(DELIVERY_PLACE));
            reportingData.put(BF_POSTBIRTH, childFields.get(DID_BREAST_FEEDING_START));
            reportingData.put(REGISTRATION_DATE, referenceDate);
            childReportingService.registerChild(reportingData);

            childSchedulesService.enrollChild(child);
        }
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

    public void pncVisitHappened(FormSubmission submission) {
        Map<String, String> reportFieldsMap = submission.getFields(reportFieldsDefinition.get(submission.formName()));

        SubFormData subFormData = submission.getSubFormByName(AllConstants.Form.PNC_VISIT_CHILD_SUB_FORM_NAME);

        if (handleStillBirth(submission, subFormData)) return;

        for (Map<String, String> childFields : subFormData.instances()) {
            SafeMap reportingData = new SafeMap(reportFieldsMap);
            reportingData.put(ChildReportingService.CHILD_ID_FIELD, childFields.get(ID));
            reportingData.put(URINE_STOOL_PROBLEMS, childFields.get(URINE_STOOL_PROBLEMS));
            childReportingService.pncVisitHappened(reportingData);
        }
    }

    public void sickVisitHappened(FormSubmission submission) {
        Map<String, String> reportFieldsMap = submission.getFields(reportFieldsDefinition.get(submission.formName()));
        childReportingService.sickVisitHappened(new SafeMap(reportFieldsMap));
    }
}
