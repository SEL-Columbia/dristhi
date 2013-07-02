package org.ei.drishti.service;

import org.ei.drishti.contract.ChildCloseRequest;
import org.ei.drishti.contract.ChildImmunizationUpdationRequest;
import org.ei.drishti.domain.Child;
import org.ei.drishti.domain.Mother;
import org.ei.drishti.form.domain.FormSubmission;
import org.ei.drishti.repository.AllChildren;
import org.ei.drishti.repository.AllEligibleCouples;
import org.ei.drishti.repository.AllMothers;
import org.ei.drishti.service.formSubmission.handler.ReportFieldsDefinition;
import org.ei.drishti.service.reporting.ChildReportingService;
import org.ei.drishti.service.reporting.MotherReportingService;
import org.ei.drishti.service.scheduling.ChildSchedulesService;
import org.ei.drishti.service.scheduling.PNCSchedulesService;
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

import static java.util.Arrays.asList;
import static org.ei.drishti.util.EasyMap.create;
import static org.ei.drishti.util.EasyMap.mapOf;
import static org.ei.drishti.util.FormSubmissionBuilder.create;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class ChildServiceTest extends BaseUnitTest {
    @Mock
    private ActionService actionService;
    @Mock
    private ChildSchedulesService childSchedulesService;
    @Mock
    private AllEligibleCouples allEligibleCouples;
    @Mock
    private AllMothers mothers;
    @Mock
    private AllChildren children;
    @Mock
    private MotherReportingService motherReportingService;
    @Mock
    private ChildReportingService childReportingService;
    @Mock
    private PNCSchedulesService pncSchedulesService;
    @Mock
    private ReportFieldsDefinition reportFieldsDefinition;
    private ChildService service;
    private Map<String, Map<String, String>> EXTRA_DATA = create("details", mapOf("someKey", "someValue")).put("reporting", mapOf("someKey", "someValue")).map();

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        service = new ChildService(childSchedulesService, mothers, children,
                childReportingService, actionService);
    }

    @Test
    public void shouldEnrollEveryChildIntoSchedulesAndReportDuringRegistration() {
        DateTime currentTime = DateUtil.now();
        mockCurrentDate(currentTime);
        when(mothers.findByCaseId("mother id 1")).thenReturn(new Mother("mother id 1", "EC-CASE-1", "TC1"));
        Child firstChild = new Child("child id 1", "mother id 1", "opv", "2", "female");
        Child secondChild = new Child("child id 2", "mother id 1", "opv", "2", "male");
        when(children.findByMotherId("mother id 1")).thenReturn(asList(firstChild, secondChild));
        FormSubmission submission = create()
                .withFormName("delivery_outcome")
                .withANMId("anm id 1")
                .withEntityId("mother id 1")
                .addFormField("referenceDate", "2012-01-01")
                .addFormField("didBreastfeedingStart", "no")
                .build();

        service.registerChildren(submission);

        InOrder inOrder = inOrder(childReportingService, childSchedulesService);
        inOrder.verify(childReportingService).registerChild(new SafeMap(create("didBreastfeedingStart", "no").put("id", "child id 1").map()));
        inOrder.verify(childSchedulesService).enrollChild(firstChild.withAnm("anm id 1").withDateOfBirth("2012-01-01").withThayiCard("TC1"));
        inOrder.verify(childReportingService).registerChild(new SafeMap(create("didBreastfeedingStart", "no").put("id", "child id 2").map()));
        inOrder.verify(childSchedulesService).enrollChild(secondChild.withAnm("anm id 1").withDateOfBirth("2012-01-01").withThayiCard("TC1"));
    }

    @Test
    public void shouldUpdateEveryChildWithMotherInfoDuringRegistration() {
        DateTime currentTime = DateUtil.now();
        mockCurrentDate(currentTime);
        when(mothers.findByCaseId("mother id 1")).thenReturn(new Mother("mother id 1", "EC-CASE-1", "TC1"));
        Child firstChild = new Child("child id 1", "mother id 1", "opv", "2", "female");
        Child secondChild = new Child("child id 2", "mother id 1", "opv", "2", "male");
        when(children.findByMotherId("mother id 1")).thenReturn(asList(firstChild, secondChild));
        FormSubmission submission = create()
                .withFormName("delivery_outcome")
                .withANMId("anm id 1")
                .withEntityId("mother id 1")
                .addFormField("referenceDate", "2012-01-01")
                .addFormField("didBreastfeedingStart", "no")
                .build();

        service.registerChildren(submission);

        verify(children).update(firstChild.withAnm("anm id 1").withDateOfBirth("2012-01-01").withThayiCard("TC1"));
        verify(children).update(secondChild.withAnm("anm id 1").withDateOfBirth("2012-01-01").withThayiCard("TC1"));
    }

    @Test
    public void shouldNotHandleChildRegistrationWhenMotherIsNotFound() {
        DateTime currentTime = DateUtil.now();
        mockCurrentDate(currentTime);
        when(mothers.findByCaseId("MOTHER-CASE-1")).thenReturn(null);
        FormSubmission submission = create()
                .withFormName("delivery_outcome")
                .withANMId("anm id 1")
                .withEntityId("mother id 1")
                .addFormField("referenceDate", "2012-01-01")
                .addFormField("didBreastfeedingStart", "no")
                .build();

        service.registerChildren(submission);

        verifyZeroInteractions(children);
        verifyZeroInteractions(childReportingService);
        verifyZeroInteractions(childSchedulesService);
    }

    @Test
    public void shouldUpdateEnrollmentsForUpdatedImmunizations() {
        Child child = mock(Child.class);
        when(children.childExists("Case X")).thenReturn(true);
        when(children.findByCaseId("Case X")).thenReturn(child);
        when(child.immunizationsProvided()).thenReturn(asList(""));
        ChildImmunizationUpdationRequest request = new ChildImmunizationUpdationRequest("Case X", "DEMO ANM", "bcg opv0", "2012-01-01");
        when(children.update("Case X", EXTRA_DATA.get("details")))
                .thenReturn(new Child("Case X", "MOTHER-CASE-1", "TC 1", "Child 1", Arrays.asList("bcg", "hep"), "female")
                        .withDetails(EXTRA_DATA.get("details")));

        service.updateChildImmunization(request, EXTRA_DATA);

        verify(childSchedulesService).updateEnrollments(request);
    }

    @Test
    public void shouldUpdateChildDetailsForUpdatedImmunizations() {
        Child child = mock(Child.class);
        when(children.childExists("Case X")).thenReturn(true);
        when(children.findByCaseId("Case X")).thenReturn(child);
        when(child.immunizationsProvided()).thenReturn(asList(""));
        ChildImmunizationUpdationRequest request = new ChildImmunizationUpdationRequest("Case X", "DEMO ANM", "bcg opv0", "2012-01-01");
        when(children.update("Case X", EXTRA_DATA.get("details")))
                .thenReturn(new Child("Case X", "MOTHER-CASE-1", "TC 1", "Child 1", Arrays.asList("bcg", "hep"), "female")
                        .withDetails(EXTRA_DATA.get("details")));

        service.updateChildImmunization(request, EXTRA_DATA);

        verify(children).update("Case X", EXTRA_DATA.get("details"));
    }

    @Test
    public void shouldCreateActionForUpdatedImmunizations() {
        Child child = mock(Child.class);
        when(children.childExists("Case X")).thenReturn(true);
        when(children.findByCaseId("Case X")).thenReturn(child);
        when(child.immunizationsProvided()).thenReturn(asList(""));
        ChildImmunizationUpdationRequest request = new ChildImmunizationUpdationRequest("Case X", "DEMO ANM", "bcg opv0", "2012-01-01").withVitaminADose("1");
        when(children.update("Case X", EXTRA_DATA.get("details")))
                .thenReturn(new Child("Case X", "MOTHER-CASE-1", "TC 1", "Child 1", Arrays.asList("bcg", "hep"), "female")
                        .withDetails(EXTRA_DATA.get("details")));

        service.updateChildImmunization(request, EXTRA_DATA);

        verify(actionService).updateImmunizations("Case X", "DEMO ANM", EXTRA_DATA.get("details"), "bcg opv0", LocalDate.parse("2012-01-01"), "1");
    }

    @Test
    public void shouldNotDoAnythingIfChildIsNotFoundForUpdatedImmunizations() {
        ChildImmunizationUpdationRequest request = new ChildImmunizationUpdationRequest("Case X", "DEMO ANM", "bcg opv0", "2012-01-01").withVitaminADose("1");
        when(children.childExists("Case X")).thenReturn(false);

        service.updateChildImmunization(request, EXTRA_DATA);

        verifyZeroInteractions(actionService);
        verifyZeroInteractions(childReportingService);
        verifyZeroInteractions(childSchedulesService);
    }

    @Test
    public void shouldCallReportingServiceWithPreviousImmunizationsInsteadOfCurrentImmunizations() throws Exception {
        when(children.childExists("Case X")).thenReturn(true);
        when(children.findByCaseId("Case X")).thenReturn(new Child("Case X", "MOTHER-CASE-1", "TC 1", "Child 1", Arrays.asList("hep"), "female")
                .withDetails(EXTRA_DATA.get("details")));
        when(children.update("Case X", EXTRA_DATA.get("details")))
                .thenReturn(new Child("Case X", "MOTHER-CASE-1", "TC 1", "Child 1", Arrays.asList("hep", "bcg", "opv0"), "female")
                        .withDetails(EXTRA_DATA.get("details")));
        ChildImmunizationUpdationRequest request = new ChildImmunizationUpdationRequest("Case X", "DEMO ANM", "bcg opv0", "2012-01-01");

        service.updateChildImmunization(request, EXTRA_DATA);

        verify(childReportingService).immunizationProvided(new SafeMap(EXTRA_DATA.get("reporting")), asList("hep"));
    }

    @Test
    public void shouldCreateACloseChildActionForChildCaseClose() {
        DateTime currentTime = DateUtil.now();
        mockCurrentDate(currentTime);
        when(children.childExists("Case X")).thenReturn(true);

        service.closeChildCase(new ChildCloseRequest("Case X", "DEMO ANM"), EXTRA_DATA);

        verify(actionService).closeChild("Case X", "DEMO ANM");
    }

    @Test
    public void shouldUnenrollChildWhoseCaseHasBeenClosed() {
        when(children.childExists("Case X")).thenReturn(true);

        service.closeChildCase(new ChildCloseRequest("Case X", "ANM Y"), EXTRA_DATA);

        verify(childSchedulesService).unenrollChild("Case X");
    }

    @Test
    public void shouldReportWhenChildCaseIsClosed() {
        when(children.childExists("Case X")).thenReturn(true);

        service.closeChildCase(new ChildCloseRequest("Case X", "ANM Y"), EXTRA_DATA);

        verify(childReportingService).closeChild(new SafeMap(EXTRA_DATA.get("reporting")));
    }

    @Test
    public void shouldCloseWhenChildCaseIsClosed() {
        when(children.childExists("Case X")).thenReturn(true);

        service.closeChildCase(new ChildCloseRequest("Case X", "ANM Y"), EXTRA_DATA);

        verify(children).close("Case X");
    }

    @Test
    public void shouldNotDoAnythingIfChildDoesNotExistsDuringClose() {
        when(children.childExists("Case X")).thenReturn(false);

        service.closeChildCase(new ChildCloseRequest("Case X", "ANM Y"), EXTRA_DATA);

        verify(children).childExists("Case X");
        verifyZeroInteractions(childReportingService);
        verifyZeroInteractions(childSchedulesService);
        verifyZeroInteractions(actionService);
        verifyNoMoreInteractions(children);
    }

    @Test
    public void shouldCloseAlertsForProvidedImmunizations() {
        DateTime currentTime = DateUtil.now();
        mockCurrentDate(currentTime);

        Child child = mock(Child.class);
        when(children.childExists("Case X")).thenReturn(true);
        when(children.findByCaseId("Case X")).thenReturn(child);
        when(child.immunizationsProvided()).thenReturn(asList(""));
        when(children.update("Case X", EXTRA_DATA.get("details")))
                .thenReturn(new Child("Case X", "MOTHER-CASE-1", "TC 1", "Child 1", Arrays.asList("bcg", "hep"), "female")
                        .withDetails(EXTRA_DATA.get("details")));

        ChildImmunizationUpdationRequest updationRequest = new ChildImmunizationUpdationRequest("Case X", "DEMO ANM", "bcg dpt_1 measlesbooster", "2012-01-01").withVitaminADose("1");
        service.updateChildImmunization(updationRequest, EXTRA_DATA);

        verify(actionService).updateImmunizations("Case X", "DEMO ANM", EXTRA_DATA.get("details"), "bcg dpt_1 measlesbooster", LocalDate.parse("2012-01-01"), "1");
        for (String expectedAlert : updationRequest.immunizationsProvidedList()) {
            verify(actionService).markAlertAsClosed("Case X", "DEMO ANM", expectedAlert, "2012-01-01");
        }
        verifyNoMoreInteractions(actionService);
    }
}
