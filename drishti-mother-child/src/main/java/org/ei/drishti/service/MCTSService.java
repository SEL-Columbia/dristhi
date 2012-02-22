package org.ei.drishti.service;

import org.joda.time.LocalDate;
import org.motechproject.sms.api.service.SmsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MCTSService {
    private final SmsService smsService;

    @Autowired
    public MCTSService(SmsService smsService) {
        this.smsService = smsService;
    }

    public void send(MCTSServiceCode typeOfService, String thaayiCardNumber, LocalDate date) {
        throw new RuntimeException("Unsupported.");
    }
}
