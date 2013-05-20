package org.ei.drishti.service;

import org.ei.drishti.contract.*;
import org.ei.drishti.domain.EligibleCouple;
import org.ei.drishti.domain.Mother;
import org.ei.drishti.form.domain.FormSubmission;
import org.ei.drishti.repository.AllEligibleCouples;
import org.ei.drishti.repository.AllMothers;
import org.ei.drishti.service.formSubmissionHandler.ReportFieldsDefinition;
import org.ei.drishti.service.reporting.MotherReportingService;
import org.ei.drishti.util.SafeMap;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.motechproject.model.Time;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import static java.text.MessageFormat.format;
import static org.ei.drishti.common.AllConstants.ANCCloseCommCareFields.DEATH_OF_WOMAN_COMMCARE_VALUE;
import static org.ei.drishti.common.AllConstants.ANCCloseCommCareFields.PERMANENT_RELOCATION_COMMCARE_VALUE;
import static org.ei.drishti.common.AllConstants.ANCFormFields.MOTHER_ID;
import static org.ei.drishti.common.AllConstants.ANCFormFields.REFERENCE_DATE;
import static org.ei.drishti.common.AllConstants.DETAILS_EXTRA_DATA_KEY_NAME;
import static org.ei.drishti.common.AllConstants.Report.REPORT_EXTRA_DATA_KEY_NAME;
import static org.ei.drishti.common.util.DateUtil.today;
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
    private ECService ecService;

    @Autowired
    public ANCService(ECService ecService,
                      AllMothers allMothers,
                      AllEligibleCouples eligibleCouples,
                      ANCSchedulesService ancSchedulesService,
                      ActionService actionService,
                      MotherReportingService reportingService,
                      ReportFieldsDefinition reportFieldsDefinition) {
        this.ecService = ecService;
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

        List<String> reportFields = reportFieldsDefinition.get(submission.formName());
        reportingService.registerANC(new SafeMap(submission.getFields(reportFields)));

        enrollMotherIntoSchedules(motherId, parse(submission.getField(REFERENCE_DATE)));
    }

    public void registerOutOfAreaANC(OutOfAreaANCRegistrationRequest request, EligibleCouple couple, Map<String, Map<String, String>> extraData) {
        Map<String, String> details = extraData.get("details");

        Mother mother = new Mother(request.caseId(), couple.caseId(), request.thaayiCardNumber())
                .withAnm(request.anmIdentifier())
                .withLMP(request.lmpDate()).withLocation(request.village(), request.subCenter(), request.phc())
                .withDetails(details);
        allMothers.register(mother);

        actionService.registerOutOfAreaANC(request.caseId(), couple.caseId(), request.wife(), request.husband(), request.anmIdentifier(),
                request.village(), request.subCenter(), request.phc(), request.thaayiCardNumber(), request.lmpDate(), details);

        enrollMotherIntoSchedules(request.caseId(), request.lmpDate());
    }

    public void ancHasBeenProvided(AnteNatalCareInformation ancInformation, Map<String, Map<String, String>> extraData) {
        if (!allMothers.motherExists(ancInformation.caseId())) {
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
        if (!allMothers.motherExists(caseId)) {
            logger.warn("Failed to update delivery outcome as there is no mother registered: " + outcomeInformation);
            return;
        }
        reportingService.updatePregnancyOutcome(new SafeMap(extraData.get(REPORT_EXTRA_DATA_KEY_NAME)));
        ancSchedulesService.unEnrollFromSchedules(caseId);
        allMothers.updateDeliveryOutcomeFor(caseId, outcomeInformation.deliveryOutcomeDate());
        Mother updatedMother = allMothers.updateDetails(caseId, extraData.get(DETAILS_EXTRA_DATA_KEY_NAME));
        actionService.updateANCOutcome(caseId, outcomeInformation.anmIdentifier(), updatedMother.details());
    }

    public void closeANCCase(AnteNatalCareCloseInformation closeInformation, SafeMap data) {
        if (!allMothers.motherExists(closeInformation.caseId())) {
            logger.warn("Tried to close case without registered mother for case ID: " + closeInformation.caseId());
            return;
        }

        allMothers.close(closeInformation.caseId());
        reportingService.closeANC(data);
        ancSchedulesService.unEnrollFromSchedules(closeInformation.caseId());
        actionService.closeMother(closeInformation.caseId(), closeInformation.anmIdentifier(), closeInformation.reason());

        if (DEATH_OF_WOMAN_COMMCARE_VALUE.equalsIgnoreCase(closeInformation.reason())
                || PERMANENT_RELOCATION_COMMCARE_VALUE.equalsIgnoreCase(closeInformation.reason())) {
            logger.info("Closing EC case along with ANC case. Details: " + closeInformation);
            ecService.closeEligibleCouple(new EligibleCoupleCloseRequest(closeInformation.caseId(), closeInformation.anmIdentifier()));
        }
    }

    public void updateBirthPlanning(BirthPlanningRequest request, Map<String, Map<String, String>> extraData) {
        if (!allMothers.motherExists(request.caseId())) {
            logger.warn("Tried to update birth planning without registered mother: " + request);
            return;
        }

        Mother motherWithUpdatedDetails = allMothers.updateDetails(request.caseId(), extraData.get(DETAILS_EXTRA_DATA_KEY_NAME));
        actionService.updateBirthPlanning(request.caseId(), request.anmIdentifier(), motherWithUpdatedDetails.details());
    }

    public void updateSubsetOfANCInformation(AnteNatalCareInformationSubset request, Map<String, Map<String, String>> extraData) {
        if (!allMothers.motherExists(request.caseId())) {
            logger.warn("Tried to update subset of ANC information without registered mother: " + request);
            return;
        }

        reportingService.subsetOfANCHasBeenProvided(new SafeMap(extraData.get(REPORT_EXTRA_DATA_KEY_NAME)));
    }

    private void enrollMotherIntoSchedules(String caseId, LocalDate lmpDate) {
        Time preferredAlertTime = new Time(new LocalTime(14, 0));
        LocalDate referenceDate = lmpDate != null ? lmpDate : today();

        ancSchedulesService.enrollMother(caseId, referenceDate, new Time(now()), preferredAlertTime);
    }
}
