package org.ei.drishti.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.sms.api.service.SmsService;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
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
        DrishtiSMSService drishtiSMSService = new DrishtiSMSService(smsService);

        drishtiSMSService.canSendSMSes(false);

        drishtiSMSService.sendSMS("9845700000", "Some message");

        verifyZeroInteractions(smsService);
    }

    @Test
    public void shouldNotSendAnSMSIfTheFlagIsTurnedOn() {
        DrishtiSMSService drishtiSMSService = new DrishtiSMSService(smsService);

        drishtiSMSService.canSendSMSes(true);

        drishtiSMSService.sendSMS("9845700000", "Some message");

        verify(smsService).sendSMS("9845700000", "Some message");
    }
}
