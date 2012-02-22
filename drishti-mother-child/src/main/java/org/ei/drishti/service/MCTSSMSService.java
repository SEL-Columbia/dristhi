package org.ei.drishti.service;

import org.joda.time.LocalDate;
import org.motechproject.sms.api.service.SmsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MCTSSMSService {
    private final SmsService smsService;
    private static final String MCTS_PHONE_NUMBER = "9243355223";

    @Autowired
    public MCTSSMSService(SmsService smsService) {
        this.smsService = smsService;
    }

    public void send(MCTSServiceCode typeOfService, String thaayiCardNumber, LocalDate date) {
        smsService.sendSMS(MCTS_PHONE_NUMBER, typeOfService.messageFor(thaayiCardNumber, date));
    }
}
