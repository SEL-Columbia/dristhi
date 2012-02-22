package org.ei.drishti.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.sms.api.service.SmsService;
import org.motechproject.util.DateUtil;

import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class MCTSServiceTest {
    @Mock
    SmsService smsService;
    private MCTSService mctsService;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        mctsService = new MCTSService(smsService);
    }

    @Test
    public void shouldSendSMSBasedOnCodeForANC1() {
        mctsService.send(MCTSServiceCode.ANC_1, "1234567", DateUtil.newDate(2012, 3, 21));

        verify(smsService).sendSMS("9243355223", "ANMPW 1234567 ANC1 210312");
    }

    @Test
    public void shouldSendSMSWithZeroPaddedDateWhenDateIsBefore10th() {
        mctsService.send(MCTSServiceCode.ANC_1, "1234567", DateUtil.newDate(2012, 3, 8));

        verify(smsService).sendSMS("9243355223", "ANMPW 1234567 ANC1 080312");
    }
}
