package org.ei.drishti.service;

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

    private void alertForMissingImmunization(ChildRegistrationRequest childRegistrationRequest, String checkForThisImmunization, String visitCodeIfNotProvided) {
        DateTime dueDate = new LocalDate(childRegistrationRequest.dateOfBirth()).plusDays(2).toDateTime(DateUtil.now().toLocalTime());
        if (!(" " + childRegistrationRequest.immunizationsProvided() + " ").contains(" " + checkForThisImmunization + " ")) {
            alertService.alertForChild(childRegistrationRequest.name(), childRegistrationRequest.anmIdentifier(), childRegistrationRequest.thaayiCardNumber(), visitCodeIfNotProvided, "due", dueDate);
        }
    }
}
