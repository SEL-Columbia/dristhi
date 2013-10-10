package org.ei.drishti.service;

import org.ei.drishti.common.AllConstants;
import org.ei.drishti.common.util.IntegerUtil;
import org.ei.drishti.domain.Mother;
import org.ei.drishti.form.domain.FormSubmission;
import org.ei.drishti.repository.AllEligibleCouples;
import org.ei.drishti.repository.AllMothers;
import org.ei.drishti.service.formSubmission.handler.ReportFieldsDefinition;
import org.ei.drishti.service.reporting.MotherReportingService;
import org.ei.drishti.service.scheduling.ANCSchedulesService;
import org.ei.drishti.util.SafeMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.lang.Integer.parseInt;
import static java.text.MessageFormat.format;
import static org.ei.drishti.common.AllConstants.ANCCloseFields.DEATH_OF_WOMAN_VALUE;
import static org.ei.drishti.common.AllConstants.ANCCloseFields.PERMANENT_RELOCATION_VALUE;
import static org.ei.drishti.common.AllConstants.ANCFormFields.TT_DATE_FIELD;
import static org.ei.drishti.common.AllConstants.ANCFormFields.TT_DOSE_FIELD;
import static org.ei.drishti.common.AllConstants.CommonFormFields.REFERENCE_DATE;
import static org.ei.drishti.common.AllConstants.EntityCloseFormFields.CLOSE_REASON_FIELD_NAME;
import static org.ei.drishti.common.AllConstants.HbTestFormFields.ANAEMIC_STATUS_FIELD;
import static org.ei.drishti.common.AllConstants.HbTestFormFields.HB_TEST_DATE_FIELD;
import static org.ei.drishti.common.AllConstants.IFAFields.IFA_TABLETS_DATE;
import static org.ei.drishti.common.AllConstants.IFAFields.NUMBER_OF_IFA_TABLETS_GIVEN;
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
        if (!allMothers.exists(submission.entityId())) {
            logger.warn("Found ANC visit without registered mother for Entity ID: " + submission.entityId());
            return;
        }

        ancSchedulesService.ancVisitHasHappened(submission.entityId(), submission.anmId(),
                parseInt(submission.getField(AllConstants.ANCFormFields.ANC_VISIT_NUMBER_FIELD)), submission.getField(AllConstants.ANCFormFields.ANC_VISIT_DATE_FIELD));

        List<String> reportFields = reportFieldsDefinition.get(submission.formName());
        reportingService.ancVisit(new SafeMap(submission.getFields(reportFields)));
    }

    public void ttProvided(FormSubmission submission) {
        ancSchedulesService.ttVisitHasHappened(submission.entityId(), submission.anmId(), submission.getField(TT_DOSE_FIELD), submission.getField(TT_DATE_FIELD));

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

        ancSchedulesService.hbTestDone(submission.entityId(), submission.anmId(), submission.getField(HB_TEST_DATE_FIELD),
                submission.getField(ANAEMIC_STATUS_FIELD), mother.lmp());
    }

    public void deliveryOutcome(FormSubmission submission) {
        if (!allMothers.exists(submission.entityId())) {
            logger.warn("Tried to handle delivery outcome without registered mother. Submission: " + submission);
            return;
        }

        ancSchedulesService.unEnrollFromSchedules(submission.entityId());

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

        ancSchedulesService.unEnrollFromSchedules(submission.entityId());
        actionService.markAllAlertsAsInactive(submission.entityId());

        if (DEATH_OF_WOMAN_VALUE.equalsIgnoreCase(submission.getField(CLOSE_REASON_FIELD_NAME))
                || PERMANENT_RELOCATION_VALUE.equalsIgnoreCase(submission.getField(CLOSE_REASON_FIELD_NAME))) {
            logger.info("Closing EC case along with ANC case. Submission: " + submission);
            eligibleCouples.close(mother.ecCaseId());
        }
    }
}
