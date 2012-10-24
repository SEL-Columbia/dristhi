package org.ei.drishti.service;

import org.ei.drishti.contract.AnteNatalCareOutcomeInformation;
import org.ei.drishti.contract.ChildCloseRequest;
import org.ei.drishti.contract.ChildImmunizationUpdationRequest;
import org.ei.drishti.contract.PostNatalCareInformation;
import org.ei.drishti.domain.Child;
import org.ei.drishti.domain.Mother;
import org.ei.drishti.dto.BeneficiaryType;
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

import static java.text.MessageFormat.format;
import static org.ei.drishti.dto.AlertPriority.normal;
import static org.ei.drishti.dto.BeneficiaryType.child;
import static org.ei.drishti.dto.BeneficiaryType.mother;

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
            allChildren.register(new Child(request.caseId(), mother.ecCaseId(), request.motherCaseId(), mother.thaayiCardNo(), request.childName(),
                    request.immunizationsProvided(), request.gender()).withAnm(request.anmIdentifier()).withDetails(extraData.get("details")));

            actionService.registerChildBirth(request.caseId(), request.anmIdentifier(), mother.caseId(), mother.thaayiCardNo(), request.dateOfBirth(), request.gender(), extraData.get("details"));

            alertForMissingImmunization(request, "opv_0", "OPV 0");
            alertForMissingImmunization(request, "bcg", "BCG");
            alertForMissingImmunization(request, "hepb_0", "HEP B0");

            pncSchedulesService.enrollChild(request);
        }
    }

    public void pncVisitHappened(PostNatalCareInformation info, Map<String, Map<String, String>> extraData) {
        if (!allMothers.motherExists(info.caseId())) {
            logger.warn("Found PNC visit without registered mother for case ID: " + info.caseId());
            return;
        }

        Child child = allChildren.findByMotherCaseId(info.caseId());
        Map<String, String> details = extraData.get("details");

        Mother updatedMother = allMothers.updateDetails(info.caseId(), details);
        actionService.pncVisitHappened(mother, info.caseId(), info.anmIdentifier(), info.visitDate(), info.visitNumber(), info.numberOfIFATabletsProvided(), updatedMother.details());

        if (child != null) {
            Child updatedChild = allChildren.updateDetails(child.caseId(), details);
            actionService.pncVisitHappened(BeneficiaryType.child, child.caseId(), info.anmIdentifier(), info.visitDate(), info.visitNumber(), info.numberOfIFATabletsProvided(), updatedChild.details());
        }
    }

    public void updateChildImmunization(ChildImmunizationUpdationRequest updationRequest, Map<String, Map<String, String>> extraData) {
        if (!allChildren.childExists(updationRequest.caseId())) {
            logger.warn("Found immunization update without registered child for case ID: " + updationRequest.caseId());
            return;
        }

        Child updatedChild = allChildren.updateDetails(updationRequest.caseId(), extraData.get("details"));
        actionService.updateImmunizations(updationRequest.caseId(), updationRequest.anmIdentifier(), updatedChild.details(), updationRequest.immunizationsProvided(),
                updationRequest.immunizationsProvidedDate(), updationRequest.vitaminADose());

        childReportingService.updateChildImmunization(updationRequest, new SafeMap(extraData.get("reporting")));

        alertForImmunizationProvided(updationRequest, "opv_0", "OPV 0");
        alertForImmunizationProvided(updationRequest, "bcg", "BCG");
        alertForImmunizationProvided(updationRequest, "hepb_0", "HEP B0");

        pncSchedulesService.updateEnrollments(updationRequest);
    }

    public void closeChildCase(ChildCloseRequest childCloseRequest) {
        actionService.deleteAllAlertsForChild(childCloseRequest.caseId(), childCloseRequest.anmIdentifier());
        actionService.closeChild(childCloseRequest.caseId(),childCloseRequest.anmIdentifier());

        pncSchedulesService.unenrollChild(childCloseRequest.caseId());
    }

    private void alertForImmunizationProvided(ChildImmunizationUpdationRequest updationRequest, String checkForThisImmunization, String visitCodeIfNotProvided) {
        if (updationRequest.isImmunizationProvided(checkForThisImmunization)) {
            actionService.markAlertAsClosedForVisitForChild(updationRequest.caseId(), updationRequest.anmIdentifier(), visitCodeIfNotProvided, updationRequest.immunizationsProvidedDate().toString());
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
