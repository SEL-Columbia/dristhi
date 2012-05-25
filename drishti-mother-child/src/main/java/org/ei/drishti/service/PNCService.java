package org.ei.drishti.service;

import org.ei.drishti.contract.ChildCloseRequest;
import org.ei.drishti.contract.ChildImmunizationUpdationRequest;
import org.ei.drishti.contract.ChildRegistrationRequest;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PNCService {
    private final ActionService actionService;

    @Autowired
    public PNCService(ActionService actionService) {
        this.actionService = actionService;
    }

    public void registerNewChild(ChildRegistrationRequest childRegistrationRequest) {
        alertForMissingImmunization(childRegistrationRequest, "opv0", "OPV 0");
        alertForMissingImmunization(childRegistrationRequest, "bcg", "BCG");
        alertForMissingImmunization(childRegistrationRequest, "hepB0", "HEP B0");
    }

    public void updateChildImmunization(ChildImmunizationUpdationRequest updationRequest) {
        alertForImmunizationProvided(updationRequest, "opv0", "OPV 0");
        alertForImmunizationProvided(updationRequest, "bcg", "BCG");
        alertForImmunizationProvided(updationRequest, "hepB0", "HEP B0");
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

        DateTime dueDate = new LocalDate(childRegistrationRequest.dateOfBirth()).plusDays(2).toDateTime(DateUtil.now().toLocalTime());
        actionService.alertForChild(childRegistrationRequest.caseId(), childRegistrationRequest.name(),
                childRegistrationRequest.village(), childRegistrationRequest.anmIdentifier(), childRegistrationRequest.thaayiCardNumber(), visitCodeIfNotProvided, "due", dueDate);
    }

    public void closeChildCase(ChildCloseRequest childCloseRequest) {
        actionService.deleteAllAlertsForChild(childCloseRequest.caseId(), childCloseRequest.anmIdentifier());
    }
}
