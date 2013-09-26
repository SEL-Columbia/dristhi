package org.ei.drishti.service;

import org.ei.drishti.domain.Mother;
import org.ei.drishti.form.domain.FormSubmission;
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

import java.util.List;

import static java.text.MessageFormat.format;
import static org.ei.drishti.common.AllConstants.ANCCloseFields.DEATH_OF_WOMAN_VALUE;
import static org.ei.drishti.common.AllConstants.ANCCloseFields.PERMANENT_RELOCATION_VALUE;
import static org.ei.drishti.common.AllConstants.ANCFormFields.REFERENCE_DATE;
import static org.ei.drishti.common.AllConstants.DeliveryOutcomeFields.DID_MOTHER_SURVIVE;
import static org.ei.drishti.common.AllConstants.DeliveryOutcomeFields.DID_WOMAN_SURVIVE;
import static org.ei.drishti.common.AllConstants.EntityCloseFormFields.CLOSE_REASON_FIELD_NAME;
import static org.ei.drishti.common.AllConstants.Form.BOOLEAN_TRUE_VALUE;
import static org.ei.drishti.common.AllConstants.PNCVisitFormFields.VISIT_DATES_FIELD_NAME;
import static org.ei.drishti.common.AllConstants.PNCVisitFormFields.VISIT_DATE_FIELD_NAME;

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
        if (!allMothers.exists(submission.entityId())) {
            logger.warn("Failed to handle delivery outcome as there is no mother registered with id: " + submission.entityId());
            return;
        }

        if (BOOLEAN_TRUE_VALUE.equals(submission.getField(DID_WOMAN_SURVIVE)) || BOOLEAN_TRUE_VALUE.equals(submission.getField(DID_MOTHER_SURVIVE))) {
            pncSchedulesService.deliveryOutcome(submission.entityId(), submission.getField(REFERENCE_DATE));
        }
    }

    public void pncRegistrationOA(FormSubmission submission) {
        List<Mother> mothers = allMothers.findByEcCaseId(submission.entityId());
        if (mothers.size() <= 0) {
            logger.warn("Failed to handle PNC registration as there is no mother registered with ec id: " + submission.entityId());
            return;
        }

        Mother mother = mothers.get(0);
        mother.withAnm(submission.anmId());
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

        if (DEATH_OF_WOMAN_VALUE.equalsIgnoreCase(submission.getField(CLOSE_REASON_FIELD_NAME))
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
    }

    public void pncVisitHappened(FormSubmission submission) {
        Mother mother = allMothers.findByCaseId(submission.entityId());
        if (mother == null) {
            logger.warn("Found PNC visit without registered mother for entity ID: " + submission.entityId());
            return;
        }

        updatePNCVisitDatesOfMother(submission, mother);

        List<String> reportFields = reportFieldsDefinition.get(submission.formName());
        motherReportingService.pncVisitHappened(new SafeMap(submission.getFields(reportFields)));
    }

    private void updatePNCVisitDatesOfMother(FormSubmission submission, Mother mother) {
        String visitDate = submission.getField(VISIT_DATE_FIELD_NAME);
        String pncVisitDates = mother.getDetail(VISIT_DATES_FIELD_NAME) == null
                ? visitDate
                : mother.getDetail(VISIT_DATES_FIELD_NAME) + " " + visitDate;
        mother.details().put(VISIT_DATES_FIELD_NAME, pncVisitDates);
        allMothers.update(mother);
    }
}
