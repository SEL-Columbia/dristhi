package org.opensrp.register.service;

import org.opensrp.common.AllConstants;
import org.opensrp.common.util.IntegerUtil;
import org.opensrp.register.domain.Mother;
import org.opensrp.form.domain.FormSubmission;
import org.opensrp.util.SafeMap;
import org.opensrp.register.repository.AllEligibleCouples;
import org.opensrp.register.repository.AllMothers;
import org.opensrp.service.formSubmission.handler.ReportFieldsDefinition;
import org.opensrp.register.service.reporting.MotherReportingService;
import org.opensrp.service.reporting.rules.IsHypertensionDetectedRule;
import org.opensrp.register.service.scheduling.ANCSchedulesService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import static java.lang.Integer.parseInt;
import static java.text.MessageFormat.format;
import static org.opensrp.common.AllConstants.ANCCloseFields.DEATH_OF_WOMAN_VALUE;
import static org.opensrp.common.AllConstants.ANCCloseFields.PERMANENT_RELOCATION_VALUE;
import static org.opensrp.common.AllConstants.ANCFormFields.*;
import static org.opensrp.common.AllConstants.ANCInvestigationsFormFields.*;
import static org.opensrp.common.AllConstants.ANCVisitFormFields.*;
import static org.opensrp.common.AllConstants.BOOLEAN_FALSE_VALUE;
import static org.opensrp.common.AllConstants.BOOLEAN_TRUE_VALUE;
import static org.opensrp.common.AllConstants.CommonFormFields.REFERENCE_DATE;
import static org.opensrp.common.AllConstants.CommonFormFields.SUBMISSION_DATE_FIELD_NAME;
import static org.opensrp.common.AllConstants.EntityCloseFormFields.CLOSE_REASON_FIELD_NAME;
import static org.opensrp.common.AllConstants.HbTestFormFields.*;
import static org.opensrp.common.AllConstants.IFAFields.IFA_TABLETS_DATE;
import static org.opensrp.common.AllConstants.IFAFields.NUMBER_OF_IFA_TABLETS_GIVEN;
import static org.opensrp.common.util.EasyMap.create;
import static org.joda.time.LocalDate.parse;

@Service
public class ANCService {
    private static Logger logger = LoggerFactory.getLogger(ANCService.class.toString());

    private AllMothers allMothers;
    private AllEligibleCouples eligibleCouples;
    private ANCSchedulesService ancSchedulesService;
    private ActionService actionService;
    private MotherReportingService reportingService;
    private ReportFieldsDefinition reportFieldsDefinition;

    @Autowired
    public ANCService(AllMothers allMothers,
                      AllEligibleCouples eligibleCouples,
                      ANCSchedulesService ancSchedulesService,
                      ActionService actionService,
                      MotherReportingService reportingService,
                      ReportFieldsDefinition reportFieldsDefinition) {
        this.allMothers = allMothers;
        this.eligibleCouples = eligibleCouples;
        this.ancSchedulesService = ancSchedulesService;
        this.actionService = actionService;
        this.reportingService = reportingService;
        this.reportFieldsDefinition = reportFieldsDefinition;
    }

    public void registerANC(FormSubmission submission) {
        String motherId = submission.getField(AllConstants.ANCFormFields.MOTHER_ID);

        if (!eligibleCouples.exists(submission.entityId())) {
            logger.warn(format("Found mother without registered eligible couple. Ignoring: {0} for mother with id: {1} for ANM: {2}",
                    submission.entityId(), motherId, submission.anmId()));
            return;
        }

        Mother mother = allMothers.findByCaseId(motherId);
        allMothers.update(mother.withAnm(submission.anmId()));

        ancSchedulesService.enrollMother(motherId, parse(submission.getField(REFERENCE_DATE)));

        List<String> reportFields = reportFieldsDefinition.get(submission.formName());
        reportingService.registerANC(new SafeMap(submission.getFields(reportFields)));
    }

    public void registerOutOfAreaANC(FormSubmission submission) {
        String motherId = submission.getField(AllConstants.ANCFormFields.MOTHER_ID);

        if (!eligibleCouples.exists(submission.entityId())) {
            logger.warn(format("Found mother without registered eligible couple. Ignoring: {0} for mother with id: {1} for ANM: {2}",
                    submission.entityId(), motherId, submission.anmId()));
            return;
        }

        Mother mother = allMothers.findByCaseId(motherId);
        allMothers.update(mother.withAnm(submission.anmId()));

        ancSchedulesService.enrollMother(motherId, parse(submission.getField(REFERENCE_DATE)));
    }

    public void ancVisit(FormSubmission submission) {
        Mother mother = allMothers.findByCaseId(submission.entityId());
        if (mother == null) {
            logger.warn("Found ANC visit without registered mother for Entity ID: " + submission.entityId());
            return;
        }

        updateMotherAfterANCVisit(submission, mother);
        ancSchedulesService.ancVisitHasHappened(submission.entityId(), submission.anmId(),
                parseInt(submission.getField(AllConstants.ANCFormFields.ANC_VISIT_NUMBER_FIELD)), submission.getField(AllConstants.ANCFormFields.ANC_VISIT_DATE_FIELD));

        List<String> reportFields = reportFieldsDefinition.get(submission.formName());
        reportingService.ancVisit(new SafeMap(submission.getFields(reportFields)));
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
        SafeMap safeMap = new SafeMap(create(BP_DIASTOLIC, bpDiastolic).put(BP_SYSTOLIC, bpSystolic).map());

        IsHypertensionDetectedRule isHypertensionDetectedRule = new IsHypertensionDetectedRule();
        boolean isHyperTensionDetected = isHypertensionDetectedRule.apply(safeMap);
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
                submission.getField(TT_DOSE_FIELD), submission.getField(TT_DATE_FIELD));

        List<String> reportFields = reportFieldsDefinition.get(submission.formName());
        reportingService.ttProvided(new SafeMap(submission.getFields(reportFields)));
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
                submission.getField(IFA_TABLETS_DATE));
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
                submission.getField(ANAEMIC_STATUS_FIELD), mother.lmp());
    }

    public void deliveryPlanned(FormSubmission submission) {
        if (!allMothers.exists(submission.entityId())) {
            logger.warn("Tried to handle delivery plan without registered mother. Submission: " + submission);
            return;
        }

        ancSchedulesService.deliveryHasBeenPlanned(submission.entityId(), submission.anmId(), submission.getField(SUBMISSION_DATE_FIELD_NAME));
    }

    public void deliveryOutcome(FormSubmission submission) {
        if (!allMothers.exists(submission.entityId())) {
            logger.warn("Tried to handle delivery outcome without registered mother. Submission: " + submission);
            return;
        }

        ancSchedulesService.unEnrollFromAllSchedules(submission.entityId());

        List<String> reportFields = reportFieldsDefinition.get(submission.formName());
        reportingService.deliveryOutcome(new SafeMap(submission.getFields(reportFields)));
    }

    public void close(FormSubmission submission) {
        Mother mother = allMothers.findByCaseId(submission.entityId());
        if (mother == null) {
            logger.warn("Tried to close case without registered mother for case ID: " + submission.entityId());
            return;
        }

        allMothers.close(submission.entityId());
        List<String> reportFields = reportFieldsDefinition.get(submission.formName());
        reportingService.closeANC(new SafeMap(submission.getFields(reportFields)));

        ancSchedulesService.unEnrollFromAllSchedules(submission.entityId());
        actionService.markAllAlertsAsInactive(submission.entityId());

        if (DEATH_OF_WOMAN_VALUE.equalsIgnoreCase(submission.getField(CLOSE_REASON_FIELD_NAME))
                || PERMANENT_RELOCATION_VALUE.equalsIgnoreCase(submission.getField(CLOSE_REASON_FIELD_NAME))) {
            logger.info("Closing EC case along with ANC case. Submission: " + submission);
            eligibleCouples.close(mother.ecCaseId());
        }
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
