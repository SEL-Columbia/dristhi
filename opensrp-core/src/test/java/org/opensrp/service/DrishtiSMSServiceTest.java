package org.opensrp.service;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.opensrp.common.audit.AuditMessageType.SMS;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.sms.api.service.SmsService;
import org.opensrp.common.audit.Auditor;

public class DrishtiSMSServiceTest {
    @Mock
    private SmsService smsService;
    @Mock
    private Auditor auditor;
    @Mock
    Auditor.AuditMessageBuilder messageBuilder;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        when(auditor.audit(SMS)).thenReturn(messageBuilder);
        when(messageBuilder.with(any(String.class), any(String.class))).thenReturn(messageBuilder);
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

        verify(messageBuilder).with("recipient", "9845700000");
        verify(messageBuilder).with("message", "Some message");
        verify(messageBuilder).with("smsIsSent", "true");
    }

    @Test
    public void shouldAuditEvenWhenTheSMSMessageWasNotSent() {
        DrishtiSMSService drishtiSMSService = new DrishtiSMSService(smsService, auditor, false);
        drishtiSMSService.sendSMS("9845700000", "Some message");

        verify(messageBuilder).with("recipient", "9845700000");
        verify(messageBuilder).with("message", "Some message");
        verify(messageBuilder).with("smsIsSent", "false");
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
