package org.ei.drishti.service;

import org.ei.drishti.contract.ChildCloseRequest;
import org.ei.drishti.contract.ChildImmunizationUpdationRequest;
import org.ei.drishti.contract.ChildInformation;
import org.ei.drishti.contract.PostNatalCareInformation;
import org.ei.drishti.domain.Child;
import org.ei.drishti.domain.Mother;
import org.ei.drishti.dto.BeneficiaryType;
import org.ei.drishti.repository.AllChildren;
import org.ei.drishti.repository.AllMothers;
import org.ei.drishti.service.reporting.ChildReportingService;
import org.ei.drishti.service.scheduling.ChildSchedulesService;
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
    private ChildSchedulesService childSchedulesService;
    @Mock
    private AllMothers allMothers;
    @Mock
    private AllChildren allChildren;
    @Mock
    private ChildReportingService childReportingService;

    private PNCService service;
    private Map<String, Map<String, String>> EXTRA_DATA = mapOf("details", mapOf("someKey", "someValue"));

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        service = new PNCService(actionService, childSchedulesService, allMothers, allChildren, childReportingService);
    }

    @Test
    public void shouldAddAlertsForVaccinationsForChildren() {
        DateTime currentTime = DateUtil.now();
        mockCurrentDate(currentTime);
        when(allMothers.findByCaseId("MOTHER-CASE-1")).thenReturn(new Mother("MOTHER-CASE-1", "EC-CASE-1", "TC1", "Theresa"));

        service.registerChild(new ChildInformation("Case X", "MOTHER-CASE-1", "ANM X", "Child 1", "female", LocalDate.now().toString(), "", EXTRA_DATA));

        verify(actionService).alertForBeneficiary(child, "Case X", "OPV 0", normal, currentTime.plusDays(2), currentTime.plusDays(2).plusWeeks(1));
        verify(actionService).alertForBeneficiary(child, "Case X", "BCG", normal, currentTime.plusDays(2), currentTime.plusDays(2).plusWeeks(1));
        verify(actionService).alertForBeneficiary(child, "Case X", "HEP B0", normal, currentTime.plusDays(2), currentTime.plusDays(2).plusWeeks(1));
    }

    @Test
    public void shouldEnrollChildIntoSchedulesDuringRegistration() {
        DateTime currentTime = DateUtil.now();
        mockCurrentDate(currentTime);
        when(allMothers.findByCaseId("MOTHER-CASE-1")).thenReturn(new Mother("MOTHER-CASE-1", "EC-CASE-1", "TC1", "Theresa"));
        ChildInformation childInformation = new ChildInformation("Case X", "MOTHER-CASE-1", "ANM X", "Child 1", "female", LocalDate.now().toString(), "", EXTRA_DATA);
        service.registerChild(childInformation);

        verify(childSchedulesService).enrollChild(childInformation);
    }

    @Test
    public void shouldSaveChildIntoRepositoryDuringRegistration() {
        DateTime currentTime = DateUtil.now();
        mockCurrentDate(currentTime);
        when(allMothers.findByCaseId("MOTHER-CASE-1")).thenReturn(new Mother("MOTHER-CASE-1", "EC-CASE-1", "TC 1", "Theresa"));

        service.registerChild(new ChildInformation("Case X", "MOTHER-CASE-1", "ANM X", "Child 1", "female", "2012-01-01", "bcg hep", EXTRA_DATA));

        verify(allChildren).register(objectWithSameFieldsAs(new Child("Case X", "EC-CASE-1", "MOTHER-CASE-1", "TC 1", "Child 1", Arrays.asList("bcg", "hep"), "female").withAnm("ANM X").withDateOfBirth("2012-01-01").withDetails(EXTRA_DATA.get("details"))));
    }

    @Test
    public void shouldNotSaveChildIntoRepositoryDuringRegistrationWhenMotherIsNotFound() {
        DateTime currentTime = DateUtil.now();
        mockCurrentDate(currentTime);
        when(allMothers.findByCaseId("MOTHER-CASE-1")).thenReturn(null);

        service.registerChild(new ChildInformation("Case X", "MOTHER-CASE-1", "ANM X", "Child 1", "female", "2012-01-01", "bcg hep", EXTRA_DATA));

        verifyZeroInteractions(allChildren);
    }

    @Test
    public void shouldUpdateMotherAndChildDetailsWhenPNCVisitHappens() throws Exception {
        Map<String, String> newDetails = EXTRA_DATA.get("details");
        Map<String, String> motherUpdatedDetails = create("motherKey", "motherValue").put("someKey", "someValue").map();
        Map<String, String> childUpdatedDetails = create("childKey", "childValue").put("someKey", "someValue").map();
        String childCaseId = "Case X";
        String motherCaseId = "MOTHER-CASE-1";

        Mother mother = new Mother(motherCaseId, "EC-CASE-1", "TC 1", "Theresa");
        Child child = new Child(childCaseId, "EC-CASE-1", motherCaseId, "TC 1", "Child 1", Arrays.asList("bcg", "hep"), "female");

        when(allMothers.motherExists(motherCaseId)).thenReturn(true);
        when(allChildren.findByMotherCaseId(motherCaseId)).thenReturn(child);
        when(allChildren.updateDetails(childCaseId, newDetails)).thenReturn(child.withDetails(childUpdatedDetails));
        when(allMothers.updateDetails(motherCaseId, newDetails)).thenReturn(mother.withDetails(motherUpdatedDetails));

        service.pncVisitHappened(new PostNatalCareInformation(motherCaseId, "ANM X", "1", "50", "2012-12-12"), EXTRA_DATA);

        verify(actionService).pncVisitHappened(BeneficiaryType.mother, motherCaseId, "ANM X", LocalDate.parse("2012-12-12"), 1, "50", motherUpdatedDetails);
        verify(actionService).pncVisitHappened(BeneficiaryType.child, childCaseId, "ANM X", LocalDate.parse("2012-12-12"), 1, "50", childUpdatedDetails);
    }

    @Test
    public void shouldUpdateMotherButNotChildDetailsWhenChildIsNotFoundDuringPNCVisit() throws Exception {
        String motherCaseId = "MOTHER-CASE-1";
        when(allMothers.motherExists(motherCaseId)).thenReturn(true);
        when(allChildren.findByMotherCaseId(motherCaseId)).thenReturn(null);
        Map<String, String> expectedDetails = create("key", "value").put("someKey", "someValue").map();
        Map<String, String> details = mapOf("someKey", "someValue");

        when(allMothers.updateDetails(motherCaseId, details)).thenReturn(new Mother(motherCaseId, "EC-CASE-1", "TC 1", "Theresa").withDetails(expectedDetails));

        service.pncVisitHappened(new PostNatalCareInformation(motherCaseId, "ANM X", "1", "50", "2012-12-12"), EXTRA_DATA);

        verify(actionService).pncVisitHappened(mother, motherCaseId, "ANM X", LocalDate.parse("2012-12-12"), 1, "50", expectedDetails);
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
        assertMissingAlertsAdded("bcg opv_0", "HEP B0");
        assertMissingAlertsAdded("bcg opv_0 hepb_0");

        assertMissingAlertsAdded("opv_0 bcg hepb_0");
        assertMissingAlertsAdded("opv_0 bcg", "HEP B0");
        assertMissingAlertsAdded("opv_0 bcg_1", "BCG", "HEP B0");
    }

    @Test
    public void shouldRemoveAlertsForUpdatedImmunizations() throws Exception {
        assertCloseOfAlertsForProvidedImmunizations("bcg opv_0", "BCG", "OPV 0");
    }

    @Test
    public void shouldUpdateEnrollmentsForUpdatedImmunizations() {
        ChildImmunizationUpdationRequest request = new ChildImmunizationUpdationRequest("Case X", "DEMO ANM", "bcg opv0", "2012-01-01");
        when(allChildren.childExists("Case X")).thenReturn(true);
        when(allChildren.updateDetails("Case X", EXTRA_DATA.get("details")))
                .thenReturn(new Child("Case X", "EC-CASE-1", "MOTHER-CASE-1", "TC 1", "Child 1", Arrays.asList("bcg", "hep"), "female")
                        .withDetails(EXTRA_DATA.get("details")));

        service.updateChildImmunization(request, EXTRA_DATA);

        verify(childSchedulesService).updateEnrollments(request);
    }

    @Test
    public void shouldUpdateChildDetailsForUpdatedImmunizations() {
        ChildImmunizationUpdationRequest request = new ChildImmunizationUpdationRequest("Case X", "DEMO ANM", "bcg opv0", "2012-01-01");
        when(allChildren.childExists("Case X")).thenReturn(true);
        when(allChildren.updateDetails("Case X", EXTRA_DATA.get("details")))
                .thenReturn(new Child("Case X", "EC-CASE-1", "MOTHER-CASE-1", "TC 1", "Child 1", Arrays.asList("bcg", "hep"), "female")
                        .withDetails(EXTRA_DATA.get("details")));

        service.updateChildImmunization(request, EXTRA_DATA);

        verify(allChildren).updateDetails("Case X", EXTRA_DATA.get("details"));
    }

    @Test
    public void shouldCreateActionForUpdatedImmunizations() {
        ChildImmunizationUpdationRequest request = new ChildImmunizationUpdationRequest("Case X", "DEMO ANM", "bcg opv0", "2012-01-01").withVitaminADose("1");
        when(allChildren.childExists("Case X")).thenReturn(true);
        when(allChildren.updateDetails("Case X", EXTRA_DATA.get("details")))
                .thenReturn(new Child("Case X", "EC-CASE-1", "MOTHER-CASE-1", "TC 1", "Child 1", Arrays.asList("bcg", "hep"), "female")
                        .withDetails(EXTRA_DATA.get("details")));

        service.updateChildImmunization(request, EXTRA_DATA);

        verify(actionService).updateImmunizations("Case X", "DEMO ANM", EXTRA_DATA.get("details"), "bcg opv0", LocalDate.parse("2012-01-01"), "1");
    }

    @Test
    public void shouldNotDoAnythingIfChildIsNotFoundForUpdatedImmunizations() {
        ChildImmunizationUpdationRequest request = new ChildImmunizationUpdationRequest("Case X", "DEMO ANM", "bcg opv0", "2012-01-01").withVitaminADose("1");
        when(allChildren.childExists("Case X")).thenReturn(false);

        service.updateChildImmunization(request, EXTRA_DATA);

        verifyZeroInteractions(actionService);
        verifyZeroInteractions(childReportingService);
        verifyZeroInteractions(childSchedulesService);
    }

    @Test
    public void shouldSendDataForReportingBeforeUpdatingChildInDBSoThatUpdatedImmunizationsAreDecided() throws Exception {
        ChildImmunizationUpdationRequest request = new ChildImmunizationUpdationRequest("Case X", "DEMO ANM", "bcg opv0", "2012-01-01");
        when(allChildren.childExists("Case X")).thenReturn(true);
        when(allChildren.updateDetails("Case X", EXTRA_DATA.get("details")))
                .thenReturn(new Child("Case X", "EC-CASE-1", "MOTHER-CASE-1", "TC 1", "Child 1", Arrays.asList("bcg", "hep"), "female")
                        .withDetails(EXTRA_DATA.get("details")));

        service.updateChildImmunization(request, EXTRA_DATA);

        InOrder inOrder = inOrder(childReportingService, childSchedulesService);
        inOrder.verify(childReportingService).updateChildImmunization(eq(request), any(SafeMap.class));
        inOrder.verify(childSchedulesService).updateEnrollments(request);
    }

    @Test
    public void shouldDeleteAllAlertsForChildCaseClose() {
        DateTime currentTime = DateUtil.now();
        mockCurrentDate(currentTime);

        ActionService alertServiceMock = mock(ActionService.class);
        PNCService pncService = new PNCService(alertServiceMock, mock(ChildSchedulesService.class), allMothers, allChildren, childReportingService);

        pncService.closeChildCase(new ChildCloseRequest("Case X", "DEMO ANM"), EXTRA_DATA);

        verify(alertServiceMock).deleteAllAlertsForChild("Case X", "DEMO ANM");
    }

    @Test
    public void shouldCreateACloseChildActionForChildCaseClose() {
        DateTime currentTime = DateUtil.now();
        mockCurrentDate(currentTime);

        ActionService alertServiceMock = mock(ActionService.class);
        PNCService pncService = new PNCService(alertServiceMock, mock(ChildSchedulesService.class), allMothers, allChildren, childReportingService);

        pncService.closeChildCase(new ChildCloseRequest("Case X", "DEMO ANM"), EXTRA_DATA);

        verify(alertServiceMock).closeChild("Case X", "DEMO ANM");
    }

    @Test
    public void shouldUnenrollChildWhoseCaseHasBeenClosed() {
        service.closeChildCase(new ChildCloseRequest("Case X", "ANM Y"), EXTRA_DATA);

        verify(childSchedulesService).unenrollChild("Case X");
    }

    @Test
    public void shouldReportWhenChildCaseIsClosed() {
        service.closeChildCase(new ChildCloseRequest("Case X", "ANM Y"), EXTRA_DATA);

        verify(childReportingService).closeChild(EXTRA_DATA.get("reporting"));
    }

    private void assertCloseOfAlertsForProvidedImmunizations(String providedImmunizations, String... expectedDeletedAlertsRaised) {
        DateTime currentTime = DateUtil.now();
        mockCurrentDate(currentTime);

        ActionService actionService = mock(ActionService.class);
        PNCService pncService = new PNCService(actionService, mock(ChildSchedulesService.class), allMothers, allChildren, childReportingService);
        when(allChildren.childExists("Case X")).thenReturn(true);
        when(allChildren.updateDetails("Case X", EXTRA_DATA.get("details")))
                .thenReturn(new Child("Case X", "EC-CASE-1", "MOTHER-CASE-1", "TC 1", "Child 1", Arrays.asList("bcg", "hep"), "female")
                        .withDetails(EXTRA_DATA.get("details")));

        pncService.updateChildImmunization(new ChildImmunizationUpdationRequest("Case X", "DEMO ANM", providedImmunizations, "2012-01-01").withVitaminADose("1"), EXTRA_DATA);

        verify(actionService).updateImmunizations("Case X", "DEMO ANM", EXTRA_DATA.get("details"), providedImmunizations, LocalDate.parse("2012-01-01"), "1");
        for (String expectedAlert : expectedDeletedAlertsRaised) {
            verify(actionService).markAlertAsClosedForVisitForChild("Case X", "DEMO ANM", expectedAlert, "2012-01-01");
        }
        verifyNoMoreInteractions(actionService);
    }

    @Test
    public void shoulReportChildImmunizationsDuringRegistration() {
        DateTime currentTime = DateUtil.now();
        mockCurrentDate(currentTime);
        when(allMothers.findByCaseId("MOTHER-CASE-1")).thenReturn(new Mother("MOTHER-CASE-1", "EC-CASE-1", "TC1", "Theresa"));
        ChildInformation childInformation = new ChildInformation("Case X", "MOTHER-CASE-1", "ANM X", "Child 1", "female", LocalDate.now().toString(), "", EXTRA_DATA);

        service.registerChild(childInformation);

        verify(childReportingService).updateChildImmunization(childInformation);
    }

    private void assertMissingAlertsAdded(String providedImmunizations, String... expectedAlertsRaised) {
        DateTime currentTime = DateUtil.now();
        mockCurrentDate(currentTime);
        ActionService actionService = mock(ActionService.class);
        PNCService pncService = new PNCService(actionService, mock(ChildSchedulesService.class), allMothers, allChildren, childReportingService);
        when(allMothers.findByCaseId("MOTHER-CASE-1")).thenReturn(new Mother("MOTHER-CASE-1", "EC-CASE-1", "TC1", "Theresa"));

        pncService.registerChild(new ChildInformation("Case X", "MOTHER-CASE-1", "ANM X", "Child 1", "female", LocalDate.now().toString(), providedImmunizations, EXTRA_DATA));

        for (String expectedAlert : expectedAlertsRaised) {
            verify(actionService).alertForBeneficiary(child, "Case X", expectedAlert, normal, currentTime.plusDays(2), currentTime.plusDays(2).plusWeeks(1));
        }
        verify(actionService, times(1)).registerChildBirth(any(String.class), any(String.class), any(String.class), any(String.class), any(LocalDate.class), any(String.class), eq(EXTRA_DATA.get("details")));
        verifyNoMoreInteractions(actionService);
    }
}
