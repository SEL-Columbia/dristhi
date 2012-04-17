package org.ei.drishti.service;

import org.ei.drishti.contract.ChildRegistrationInformation;
import org.ei.drishti.contract.ChildRegistrationRequest;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.testing.utils.BaseUnitTest;
import org.motechproject.util.DateUtil;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.MockitoAnnotations.initMocks;

public class PNCServiceTest extends BaseUnitTest {
    @Mock
    DrishtiSMSService service;
    @Mock
    AlertService alertService;
    private PNCService pncService;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        pncService = new PNCService(service, alertService);
    }

    @Test
    public void shouldSendAnSMSWithMissingVaccinationDataDuringChildRegistration() {
        pncService.registerChild(new ChildRegistrationInformation("Theresa", "9845700000", "bcg_no", "opv0_yes", "hepb1_no"));

        verify(service).sendSMS("9845700000", "Dear ANM, please provide BCG, Hepatitis B for child of mother, Theresa.");
    }

    @Test
    public void shouldSendNoSMSIfThereAreNoMissingVaccinations() {
        pncService.registerChild(new ChildRegistrationInformation("Theresa", "9845700000", "BCG_yes", "opv0_yes", "hepb1_yes"));

        verifyZeroInteractions(service);
    }

    @Test
    public void shouldAddAlertsForVaccinationsForChildren() {
        DateTime currentTime = DateUtil.now();
        mockCurrentDate(currentTime);

        pncService.registerNewChild(new ChildRegistrationRequest("Child 1", "TC 1", currentTime.toDate(), "DEMO ANM"));

        verify(alertService).alertForChild("Child 1", "DEMO ANM", "TC 1", "OPV 1", "due", currentTime.plusDays(2));
        verify(alertService).alertForChild("Child 1", "DEMO ANM", "TC 1", "DPT 1", "due", currentTime.plusDays(2));
        verify(alertService).alertForChild("Child 1", "DEMO ANM", "TC 1", "BCG 1", "due", currentTime.plusDays(2));
    }
}
