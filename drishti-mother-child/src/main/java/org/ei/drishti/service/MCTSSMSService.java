package org.ei.drishti.service;

import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MCTSSMSService {
    private final DrishtiSMSService smsService;
    private static final String MCTS_PHONE_NUMBER = "9243355223";

    @Autowired
    public MCTSSMSService(DrishtiSMSService smsService) {
        this.smsService = smsService;
    }

    public void send(MCTSServiceCode typeOfService, String thaayiCardNumber, LocalDate date) {
        smsService.sendSMS(MCTS_PHONE_NUMBER, typeOfService.messageFor(thaayiCardNumber, date));
    }
}
