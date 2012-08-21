package org.ei.drishti.service;

import org.ei.drishti.contract.ChildCloseRequest;
import org.ei.drishti.contract.ChildImmunizationUpdationRequest;
import org.ei.drishti.contract.ChildRegistrationRequest;
import org.ei.drishti.domain.Child;
import org.ei.drishti.repository.AllChildren;
import org.ei.drishti.util.SafeMap;
import org.ei.drishti.service.reporting.ChildReportingService;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PNCService {
    private final ActionService actionService;
    private final PNCSchedulesService pncSchedulesService;
    private final AllChildren allChildren;
    private final ChildReportingService childReportingService;

    @Autowired
    public PNCService(ActionService actionService, PNCSchedulesService pncSchedulesService, ChildReportingService childReportingService, AllChildren allChildren) {
        this.actionService = actionService;
        this.pncSchedulesService = pncSchedulesService;
        this.allChildren = allChildren;
        this.childReportingService = childReportingService;
    }

    public void registerChild(ChildRegistrationRequest request) {
        allChildren.register(new Child(request.caseId(), request.thaayiCardNumber(), request.name(), request.immunizationsProvided(), request.gender())
                .withAnm(request.anmIdentifier()).withLocation(request.village(), request.subCenter(), request.phc()));

        alertForMissingImmunization(request, "opv0", "OPV 0");
        alertForMissingImmunization(request, "bcg", "BCG");
        alertForMissingImmunization(request, "hepB0", "HEP B0");

        pncSchedulesService.enrollChild(request);
        actionService.registerChildBirth(request.caseId(), request.anmIdentifier(), request.thaayiCardNumber(), request.dateOfBirth(), request.gender());
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
            actionService.deleteAlertForVisitForChild(updationRequest.caseId(), updationRequest.anmIdentifier(), visitCodeIfNotProvided);
        }
    }

    private void alertForMissingImmunization(ChildRegistrationRequest childRegistrationRequest, String checkForThisImmunization, String visitCodeIfNotProvided) {
        if (childRegistrationRequest.isImmunizationProvided(checkForThisImmunization)) {
            return;
        }

        LocalDate dueDateLocal = childRegistrationRequest.dateOfBirth().plusDays(2);
        LocalTime currentTime = DateUtil.now().toLocalTime();
        DateTime dueDate = dueDateLocal.toDateTime(currentTime);
        actionService.alertForBeneficiary(childRegistrationRequest.caseId(), "child", visitCodeIfNotProvided, "normal", dueDate, dueDateLocal.plusWeeks(1).toDateTime(currentTime));
    }
}
