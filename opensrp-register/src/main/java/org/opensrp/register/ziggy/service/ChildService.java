package org.opensrp.register.ziggy.service;

import static java.util.Arrays.asList;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.opensrp.register.RegisterConstants.ANCFormFields.THAYI_CARD_NUMBER;
import static org.opensrp.register.RegisterConstants.ChildImmunizationFields.IMMUNIZATIONS_GIVEN_FIELD_NAME;
import static org.opensrp.register.RegisterConstants.ChildImmunizationFields.IMMUNIZATION_DATE_FIELD_NAME;
import static org.opensrp.register.RegisterConstants.ChildImmunizationFields.PREVIOUS_IMMUNIZATIONS_FIELD_NAME;
import static org.opensrp.register.RegisterConstants.ChildRegistrationFormFields.CHILD_VITAMIN_A_HISTORY;
import static org.opensrp.register.RegisterConstants.ChildRegistrationFormFields.DATE;
import static org.opensrp.register.RegisterConstants.ChildRegistrationFormFields.VITAMIN;
import static org.opensrp.register.RegisterConstants.CommonFormFields.ID;
import static org.opensrp.register.RegisterConstants.CommonFormFields.REFERENCE_DATE;
import static org.opensrp.register.RegisterConstants.CommonFormFields.SUBMISSION_DATE_FIELD_NAME;
import static org.opensrp.register.RegisterConstants.VitaminAFields.VITAMIN_A_DOSE_PREFIX;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.opensrp.form.domain.FormSubmission;
import org.opensrp.form.domain.SubFormData;
import org.opensrp.register.RegisterConstants;
import org.opensrp.register.ziggy.domain.Child;
import org.opensrp.register.ziggy.domain.Mother;
import org.opensrp.register.ziggy.repository.AllChildren;
import org.opensrp.register.ziggy.repository.AllMothers;
import org.opensrp.register.ziggy.scheduling.ChildSchedulesService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ChildService {
    public static final String IMMUNIZATIONS_SEPARATOR = " ";
    private static Logger logger = LoggerFactory.getLogger(ChildService.class.toString());
    private ChildSchedulesService childSchedulesService;
    private AllMothers allMothers;
    private AllChildren allChildren;

    @Autowired
    public ChildService(ChildSchedulesService childSchedulesService,
                        AllMothers allMothers,
                        AllChildren allChildren){
    	this.childSchedulesService = childSchedulesService;
        this.allMothers = allMothers;
        this.allChildren = allChildren;
    }

    public void registerChildren(FormSubmission submission) {
        Mother mother = allMothers.findByCaseId(submission.entityId());
        if (mother == null) {
            logger.warn("Failed to handle children registration as there is no mother registered with id: " + submission.entityId());
            return;
        }

        SubFormData subFormData = submission.getSubFormByName(RegisterConstants.DeliveryOutcomeFields.CHILD_REGISTRATION_SUB_FORM_NAME);
        if (handleStillBirth(submission, subFormData)) return;

        String referenceDate = submission.getField(REFERENCE_DATE);
        for (Map<String, String> childFields : subFormData.instances()) {
            Child child = allChildren.findByCaseId(childFields.get(ID));

            List<String> immunizationsGiven = getNamesOfImmunizationsGiven(childFields.get(IMMUNIZATIONS_GIVEN_FIELD_NAME));
            child = child.withAnm(submission.anmId()).withDateOfBirth(referenceDate)
                    .withThayiCard(mother.thayiCardNumber()).setIsClosed(false)
                    .withImmunizations(getChildImmunizationDetails(submission, child, immunizationsGiven));
            allChildren.update(child);

            childSchedulesService.enrollChild(child, submission.instanceId());
        }
    }

    private ArrayList<String> split(String immunizationsGivenField) {
        return new ArrayList<>(asList(immunizationsGivenField.split(IMMUNIZATIONS_SEPARATOR)));
    }

    private boolean isDeliveryOutcomeStillBirth(FormSubmission submission) {
        return RegisterConstants.DeliveryOutcomeFields.STILL_BIRTH_VALUE.equalsIgnoreCase(submission.getField(RegisterConstants.DeliveryOutcomeFields.DELIVERY_OUTCOME));
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

        childSchedulesService.enrollChild(child, submission.instanceId());
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

        childSchedulesService.updateEnrollments(submission.entityId(), previousImmunizations, submission.instanceId());
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
        childSchedulesService.unenrollChild(submission.entityId(), submission.instanceId());
    }

    public void pncOAChildRegistration(FormSubmission submission) {
        List<Mother> mothers = allMothers.findByEcCaseId(submission.entityId());
        if (mothers.size() <= 0) {
            logger.warn("Failed to handle PNC OA children registration as there is no mother registered with id: " + submission.entityId());
            return;
        }
        Mother mother = mothers.get(0);
        allMothers.update(mother.withAnm(submission.anmId()));

        SubFormData subFormData = submission.getSubFormByName(RegisterConstants.Form.PNC_REGISTRATION_OA_SUB_FORM_NAME);
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

            childSchedulesService.enrollChild(child, submission.instanceId());
        }
    }

    private boolean handleStillBirth(FormSubmission submission, SubFormData subFormData) {
        if (!isDeliveryOutcomeStillBirth(submission)) {
            return false;
        }
        if (!subFormData.instances().isEmpty()) {
            String childId = subFormData.instances().get(0).get(ID);
            if (childId != null) {
                allChildren.remove(childId);
            }
        }
        return true;
    }
}
