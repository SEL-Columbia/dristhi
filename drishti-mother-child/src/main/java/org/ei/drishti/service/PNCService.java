package org.ei.drishti.service;

import org.ei.drishti.contract.ChildRegistrationInformation;
import org.motechproject.sms.api.service.SmsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static ch.lambdaj.Lambda.join;
import static java.text.MessageFormat.format;

@Service
public class PNCService {
    private final SmsService smsService;

    @Autowired
    public PNCService(SmsService smsService) {
        this.smsService = smsService;
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
}
