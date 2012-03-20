package org.ei.drishti.service;

import org.ei.drishti.common.audit.Auditor;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.sms.api.service.SmsService;

import static java.text.MessageFormat.format;
import static org.ei.drishti.common.audit.AuditMessageType.SMS;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class DrishtiSMSServiceTest {
    @Mock
    private SmsService smsService;
    @Mock
    private Auditor auditor;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
    }

    @Test
    public void shouldNotSendAnSMSIfTheFlagIsTurnedOff() {
        DrishtiSMSService drishtiSMSService = new DrishtiSMSService(smsService, auditor, true);
        drishtiSMSService.canSendSMSes(false);

        assertMessageWillNotBeSent(drishtiSMSService);
    }

    @Test
    public void shouldNotSendAnSMSIfTheFlagIsTurnedOn() {
        DrishtiSMSService drishtiSMSService = new DrishtiSMSService(smsService, auditor, true);
        drishtiSMSService.canSendSMSes(true);

        assertMessageWillBeSent(drishtiSMSService);
    }

    @Test
    public void shouldSendSMSIfDefaultValueIsTrueAndHasNotBeenChanged() {
        DrishtiSMSService drishtiSMSService = new DrishtiSMSService(smsService, auditor, true);

        assertMessageWillBeSent(drishtiSMSService);
    }

    @Test
    public void shouldNotSendSMSIfDefaultValueIsFalseAndHasNotBeenChanged() {
        DrishtiSMSService drishtiSMSService = new DrishtiSMSService(smsService, auditor, false);

        assertMessageWillNotBeSent(drishtiSMSService);
    }

    @Test
    public void shouldAuditTheSMSMessageThatWasSent() {
        DrishtiSMSService drishtiSMSService = new DrishtiSMSService(smsService, auditor, true);
        drishtiSMSService.sendSMS("9845700000", "Some message");

        verify(auditor).audit(SMS, format("SMS sent to {0}: {1}", "9845700000", "Some message"), "9845700000", "Some message");
    }

    @Test
    public void shouldAuditEvenWhenTheSMSMessageWasNotSent() {
        DrishtiSMSService drishtiSMSService = new DrishtiSMSService(smsService, auditor, false);
        drishtiSMSService.sendSMS("9845700000", "Some message");

        verify(auditor).audit(SMS, format("SMS NOT sent to {0}: {1}", "9845700000", "Some message"), "9845700000", "Some message");
    }

    private void assertMessageWillBeSent(DrishtiSMSService drishtiSMSService) {
        drishtiSMSService.sendSMS("9845700000", "Some message");
        verify(smsService, times(1)).sendSMS("9845700000", "Some message");
    }

    private void assertMessageWillNotBeSent(DrishtiSMSService drishtiSMSService) {
        drishtiSMSService.sendSMS("9845700000", "Some message");
        verify(smsService, times(0)).sendSMS("9845700000", "Some message");
    }
}
