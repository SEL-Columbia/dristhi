package org.ei.drishti.service;

import org.ei.drishti.domain.Child;
import org.ei.drishti.domain.Mother;
import org.ei.drishti.form.domain.FormSubmission;
import org.ei.drishti.form.domain.SubFormData;
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
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.motechproject.testing.utils.BaseUnitTest;
import org.motechproject.util.DateUtil;

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
    private AllMothers allMothers;
    @Mock
    private AllChildren allChildren;
    @Mock
    private MotherReportingService motherReportingService;
    @Mock
    private ChildReportingService childReportingService;
    @Mock
    private PNCSchedulesService pncSchedulesService;
    @Mock
    private ReportFieldsDefinition reportFieldsDefinition;
    private ChildService service;
    private Map<String, Map<String, String>> EXTRA_DATA = create("details", mapOf("someKey", "someValue"))
            .put("reporting", mapOf("someKey", "someValue")).put("child_close", mapOf("someKey", "someValue")).map();

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        service = new ChildService(childSchedulesService, allMothers, allChildren,
                childReportingService, actionService, reportFieldsDefinition);
    }

    @Test
    public void shouldEnrollEveryChildIntoSchedulesAndReportDuringRegistration() {
        DateTime currentTime = DateUtil.now();
        mockCurrentDate(currentTime);
        when(allMothers.findByCaseId("mother id 1")).thenReturn(new Mother("mother id 1", "EC-CASE-1", "TC1"));
        Child firstChild = new Child("child id 1", "mother id 1", "opv", "2", "female");
        Child secondChild = new Child("child id 2", "mother id 1", "opv", "2", "male");
        when(allChildren.findByMotherId("mother id 1")).thenReturn(asList(firstChild, secondChild));
        when(allChildren.findByCaseId("child id 1")).thenReturn(firstChild);
        when(allChildren.findByCaseId("child id 2")).thenReturn(secondChild);
        FormSubmission submission = create()
                .withFormName("delivery_outcome")
                .withANMId("anm id 1")
                .withEntityId("mother id 1")
                .addFormField("referenceDate", "2012-01-01")
                .addFormField("didBreastfeedingStart", "no")
                .withSubForm(new SubFormData("Child Registration",
                        asList(mapOf("id", "child id 1"), mapOf("id", "child id 2"))))
                .build();

        service.registerChildren(submission);

        InOrder inOrder = inOrder(childReportingService, childSchedulesService);
        inOrder.verify(childReportingService).registerChild(new SafeMap(create("didBreastfeedingStart", "no").put("id", "child id 1").map()));
        inOrder.verify(childSchedulesService).enrollChild(firstChild.withAnm("anm id 1").withDateOfBirth("2012-01-01").withThayiCard("TC1"));
        inOrder.verify(childReportingService).registerChild(new SafeMap(create("didBreastfeedingStart", "no").put("id", "child id 2").map()));
        inOrder.verify(childSchedulesService).enrollChild(secondChild.withAnm("anm id 1").withDateOfBirth("2012-01-01").withThayiCard("TC1"));
    }

    @Test
    public void shouldDeleteRegisteredChildWhenDeliveryOutcomeIsStillBirth() {
        DateTime currentTime = DateUtil.now();
        mockCurrentDate(currentTime);
        FormSubmission submission = create()
                .withFormName("delivery_outcome")
                .withANMId("anm id 1")
                .withEntityId("mother id 1")
                .addFormField("deliveryOutcome", "still_birth")
                .withSubForm(new SubFormData("Child Registration",
                        asList(mapOf("id", "child id 1"))))
                .build();
        when(allMothers.findByCaseId("mother id 1")).thenReturn(new Mother("mother id 1", "EC-CASE-1", "TC1"));

        service.registerChildren(submission);

        verify(allChildren).remove("child id 1");
        verifyZeroInteractions(childReportingService);
        verifyZeroInteractions(childSchedulesService);
    }

    @Test
    public void shouldUpdateEveryChildWithMotherInfoDuringRegistration() {
        DateTime currentTime = DateUtil.now();
        mockCurrentDate(currentTime);
        when(allMothers.findByCaseId("mother id 1")).thenReturn(new Mother("mother id 1", "EC-CASE-1", "TC1"));
        Child firstChild = new Child("child id 1", "mother id 1", "opv", "2", "female");
        Child secondChild = new Child("child id 2", "mother id 1", "opv", "2", "male");
        when(allChildren.findByMotherId("mother id 1")).thenReturn(asList(firstChild, secondChild));
        when(allChildren.findByCaseId("child id 1")).thenReturn(firstChild);
        when(allChildren.findByCaseId("child id 2")).thenReturn(secondChild);
        FormSubmission submission = create()
                .withFormName("delivery_outcome")
                .withANMId("anm id 1")
                .withEntityId("mother id 1")
                .addFormField("referenceDate", "2012-01-01")
                .addFormField("didBreastfeedingStart", "no")
                .withSubForm(new SubFormData("Child Registration",
                        asList(mapOf("id", "child id 1"), mapOf("id", "child id 2"))))
                .build();

        service.registerChildren(submission);

        verify(allChildren).update(firstChild.withAnm("anm id 1").withDateOfBirth("2012-01-01").withThayiCard("TC1"));
        verify(allChildren).update(secondChild.withAnm("anm id 1").withDateOfBirth("2012-01-01").withThayiCard("TC1"));
    }

    @Test
    public void shouldNotHandleChildRegistrationWhenMotherIsNotFound() {
        DateTime currentTime = DateUtil.now();
        mockCurrentDate(currentTime);
        when(allMothers.findByCaseId("MOTHER-CASE-1")).thenReturn(null);
        FormSubmission submission = create()
                .withFormName("delivery_outcome")
                .withANMId("anm id 1")
                .withEntityId("mother id 1")
                .addFormField("referenceDate", "2012-01-01")
                .addFormField("didBreastfeedingStart", "no")
                .build();

        service.registerChildren(submission);

        verifyZeroInteractions(allChildren);
        verifyZeroInteractions(childReportingService);
        verifyZeroInteractions(childSchedulesService);
    }

    @Test
    public void shouldUpdateChildWithANMDetailsWhenItIsRegisteredForAnEC() {
        DateTime currentTime = DateUtil.now();
        mockCurrentDate(currentTime);
        Child child = new Child("child id 1", "mother id 1", "opv", "2", "female");
        when(allChildren.findByCaseId("child id 1")).thenReturn(child);
        FormSubmission submission = create()
                .withFormName("child_registration_ec")
                .withANMId("anm id 1")
                .withEntityId("mother id 1")
                .addFormField("childId", "child id 1")
                .build();

        service.registerChildrenForEC(submission);

        verify(allChildren).update(child.withAnm("anm id 1"));
    }

    @Test
    public void shouldUpdateEnrollmentsForUpdatedImmunizations() {
        Child child = mock(Child.class);
        when(allChildren.childExists("Case X")).thenReturn(true);
        when(allChildren.findByCaseId("Case X")).thenReturn(child);
        FormSubmission submission = create()
                .withFormName("child_immunizations")
                .withANMId("anm id 1")
                .withEntityId("Case X")
                .addFormField("previousImmunizations", "bcg")
                .addFormField("immunizationsGiven", "bcg opv_0")
                .build();

        service.updateChildImmunization(submission);

        verify(childSchedulesService).updateEnrollments("Case X", asList("bcg"));
    }

    @Test
    public void shouldNotDoAnythingIfChildIsNotFoundForUpdatedImmunizations() {
        when(allChildren.childExists("Case X")).thenReturn(false);
        FormSubmission submission = create()
                .withFormName("child_immunizations")
                .withANMId("anm id 1")
                .withEntityId("Case X")
                .addFormField("previousImmunizations", "bcg")
                .addFormField("immunizationsGiven", "bcg opv_0")
                .build();

        service.updateChildImmunization(submission);

        verifyZeroInteractions(actionService);
        verifyZeroInteractions(childReportingService);
        verifyZeroInteractions(childSchedulesService);
    }

    @Test
    public void shouldCallReportServiceWithPreviousImmunizationsInsteadOfCurrentImmunizations() throws Exception {
        when(allChildren.childExists("Case X")).thenReturn(true);
        when(allChildren.findByCaseId("Case X")).thenReturn(new Child("Case X", "MOTHER-CASE-1", "bcg", "3", "female")
                .withDetails(EXTRA_DATA.get("details")));
        when(reportFieldsDefinition.get("child_immunizations")).thenReturn(asList("id", "immunizationsGiven", "immunizationDate"));
        FormSubmission submission = create()
                .withFormName("child_immunizations")
                .withANMId("anm id 1")
                .withEntityId("Case X")
                .addFormField("id", "Case X")
                .addFormField("previousImmunizations", "bcg")
                .addFormField("immunizationsGiven", "bcg opv_0")
                .addFormField("immunizationDate", "2013-01-01")
                .build();

        service.updateChildImmunization(submission);

        verify(childReportingService).immunizationProvided(new SafeMap(create("id", "Case X").put("immunizationsGiven", "bcg opv_0").put("immunizationDate", "2013-01-01").map()), asList("bcg"));
    }

    @Test
    public void shouldCallReportServiceWithEmptyImmunizationsWhenThereIsNoPreviousImmunization() throws Exception {
        when(allChildren.childExists("Case X")).thenReturn(true);
        when(allChildren.findByCaseId("Case X")).thenReturn(new Child("Case X", "MOTHER-CASE-1", "bcg", "3", "female")
                .withDetails(EXTRA_DATA.get("details")));
        when(reportFieldsDefinition.get("child_immunizations")).thenReturn(asList("id", "immunizationsGiven", "immunizationDate"));
        FormSubmission submission = create()
                .withFormName("child_immunizations")
                .withANMId("anm id 1")
                .withEntityId("Case X")
                .addFormField("id", "Case X")
                .addFormField("immunizationsGiven", "bcg opv_0")
                .addFormField("immunizationDate", "2013-01-01")
                .build();

        service.updateChildImmunization(submission);

        verify(childReportingService).immunizationProvided(
                new SafeMap(create("id", "Case X").put("immunizationsGiven", "bcg opv_0").put("immunizationDate", "2013-01-01").map()),
                asList(""));
    }

    @Test
    public void shouldCreateACloseChildActionForChildCaseClose() {
        DateTime currentTime = DateUtil.now();
        mockCurrentDate(currentTime);
        FormSubmission submission = create()
                .withFormName("child_close")
                .withANMId("anm id 1")
                .withEntityId("child id 1")
                .build();

        when(allChildren.childExists("child id 1")).thenReturn(true);

        service.closeChild(submission);

        verify(actionService).markAllAlertsAsInactive("child id 1");
    }

    @Test
    public void shouldUnenrollChildWhoseCaseHasBeenClosed() {
        FormSubmission submission = create()
                .withFormName("child_close")
                .withANMId("anm id 1")
                .withEntityId("child id 1")
                .build();

        when(allChildren.childExists("child id 1")).thenReturn(true);

        service.closeChild(submission);

        verify(childSchedulesService).unenrollChild("child id 1");
    }

    @Test
    public void shouldReportWhenChildCaseIsClosed() {
        FormSubmission submission = create()
                .withFormName("child_close")
                .withANMId("anm id 1")
                .withEntityId("child id 1")
                .addFormField("someKey", "someValue")
                .build();

        when(allChildren.childExists("child id 1")).thenReturn(true);
        when(reportFieldsDefinition.get("child_close")).thenReturn(asList("someKey"));

        service.closeChild(submission);

        verify(childReportingService).closeChild(new SafeMap(EXTRA_DATA.get("child_close")));
    }

    @Test
    public void shouldCloseWhenChildCaseIsClosed() {
        FormSubmission submission = create()
                .withFormName("child_close")
                .withANMId("anm id 1")
                .withEntityId("child id 1")
                .build();

        when(allChildren.childExists("child id 1")).thenReturn(true);

        service.closeChild(submission);

        verify(allChildren).close("child id 1");
    }

    @Test
    public void shouldNotDoAnythingIfChildDoesNotExistsDuringClose() {
        FormSubmission submission = create()
                .withFormName("child_close")
                .withANMId("anm id 1")
                .withEntityId("child id 1")
                .build();

        when(allChildren.childExists("child id 1")).thenReturn(false);

        service.closeChild(submission);

        verify(allChildren).childExists("child id 1");
        verifyZeroInteractions(childReportingService);
        verifyZeroInteractions(childSchedulesService);
        verifyZeroInteractions(actionService);
        verifyNoMoreInteractions(allChildren);
    }
}
