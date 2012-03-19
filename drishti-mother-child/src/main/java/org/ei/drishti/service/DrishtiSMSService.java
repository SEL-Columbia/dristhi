package org.ei.drishti.service;

import org.motechproject.sms.api.service.SmsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import static java.text.MessageFormat.format;

@Service
public class DrishtiSMSService {
    private final SmsService smsService;
    private boolean canSendSMSes;
    private static Logger logger = LoggerFactory.getLogger(DrishtiSMSService.class.toString());

    @Autowired
    public DrishtiSMSService(SmsService smsService, @Value("#{drishti['sms.can.be.sent']}") boolean defaultValueForWhetherSMSCanBeSent) {
        this.smsService = smsService;
        this.canSendSMSes = defaultValueForWhetherSMSCanBeSent;
    }

    public void sendSMS(String recipient, String message) {
        if (canSendSMSes) {
            smsService.sendSMS(recipient, message);
        }
        else {
            logger.info(format("SMS not sent: Recipient: {0}, Message: {1}", recipient, message));
        }
    }

    public void canSendSMSes(boolean value) {
        this.canSendSMSes = value;
    }
}
