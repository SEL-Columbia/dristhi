package org.ei.drishti.service;

import org.ei.drishti.contract.ChildCloseRequest;
import org.ei.drishti.contract.ChildImmunizationUpdationRequest;
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
    ActionService actionService;
    private PNCService pncService;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        pncService = new PNCService(actionService);
    }

    @Test
    public void shouldAddAlertsForVaccinationsForChildren() {
        DateTime currentTime = DateUtil.now();
        mockCurrentDate(currentTime);

        pncService.registerNewChild(new ChildRegistrationRequest("Case X", "Child 1", "bherya", "TC 1", currentTime.toDate(), "DEMO ANM", ""));

        verify(actionService).alertForChild("Case X", "Child 1", "bherya", "DEMO ANM", "TC 1", "OPV 0", "due", currentTime.plusDays(2));
        verify(actionService).alertForChild("Case X", "Child 1", "bherya", "DEMO ANM", "TC 1", "BCG", "due", currentTime.plusDays(2));
        verify(actionService).alertForChild("Case X", "Child 1", "bherya", "DEMO ANM", "TC 1", "HEP B0", "due", currentTime.plusDays(2));
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

        ActionService alertServiceMock = mock(ActionService.class);
        PNCService pncService = new PNCService(alertServiceMock);

        pncService.closeChildCase(new ChildCloseRequest("Case X", "DEMO ANM"));

        verify(alertServiceMock).deleteAllAlertsForChild("Case X", "DEMO ANM");
    }

    private void assertDeletionOfAlertsForProvidedImmunizations(String providedImmunizations, String... expectedDeletedAlertsRaised) {
        DateTime currentTime = DateUtil.now();
        mockCurrentDate(currentTime);

        ActionService alertServiceMock = mock(ActionService.class);
        PNCService pncService = new PNCService(alertServiceMock);

        pncService.updateChildImmunization(new ChildImmunizationUpdationRequest("Case X", "DEMO ANM", providedImmunizations));

        for (String expectedAlert : expectedDeletedAlertsRaised) {
            verify(alertServiceMock).deleteAlertForVisitForChild("Case X", "DEMO ANM", expectedAlert);
        }
        verifyNoMoreInteractions(alertServiceMock);
    }

    private void assertMissingAlertsAdded(String providedImmunizations, String... expectedAlertsRaised) {
        DateTime currentTime = DateUtil.now();
        mockCurrentDate(currentTime);
        ActionService alertServiceMock = mock(ActionService.class);
        PNCService pncService = new PNCService(alertServiceMock);

        pncService.registerNewChild(new ChildRegistrationRequest("Case X", "Child 1", "bherya", "TC 1", currentTime.toDate(), "DEMO ANM", providedImmunizations));

        for (String expectedAlert : expectedAlertsRaised) {
            verify(alertServiceMock).alertForChild("Case X", "Child 1", "bherya", "DEMO ANM", "TC 1", expectedAlert, "due", currentTime.plusDays(2));
        }
        verifyNoMoreInteractions(alertServiceMock);
    }
}
