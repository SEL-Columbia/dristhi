package org.ei.drishti.service;

import org.ei.drishti.contract.ChildCloseRequest;
import org.ei.drishti.contract.ChildImmunizationUpdationRequest;
import org.ei.drishti.contract.ChildRegistrationInformation;
import org.ei.drishti.contract.ChildRegistrationRequest;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.testing.utils.BaseUnitTest;
import org.motechproject.util.DateUtil;

import static org.mockito.Mockito.*;
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

        pncService.registerNewChild(new ChildRegistrationRequest("Case X", "Child 1", "TC 1", currentTime.toDate(), "DEMO ANM", ""));

        verify(alertService).alertForChild("Case X", "Child 1", "DEMO ANM", "TC 1", "OPV 0", "due", currentTime.plusDays(2));
        verify(alertService).alertForChild("Case X", "Child 1", "DEMO ANM", "TC 1", "BCG", "due", currentTime.plusDays(2));
        verify(alertService).alertForChild("Case X", "Child 1", "DEMO ANM", "TC 1", "HEP B0", "due", currentTime.plusDays(2));
    }

    @Test
    public void shouldAddAlertsOnlyForMissingVaccinations() {
        assertMissingAlertsAdded("", "BCG", "OPV 0", "HEP B0");
        assertMissingAlertsAdded("bcg", "OPV 0", "HEP B0");
        assertMissingAlertsAdded("bcg opv0", "HEP B0");
        assertMissingAlertsAdded("bcg opv0 hepB0");

        assertMissingAlertsAdded("opv0 bcg hepB0");
        assertMissingAlertsAdded("opv0 bcg", "HEP B0");
        assertMissingAlertsAdded("opv0 bcg1", "BCG", "HEP B0");
    }

    @Test
    public void shouldRemoveAlertsForUpdatedImmunizations() throws Exception {
        assertDeletionOfAlertsForProvidedImmunizations("bcg opv0", "BCG", "OPV 0");
    }

    @Test
    public void shouldDeleteAllAlertsForChildCaseClose() {
        DateTime currentTime = DateUtil.now();
        mockCurrentDate(currentTime);

        AlertService alertServiceMock = mock(AlertService.class);
        PNCService pncService = new PNCService(service, alertServiceMock);

        pncService.closeChildCase(new ChildCloseRequest("Case X", "DEMO ANM"));

        verify(alertServiceMock).deleteAllAlertsForChild("Case X", "DEMO ANM");
    }

    private void assertDeletionOfAlertsForProvidedImmunizations(String providedImmunizations, String... expectedDeletedAlertsRaised) {
        DateTime currentTime = DateUtil.now();
        mockCurrentDate(currentTime);

        AlertService alertServiceMock = mock(AlertService.class);
        PNCService pncService = new PNCService(service, alertServiceMock);

        pncService.updateChildImmunization(new ChildImmunizationUpdationRequest("Case X", "DEMO ANM", providedImmunizations));

        for (String expectedAlert : expectedDeletedAlertsRaised) {
            verify(alertServiceMock).deleteAlertForVisitForChild("Case X", "DEMO ANM", expectedAlert);
        }
        verifyNoMoreInteractions(alertServiceMock);
    }

    private void assertMissingAlertsAdded(String providedImmunizations, String... expectedAlertsRaised) {
        DateTime currentTime = DateUtil.now();
        mockCurrentDate(currentTime);
        AlertService alertServiceMock = mock(AlertService.class);
        PNCService pncService = new PNCService(service, alertServiceMock);

        pncService.registerNewChild(new ChildRegistrationRequest("Case X", "Child 1", "TC 1", currentTime.toDate(), "DEMO ANM", providedImmunizations));

        for (String expectedAlert : expectedAlertsRaised) {
            verify(alertServiceMock).alertForChild("Case X", "Child 1", "DEMO ANM", "TC 1", expectedAlert, "due", currentTime.plusDays(2));
        }
        verifyNoMoreInteractions(alertServiceMock);
    }
}
