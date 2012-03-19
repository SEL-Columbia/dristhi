package org.ei.drishti.service;

import org.ei.drishti.contract.ChildRegistrationInformation;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.MockitoAnnotations.initMocks;

public class PNCServiceTest {
    @Mock
    DrishtiSMSService service;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
    }

    @Test
    public void shouldSendAnSMSWithMissingVaccinationDataDuringChildRegistration() {
        PNCService pncService = new PNCService(service);

        pncService.registerChild(new ChildRegistrationInformation("Theresa", "9845700000", "bcg_no", "opv0_yes", "hepb1_no"));

        verify(service).sendSMS("9845700000", "Dear ANM, please provide BCG, Hepatitis B for child of mother, Theresa.");
    }

    @Test
    public void shouldSendNoSMSIfThereAreNoMissingVaccinations() {
        PNCService pncService = new PNCService(service);

        pncService.registerChild(new ChildRegistrationInformation("Theresa", "9845700000", "BCG_yes", "opv0_yes", "hepb1_yes"));

        verifyZeroInteractions(service);
    }
}
