package org.ei.drishti.service;

import org.ei.drishti.contract.ChildImmunizationUpdationRequest;
import org.ei.drishti.contract.ChildInformation;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.Mock;
import org.motechproject.scheduletracking.api.service.EnrollmentRecord;
import org.motechproject.scheduletracking.api.service.EnrollmentRequest;
import org.motechproject.scheduletracking.api.service.EnrollmentsQuery;
import org.motechproject.scheduletracking.api.service.ScheduleTrackingService;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static org.apache.commons.lang.StringUtils.join;
import static org.ei.drishti.common.AllConstants.ChildImmunizationCommCareValues.*;
import static org.ei.drishti.scheduler.DrishtiScheduleConstants.ChildScheduleConstants.*;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class ChildSchedulesServiceTest {
    @Mock
    private ScheduleTrackingService scheduleTrackingService;

    private ChildSchedulesService childSchedulesService;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        childSchedulesService = new ChildSchedulesService(scheduleTrackingService);
    }

    @Test
    public void shouldEnrollChildIntoSchedulesAndShouldUpdateEnrollmentsForGivenImmunizations() {

        new TestForChildEnrollment()
                .whenEnrolledWithImmunizationsProvided("bcg", "opv_0", "dpt_2", "opv_2")
                .shouldEnrollWithEnrollmentDateAsDateOfBirth(CHILD_SCHEDULE_BCG, CHILD_SCHEDULE_DPT, CHILD_SCHEDULE_HEPATITIS, CHILD_SCHEDULE_MEASLES, CHILD_SCHEDULE_OPV)
                .shouldFulfillWithFulfillmentDateAsDateOfBirth(CHILD_SCHEDULE_BCG, 1)
                .shouldFulfillWithFulfillmentDateAsDateOfBirth(CHILD_SCHEDULE_OPV, 2)
                .shouldFulfillWithFulfillmentDateAsDateOfBirth(CHILD_SCHEDULE_DPT, 1)
                .shouldNotEnrollAndFulfillAnythingElse();
    }

    @Test
    public void shouldEnrollChildIntoSchedulesButShouldNotUpdateEnrollmentsIfImmunizationsAreNotProvided() {

        new TestForChildEnrollment()
                .whenEnrolledWithImmunizationsProvided("")
                .shouldEnrollWithEnrollmentDateAsDateOfBirth(CHILD_SCHEDULE_BCG, CHILD_SCHEDULE_DPT, CHILD_SCHEDULE_HEPATITIS, CHILD_SCHEDULE_MEASLES, CHILD_SCHEDULE_OPV)
                .shouldNotEnrollAndFulfillAnythingElse();
    }

    @Test
    public void shouldEnrollChildIntoDependentModulesIfRequiredAndShouldUpdateEnrollments(){

        new TestForChildEnrollment()
                .whenEnrolledWithImmunizationsProvided("bcg", "opv_0", "dpt_2", "opv_2", "measles")
                .shouldEnrollWithEnrollmentDateAsDateOfBirth(CHILD_SCHEDULE_BCG, CHILD_SCHEDULE_DPT, CHILD_SCHEDULE_HEPATITIS, CHILD_SCHEDULE_MEASLES, CHILD_SCHEDULE_OPV)
                .shouldEnrollWithEnrollmentDateAsDateOfBirth(CHILD_SCHEDULE_MEASLES_BOOSTER)
                .shouldFulfillWithFulfillmentDateAsDateOfBirth(CHILD_SCHEDULE_BCG, 1)
                .shouldFulfillWithFulfillmentDateAsDateOfBirth(CHILD_SCHEDULE_OPV, 2)
                .shouldFulfillWithFulfillmentDateAsDateOfBirth(CHILD_SCHEDULE_DPT, 1)
                .shouldFulfillWithFulfillmentDateAsDateOfBirth(CHILD_SCHEDULE_MEASLES, 1)
                .shouldNotEnrollAndFulfillAnythingElse();
    }

    @Test
    public void shouldEnrollInDependentSchedulesAndUpdateThemEvenIfDependyIsNotPresentButImmunizationIsGiven() {
        new TestForChildEnrollment()
                .givenEnrollmentWillHappenIn(CHILD_SCHEDULE_MEASLES_BOOSTER, MEASLES_BOOSTER_COMMCARE_VALUE)
                .whenProvidedWithImmunizations("bcg opv_0 measles measlesbooster dptbooster2 opvbooster")
                .shouldEnrollWithEnrollmentDateAsImmunizationsProvidedDate(CHILD_SCHEDULE_MEASLES_BOOSTER)
                .shouldFulfillWithFulfillmentDateAsImmunizationsProvidedDate(CHILD_SCHEDULE_MEASLES_BOOSTER, 1)
                .shouldNotEnrollAndFulfillAnythingElse();
    }

    @Test
    public void shouldNotEnrollDependentScheduleIfAlreadyEnrolled(){
        new TestForChildEnrollment()
                .givenEnrollmentIn(CHILD_SCHEDULE_MEASLES_BOOSTER, MEASLES_BOOSTER_COMMCARE_VALUE)
                .whenProvidedWithImmunizations("measles")
                .shouldNotEnrollAndFulfillAnythingElse();
    }

    @Test
    public void shouldNotUpdateEnrollmentForAScheduleWhenNotEnrolledInSchedule() {

        new TestForChildEnrollment()
                .whenProvidedWithImmunizations("bcg opv_0 hepb_1 opv_1 dpt_1 hepb_2 opv_2 dpt_2 hepb_2 dpt_3 hepb_3 dptbooster1 dptbooster2 opvbooster")
                .shouldNotEnrollAndFulfillAnythingElse();
    }

    @Test
    public void shouldUpdateEnrollmentForBCGOnlyWhenBCGHasBeenProvided() {
        new TestForChildEnrollment()
                .givenEnrollmentIn(CHILD_SCHEDULE_BCG, BCG_COMMCARE_VALUE)
                .whenProvidedWithImmunizations("bcg")
                .shouldFulfillWithFulfillmentDateAsImmunizationsProvidedDate(CHILD_SCHEDULE_BCG, 1)
                .shouldNotEnrollAndFulfillAnythingElse();

        new TestForChildEnrollment()
                .givenEnrollmentIn(CHILD_SCHEDULE_BCG, BCG_COMMCARE_VALUE)
                .whenProvidedWithImmunizations("SOME OTHER IMM")
                .shouldNotEnrollAndFulfillAnythingElse();
    }

    @Test
    public void shouldUpdateEnrollmentForDPTWhenDPTHasBeenProvided() {
        new TestForChildEnrollment()
                .givenEnrollmentIn(CHILD_SCHEDULE_DPT, DPT_0_COMMCARE_VALUE, DPT_1_COMMCARE_VALUE, DPT_2_COMMCARE_VALUE, DPT_3_COMMCARE_VALUE)
                .whenProvidedWithImmunizations("dpt_0 dpt_1 dpt_2 dpt_3")
                .shouldFulfillWithFulfillmentDateAsImmunizationsProvidedDate(CHILD_SCHEDULE_DPT, 4)
                .shouldNotEnrollAndFulfillAnythingElse();

        new TestForChildEnrollment()
                .givenEnrollmentIn(CHILD_SCHEDULE_DPT, DPT_1_COMMCARE_VALUE)
                .whenProvidedWithImmunizations("SOME OTHER IMMUNIZATION")
                .shouldNotEnrollAndFulfillAnythingElse();
    }

    @Test
    public void shouldUpdateEnrollmentForHepatitis() {
        new TestForChildEnrollment()
                .givenEnrollmentIn(CHILD_SCHEDULE_HEPATITIS, HEPATITIS_0_COMMCARE_VALUE, HEPATITIS_1_COMMCARE_VALUE, HEPATITIS_2_COMMCARE_VALUE, HEPATITIS_3_COMMCARE_VALUE)
                .whenProvidedWithImmunizations("hepb_0 hepb_1 hepb_2 hepb_3")
                .shouldFulfillWithFulfillmentDateAsImmunizationsProvidedDate(CHILD_SCHEDULE_HEPATITIS, 4)
                .shouldNotEnrollAndFulfillAnythingElse();

        new TestForChildEnrollment()
                .givenEnrollmentIn(CHILD_SCHEDULE_HEPATITIS, HEPATITIS_0_COMMCARE_VALUE)
                .whenProvidedWithImmunizations("SOME OTHER IMMUNIZATION")
                .shouldNotEnrollAndFulfillAnythingElse();
    }

    @Test
    public void shouldUpdateEnrollmentForMeasles() {
        new TestForChildEnrollment()
                .givenEnrollmentIn(CHILD_SCHEDULE_MEASLES, MEASLES_COMMCARE_VALUE)
                .whenProvidedWithImmunizations("measles")
                .shouldFulfillWithFulfillmentDateAsImmunizationsProvidedDate(CHILD_SCHEDULE_MEASLES, 1)
                .shouldEnrollWithEnrollmentDateAsImmunizationsProvidedDate(CHILD_SCHEDULE_MEASLES_BOOSTER)
                .shouldNotEnrollAndFulfillAnythingElse();

        new TestForChildEnrollment()
                .givenEnrollmentIn(CHILD_SCHEDULE_MEASLES, MEASLES_COMMCARE_VALUE)
                .whenProvidedWithImmunizations("SOME OTHER IMMUNIZATION")
                .shouldNotEnrollAndFulfillAnythingElse();
    }

    @Test
    public void shouldUpdateEnrollmentForOPV() {
        new TestForChildEnrollment()
                .givenEnrollmentIn(CHILD_SCHEDULE_OPV, OPV_0_COMMCARE_VALUE, OPV_1_COMMCARE_VALUE, OPV_2_COMMCARE_VALUE, OPV_3_COMMCARE_VALUE)
                .whenProvidedWithImmunizations("opv_0 opv_1 opv_2 opv_3")
                .shouldFulfillWithFulfillmentDateAsImmunizationsProvidedDate(CHILD_SCHEDULE_OPV, 4)
                .shouldNotEnrollAndFulfillAnythingElse();

        new TestForChildEnrollment()
                .givenEnrollmentIn(CHILD_SCHEDULE_OPV, OPV_2_COMMCARE_VALUE)
                .whenProvidedWithImmunizations("SOME OTHER IMMUNIZATION")
                .shouldNotEnrollAndFulfillAnythingElse();
    }

    @Test
    public void shouldUpdateEnrollmentsWhenMultipleDifferentKindsOfEnrollmentsArePresent() {
        new TestForChildEnrollment()
                .givenEnrollmentIn(CHILD_SCHEDULE_BCG, BCG_COMMCARE_VALUE)
                .givenEnrollmentIn(CHILD_SCHEDULE_OPV, OPV_1_COMMCARE_VALUE)
                .givenEnrollmentIn(CHILD_SCHEDULE_DPT, DPT_3_COMMCARE_VALUE)
                .whenProvidedWithImmunizations("dpt_1 dpt_3 hepb_0 hepb_3 measlesbooster opv_1")
                .shouldFulfillWithFulfillmentDateAsImmunizationsProvidedDate(CHILD_SCHEDULE_OPV, 1)
                .shouldFulfillWithFulfillmentDateAsImmunizationsProvidedDate(CHILD_SCHEDULE_DPT, 1)
                .shouldNotEnrollAndFulfillAnythingElse();
    }

    @Test
    public void shouldCloseAllOpenSchedulesWhenAChildIsUnEnrolled() {
        new TestForChildEnrollment()
                .givenEnrollmentIn(CHILD_SCHEDULE_BCG, BCG_COMMCARE_VALUE)
                .givenEnrollmentIn(CHILD_SCHEDULE_OPV, OPV_1_COMMCARE_VALUE)
                .givenEnrollmentIn(CHILD_SCHEDULE_HEPATITIS, HEPATITIS_0_COMMCARE_VALUE)
                .givenEnrollmentIn(CHILD_SCHEDULE_DPT, DPT_0_COMMCARE_VALUE)
                .whenUnenrolled()
                .shouldUnEnrollFrom(CHILD_SCHEDULE_BCG, CHILD_SCHEDULE_OPV, CHILD_SCHEDULE_HEPATITIS, CHILD_SCHEDULE_DPT);
    }

    private class TestForChildEnrollment {
        private final String caseId = "Case X";
        private final String name = "Asha";
        private final String dateOfBirth = "2012-01-01";
        private final String immunizationsDate = "2012-05-04";

        private final ScheduleTrackingService scheduleTrackingService;
        private final ChildSchedulesService childSchedulesService;

        private List<EnrollmentRecord> allEnrollments;

        public TestForChildEnrollment() {
            scheduleTrackingService = mock(ScheduleTrackingService.class);
            childSchedulesService = new ChildSchedulesService(scheduleTrackingService);
            allEnrollments = new ArrayList<>();
        }

        public TestForChildEnrollment givenEnrollmentIn(String schedule, String... milestoneNames) {
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

        public TestForChildEnrollment givenEnrollmentWillHappenIn(String schedule, String... milestoneNames){
            ArrayList<EnrollmentRecord> records = new ArrayList<>();
            for (String milestoneName : milestoneNames) {
                records.add(new EnrollmentRecord(caseId, schedule, milestoneName, null, null, null, null, null, null, null));
            }

            when(scheduleTrackingService.getEnrollment(caseId,schedule)).thenReturn(null, records.toArray(new EnrollmentRecord[0]));
            allEnrollments.addAll(records);

            return this;
        }

        public TestForChildEnrollment whenUnenrolled() {
            when(scheduleTrackingService.search(any(EnrollmentsQuery.class))).thenReturn(allEnrollments);

            childSchedulesService.unenrollChild(caseId);

            return this;
        }

        public TestForChildEnrollment whenProvidedWithImmunizations(String providedImmunizations) {
            childSchedulesService.updateEnrollments(new ChildImmunizationUpdationRequest(caseId, "ANM X", providedImmunizations, immunizationsDate));

            return this;
        }

        public TestForChildEnrollment whenEnrolledWithImmunizationsProvided(String... immunizationsProvided) {
            setExpectationsForNonDependentSchedules();

            childSchedulesService.enrollChild(new ChildInformation(caseId, null, null, name, null, dateOfBirth, join(asList(immunizationsProvided), " "), null));

            return this;
        }

        private void setExpectationsForNonDependentSchedules() {
            this.givenEnrollmentIn(CHILD_SCHEDULE_BCG, BCG_COMMCARE_VALUE)
                    .givenEnrollmentIn(CHILD_SCHEDULE_DPT, DPT_0_COMMCARE_VALUE, DPT_1_COMMCARE_VALUE, DPT_2_COMMCARE_VALUE, DPT_3_COMMCARE_VALUE)
                    .givenEnrollmentIn(CHILD_SCHEDULE_HEPATITIS, HEPATITIS_0_COMMCARE_VALUE, HEPATITIS_1_COMMCARE_VALUE, HEPATITIS_2_COMMCARE_VALUE, HEPATITIS_3_COMMCARE_VALUE)
                    .givenEnrollmentIn(CHILD_SCHEDULE_MEASLES, MEASLES_COMMCARE_VALUE)
                    .givenEnrollmentIn(CHILD_SCHEDULE_OPV, OPV_0_COMMCARE_VALUE, OPV_1_COMMCARE_VALUE, OPV_2_COMMCARE_VALUE, OPV_3_COMMCARE_VALUE);
        }

        public TestForChildEnrollment shouldEnrollWithEnrollmentDateAsDateOfBirth(String... expectedEnrolledSchedules) {
            shouldEnroll(expectedEnrolledSchedules,dateOfBirth);
            return this;
        }

        public TestForChildEnrollment shouldEnrollWithEnrollmentDateAsImmunizationsProvidedDate(String... expectedEnrolledSchedules) {
            shouldEnroll(expectedEnrolledSchedules,immunizationsDate);
            return this;
        }

        private void shouldEnroll(String[] expectedEnrolledSchedules, String enrollmentDate) {
            for (String expectedEnrolledSchedule : expectedEnrolledSchedules) {
                verify(scheduleTrackingService).enroll(enrollmentFor(caseId, expectedEnrolledSchedule, LocalDate.parse(enrollmentDate)));
            }
        }

        public TestForChildEnrollment shouldFulfillWithFulfillmentDateAsDateOfBirth(String expectedFulfillment, int numberOfTimes) {
            shouldFulfill(expectedFulfillment, numberOfTimes, dateOfBirth);
            return this;
        }

        public TestForChildEnrollment shouldFulfillWithFulfillmentDateAsImmunizationsProvidedDate(String expectedFulfillment, int numberOfTimes) {
            shouldFulfill(expectedFulfillment, numberOfTimes, immunizationsDate);
            return this;
        }

        private void shouldFulfill(String expectedFulfillment, int numberOfTimes, String dateOfFulfillment) {
            verify(scheduleTrackingService, times(numberOfTimes)).fulfillCurrentMilestone(caseId, expectedFulfillment, LocalDate.parse(dateOfFulfillment));
        }

        public TestForChildEnrollment shouldNotEnrollAndFulfillAnythingElse() {
            verify(scheduleTrackingService, atLeastOnce()).getEnrollment(eq(caseId), any(String.class));
            verifyNoMoreInteractions(scheduleTrackingService);

            return this;
        }

        public TestForChildEnrollment shouldUnEnrollFrom(String... schedules) {
            verify(scheduleTrackingService).search(any(EnrollmentsQuery.class));
            for (String schedule : schedules) {
                verify(scheduleTrackingService).unenroll(caseId, asList(schedule));
            }
            verifyNoMoreInteractions(scheduleTrackingService);

            return this;
        }

    }

    private EnrollmentRequest enrollmentFor(final String caseId, final String scheduleName, final LocalDate lmp) {
        return argThat(new ArgumentMatcher<EnrollmentRequest>() {
            @Override
            public boolean matches(Object o) {
                EnrollmentRequest request = (EnrollmentRequest) o;
                return caseId.equals(request.getExternalId()) && lmp.equals(request.getReferenceDate())
                        && scheduleName.equals(request.getScheduleName());
            }
        });
    }
}
