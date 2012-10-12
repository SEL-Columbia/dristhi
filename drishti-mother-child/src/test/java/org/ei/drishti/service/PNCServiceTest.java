package org.ei.drishti.service;

import org.ei.drishti.contract.AnteNatalCareOutcomeInformation;
import org.ei.drishti.contract.ChildCloseRequest;
import org.ei.drishti.contract.ChildImmunizationUpdationRequest;
import org.ei.drishti.contract.PostNatalCareInformation;
import org.ei.drishti.domain.Child;
import org.ei.drishti.domain.Mother;
import org.ei.drishti.dto.BeneficiaryType;
import org.ei.drishti.repository.AllChildren;
import org.ei.drishti.repository.AllMothers;
import org.ei.drishti.service.reporting.ChildReportingService;
import org.ei.drishti.util.SafeMap;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.motechproject.testing.utils.BaseUnitTest;
import org.motechproject.util.DateUtil;

import java.util.Arrays;
import java.util.Map;

import static org.ei.drishti.dto.AlertPriority.normal;
import static org.ei.drishti.dto.BeneficiaryType.child;
import static org.ei.drishti.dto.BeneficiaryType.mother;
import static org.ei.drishti.util.EasyMap.create;
import static org.ei.drishti.util.EasyMap.mapOf;
import static org.ei.drishti.util.Matcher.objectWithSameFieldsAs;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class PNCServiceTest extends BaseUnitTest {
    @Mock
    ActionService actionService;
    @Mock
    private PNCSchedulesService pncSchedulesService;
    @Mock
    private AllMothers allMothers;
    @Mock
    private AllChildren allChildren;

    @Mock
    private ChildReportingService reportingService;
    private PNCService service;

    private Map<String, Map<String, String>> EXTRA_DATA = mapOf("details", mapOf("someKey", "someValue"));

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        service = new PNCService(actionService, pncSchedulesService, allMothers, allChildren, reportingService);
    }

    @Test
    public void shouldAddAlertsForVaccinationsForChildren() {
        DateTime currentTime = DateUtil.now();
        mockCurrentDate(currentTime);
        when(allMothers.findByCaseId("MOTHER-CASE-1")).thenReturn(new Mother("MOTHER-CASE-1", "TC1", "Theresa"));

        service.registerChild(new AnteNatalCareOutcomeInformation("Case X", "MOTHER-CASE-1", "ANM X", "Child 1", "female", "", "live_birth", LocalDate.now().toString()), EXTRA_DATA);

        verify(actionService).alertForBeneficiary(child, "Case X", "OPV 0", normal, currentTime.plusDays(2), currentTime.plusDays(2).plusWeeks(1));
        verify(actionService).alertForBeneficiary(child, "Case X", "BCG", normal, currentTime.plusDays(2), currentTime.plusDays(2).plusWeeks(1));
        verify(actionService).alertForBeneficiary(child, "Case X", "HEP B0", normal, currentTime.plusDays(2), currentTime.plusDays(2).plusWeeks(1));
    }

    @Test
    public void shouldEnrollChildIntoSchedulesDuringRegistration() {
        DateTime currentTime = DateUtil.now();
        mockCurrentDate(currentTime);
        when(allMothers.findByCaseId("MOTHER-CASE-1")).thenReturn(new Mother("MOTHER-CASE-1", "TC1", "Theresa"));
        AnteNatalCareOutcomeInformation outcomeInformation = new AnteNatalCareOutcomeInformation("Case X", "MOTHER-CASE-1", "ANM X", "Child 1", "female", "", "live_birth", LocalDate.now().toString());
        service.registerChild(outcomeInformation, EXTRA_DATA);

        verify(pncSchedulesService).enrollChild(outcomeInformation);
    }

    @Test
    public void shouldSaveChildIntoRepositoryDuringRegistration() {
        DateTime currentTime = DateUtil.now();
        mockCurrentDate(currentTime);
        when(allMothers.findByCaseId("MOTHER-CASE-1")).thenReturn(new Mother("MOTHER-CASE-1", "TC 1", "Theresa"));

        service.registerChild(new AnteNatalCareOutcomeInformation("Case X", "MOTHER-CASE-1", "ANM X", "Child 1", "female", "bcg hep", "live_birth", "2012-12-12"), EXTRA_DATA);

        verify(allChildren).register(objectWithSameFieldsAs(new Child("Case X", "MOTHER-CASE-1", "TC 1", "Child 1", Arrays.asList("bcg", "hep"), "female").withAnm("ANM X").withDetails(EXTRA_DATA.get("details"))));
    }

    @Test
    public void shouldNotSaveChildIntoRepositoryDuringRegistrationWhenMotherIsNotFound() {
        DateTime currentTime = DateUtil.now();
        mockCurrentDate(currentTime);
        when(allMothers.findByCaseId("MOTHER-CASE-1")).thenReturn(null);

        service.registerChild(new AnteNatalCareOutcomeInformation("Case X", "MOTHER-CASE-1", "ANM X", "Child 1", "female", "bcg hep", "live_birth", "2012-12-12"), EXTRA_DATA);

        verifyZeroInteractions(allChildren);
    }

    @Test
    public void shouldUpdateMotherAndChildDetailsWhenPNCVisitHappens() throws Exception {
        Map<String, String> newDetails = EXTRA_DATA.get("details");
        Map<String, String> motherUpdatedDetails = create("motherKey", "motherValue").put("someKey", "someValue").map();
        Map<String, String> childUpdatedDetails = create("childKey", "childValue").put("someKey", "someValue").map();
        String childCaseId = "Case X";
        String motherCaseId = "MOTHER-CASE-1";

        Mother mother = new Mother(motherCaseId, "TC 1", "Theresa");
        Child child = new Child(childCaseId, motherCaseId, "TC 1", "Child 1", Arrays.asList("bcg", "hep"), "female");

        when(allMothers.motherExists(motherCaseId)).thenReturn(true);
        when(allChildren.findByMotherCaseId(motherCaseId)).thenReturn(child);
        when(allChildren.updateDetails(childCaseId, newDetails)).thenReturn(child.withDetails(childUpdatedDetails));
        when(allMothers.updateDetails(motherCaseId, newDetails)).thenReturn(mother.withDetails(motherUpdatedDetails));

        service.pncVisitHappened(new PostNatalCareInformation(motherCaseId, "ANM X", "1", "50", "2012-12-12"), EXTRA_DATA);

        verify(actionService).pncVisitHappened(BeneficiaryType.mother, motherCaseId, "ANM X", motherUpdatedDetails);
        verify(actionService).pncVisitHappened(BeneficiaryType.child, childCaseId, "ANM X", childUpdatedDetails);
    }

    @Test
    public void shouldUpdateMotherButNotChildDetailsWhenChildIsNotFoundDuringPNCVisit() throws Exception {
        String motherCaseId = "MOTHER-CASE-1";
        when(allMothers.motherExists(motherCaseId)).thenReturn(true);
        when(allChildren.findByMotherCaseId(motherCaseId)).thenReturn(null);
        Map<String, String> expectedDetails = create("key", "value").put("someKey", "someValue").map();
        Map<String, String> details = mapOf("someKey", "someValue");

        when(allMothers.updateDetails(motherCaseId, details)).thenReturn(new Mother(motherCaseId, "TC 1", "Theresa").withDetails(expectedDetails));

        service.pncVisitHappened(new PostNatalCareInformation(motherCaseId, "ANM X", "1", "50", "2012-12-12"), EXTRA_DATA);

        verify(actionService).pncVisitHappened(mother, motherCaseId, "ANM X", expectedDetails);
    }

    @Test
    public void shouldNotDoAnythingIfMotherIsNotFoundDuringPNCVisit() throws Exception {
        String motherCaseId = "MOTHER-CASE-1";
        when(allMothers.motherExists(motherCaseId)).thenReturn(false);

        service.pncVisitHappened(new PostNatalCareInformation(motherCaseId, "ANM X", "1", "50", "2012-12-12"), EXTRA_DATA);

        verify(allMothers).motherExists("MOTHER-CASE-1");
        verifyNoMoreInteractions(allMothers);
        verifyZeroInteractions(allChildren);
        verifyZeroInteractions(actionService);
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
        assertCloseOfAlertsForProvidedImmunizations("bcg opv0", "BCG", "OPV 0");
    }

    @Test
    public void shouldUpdateEnrollmentsForUpdatedImmunizations() {
        ChildImmunizationUpdationRequest request = new ChildImmunizationUpdationRequest("Case X", "DEMO ANM", "bcg opv0", "2012-01-01");

        service.updateChildImmunization(request, new SafeMap());

        verify(pncSchedulesService).updateEnrollments(request);
    }

    @Test
    public void shouldSendDataForReportingBeforeUpdatingChildInDBSoThatUpdatedImmunizationsAreDecided() throws Exception {
        ChildImmunizationUpdationRequest request = new ChildImmunizationUpdationRequest("Case X", "DEMO ANM", "bcg opv0", "2012-01-01");

        service.updateChildImmunization(request, new SafeMap());

        InOrder inOrder = inOrder(reportingService, pncSchedulesService);
        inOrder.verify(reportingService).updateChildImmunization(eq(request), any(SafeMap.class));
        inOrder.verify(pncSchedulesService).updateEnrollments(request);
    }

    @Test
    public void shouldDeleteAllAlertsForChildCaseClose() {
        DateTime currentTime = DateUtil.now();
        mockCurrentDate(currentTime);

        ActionService alertServiceMock = mock(ActionService.class);
        PNCService pncService = new PNCService(alertServiceMock, mock(PNCSchedulesService.class), allMothers, allChildren, reportingService);

        pncService.closeChildCase(new ChildCloseRequest("Case X", "DEMO ANM"));

        verify(alertServiceMock).deleteAllAlertsForChild("Case X", "DEMO ANM");
    }

    @Test
    public void shouldUnenrollChildWhoseCaseHasBeenClosed() {
        service.closeChildCase(new ChildCloseRequest("Case X", "ANM Y"));

        verify(pncSchedulesService).unenrollChild("Case X");
    }

    private void assertCloseOfAlertsForProvidedImmunizations(String providedImmunizations, String... expectedDeletedAlertsRaised) {
        DateTime currentTime = DateUtil.now();
        mockCurrentDate(currentTime);

        ActionService actionService = mock(ActionService.class);
        PNCService pncService = new PNCService(actionService, mock(PNCSchedulesService.class), allMothers, allChildren, reportingService);

        pncService.updateChildImmunization(new ChildImmunizationUpdationRequest("Case X", "DEMO ANM", providedImmunizations, "2012-01-01"), new SafeMap());

        for (String expectedAlert : expectedDeletedAlertsRaised) {
            verify(actionService).markAlertAsClosedForVisitForChild("Case X", "DEMO ANM", expectedAlert, "2012-01-01");
        }
        verifyNoMoreInteractions(actionService);
    }

    private void assertMissingAlertsAdded(String providedImmunizations, String... expectedAlertsRaised) {
        DateTime currentTime = DateUtil.now();
        mockCurrentDate(currentTime);
        ActionService actionService = mock(ActionService.class);
        PNCService pncService = new PNCService(actionService, mock(PNCSchedulesService.class), allMothers, allChildren, reportingService);
        when(allMothers.findByCaseId("MOTHER-CASE-1")).thenReturn(new Mother("MOTHER-CASE-1", "TC1", "Theresa"));

        pncService.registerChild(new AnteNatalCareOutcomeInformation("Case X", "MOTHER-CASE-1", "ANM X", "Child 1", "female", providedImmunizations, "live_birth", LocalDate.now().toString()), EXTRA_DATA);

        for (String expectedAlert : expectedAlertsRaised) {
            verify(actionService).alertForBeneficiary(child, "Case X", expectedAlert, normal, currentTime.plusDays(2), currentTime.plusDays(2).plusWeeks(1));
        }
        verify(actionService, times(1)).registerChildBirth(any(String.class), any(String.class), any(String.class), any(String.class), any(LocalDate.class), any(String.class), any(Map.class));
        verifyNoMoreInteractions(actionService);
    }
}
