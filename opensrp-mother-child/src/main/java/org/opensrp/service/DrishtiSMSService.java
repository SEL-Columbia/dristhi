package org.opensrp.service;

import org.opensrp.common.audit.Auditor;
import org.motechproject.sms.api.service.SmsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import static java.text.MessageFormat.format;
import static org.opensrp.common.audit.AuditMessageType.SMS;

@Service
public class DrishtiSMSService {
    private final SmsService smsService;
    private final Auditor auditor;
    private boolean canSendSMSes;
    private static Logger logger = LoggerFactory.getLogger(DrishtiSMSService.class.toString());

    @Autowired
    public DrishtiSMSService(SmsService smsService, Auditor auditor, @Value("#{drishti['sms.can.be.sent']}") boolean defaultValueForWhetherSMSCanBeSent) {
        this.smsService = smsService;
        this.auditor = auditor;
        this.canSendSMSes = defaultValueForWhetherSMSCanBeSent;
    }

    public void sendSMS(String recipient, String message) {
        auditor.audit(SMS).with("recipient", recipient).with("message", message).with("smsIsSent", String.valueOf(canSendSMSes)).done();
        logger.info(format("SMS {2}sent to {0}: {1}", recipient, message, canSendSMSes ? "" : "NOT "));

        if (canSendSMSes) {
            smsService.sendSMS(recipient, message);
        }
    }

    public void canSendSMSes(boolean value) {
        this.canSendSMSes = value;
    }
}
