package org.opensrp.register.ziggy.service;

import org.opensrp.form.domain.FormSubmission;
import org.opensrp.form.domain.SubFormData;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.motechproject.testing.utils.BaseUnitTest;
import org.motechproject.util.DateUtil;

import java.util.Collections;
import java.util.Map;

import static java.util.Arrays.asList;
import static org.opensrp.common.util.EasyMap.create;
import static org.opensrp.common.util.EasyMap.mapOf;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.opensrp.register.util.FormSubmissionBuilder.create;

import org.opensrp.register.ziggy.domain.Child;
import org.opensrp.register.ziggy.domain.Mother;
import org.opensrp.register.ziggy.repository.AllChildren;
import org.opensrp.register.ziggy.repository.AllMothers;
import org.opensrp.register.ziggy.scheduling.ChildSchedulesService;
import org.opensrp.register.ziggy.service.ChildService;

public class ChildServiceTest extends BaseUnitTest {
    @Mock
    private ChildSchedulesService childSchedulesService;
    @Mock
    private AllMothers allMothers;
    @Mock
    private AllChildren allChildren;
    private ChildService service;
    private Map<String, Map<String, String>> EXTRA_DATA = create("details", mapOf("someKey", "someValue"))
            .put("reporting", mapOf("someKey", "someValue")).put("child_close", mapOf("someKey", "someValue")).map();

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        service = new ChildService(childSchedulesService, allMothers, allChildren);
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
                .addFormField("deliveryPlace", "phc")
                .addFormField("deliveryRegistrationDate", "2012-01-02")
                .addFormField("submissionDate", "2012-01-02")
                .withSubForm(new SubFormData("child_registration",
                        asList(mapOf("id", "child id 1"), mapOf("id", "child id 2"))))
                .build();

        service.registerChildren(submission);

        InOrder inOrder = inOrder(childSchedulesService);
        inOrder.verify(childSchedulesService).enrollChild(firstChild.withAnm("anm id 1").withDateOfBirth("2012-01-01").withThayiCard("TC1"), submission.instanceId());
        inOrder.verify(childSchedulesService).enrollChild(secondChild.withAnm("anm id 1").withDateOfBirth("2012-01-01").withThayiCard("TC1"), submission.instanceId());
        assertFalse(firstChild.isClosed());
        assertFalse(secondChild.isClosed());
    }

    @Test
    public void shouldDeleteChildRegisteredThroughDeliveryOutcomeFormWhenDeliveryOutcomeIsStillBirth() {
        DateTime currentTime = DateUtil.now();
        mockCurrentDate(currentTime);
        FormSubmission submission = create()
                .withFormName("delivery_outcome")
                .withANMId("anm id 1")
                .withEntityId("mother id 1")
                .addFormField("deliveryOutcome", "still_birth")
                .withSubForm(new SubFormData("child_registration",
                        asList(mapOf("id", "child id 1"))))
                .build();
        when(allMothers.findByCaseId("mother id 1")).thenReturn(new Mother("mother id 1", "EC-CASE-1", "TC1"));

        service.registerChildren(submission);

        verify(allChildren).remove("child id 1");
        verifyZeroInteractions(childSchedulesService);
    }

    @Test
    public void shouldUpdateEveryChildWithMotherInfoAndImmunizationInfoDuringRegistration() {
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
                .addFormField("submissionDate", "2012-01-01")
                .addFormField("didBreastfeedingStart", "no")
                .withSubForm(new SubFormData("child_registration",
                        asList(create("id", "child id 1").put("immunizationsGiven", "bcg").map(), mapOf("id", "child id 2"))))
                .build();

        service.registerChildren(submission);

        verify(allChildren).update(new Child("child id 1", "mother id 1", "opv", "2", "female").withAnm("anm id 1").withDateOfBirth("2012-01-01").withThayiCard("TC1")
                .withImmunizations(mapOf("bcg", "2012-01-01")));
        verify(allChildren).update(new Child("child id 2", "mother id 1", "opv", "2", "male").withAnm("anm id 1").withDateOfBirth("2012-01-01").withThayiCard("TC1"));

        assertFalse(firstChild.isClosed());
        assertFalse(secondChild.isClosed());
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
        verifyZeroInteractions(childSchedulesService);
    }

    @Test
    public void shouldUpdateChildWithANMDetailsImmunizationDetailsAndVitaminDetailsWhenItIsRegisteredAsOA() {
        DateTime currentTime = DateUtil.now();
        mockCurrentDate(currentTime);
        Child child = new Child("child id 1", "mother id 1", "opv", "2", "female");
        when(allChildren.findByCaseId("child id 1")).thenReturn(child);
        FormSubmission submission = create()
                .withFormName("child_registration_oa")
                .withANMId("anm id 1")
                .addFormField("id", "child id 1")
                .addFormField("motherId", "mother id 1")
                .addFormField("immunizationsGiven", "bcg opv_0 dptbooster_1")
                .addFormField("childVitaminAHistory", "1 2")
                .addFormField("bcgDate", "2013-01-01")
                .addFormField("opv0Date", "2013-01-02")
                .addFormField("dptbooster1Date", "2013-01-03")
                .addFormField("vitamin1Date", "2013-01-01")
                .addFormField("vitamin2Date", "2013-01-02")
                .build();

        service.registerChildrenForOA(submission);

        verify(allChildren).update(new Child("child id 1", "mother id 1", "opv", "2", "female").withAnm("anm id 1")
                .withImmunizations(create("bcg", "2013-01-01")
                        .put("opv_0", "2013-01-02")
                        .put("dptbooster_1", "2013-01-03").map())
                .withVitaminADoses(create("dose1", "2013-01-01")
                        .put("dose2", "2013-01-02")
                        .map()));
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
                .addFormField("immunizationsGiven", "bcg")
                .addFormField("bcgDate", "2013-01-01")
                .build();

        service.updateChildImmunization(submission);

        verify(childSchedulesService).updateEnrollments("Case X", asList("bcg"), submission.instanceId());
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

        verifyZeroInteractions(childSchedulesService);
        verifyZeroInteractions(childSchedulesService);
    }

    @Test
    public void shouldUpdateChildWithImmunizationsInfoFromChildImmunizationsForm() throws Exception {
        when(allChildren.findByCaseId("Case X")).thenReturn(new Child("Case X", "MOTHER-CASE-1", "bcg", "3", "female")
                .withDetails(EXTRA_DATA.get("details")));
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

        verify(allChildren).update(new Child("Case X", "MOTHER-CASE-1", "bcg", "3", "female")
                .withDetails(EXTRA_DATA.get("details")).withImmunizations(mapOf("opv_0", "2013-01-01")));
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

        verify(childSchedulesService).unenrollChild("child id 1", submission.instanceId());
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

        verify(childSchedulesService).unenrollChild("child id 1", submission.instanceId());
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
        verifyZeroInteractions(childSchedulesService);
        verifyZeroInteractions(childSchedulesService);
        verifyNoMoreInteractions(allChildren);
    }

    @Test
    public void shouldUpdateANMIdOnMotherWhenOAPNCIsRegistered() {
        DateTime currentTime = DateUtil.now();
        mockCurrentDate(currentTime);
        Mother mother = new Mother("mother id 1", "ec id 1", "TC1");
        when(allMothers.findByEcCaseId("ec id 1")).thenReturn(asList(mother));
        FormSubmission submission = create()
                .withFormName("pnc_registration_oa")
                .withANMId("anm id 1")
                .withEntityId("ec id 1")
                .addFormField("referenceDate", "2012-01-01")
                .withSubForm(new SubFormData("child_registration_oa", Collections.<Map<String, String>>emptyList()))
                .build();

        service.pncOAChildRegistration(submission);

        verify(allMothers).update(mother.withAnm("anm id 1"));
    }

    @Test
    public void shouldUpdateEveryChildWithMotherInfoDuringPNCgRegistration() {
        DateTime currentTime = DateUtil.now();
        mockCurrentDate(currentTime);
        when(allMothers.findByEcCaseId("ec id 1")).thenReturn(asList(new Mother("mother id 1", "ec id 1", "TC1")));
        Child firstChild = new Child("child id 1", "mother id 1", "opv", "2", "female");
        Child secondChild = new Child("child id 2", "mother id 1", "opv", "2", "male");
        when(allChildren.findByCaseId("child id 1")).thenReturn(firstChild);
        when(allChildren.findByCaseId("child id 2")).thenReturn(secondChild);
        FormSubmission submission = create()
                .withFormName("pnc_registration_oa")
                .withANMId("anm id 1")
                .withEntityId("ec id 1")
                .addFormField("referenceDate", "2012-01-01")
                .withSubForm(new SubFormData("child_registration_oa",
                        asList(mapOf("id", "child id 1"), mapOf("id", "child id 2"))))
                .build();

        service.pncOAChildRegistration(submission);

        verify(allChildren).update(firstChild.withAnm("anm id 1").withDateOfBirth("2012-01-01").withThayiCard("TC1"));
        verify(allChildren).update(secondChild.withAnm("anm id 1").withDateOfBirth("2012-01-01").withThayiCard("TC1"));
        assertFalse(firstChild.isClosed());
        assertFalse(secondChild.isClosed());
    }

    @Test
    public void shouldUpdateEveryChildWithImmunizationInfoDuringPNCOARegistration() {
        DateTime currentTime = DateUtil.now();
        mockCurrentDate(currentTime);
        when(allMothers.findByEcCaseId("ec id 1")).thenReturn(asList(new Mother("mother id 1", "ec id 1", "TC1")));
        Child firstChild = new Child("child id 1", "mother id 1", "opv", "2", "female");
        Child secondChild = new Child("child id 2", "mother id 1", "opv", "2", "male");
        when(allChildren.findByCaseId("child id 1")).thenReturn(firstChild);
        when(allChildren.findByCaseId("child id 2")).thenReturn(secondChild);
        FormSubmission submission = create()
                .withFormName("pnc_registration_oa")
                .withANMId("anm id 1")
                .withEntityId("ec id 1")
                .addFormField("referenceDate", "2012-01-01")
                .addFormField("submissionDate", "2012-01-01")
                .withSubForm(new SubFormData("child_registration_oa",
                        asList(create("id", "child id 1").put("immunizationsGiven", "bcg").map(), mapOf("id", "child id 2"))))
                .build();

        service.pncOAChildRegistration(submission);

        verify(allChildren).update(new Child("child id 1", "mother id 1", "opv", "2", "female").withAnm("anm id 1").withDateOfBirth("2012-01-01").withThayiCard("TC1")
                .withImmunizations(mapOf("bcg", "2012-01-01")));

    }

    @Test
    public void shouldNotHandlePNCChildRegistrationWhenMotherIsNotFound() {
        DateTime currentTime = DateUtil.now();
        mockCurrentDate(currentTime);
        when(allMothers.findByEcCaseId("ec id 1")).thenReturn(Collections.EMPTY_LIST);
        FormSubmission submission = create()
                .withFormName("pnc_registration_oa")
                .withANMId("anm id 1")
                .withEntityId("ec id 1")
                .addFormField("referenceDate", "2012-01-01")
                .build();

        service.pncOAChildRegistration(submission);

        verifyZeroInteractions(allChildren);
        verifyZeroInteractions(childSchedulesService);
    }

    @Test
    public void shouldDeleteChildRegisteredThroughPNCRegistrationFormOAWhenDeliveryOutcomeIsStillBirth() {
        DateTime currentTime = DateUtil.now();
        mockCurrentDate(currentTime);
        FormSubmission submission = create()
                .withFormName("pnc_registration_oa")
                .withANMId("anm id 1")
                .withEntityId("ec id 1")
                .addFormField("deliveryOutcome", "still_birth")
                .withSubForm(new SubFormData("child_registration_oa",
                        asList(mapOf("id", "child id 1"))))
                .build();
        when(allMothers.findByEcCaseId("ec id 1")).thenReturn(asList(new Mother("mother id 1", "EC-CASE-1", "TC1")));

        service.pncOAChildRegistration(submission);

        verify(allChildren).remove("child id 1");
        verifyZeroInteractions(childSchedulesService);
    }

    @Test
    public void shouldNotFailTryingToDeleteChildWhenPNCRegistrationOAIsHandledAndDeliveryOutcomeIsStillBirthAndThereIsNoChild() {
        DateTime currentTime = DateUtil.now();
        mockCurrentDate(currentTime);
        FormSubmission submission = create()
                .withFormName("pnc_registration_oa")
                .withANMId("anm id 1")
                .withEntityId("ec id 1")
                .addFormField("deliveryOutcome", "still_birth")
                .withSubForm(new SubFormData("child_registration_oa",
                        Collections.<Map<String, String>>emptyList()))
                .build();
        when(allMothers.findByEcCaseId("ec id 1")).thenReturn(asList(new Mother("mother id 1", "EC-CASE-1", "TC1")));

        service.pncOAChildRegistration(submission);

        verifyZeroInteractions(allChildren);
        verifyZeroInteractions(childSchedulesService);
    }

    @Test
    public void shouldUpdateVitaminADosageInChild() {
        when(allChildren.findByCaseId("Case X")).thenReturn(new Child("Case X", "Mother id 1", "bcg", "3", "male"));
        FormSubmission submission = create()
                .withFormName("vitamin_a")
                .withEntityId("Case X")
                .withANMId("anm id 1")
                .addFormField("some-field", "some-value")
                .addFormField("vitaminADose", "1")
                .addFormField("vitaminADate", "2013-01-01")
                .build();

        service.vitaminAProvided(submission);

        verify(allChildren).update(new Child("Case X", "Mother id 1", "bcg", "3", "male")
                .withVitaminADoses(mapOf("dose1", "2013-01-01")));
    }
}
