package org.ei.drishti.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.sms.api.service.SmsService;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class DrishtiSMSServiceTest {
    @Mock
    private SmsService smsService;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
    }

    @Test
    public void shouldNotSendAnSMSIfTheFlagIsTurnedOff() {
        DrishtiSMSService drishtiSMSService = new DrishtiSMSService(smsService, true);
        drishtiSMSService.canSendSMSes(false);

        assertMessageWillNotBeSent(drishtiSMSService);
    }

    @Test
    public void shouldNotSendAnSMSIfTheFlagIsTurnedOn() {
        DrishtiSMSService drishtiSMSService = new DrishtiSMSService(smsService, true);
        drishtiSMSService.canSendSMSes(true);

        assertMessageWillBeSent(drishtiSMSService);
    }

    @Test
    public void shouldSendSMSIfDefaultValueIsTrueAndHasNotBeenChanged() {
        DrishtiSMSService drishtiSMSService = new DrishtiSMSService(smsService, true);

        assertMessageWillBeSent(drishtiSMSService);
    }

    @Test
    public void shouldNotSendSMSIfDefaultValueIsFalseAndHasNotBeenChanged() {
        DrishtiSMSService drishtiSMSService = new DrishtiSMSService(smsService, false);

        assertMessageWillNotBeSent(drishtiSMSService);
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
