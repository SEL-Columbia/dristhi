package org.ei.drishti.service;

import org.motechproject.sms.api.service.SmsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DrishtiSMSService {
    private final SmsService smsService;
    private boolean canSendSMSes;

    @Autowired
    public DrishtiSMSService(SmsService smsService) {
        this.smsService = smsService;
    }

    public void canSendSMSes(boolean value) {
        this.canSendSMSes = value;
    }

    public void sendSMS(String recipient, String message) {
        if (canSendSMSes) {
            smsService.sendSMS(recipient, message);
        }
    }
}
