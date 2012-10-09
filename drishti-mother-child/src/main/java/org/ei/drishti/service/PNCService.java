package org.ei.drishti.service;

import org.ei.drishti.contract.AnteNatalCareOutcomeInformation;
import org.ei.drishti.contract.ChildCloseRequest;
import org.ei.drishti.contract.ChildImmunizationUpdationRequest;
import org.ei.drishti.domain.Child;
import org.ei.drishti.domain.Mother;
import org.ei.drishti.repository.AllChildren;
import org.ei.drishti.repository.AllMothers;
import org.ei.drishti.service.reporting.ChildReportingService;
import org.ei.drishti.util.SafeMap;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.motechproject.util.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

import static java.lang.String.format;
import static org.ei.drishti.dto.AlertPriority.normal;
import static org.ei.drishti.dto.BeneficiaryType.child;

@Service
public class PNCService {
    private static Logger logger = LoggerFactory.getLogger(PNCService.class.toString());

    private ActionService actionService;
    private PNCSchedulesService pncSchedulesService;
    private AllMothers allMothers;
    private AllChildren allChildren;
    private ChildReportingService childReportingService;

    @Autowired
    public PNCService(ActionService actionService, PNCSchedulesService pncSchedulesService, AllMothers allMothers, AllChildren allChildren, ChildReportingService childReportingService) {
        this.actionService = actionService;
        this.pncSchedulesService = pncSchedulesService;
        this.allMothers = allMothers;
        this.allChildren = allChildren;
        this.childReportingService = childReportingService;
    }

    public void registerChild(AnteNatalCareOutcomeInformation request, Map<String, Map<String, String>> extraData) {
        Mother mother = allMothers.findByCaseId(request.motherCaseId());

        if (mother == null) {
            logger.warn(format("Failed to register child as there is no mother registered with case ID: {0} for child case ID: {1} for ANM: {2}", request.motherCaseId(), request.caseId(), request.anmIdentifier()));
            return;
        }

        if ("live_birth".equals(request.pregnancyOutcome())) {
            allChildren.register(new Child(request.caseId(), request.motherCaseId(), mother.thaayiCardNo(), request.childName(),
                    request.immunizationsProvided(), request.gender()).withAnm(request.anmIdentifier()));

            actionService.registerChildBirth(request.caseId(), request.anmIdentifier(), mother.caseId(), mother.thaayiCardNo(), request.dateOfBirth(), request.gender(), extraData.get("details"));

            alertForMissingImmunization(request, "opv0", "OPV 0");
            alertForMissingImmunization(request, "bcg", "BCG");
            alertForMissingImmunization(request, "hepB0", "HEP B0");

            pncSchedulesService.enrollChild(request);
        }
    }

    public void updateChildImmunization(ChildImmunizationUpdationRequest updationRequest, SafeMap reportingData) {
        childReportingService.updateChildImmunization(updationRequest, reportingData);

        alertForImmunizationProvided(updationRequest, "opv0", "OPV 0");
        alertForImmunizationProvided(updationRequest, "bcg", "BCG");
        alertForImmunizationProvided(updationRequest, "hepB0", "HEP B0");

        pncSchedulesService.updateEnrollments(updationRequest);
    }

    public void closeChildCase(ChildCloseRequest childCloseRequest) {
        actionService.deleteAllAlertsForChild(childCloseRequest.caseId(), childCloseRequest.anmIdentifier());

        pncSchedulesService.unenrollChild(childCloseRequest.caseId());
    }

    private void alertForImmunizationProvided(ChildImmunizationUpdationRequest updationRequest, String checkForThisImmunization, String visitCodeIfNotProvided) {
        if (updationRequest.isImmunizationProvided(checkForThisImmunization)) {
            actionService.markAlertAsClosedForVisitForChild(updationRequest.caseId(), updationRequest.anmIdentifier(), visitCodeIfNotProvided, updationRequest.visitDate().toString());
        }
    }

    private void alertForMissingImmunization(AnteNatalCareOutcomeInformation information, String checkForThisImmunization, String visitCodeIfNotProvided) {
        if (information.isImmunizationProvided(checkForThisImmunization)) {
            return;
        }

        LocalDate dueDateLocal = information.dateOfBirth().plusDays(2);
        LocalTime currentTime = DateUtil.now().toLocalTime();
        DateTime dueDate = dueDateLocal.toDateTime(currentTime);
        actionService.alertForBeneficiary(child, information.caseId(), visitCodeIfNotProvided, normal, dueDate, dueDateLocal.plusWeeks(1).toDateTime(currentTime));
    }
}
