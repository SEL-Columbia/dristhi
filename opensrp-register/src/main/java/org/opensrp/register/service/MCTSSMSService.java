package org.opensrp.register.service;

import org.opensrp.register.domain.MCTSServiceCode;
import org.opensrp.service.DrishtiSMSService;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class MCTSSMSService {
    private final DrishtiSMSService smsService;
    private String mctsPhoneNumber;

    @Autowired
    public MCTSSMSService(DrishtiSMSService smsService, @Value("#{opensrp['mcts.phone.number']}") String mctsPhoneNumber) {
        this.smsService = smsService;
        this.mctsPhoneNumber = mctsPhoneNumber;
    }

    public void send(MCTSServiceCode typeOfService, String thayiCardNumber, LocalDate date) {
        smsService.sendSMS(mctsPhoneNumber, typeOfService.messageFor(thayiCardNumber, date));
    }

    public void send(String smsText) {
        smsService.sendSMS(mctsPhoneNumber, smsText);
    }

}
