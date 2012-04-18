package org.ei.drishti.service;

import org.ei.drishti.contract.ChildCloseRequest;
import org.ei.drishti.contract.ChildImmunizationUpdationRequest;
import org.ei.drishti.contract.ChildRegistrationInformation;
import org.ei.drishti.contract.ChildRegistrationRequest;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static ch.lambdaj.Lambda.join;
import static java.text.MessageFormat.format;

@Service
public class PNCService {
    private final DrishtiSMSService smsService;
    private final AlertService alertService;

    @Autowired
    public PNCService(DrishtiSMSService smsService, AlertService alertService) {
        this.smsService = smsService;
        this.alertService = alertService;
    }

    public void registerChild(ChildRegistrationInformation childInformation) {
        List<String> vaccinationsMissed = childInformation.missingVaccinations();

        if (vaccinationsMissed.isEmpty()) {
            return;
        }

        String missingVaccinations = join(vaccinationsMissed, ", ");
        String format = format("Dear ANM, please provide {0} for child of mother, {1}.", missingVaccinations, childInformation.mother());
        smsService.sendSMS(childInformation.anmPhoneNumber(), format);
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
            alertService.deleteAlertForVisitForChild(updationRequest.caseId(), updationRequest.anmIdentifier(), visitCodeIfNotProvided);
        }
    }

    private void alertForMissingImmunization(ChildRegistrationRequest childRegistrationRequest, String checkForThisImmunization, String visitCodeIfNotProvided) {
        if (childRegistrationRequest.isImmunizationProvided(checkForThisImmunization)) {
            return;
        }

        DateTime dueDate = new LocalDate(childRegistrationRequest.dateOfBirth()).plusDays(2).toDateTime(DateUtil.now().toLocalTime());
        alertService.alertForChild(childRegistrationRequest.caseId(), childRegistrationRequest.name(),
                childRegistrationRequest.anmIdentifier(), childRegistrationRequest.thaayiCardNumber(), visitCodeIfNotProvided, "due", dueDate);
    }

    public void closeChildCase(ChildCloseRequest childCloseRequest) {
        alertService.deleteAllAlertsForChild(childCloseRequest.caseId(), childCloseRequest.anmIdentifier());
    }
}
