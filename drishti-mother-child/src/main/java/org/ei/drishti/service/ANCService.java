package org.ei.drishti.service;

import org.ei.drishti.contract.AnteNatalCareInformation;
import org.ei.drishti.contract.AnteNatalCareInformationSubset;
import org.ei.drishti.contract.AnteNatalCareOutcomeInformation;
import org.ei.drishti.contract.BirthPlanningRequest;
import org.ei.drishti.domain.Mother;
import org.ei.drishti.form.domain.FormSubmission;
import org.ei.drishti.repository.AllEligibleCouples;
import org.ei.drishti.repository.AllMothers;
import org.ei.drishti.service.formSubmissionHandler.ReportFieldsDefinition;
import org.ei.drishti.service.reporting.MotherReportingService;
import org.ei.drishti.util.SafeMap;
import org.joda.time.LocalDate;
import org.motechproject.model.Time;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import static java.lang.Integer.parseInt;
import static java.text.MessageFormat.format;
import static org.ei.drishti.common.AllConstants.ANCCloseCommCareFields.DEATH_OF_WOMAN_COMMCARE_VALUE;
import static org.ei.drishti.common.AllConstants.ANCCloseCommCareFields.PERMANENT_RELOCATION_COMMCARE_VALUE;
import static org.ei.drishti.common.AllConstants.ANCFormFields.*;
import static org.ei.drishti.common.AllConstants.CaseCloseCommCareFields.CLOSE_REASON_COMMCARE_FIELD_NAME;
import static org.ei.drishti.common.AllConstants.DETAILS_EXTRA_DATA_KEY_NAME;
import static org.ei.drishti.common.AllConstants.Report.REPORT_EXTRA_DATA_KEY_NAME;
import static org.ei.drishti.common.util.DateUtil.today;
import static org.ei.drishti.scheduler.DrishtiScheduleConstants.PREFERED_TIME_FOR_SCHEDULES;
import static org.joda.time.LocalDate.parse;
import static org.joda.time.LocalTime.now;

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
        String motherId = submission.getField(MOTHER_ID);

        if (!eligibleCouples.exists(submission.entityId())) {
            logger.warn(format("Found mother without registered eligible couple. Ignoring: {0} for mother with id: {1} for ANM: {2}",
                    submission.entityId(), motherId, submission.anmId()));
            return;
        }

        Mother mother = allMothers.findByCaseId(motherId);
        allMothers.update(mother.withAnm(submission.anmId()));

        enrollMotherIntoSchedules(motherId, parse(submission.getField(REFERENCE_DATE)));

        List<String> reportFields = reportFieldsDefinition.get(submission.formName());
        reportingService.registerANC(new SafeMap(submission.getFields(reportFields)));
    }

    public void registerOutOfAreaANC(FormSubmission submission) {
        String motherId = submission.getField(MOTHER_ID);

        if (!eligibleCouples.exists(submission.entityId())) {
            logger.warn(format("Found mother without registered eligible couple. Ignoring: {0} for mother with id: {1} for ANM: {2}",
                    submission.entityId(), motherId, submission.anmId()));
            return;
        }

        Mother mother = allMothers.findByCaseId(motherId);
        allMothers.update(mother.withAnm(submission.anmId()));

        enrollMotherIntoSchedules(motherId, parse(submission.getField(REFERENCE_DATE)));
    }

    public void ancVisit(FormSubmission submission) {
        if (!allMothers.exists(submission.entityId())) {
            logger.warn("Found ANC visit without registered mother for Entity ID: " + submission.entityId());
            return;
        }

        ancSchedulesService.ancVisitHasHappened(submission.entityId(), submission.anmId(),
                parseInt(submission.getField(ANC_VISIT_NUMBER_FIELD)), submission.getField(ANC_VISIT_DATE_FIELD));

        List<String> reportFields = reportFieldsDefinition.get(submission.formName());
        reportingService.ancVisit(new SafeMap(submission.getFields(reportFields)));
    }

    public void ancHasBeenProvided(AnteNatalCareInformation ancInformation, Map<String, Map<String, String>> extraData) {
        if (!allMothers.exists(ancInformation.caseId())) {
            logger.warn("Found care provided without registered mother for case ID: " + ancInformation.caseId());
            return;
        }

        if (ancInformation.visitNumber() > 0) {
            ancSchedulesService.ancVisitHasHappened(ancInformation);
        }
        if (ancInformation.ifaTablesHaveBeenProvided()) {
            ancSchedulesService.ifaVisitHasHappened(ancInformation);
        }
        if (ancInformation.wasTTShotProvided()) {
            ancSchedulesService.ttVisitHasHappened(ancInformation);
        }

        reportingService.ancHasBeenProvided(new SafeMap(extraData.get(REPORT_EXTRA_DATA_KEY_NAME)));

        Mother motherWithUpdatedDetails = allMothers.updateDetails(ancInformation.caseId(), extraData.get(DETAILS_EXTRA_DATA_KEY_NAME));
        actionService.updateMotherDetails(motherWithUpdatedDetails.caseId(), motherWithUpdatedDetails.anmIdentifier(), motherWithUpdatedDetails.details());
        actionService.ancCareProvided(motherWithUpdatedDetails.caseId(), motherWithUpdatedDetails.anmIdentifier(), ancInformation.visitNumber(), ancInformation.visitDate(), ancInformation.numberOfIFATabletsProvided(), ancInformation.ttDose(), extraData.get("details"));
    }

    public void updatePregnancyOutcome(AnteNatalCareOutcomeInformation outcomeInformation, Map<String, Map<String, String>> extraData) {
        String caseId = outcomeInformation.motherCaseId();
        if (!allMothers.exists(caseId)) {
            logger.warn("Failed to update delivery outcome as there is no mother registered: " + outcomeInformation);
            return;
        }
        reportingService.updatePregnancyOutcome(new SafeMap(extraData.get(REPORT_EXTRA_DATA_KEY_NAME)));
        ancSchedulesService.unEnrollFromSchedules(caseId);
        allMothers.updateDeliveryOutcomeFor(caseId, outcomeInformation.deliveryOutcomeDate());
        Mother updatedMother = allMothers.updateDetails(caseId, extraData.get(DETAILS_EXTRA_DATA_KEY_NAME));
        actionService.updateANCOutcome(caseId, outcomeInformation.anmIdentifier(), updatedMother.details());
    }

    public void closeANCCase(FormSubmission submission) {
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

        if (DEATH_OF_WOMAN_COMMCARE_VALUE.equalsIgnoreCase(submission.getField(CLOSE_REASON_COMMCARE_FIELD_NAME))
                || PERMANENT_RELOCATION_COMMCARE_VALUE.equalsIgnoreCase(submission.getField(CLOSE_REASON_COMMCARE_FIELD_NAME))) {
            logger.info("Closing EC case along with ANC case. Submission: " + submission);
            eligibleCouples.close(mother.ecCaseId());
        }
    }

    public void updateBirthPlanning(BirthPlanningRequest request, Map<String, Map<String, String>> extraData) {
        if (!allMothers.exists(request.caseId())) {
            logger.warn("Tried to update birth planning without registered mother: " + request);
            return;
        }

        Mother motherWithUpdatedDetails = allMothers.updateDetails(request.caseId(), extraData.get(DETAILS_EXTRA_DATA_KEY_NAME));
        actionService.updateBirthPlanning(request.caseId(), request.anmIdentifier(), motherWithUpdatedDetails.details());
    }

    public void updateSubsetOfANCInformation(AnteNatalCareInformationSubset request, Map<String, Map<String, String>> extraData) {
        if (!allMothers.exists(request.caseId())) {
            logger.warn("Tried to update subset of ANC information without registered mother: " + request);
            return;
        }

        reportingService.subsetOfANCHasBeenProvided(new SafeMap(extraData.get(REPORT_EXTRA_DATA_KEY_NAME)));
    }

    private void enrollMotherIntoSchedules(String caseId, LocalDate lmpDate) {
        Time preferredAlertTime = new Time(PREFERED_TIME_FOR_SCHEDULES);
        LocalDate referenceDate = lmpDate != null ? lmpDate : today();

        ancSchedulesService.enrollMother(caseId, referenceDate, new Time(now()), preferredAlertTime);
    }

    public void ifaProvided(FormSubmission submission) {

    }
}
