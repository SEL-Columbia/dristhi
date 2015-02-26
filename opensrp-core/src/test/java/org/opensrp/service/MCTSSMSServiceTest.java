package org.opensrp.service;

import org.opensrp.domain.MCTSServiceCode;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.util.DateUtil;

import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

import org.opensrp.service.DrishtiSMSService;
import org.opensrp.service.MCTSSMSService;

public class MCTSSMSServiceTest {
    @Mock
    DrishtiSMSService smsService;
    private MCTSSMSService mctsSMSService;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        mctsSMSService = new MCTSSMSService(smsService, "9986048731");
    }

    @Test
    public void shouldSendSMSBasedOnCodeForANC1() {
        mctsSMSService.send(MCTSServiceCode.ANC1, "1234567", DateUtil.newDate(2012, 3, 21));

        verify(smsService).sendSMS("9986048731", "ANMPW 1234567 ANC1 210312");
    }

    @Test
    public void shouldSendSMSWithZeroPaddedDateWhenDateIsBefore10th() {
        mctsSMSService.send(MCTSServiceCode.ANC1, "1234567", DateUtil.newDate(2012, 3, 8));

        verify(smsService).sendSMS("9986048731", "ANMPW 1234567 ANC1 080312");
    }

    @Test
    public void shouldSendSMSWithGivenText() {
        mctsSMSService.send("SMS Text");

        verify(smsService).sendSMS("9986048731", "SMS Text");
    }
}
