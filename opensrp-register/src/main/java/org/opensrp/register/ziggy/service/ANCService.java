package org.opensrp.register.ziggy.service;

import static java.lang.Integer.parseInt;
import static org.joda.time.LocalDate.parse;
import static org.opensrp.common.util.EasyMap.create;
import static org.opensrp.register.RegisterConstants.BOOLEAN_FALSE_VALUE;
import static org.opensrp.register.RegisterConstants.BOOLEAN_TRUE_VALUE;
import static org.opensrp.register.RegisterConstants.ANCFormFields.ANC_VISIT_DATE_FIELD;
import static org.opensrp.register.RegisterConstants.ANCFormFields.ANC_VISIT_NUMBER_FIELD;
import static org.opensrp.register.RegisterConstants.ANCFormFields.TT_DATE_FIELD;
import static org.opensrp.register.RegisterConstants.ANCFormFields.TT_DOSE_FIELD;
import static org.opensrp.register.RegisterConstants.ANCInvestigationsFormFields.BILE_PIGMENTS;
import static org.opensrp.register.RegisterConstants.ANCInvestigationsFormFields.BILE_SALTS;
import static org.opensrp.register.RegisterConstants.ANCInvestigationsFormFields.RH_INCOMPATIBLE_COUPLE;
import static org.opensrp.register.RegisterConstants.ANCInvestigationsFormFields.TESTS_POSITIVE_RESULTS;
import static org.opensrp.register.RegisterConstants.ANCInvestigationsFormFields.TESTS_RESULTS_TO_ENTER;
import static org.opensrp.register.RegisterConstants.ANCInvestigationsFormFields.TEST_DATE;
import static org.opensrp.register.RegisterConstants.ANCInvestigationsFormFields.WOMAN_BLOOD_GROUP;
import static org.opensrp.register.RegisterConstants.ANCVisitFormFields.BP_DIASTOLIC;
import static org.opensrp.register.RegisterConstants.ANCVisitFormFields.BP_SYSTOLIC;
import static org.opensrp.register.RegisterConstants.ANCVisitFormFields.IS_HYPERTENSION_DETECTED_FOR_FIRST_TIME;
import static org.opensrp.register.RegisterConstants.ANCVisitFormFields.WEIGHT;
import static org.opensrp.register.RegisterConstants.CommonFormFields.REFERENCE_DATE;
import static org.opensrp.register.RegisterConstants.CommonFormFields.SUBMISSION_DATE_FIELD_NAME;
import static org.opensrp.register.RegisterConstants.HbTestFormFields.ANAEMIC_STATUS_FIELD;
import static org.opensrp.register.RegisterConstants.HbTestFormFields.HB_LEVEL_FIELD;
import static org.opensrp.register.RegisterConstants.HbTestFormFields.HB_TEST_DATE_FIELD;
import static org.opensrp.register.RegisterConstants.IFAFields.IFA_TABLETS_DATE;
import static org.opensrp.register.RegisterConstants.IFAFields.NUMBER_OF_IFA_TABLETS_GIVEN;

import java.util.Map;

import org.opensrp.common.util.IntegerUtil;
import org.opensrp.form.domain.FormSubmission;
import org.opensrp.register.RegisterConstants;
import org.opensrp.register.ziggy.domain.Mother;
import org.opensrp.register.ziggy.repository.AllMothers;
import org.opensrp.register.ziggy.scheduling.ANCSchedulesService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ANCService {
    private static Logger logger = LoggerFactory.getLogger(ANCService.class.toString());
    
    private static final int BP_DIASTOLIC_THRESHOLD_VALUE = 90;
    private static final int BP_SYSTOLIC_THRESHOLD_VALUE = 140;
    
    private AllMothers allMothers;
    private ANCSchedulesService ancSchedulesService;

    @Autowired
    public ANCService(AllMothers allMothers, ANCSchedulesService ancSchedulesService) {
        this.allMothers = allMothers;
        this.ancSchedulesService = ancSchedulesService;
    }

    public void registerANC(FormSubmission submission) {
        String motherId = submission.getField(RegisterConstants.ANCFormFields.MOTHER_ID);

        Mother mother = allMothers.findByCaseId(motherId);
        allMothers.update(mother.withAnm(submission.anmId()));

        ancSchedulesService.enrollMother(motherId, parse(submission.getField(REFERENCE_DATE)), submission.instanceId());
    }

    public void registerOutOfAreaANC(FormSubmission submission) {
        String motherId = submission.getField(RegisterConstants.ANCFormFields.MOTHER_ID);

        Mother mother = allMothers.findByCaseId(motherId);
        allMothers.update(mother.withAnm(submission.anmId()));

        ancSchedulesService.enrollMother(motherId, parse(submission.getField(REFERENCE_DATE)), submission.instanceId());
    }

    public void ancVisit(FormSubmission submission) {
        Mother mother = allMothers.findByCaseId(submission.entityId());
        if (mother == null) {
            logger.warn("Found ANC visit without registered mother for Entity ID: " + submission.entityId());
            return;
        }

        updateMotherAfterANCVisit(submission, mother);
        ancSchedulesService.ancVisitHasHappened(submission.entityId(), submission.anmId(),
                parseInt(submission.getField(RegisterConstants.ANCFormFields.ANC_VISIT_NUMBER_FIELD)), submission.getField(RegisterConstants.ANCFormFields.ANC_VISIT_DATE_FIELD), submission.instanceId());
    }

    private void updateMotherAfterANCVisit(FormSubmission submission, Mother mother) {
        updateHypertensionDetection(submission, mother);
        Map<String, String> ancVisits = create(ANC_VISIT_DATE_FIELD, submission.getField(ANC_VISIT_DATE_FIELD))
                .put(WEIGHT, submission.getField(WEIGHT))
                .put(BP_SYSTOLIC, submission.getField(BP_SYSTOLIC))
                .put(BP_DIASTOLIC, submission.getField(BP_DIASTOLIC))
                .put(ANC_VISIT_NUMBER_FIELD, submission.getField(ANC_VISIT_NUMBER_FIELD))
                .map();
        mother.updateANCVisitInformation(ancVisits);
        allMothers.update(mother);
    }

    private void updateHypertensionDetection(FormSubmission submission, Mother mother) {
        String bpDiastolic = submission.getField(BP_DIASTOLIC);
        String bpSystolic = submission.getField(BP_DIASTOLIC);

        boolean isHyperTensionDetected = ((IntegerUtil.tryParse(bpDiastolic, 0) >= BP_DIASTOLIC_THRESHOLD_VALUE)
                || (IntegerUtil.tryParse(bpSystolic, 0) >= BP_SYSTOLIC_THRESHOLD_VALUE));        
        if (isHyperTensionDetected) {
            String hypertension = (mother.getDetail(IS_HYPERTENSION_DETECTED_FOR_FIRST_TIME) == null) ? BOOLEAN_TRUE_VALUE : BOOLEAN_FALSE_VALUE;
            mother.details().put(IS_HYPERTENSION_DETECTED_FOR_FIRST_TIME, hypertension);
        }
    }

    public void ttProvided(FormSubmission submission) {
        Mother mother = allMothers.findByCaseId(submission.entityId());
        if (mother == null) {
            logger.warn("Tried to handle TT provided without registered mother. Submission: " + submission);
            return;
        }

        Map<String, String> ttDose = create(TT_DATE_FIELD, submission.getField(TT_DATE_FIELD))
                .put(TT_DOSE_FIELD, submission.getField(TT_DOSE_FIELD))
                .map();
        mother.updateTTDoseInformation(ttDose);
        allMothers.update(mother);

        ancSchedulesService.ttVisitHasHappened(submission.entityId(), submission.anmId(),
                submission.getField(TT_DOSE_FIELD), submission.getField(TT_DATE_FIELD), submission.instanceId());
    }

    public void ifaTabletsGiven(FormSubmission submission) {
        Mother mother = allMothers.findByCaseId(submission.entityId());
        if (mother == null) {
            logger.warn("Tried to handle ifa tablets given without registered mother. Submission: " + submission);
            return;
        }

        int numberOfIFATabletsGivenThisTime =
                IntegerUtil.tryParse(submission.getField(NUMBER_OF_IFA_TABLETS_GIVEN), 0);
        mother.updateTotalNumberOfIFATabletsGiven(numberOfIFATabletsGivenThisTime);
        Map<String, String> ifaTablets = create(IFA_TABLETS_DATE, submission.getField(IFA_TABLETS_DATE))
                .put(NUMBER_OF_IFA_TABLETS_GIVEN, submission.getField(NUMBER_OF_IFA_TABLETS_GIVEN))
                .map();
        mother.updateIFATabletsInformation(ifaTablets);
        allMothers.update(mother);

        ancSchedulesService.ifaTabletsGiven(
                submission.entityId(),
                submission.anmId(),
                submission.getField(NUMBER_OF_IFA_TABLETS_GIVEN),
                submission.getField(IFA_TABLETS_DATE), submission.instanceId());
    }

    public void hbTest(FormSubmission submission) {
        Mother mother = allMothers.findByCaseId(submission.entityId());
        if (mother == null) {
            logger.warn("Tried to handle Hb test given without registered mother. Submission: " + submission);
            return;
        }

        Map<String, String> hbTest = create(HB_TEST_DATE_FIELD, submission.getField(HB_TEST_DATE_FIELD))
                .put(HB_LEVEL_FIELD, submission.getField(HB_LEVEL_FIELD))
                .map();
        mother.updateHBTestInformation(hbTest);
        allMothers.update(mother);

        ancSchedulesService.hbTestDone(submission.entityId(), submission.anmId(), submission.getField(HB_TEST_DATE_FIELD),
                submission.getField(ANAEMIC_STATUS_FIELD), mother.lmp(), submission.instanceId());
    }

    public void deliveryPlanned(FormSubmission submission) {
        if (!allMothers.exists(submission.entityId())) {
            logger.warn("Tried to handle delivery plan without registered mother. Submission: " + submission);
            return;
        }

        ancSchedulesService.deliveryHasBeenPlanned(submission.entityId(), submission.anmId(), submission.getField(SUBMISSION_DATE_FIELD_NAME), submission.instanceId());
    }

    public void deliveryOutcome(FormSubmission submission) {
        if (!allMothers.exists(submission.entityId())) {
            logger.warn("Tried to handle delivery outcome without registered mother. Submission: " + submission);
            return;
        }

        ancSchedulesService.unEnrollFromAllSchedules(submission.entityId(), submission.instanceId());
    }

    public void close(FormSubmission submission) {
        Mother mother = allMothers.findByCaseId(submission.entityId());
        if (mother == null) {
            logger.warn("Tried to close case without registered mother for case ID: " + submission.entityId());
            return;
        }

        allMothers.close(submission.entityId());

        ancSchedulesService.unEnrollFromAllSchedules(submission.entityId(), submission.instanceId());
    }

    public void ancInvestigations(FormSubmission submission) {
        Mother mother = allMothers.findByCaseId(submission.entityId());
        if (mother == null) {
            logger.warn("Tried to close case without registered mother for case ID: " + submission.entityId());
            return;
        }

        Map<String, String> ancInvestigations = create(TEST_DATE, submission.getField(TEST_DATE))
                .put(TESTS_RESULTS_TO_ENTER, submission.getField(TESTS_RESULTS_TO_ENTER))
                .put(TESTS_POSITIVE_RESULTS, submission.getField(TESTS_POSITIVE_RESULTS))
                .put(BILE_PIGMENTS, submission.getField(BILE_PIGMENTS))
                .put(BILE_SALTS, submission.getField(BILE_SALTS))
                .put(WOMAN_BLOOD_GROUP, submission.getField(WOMAN_BLOOD_GROUP))
                .put(RH_INCOMPATIBLE_COUPLE, submission.getField(RH_INCOMPATIBLE_COUPLE))
                .map();
        mother.updateANCInvestigationsInformation(ancInvestigations);
        allMothers.update(mother);
    }
}
