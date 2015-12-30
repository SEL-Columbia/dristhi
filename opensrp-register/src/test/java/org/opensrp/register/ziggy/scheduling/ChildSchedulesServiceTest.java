package org.opensrp.register.ziggy.scheduling;

import static java.util.Arrays.asList;
import static org.apache.commons.lang.StringUtils.join;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.atMost;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.opensrp.register.RegisterConstants.ChildImmunizationFields.*;
import static org.opensrp.common.util.EasyMap.mapOf;
import static org.opensrp.register.RegisterConstants.ChildScheduleConstants.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.motechproject.scheduletracking.api.service.EnrollmentRecord;
import org.opensrp.register.ziggy.domain.Child;
import org.opensrp.register.ziggy.repository.AllChildren;
import org.opensrp.register.ziggy.scheduling.ChildSchedulesService;
import org.opensrp.scheduler.HealthSchedulerService;

public class ChildSchedulesServiceTest {
	@Mock
    private HealthSchedulerService scheduler;
    @Mock
    private AllChildren allChildren;
    private ChildSchedulesService childSchedulesService;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
    }

    @Test
    public void shouldEnrollChildIntoSchedulesAndShouldUpdateEnrollmentsForGivenImmunizations() {
        new TestForChildEnrollmentAndUpdate()
                .whenEnrolledWithImmunizationsGiven("bcg", "opv_0", "opv_1")
                .shouldEnrollWithEnrollmentDateAsDateOfBirth(CHILD_SCHEDULE_BCG, CHILD_SCHEDULE_MEASLES, CHILD_SCHEDULE_OPV_0_AND_1
                		//TODO MAIMOONA VERIFY AS I ADDED IT
                		,CHILD_SCHEDULE_DPT_BOOSTER1, CHILD_SCHEDULE_OPV_2, CHILD_SCHEDULE_PENTAVALENT_1)
                .shouldFulfillWithFulfillmentDateAsDateOfBirth(CHILD_SCHEDULE_BCG, 1, BCG_VALUE)
                .shouldCloseAlertWithFulfillmentDateAsDateOfBirth(CHILD_SCHEDULE_BCG, BCG_VALUE)
                .shouldFulfillWithFulfillmentDateAsDateOfBirth(CHILD_SCHEDULE_OPV_0_AND_1, 1, "opv_0", "opv_1")
                .shouldCloseAlertWithFulfillmentDateAsDateOfBirth(CHILD_SCHEDULE_OPV_0_AND_1, "opv_0")
                .shouldCloseAlertWithFulfillmentDateAsDateOfBirth(CHILD_SCHEDULE_OPV_0_AND_1, "opv_1")
                .shouldNotEnrollAndFulfillAnythingElse();
    }

    @Test
    public void shouldEnrollChildIntoSchedulesButShouldNotUpdateEnrollmentsIfImmunizationsAreNotProvided() {
        new TestForChildEnrollmentAndUpdate()
                .whenEnrolledWithImmunizationsGiven("")
                .shouldEnrollWithEnrollmentDateAsDateOfBirth(CHILD_SCHEDULE_BCG, CHILD_SCHEDULE_MEASLES, CHILD_SCHEDULE_OPV_0_AND_1 
                		//TODO MAIMOONA VERIFY AS I ADDED IT
                		,CHILD_SCHEDULE_DPT_BOOSTER1, CHILD_SCHEDULE_PENTAVALENT_1)
                .shouldNotEnrollAndFulfillAnythingElse();
    }

    @Test
    public void shouldEnrollChildIntoDependentModulesIfRequiredAndShouldUpdateEnrollments() {
        new TestForChildEnrollmentAndUpdate()
                .whenEnrolledWithImmunizationsGiven("bcg", "opv_0", "opv_1", "measles")
                .shouldEnrollWithEnrollmentDateAsDateOfBirth(CHILD_SCHEDULE_MEASLES_BOOSTER)
                .shouldEnrollWithEnrollmentDateAsDateOfBirth(CHILD_SCHEDULE_BCG, CHILD_SCHEDULE_MEASLES, CHILD_SCHEDULE_OPV_0_AND_1
                		//TODO MAIMOONA VERIFY AS I ADDED IT
                		,CHILD_SCHEDULE_DPT_BOOSTER1, CHILD_SCHEDULE_OPV_2, CHILD_SCHEDULE_PENTAVALENT_1)
                .shouldFulfillWithFulfillmentDateAsDateOfBirth(CHILD_SCHEDULE_BCG, 1, BCG_VALUE)
                .shouldCloseAlertWithFulfillmentDateAsDateOfBirth(CHILD_SCHEDULE_BCG, "bcg")
                .shouldFulfillWithFulfillmentDateAsDateOfBirth(CHILD_SCHEDULE_OPV_0_AND_1, 1, "opv_0", "opv_1")
                .shouldCloseAlertWithFulfillmentDateAsDateOfBirth(CHILD_SCHEDULE_OPV_0_AND_1, "opv_0")
                .shouldCloseAlertWithFulfillmentDateAsDateOfBirth(CHILD_SCHEDULE_OPV_0_AND_1, "opv_1")
                .shouldFulfillWithFulfillmentDateAsDateOfBirth(CHILD_SCHEDULE_MEASLES, 1, MEASLES_VALUE)
                .shouldCloseAlertWithFulfillmentDateAsDateOfBirth(CHILD_SCHEDULE_MEASLES, "measles")
                .shouldNotEnrollAndFulfillAnythingElse();
    }

    @Test
    public void shouldEnrollDependentSchedulesEvenIfDependeeIsNotPresentButImmunizationIsGiven() {
        new TestForChildEnrollmentAndUpdate()
                .givenEnrollmentWillHappenIn(CHILD_SCHEDULE_MEASLES_BOOSTER, MEASLES_BOOSTER_VALUE)
                .givenEnrollmentWillHappenIn(CHILD_SCHEDULE_DPT_BOOSTER2, DPT_BOOSTER_2_VALUE)
                .whenProvidedWithImmunizations("bcg opv_0 measles dptbooster_1 opvbooster")
                .shouldEnrollWithEnrollmentDateAsImmunizationDate(CHILD_SCHEDULE_DPT_BOOSTER2)
                .shouldEnrollWithEnrollmentDateAsImmunizationDate(CHILD_SCHEDULE_MEASLES_BOOSTER)
                .shouldNotEnrollAndFulfillAnythingElse();
    }

    @Test
    public void shouldNotEnrollDependentScheduleIfAlreadyEnrolled() {
        new TestForChildEnrollmentAndUpdate()
                .givenEnrollmentIn(CHILD_SCHEDULE_MEASLES_BOOSTER, MEASLES_BOOSTER_VALUE)
                .whenProvidedWithImmunizations("measles")
                .shouldNotEnrollAndFulfillAnythingElse();
    }

    @Test
    public void shouldNotEnrollDependentSchedulesIfImmunizationForThemIsAlreadyGiven() {
        new TestForChildEnrollmentAndUpdate()
                .givenChildIsAlreadyProvidedWithImmunizations("opv_2")
                .whenProvidedWithImmunizations("opv_1")
                .shouldNotEnrollAndFulfillAnythingElse();
    }

    @Test
    public void shouldNotUpdateEnrollmentForAScheduleWhenNotEnrolledInSchedule() {
        new TestForChildEnrollmentAndUpdate()
                .whenProvidedWithImmunizations("bcg opv_0 opv_1 opv_2 opvbooster")
                .shouldNotEnrollAndFulfillAnythingElse();
    }

    @Test
    public void shouldUpdateEnrollmentForBCGOnlyWhenBCGHasBeenProvided() {
        new TestForChildEnrollmentAndUpdate()
                .givenEnrollmentIn(CHILD_SCHEDULE_BCG, BCG_VALUE)
                .whenProvidedWithImmunizations(BCG_VALUE)
                .shouldFulfillWithFulfillmentDateAsImmunizationDate(CHILD_SCHEDULE_BCG, 1, BCG_VALUE)
                .shouldNotEnrollAndFulfillAnythingElse();

        new TestForChildEnrollmentAndUpdate()
                .givenEnrollmentIn(CHILD_SCHEDULE_BCG, BCG_VALUE)
                .whenProvidedWithImmunizations("SOME OTHER IMM")
                .shouldNotEnrollAndFulfillAnythingElse();
    }

    @Test
    public void shouldUpdateEnrollmentsForOPV() {
        new TestForChildEnrollmentAndUpdate()
                .givenEnrollmentIn(CHILD_SCHEDULE_OPV_0_AND_1, "opv_0")
                .whenProvidedWithImmunizations("opv_0")
                .shouldFulfillWithFulfillmentDateAsImmunizationDate(CHILD_SCHEDULE_OPV_0_AND_1, 1, "opv_0")
                .shouldNotEnrollAndFulfillAnythingElse();

        new TestForChildEnrollmentAndUpdate()
                .givenEnrollmentIn(CHILD_SCHEDULE_OPV_0_AND_1, "opv_0", "opv_1")
                .whenProvidedWithImmunizations("opv_0 opv_1")
                .shouldFulfillWithFulfillmentDateAsImmunizationDate(CHILD_SCHEDULE_OPV_0_AND_1, 1, "opv_0", "opv_1")
                .shouldEnrollWithEnrollmentDateAsImmunizationDate(CHILD_SCHEDULE_OPV_2)
                .shouldNotEnrollAndFulfillAnythingElse();

        new TestForChildEnrollmentAndUpdate()
                .givenEnrollmentIn(CHILD_SCHEDULE_OPV_2, "opv_2")
                .whenProvidedWithImmunizations("opv_2")
                .shouldFulfillWithFulfillmentDateAsImmunizationDate(CHILD_SCHEDULE_OPV_2, 1, "opv_2")
                .shouldEnrollWithEnrollmentDateAsImmunizationDate(CHILD_SCHEDULE_OPV_3)
                .shouldNotEnrollAndFulfillAnythingElse();

        new TestForChildEnrollmentAndUpdate()
                .givenEnrollmentIn(CHILD_SCHEDULE_OPV_3, "opv_3")
                .whenProvidedWithImmunizations("opv_3")
                .shouldFulfillWithFulfillmentDateAsImmunizationDate(CHILD_SCHEDULE_OPV_3, 1, "opv_3")
                .shouldEnrollWithEnrollmentDateAsImmunizationDate(CHILD_SCHEDULE_OPV_BOOSTER)
                .shouldNotEnrollAndFulfillAnythingElse();

        new TestForChildEnrollmentAndUpdate()
                .givenEnrollmentIn(CHILD_SCHEDULE_OPV_BOOSTER, "opvbooster")
                .whenProvidedWithImmunizations("opvbooster")
                .shouldFulfillWithFulfillmentDateAsImmunizationDate(CHILD_SCHEDULE_OPV_BOOSTER, 1, "opvbooster")
                .shouldNotEnrollAndFulfillAnythingElse();

        new TestForChildEnrollmentAndUpdate()
                .givenEnrollmentIn(CHILD_SCHEDULE_OPV_BOOSTER, "opvbooster")
                .whenProvidedWithImmunizations("SOME OTHER IMMUNIZATION")
                .shouldNotEnrollAndFulfillAnythingElse();
    }

    @Test
    public void shouldUpdateEnrollmentForMeasles() {
        when(scheduler.isNotEnrolled(any(String.class), eq(CHILD_SCHEDULE_MEASLES_BOOSTER))).thenReturn(true);

        new TestForChildEnrollmentAndUpdate()
                .givenEnrollmentIn(CHILD_SCHEDULE_MEASLES, MEASLES_VALUE)
                .whenProvidedWithImmunizations(MEASLES_VALUE)
                .shouldFulfillWithFulfillmentDateAsImmunizationDate(CHILD_SCHEDULE_MEASLES, 1, MEASLES_VALUE)
                .shouldEnrollWithEnrollmentDateAsImmunizationDate(CHILD_SCHEDULE_MEASLES_BOOSTER)
                .shouldNotEnrollAndFulfillAnythingElse();

        new TestForChildEnrollmentAndUpdate()
                .givenEnrollmentIn(CHILD_SCHEDULE_MEASLES, MEASLES_VALUE)
                .whenProvidedWithImmunizations("SOME OTHER IMMUNIZATION")
                .shouldNotEnrollAndFulfillAnythingElse();
    }

    @Test
    public void shouldUpdateEnrollmentForDPTBoosters() {
        new TestForChildEnrollmentAndUpdate()
                .givenEnrollmentIn(CHILD_SCHEDULE_DPT_BOOSTER1, "dptbooster_1")
                .whenProvidedWithImmunizations("dptbooster_1")
                .shouldFulfillWithFulfillmentDateAsImmunizationDate(CHILD_SCHEDULE_DPT_BOOSTER1, 1, "dptbooster_1")
                .shouldEnrollWithEnrollmentDateAsImmunizationDate(CHILD_SCHEDULE_DPT_BOOSTER2)
                .shouldNotEnrollAndFulfillAnythingElse();

        new TestForChildEnrollmentAndUpdate()
                .givenEnrollmentIn(CHILD_SCHEDULE_DPT_BOOSTER2, "dptbooster_2")
                .whenProvidedWithImmunizations("dptbooster_2")
                .shouldFulfillWithFulfillmentDateAsImmunizationDate(CHILD_SCHEDULE_DPT_BOOSTER2, 1, "dptbooster_2")
                .shouldNotEnrollAndFulfillAnythingElse();
    }

    @Test
    public void shouldUpdateEnrollmentForPentavalent() {
        new TestForChildEnrollmentAndUpdate()
                .givenEnrollmentIn(CHILD_SCHEDULE_PENTAVALENT_1, "pentavalent_1")
                .whenProvidedWithImmunizations("pentavalent_1")
                .shouldFulfillWithFulfillmentDateAsImmunizationDate(CHILD_SCHEDULE_PENTAVALENT_1, 1, "pentavalent_1")
                .shouldEnrollWithEnrollmentDateAsImmunizationDate(CHILD_SCHEDULE_PENTAVALENT_2)
                .shouldNotEnrollAndFulfillAnythingElse();

        new TestForChildEnrollmentAndUpdate()
                .givenEnrollmentIn(CHILD_SCHEDULE_PENTAVALENT_2, "pentavalent_2")
                .whenProvidedWithImmunizations("pentavalent_2")
                .shouldFulfillWithFulfillmentDateAsImmunizationDate(CHILD_SCHEDULE_PENTAVALENT_2, 1, "pentavalent_2")
                .shouldEnrollWithEnrollmentDateAsImmunizationDate(CHILD_SCHEDULE_PENTAVALENT_3)
                .shouldNotEnrollAndFulfillAnythingElse();

        new TestForChildEnrollmentAndUpdate()
                .givenEnrollmentIn(CHILD_SCHEDULE_PENTAVALENT_3, "pentavalent_3")
                .whenProvidedWithImmunizations("pentavalent_3")
                .shouldFulfillWithFulfillmentDateAsImmunizationDate(CHILD_SCHEDULE_PENTAVALENT_3, 1, "pentavalent_3")
                .shouldNotEnrollAndFulfillAnythingElse();

        new TestForChildEnrollmentAndUpdate()
                .givenEnrollmentIn(CHILD_SCHEDULE_PENTAVALENT_3, "pentavalent_3")
                .whenProvidedWithImmunizations("SOME OTHER IMMUNIZATION")
                .shouldNotEnrollAndFulfillAnythingElse();
    }

    @Test
    public void shouldUpdateEnrollmentsWhenMultipleDifferentKindsOfEnrollmentsArePresent() {
        new TestForChildEnrollmentAndUpdate()
                .givenEnrollmentIn(CHILD_SCHEDULE_BCG, BCG_VALUE)
                .givenEnrollmentIn(CHILD_SCHEDULE_OPV_0_AND_1, OPV_1_VALUE)
                .givenEnrollmentIn(CHILD_SCHEDULE_MEASLES, "measles")
                .whenProvidedWithImmunizations("measles hepb_0 measlesbooster opv_1")
                .shouldEnrollWithEnrollmentDateAsImmunizationDate(CHILD_SCHEDULE_MEASLES_BOOSTER)
                //TODO MAIMOONA VERIFY IF OPV SHOULD BE ENROLLED
                .shouldEnrollWithEnrollmentDateAsImmunizationDate(CHILD_SCHEDULE_OPV_2)
                .shouldFulfillWithFulfillmentDateAsImmunizationDate(CHILD_SCHEDULE_OPV_0_AND_1, 1, "opv_1")
                .shouldFulfillWithFulfillmentDateAsImmunizationDate(CHILD_SCHEDULE_MEASLES, 1, MEASLES_VALUE)
                .shouldNotEnrollAndFulfillAnythingElse();
    }

    @Test
    public void shouldCloseAllOpenSchedulesWhenAChildIsUnEnrolled() {
        new TestForChildEnrollmentAndUpdate()
                .givenEnrollmentIn(CHILD_SCHEDULE_BCG, BCG_VALUE)
                .givenEnrollmentIn(CHILD_SCHEDULE_OPV_0_AND_1, OPV_1_VALUE)
                .givenEnrollmentIn(CHILD_SCHEDULE_MEASLES, MEASLES_VALUE)
                .whenUnenrolled()
                .shouldUnEnrollFrom(CHILD_SCHEDULE_BCG, CHILD_SCHEDULE_OPV_0_AND_1, CHILD_SCHEDULE_MEASLES);
    }

    private class TestForChildEnrollmentAndUpdate {
        private final String caseId = "Case X";
        private final String anmId = "anm id 1";
        private final String dateOfBirth = "2012-01-01";
        private final String immunizationsDate = "2012-05-04";
        private final String formSubmissionId = "fs1";
       // private TaskSchedulerService scheduler;
        private List<EnrollmentRecord> allEnrollments;

        public TestForChildEnrollmentAndUpdate() {
        	scheduler = mock(HealthSchedulerService.class);
            childSchedulesService = new ChildSchedulesService(allChildren, scheduler);
            allEnrollments = new ArrayList<>();
        }

        public TestForChildEnrollmentAndUpdate givenEnrollmentIn(final String schedule, String... milestoneNames) {
            ArrayList<EnrollmentRecord> records = new ArrayList<>();
            for (String milestoneName : milestoneNames) {
                records.add(new EnrollmentRecord(caseId, schedule, milestoneName, null, null, null, null, null, null, null));
            }

            if (records.size() > 1) {
                when(scheduler.getEnrollmentRecord(caseId, schedule)).thenReturn(records.get(0), records.subList(1, records.size()).toArray(new EnrollmentRecord[0]));
            } else {
                when(scheduler.getEnrollmentRecord(caseId, schedule)).thenReturn(records.get(0));
            }

            when(scheduler.isNotEnrolled(eq(caseId), any(String.class))).thenAnswer(new Answer<Boolean>() {
				@Override
				public Boolean answer(InvocationOnMock invocation) throws Throwable {
					return invocation.getArguments()[1].toString().equalsIgnoreCase(schedule)?false:true;
				}
			});

            allEnrollments.addAll(records);
            return this;
        }

        public TestForChildEnrollmentAndUpdate givenEnrollmentWillHappenIn(String schedule, String... milestoneNames) {
            ArrayList<EnrollmentRecord> records = new ArrayList<>();
            for (String milestoneName : milestoneNames) {
                records.add(new EnrollmentRecord(caseId, schedule, milestoneName, null, null, null, null, null, null, null));
            }

            when(scheduler.getEnrollmentRecord(caseId, schedule)).thenReturn(null, records.toArray(new EnrollmentRecord[0]));
            
            when(scheduler.isNotEnrolled(eq(caseId), eq(schedule))).thenReturn(true);
            
            allEnrollments.addAll(records);

            return this;
        }

        public TestForChildEnrollmentAndUpdate whenUnenrolled() {
            when(scheduler.findActiveEnrollments(any(String.class))).thenReturn(allEnrollments);

            childSchedulesService.unenrollChild(caseId, formSubmissionId);

            return this;
        }

        public TestForChildEnrollmentAndUpdate whenProvidedWithImmunizations(String providedImmunizations) {
            return whenProvidedWithImmunizations(providedImmunizations, "");
        }

        public TestForChildEnrollmentAndUpdate whenProvidedWithImmunizations(String providedImmunizations, String previousImmunizations) {
            Child child = mock(Child.class);
            when(allChildren.findByCaseId(caseId)).thenReturn(child);
            when(child.caseId()).thenReturn(caseId);
            when(child.anmIdentifier()).thenReturn(anmId);
            when(child.immunizationsGiven()).thenReturn(asList(providedImmunizations.split(" ")));
            when(child.immunizationDate()).thenReturn(immunizationsDate);

            childSchedulesService.updateEnrollments(caseId, Arrays.asList(previousImmunizations.split(" ")), formSubmissionId);

            return this;
        }

        public TestForChildEnrollmentAndUpdate whenEnrolledWithImmunizationsGiven(String... immunizationsGiven) {
            setExpectationsForNonDependentSchedules();

            childSchedulesService.enrollChild(
                    new Child(caseId, anmId, join(asList(immunizationsGiven), " "), "4", null)
                            .withDateOfBirth(dateOfBirth)
                            .withDetails(mapOf("immunizationDate", immunizationsDate))
                            .withAnm(anmId), formSubmissionId);

            return this;
        }

        private void setExpectationsForNonDependentSchedules() {
            this.givenEnrollmentIn(CHILD_SCHEDULE_BCG, BCG_VALUE)
                    .givenEnrollmentIn(CHILD_SCHEDULE_MEASLES, MEASLES_VALUE)
                    .givenEnrollmentIn(CHILD_SCHEDULE_OPV_0_AND_1, OPV_0_VALUE, OPV_1_VALUE, OPV_2_VALUE, OPV_3_VALUE);
        }

        public TestForChildEnrollmentAndUpdate shouldEnrollWithEnrollmentDateAsDateOfBirth(String... expectedEnrolledSchedules) {
            shouldEnroll(expectedEnrolledSchedules, dateOfBirth);
            return this;
        }

        public TestForChildEnrollmentAndUpdate shouldEnrollWithEnrollmentDateAsImmunizationDate(String... expectedEnrolledSchedules) {
            shouldEnroll(expectedEnrolledSchedules, immunizationsDate);
            return this;
        }

        private void shouldEnroll(String[] expectedEnrolledSchedules, String enrollmentDate) {
            for (String expectedEnrolledSchedule : expectedEnrolledSchedules) {
                verify(scheduler).enrollIntoSchedule(caseId, expectedEnrolledSchedule, enrollmentDate, formSubmissionId);
            }
        }

        public TestForChildEnrollmentAndUpdate shouldFulfillWithFulfillmentDateAsDateOfBirth(String expectedFulfillment, int numberOfTimes, String... milestones) {
            shouldFulfill(expectedFulfillment, numberOfTimes, dateOfBirth, milestones);
            return this;
        }

        public TestForChildEnrollmentAndUpdate shouldCloseAlertWithFulfillmentDateAsDateOfBirth(String expectedFulfillment, String... milestones) {
        	shouldFulfill(expectedFulfillment, 1, dateOfBirth, milestones);
            return this;
        }

        public TestForChildEnrollmentAndUpdate shouldFulfillWithFulfillmentDateAsImmunizationDate(String expectedFulfillment, int numberOfTimes, String... milestones) {
            shouldFulfill(expectedFulfillment, numberOfTimes, immunizationsDate, milestones);
            return this;
        }

        private void shouldFulfill(String expectedFulfillment, int numberOfTimes, String dateOfFulfillment, String... milestones) {
            for (String m : milestones) {
            	verify(scheduler, times(numberOfTimes)).fullfillMilestoneAndCloseAlert(eq(caseId), eq(anmId), eq(expectedFulfillment), eq(m), eq(LocalDate.parse(dateOfFulfillment)), eq(formSubmissionId));
			}
        }

        public TestForChildEnrollmentAndUpdate shouldNotEnrollAndFulfillAnythingElse() {
            verify(scheduler, atLeastOnce()).getEnrollmentRecord(eq(caseId), any(String.class));
        	verify(scheduler, atMost(2)).isNotEnrolled(eq(caseId), any(String.class));
            verifyNoMoreInteractions(scheduler);

            return this;
        }

        public TestForChildEnrollmentAndUpdate shouldUnEnrollFrom(String... schedules) {
            verify(scheduler).unEnrollFromAllSchedules(any(String.class), eq(formSubmissionId));
            verifyNoMoreInteractions(scheduler);

            return this;
        }

        public TestForChildEnrollmentAndUpdate givenChildIsAlreadyProvidedWithImmunizations(String immunizationsAlreadyProvided) {
            Child child = mock(Child.class);
            when(allChildren.findByCaseId(caseId)).thenReturn(child);
            when(child.caseId()).thenReturn(caseId);
            when(child.immunizationsGiven()).thenReturn(asList(immunizationsAlreadyProvided.split(" ")));
            when(child.immunizationDate()).thenReturn("2012-01-01");
            return this;
        }

    }
}
