package org.ei.drishti.service.scheduling;

import org.ei.drishti.domain.Child;
import org.ei.drishti.repository.AllChildren;
import org.ei.drishti.service.ActionService;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.scheduletracking.api.service.EnrollmentRecord;
import org.motechproject.scheduletracking.api.service.EnrollmentsQuery;
import org.motechproject.scheduletracking.api.service.ScheduleTrackingService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.util.Arrays.asList;
import static org.apache.commons.lang.StringUtils.join;
import static org.ei.drishti.common.AllConstants.ChildImmunizationFields.*;
import static org.ei.drishti.scheduler.DrishtiScheduleConstants.ChildScheduleConstants.*;
import static org.ei.drishti.util.EasyMap.mapOf;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class ChildSchedulesServiceTest {
    @Mock
    private ScheduleTrackingService scheduleTrackingService;
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
                .shouldEnrollWithEnrollmentDateAsDateOfBirth(CHILD_SCHEDULE_BCG, CHILD_SCHEDULE_MEASLES, CHILD_SCHEDULE_OPV_0_AND_1)
                .shouldFulfillWithFulfillmentDateAsDateOfBirth(CHILD_SCHEDULE_BCG, 1)
                .shouldCloseAlertWithFulfillmentDateAsDateOfBirth("bcg")
                .shouldFulfillWithFulfillmentDateAsDateOfBirth(CHILD_SCHEDULE_OPV_0_AND_1, 2)
                .shouldCloseAlertWithFulfillmentDateAsDateOfBirth("opv_0")
                .shouldCloseAlertWithFulfillmentDateAsDateOfBirth("opv_1")
                .shouldNotEnrollAndFulfillAnythingElse();
    }

    @Test
    public void shouldEnrollChildIntoSchedulesButShouldNotUpdateEnrollmentsIfImmunizationsAreNotProvided() {
        new TestForChildEnrollmentAndUpdate()
                .whenEnrolledWithImmunizationsGiven("")
                .shouldEnrollWithEnrollmentDateAsDateOfBirth(CHILD_SCHEDULE_BCG, CHILD_SCHEDULE_MEASLES, CHILD_SCHEDULE_OPV_0_AND_1)
                .shouldNotEnrollAndFulfillAnythingElse();
    }

    @Test
    public void shouldEnrollChildIntoDependentModulesIfRequiredAndShouldUpdateEnrollments() {
        new TestForChildEnrollmentAndUpdate()
                .whenEnrolledWithImmunizationsGiven("bcg", "opv_0", "opv_1", "measles")
                .shouldEnrollWithEnrollmentDateAsDateOfBirth(CHILD_SCHEDULE_BCG, CHILD_SCHEDULE_MEASLES, CHILD_SCHEDULE_OPV_0_AND_1)
                .shouldEnrollWithEnrollmentDateAsDateOfBirth(CHILD_SCHEDULE_MEASLES_BOOSTER)
                .shouldFulfillWithFulfillmentDateAsDateOfBirth(CHILD_SCHEDULE_BCG, 1)
                .shouldCloseAlertWithFulfillmentDateAsDateOfBirth("bcg")
                .shouldFulfillWithFulfillmentDateAsDateOfBirth(CHILD_SCHEDULE_OPV_0_AND_1, 2)
                .shouldCloseAlertWithFulfillmentDateAsDateOfBirth("opv_0")
                .shouldCloseAlertWithFulfillmentDateAsDateOfBirth("opv_1")
                .shouldFulfillWithFulfillmentDateAsDateOfBirth(CHILD_SCHEDULE_MEASLES, 1)
                .shouldCloseAlertWithFulfillmentDateAsDateOfBirth("measles")
                .shouldNotEnrollAndFulfillAnythingElse();
    }

    @Test
    public void shouldEnrollDependentSchedulesEvenIfDependeeIsNotPresentButImmunizationIsGiven() {
        new TestForChildEnrollmentAndUpdate()
                .givenEnrollmentWillHappenIn(CHILD_SCHEDULE_MEASLES_BOOSTER, MEASLES_BOOSTER_VALUE)
                .givenEnrollmentWillHappenIn(CHILD_SCHEDULE_DPT_BOOSTER2, DPT_BOOSTER_2_VALUE)
                .whenProvidedWithImmunizations("bcg opv_0 measles dptbooster_1 opvbooster")
                .shouldEnrollWithEnrollmentDateAsImmunizationDate(CHILD_SCHEDULE_MEASLES_BOOSTER)
                .shouldEnrollWithEnrollmentDateAsImmunizationDate(CHILD_SCHEDULE_DPT_BOOSTER2)
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
                .whenProvidedWithImmunizations("bcg")
                .shouldFulfillWithFulfillmentDateAsImmunizationDate(CHILD_SCHEDULE_BCG, 1)
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
                .shouldFulfillWithFulfillmentDateAsImmunizationDate(CHILD_SCHEDULE_OPV_0_AND_1, 1)
                .shouldNotEnrollAndFulfillAnythingElse();

        new TestForChildEnrollmentAndUpdate()
                .givenEnrollmentIn(CHILD_SCHEDULE_OPV_0_AND_1, "opv_0", "opv_1")
                .whenProvidedWithImmunizations("opv_0 opv_1")
                .shouldFulfillWithFulfillmentDateAsImmunizationDate(CHILD_SCHEDULE_OPV_0_AND_1, 2)
                .shouldEnrollWithEnrollmentDateAsImmunizationDate(CHILD_SCHEDULE_OPV_2)
                .shouldNotEnrollAndFulfillAnythingElse();

        new TestForChildEnrollmentAndUpdate()
                .givenEnrollmentIn(CHILD_SCHEDULE_OPV_2, "opv_2")
                .whenProvidedWithImmunizations("opv_2")
                .shouldFulfillWithFulfillmentDateAsImmunizationDate(CHILD_SCHEDULE_OPV_2, 1)
                .shouldEnrollWithEnrollmentDateAsImmunizationDate(CHILD_SCHEDULE_OPV_3)
                .shouldNotEnrollAndFulfillAnythingElse();

        new TestForChildEnrollmentAndUpdate()
                .givenEnrollmentIn(CHILD_SCHEDULE_OPV_3, "opv_3")
                .whenProvidedWithImmunizations("opv_3")
                .shouldFulfillWithFulfillmentDateAsImmunizationDate(CHILD_SCHEDULE_OPV_3, 1)
                .shouldEnrollWithEnrollmentDateAsImmunizationDate(CHILD_SCHEDULE_OPV_BOOSTER)
                .shouldNotEnrollAndFulfillAnythingElse();

        new TestForChildEnrollmentAndUpdate()
                .givenEnrollmentIn(CHILD_SCHEDULE_OPV_BOOSTER, "opvbooster")
                .whenProvidedWithImmunizations("opvbooster")
                .shouldFulfillWithFulfillmentDateAsImmunizationDate(CHILD_SCHEDULE_OPV_BOOSTER, 1)
                .shouldNotEnrollAndFulfillAnythingElse();

        new TestForChildEnrollmentAndUpdate()
                .givenEnrollmentIn(CHILD_SCHEDULE_OPV_BOOSTER, "opvbooster")
                .whenProvidedWithImmunizations("SOME OTHER IMMUNIZATION")
                .shouldNotEnrollAndFulfillAnythingElse();
    }

    @Test
    public void shouldUpdateEnrollmentForMeasles() {
        new TestForChildEnrollmentAndUpdate()
                .givenEnrollmentIn(CHILD_SCHEDULE_MEASLES, MEASLES_VALUE)
                .whenProvidedWithImmunizations("measles")
                .shouldFulfillWithFulfillmentDateAsImmunizationDate(CHILD_SCHEDULE_MEASLES, 1)
                .shouldEnrollWithEnrollmentDateAsImmunizationDate(CHILD_SCHEDULE_MEASLES_BOOSTER)
                .shouldNotEnrollAndFulfillAnythingElse();

        new TestForChildEnrollmentAndUpdate()
                .givenEnrollmentIn(CHILD_SCHEDULE_MEASLES, MEASLES_VALUE)
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
                .shouldFulfillWithFulfillmentDateAsImmunizationDate(CHILD_SCHEDULE_OPV_0_AND_1, 1)
                .shouldFulfillWithFulfillmentDateAsImmunizationDate(CHILD_SCHEDULE_MEASLES, 1)
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
        private final ScheduleTrackingService scheduleTrackingService;
        private final ScheduleService scheduleService;
        private final ActionService actionService;
        private List<EnrollmentRecord> allEnrollments;

        public TestForChildEnrollmentAndUpdate() {
            scheduleTrackingService = mock(ScheduleTrackingService.class);
            scheduleService = mock(ScheduleService.class);
            actionService = mock(ActionService.class);
            childSchedulesService = new ChildSchedulesService(scheduleTrackingService, allChildren, scheduleService, actionService);
            allEnrollments = new ArrayList<>();
        }

        public TestForChildEnrollmentAndUpdate givenEnrollmentIn(String schedule, String... milestoneNames) {
            ArrayList<EnrollmentRecord> records = new ArrayList<>();
            for (String milestoneName : milestoneNames) {
                records.add(new EnrollmentRecord(caseId, schedule, milestoneName, null, null, null, null, null, null, null));
            }

            if (records.size() > 1) {
                when(scheduleTrackingService.getEnrollment(caseId, schedule)).thenReturn(records.get(0), records.subList(1, records.size()).toArray(new EnrollmentRecord[0]));
            } else {
                when(scheduleTrackingService.getEnrollment(caseId, schedule)).thenReturn(records.get(0));
            }
            allEnrollments.addAll(records);
            return this;
        }

        public TestForChildEnrollmentAndUpdate givenEnrollmentWillHappenIn(String schedule, String... milestoneNames) {
            ArrayList<EnrollmentRecord> records = new ArrayList<>();
            for (String milestoneName : milestoneNames) {
                records.add(new EnrollmentRecord(caseId, schedule, milestoneName, null, null, null, null, null, null, null));
            }

            when(scheduleTrackingService.getEnrollment(caseId, schedule)).thenReturn(null, records.toArray(new EnrollmentRecord[0]));
            allEnrollments.addAll(records);

            return this;
        }

        public TestForChildEnrollmentAndUpdate whenUnenrolled() {
            when(scheduleTrackingService.search(any(EnrollmentsQuery.class))).thenReturn(allEnrollments);

            childSchedulesService.unenrollChild(caseId);

            return this;
        }

        public TestForChildEnrollmentAndUpdate whenProvidedWithImmunizations(String providedImmunizations) {
            return whenProvidedWithImmunizations(providedImmunizations, "");
        }

        public TestForChildEnrollmentAndUpdate whenProvidedWithImmunizations(String providedImmunizations, String previousImmunizations) {
            Child child = mock(Child.class);
            when(allChildren.findByCaseId(caseId)).thenReturn(child);
            when(child.caseId()).thenReturn(caseId);
            when(child.immunizationsGiven()).thenReturn(asList(providedImmunizations.split(" ")));
            when(child.immunizationDate()).thenReturn(immunizationsDate);

            childSchedulesService.updateEnrollments(caseId, Arrays.asList(previousImmunizations.split(" ")));

            return this;
        }

        public TestForChildEnrollmentAndUpdate whenEnrolledWithImmunizationsGiven(String... immunizationsGiven) {
            setExpectationsForNonDependentSchedules();

            childSchedulesService.enrollChild(
                    new Child(caseId, null, join(asList(immunizationsGiven), " "), "4", null)
                            .withDateOfBirth(dateOfBirth)
                            .withDetails(mapOf("immunizationDate", immunizationsDate))
                            .withAnm(anmId));

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
                verify(scheduleService).enroll(caseId, expectedEnrolledSchedule, enrollmentDate);
            }
        }

        public TestForChildEnrollmentAndUpdate shouldFulfillWithFulfillmentDateAsDateOfBirth(String expectedFulfillment, int numberOfTimes) {
            shouldFulfill(expectedFulfillment, numberOfTimes, dateOfBirth);
            return this;
        }

        public TestForChildEnrollmentAndUpdate shouldCloseAlertWithFulfillmentDateAsDateOfBirth(String expectedFulfillment) {
            shouldCloseAlert(expectedFulfillment, dateOfBirth);
            return this;
        }

        public TestForChildEnrollmentAndUpdate shouldCloseAlertWithFulfillmentDateAsImmunizationsDate(String expectedFulfillment) {
            shouldCloseAlert(expectedFulfillment, immunizationsDate);
            return this;
        }

        public TestForChildEnrollmentAndUpdate shouldFulfillWithFulfillmentDateAsImmunizationDate(String expectedFulfillment, int numberOfTimes) {
            shouldFulfill(expectedFulfillment, numberOfTimes, immunizationsDate);
            return this;
        }

        private void shouldFulfill(String expectedFulfillment, int numberOfTimes, String dateOfFulfillment) {
            verify(scheduleTrackingService, times(numberOfTimes)).fulfillCurrentMilestone(caseId, expectedFulfillment, LocalDate.parse(dateOfFulfillment));
        }

        private void shouldCloseAlert(String expectedFulfillment, String dateOfFulfillment) {
            verify(actionService).markAlertAsClosed(caseId, anmId, expectedFulfillment, dateOfFulfillment);
        }

        public TestForChildEnrollmentAndUpdate shouldNotEnrollAndFulfillAnythingElse() {
            verify(scheduleTrackingService, atLeastOnce()).getEnrollment(eq(caseId), any(String.class));
            verifyNoMoreInteractions(scheduleTrackingService);

            return this;
        }

        public TestForChildEnrollmentAndUpdate shouldUnEnrollFrom(String... schedules) {
            verify(scheduleTrackingService).search(any(EnrollmentsQuery.class));
            for (String schedule : schedules) {
                verify(scheduleTrackingService).unenroll(caseId, asList(schedule));
            }
            verifyNoMoreInteractions(scheduleTrackingService);

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
