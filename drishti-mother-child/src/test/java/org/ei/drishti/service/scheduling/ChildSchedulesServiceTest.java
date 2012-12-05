package org.ei.drishti.service.scheduling;

import org.ei.drishti.contract.ChildImmunizationUpdationRequest;
import org.ei.drishti.contract.ChildInformation;
import org.ei.drishti.domain.Child;
import org.ei.drishti.repository.AllChildren;
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
import static org.ei.drishti.common.AllConstants.ChildImmunizationCommCareFields.*;
import static org.ei.drishti.scheduler.DrishtiScheduleConstants.ChildScheduleConstants.*;
import static org.mockito.Matchers.argThat;
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
                .whenEnrolledWithImmunizationsProvided("bcg", "opv_0", "opv_2")
                .shouldEnrollWithEnrollmentDateAsDateOfBirth(CHILD_SCHEDULE_BCG, CHILD_SCHEDULE_DPT1, CHILD_SCHEDULE_HEPATITIS, CHILD_SCHEDULE_MEASLES, CHILD_SCHEDULE_OPV)
                .shouldFulfillWithFulfillmentDateAsDateOfBirth(CHILD_SCHEDULE_BCG, 1)
                .shouldFulfillWithFulfillmentDateAsDateOfBirth(CHILD_SCHEDULE_OPV, 2)
                .shouldNotEnrollAndFulfillAnythingElse();
    }

    @Test
    public void shouldEnrollChildIntoSchedulesButShouldNotUpdateEnrollmentsIfImmunizationsAreNotProvided() {

        new TestForChildEnrollmentAndUpdate()
                .whenEnrolledWithImmunizationsProvided("")
                .shouldEnrollWithEnrollmentDateAsDateOfBirth(CHILD_SCHEDULE_BCG, CHILD_SCHEDULE_DPT1, CHILD_SCHEDULE_HEPATITIS, CHILD_SCHEDULE_MEASLES, CHILD_SCHEDULE_OPV)
                .shouldNotEnrollAndFulfillAnythingElse();
    }

    @Test
    public void shouldEnrollChildIntoDependentModulesIfRequiredAndShouldUpdateEnrollments(){

        new TestForChildEnrollmentAndUpdate()
                .whenEnrolledWithImmunizationsProvided("bcg", "opv_0", "dpt_1", "opv_2", "measles")
                .shouldEnrollWithEnrollmentDateAsDateOfBirth(CHILD_SCHEDULE_BCG, CHILD_SCHEDULE_DPT1, CHILD_SCHEDULE_HEPATITIS, CHILD_SCHEDULE_MEASLES, CHILD_SCHEDULE_OPV)
                .shouldEnrollWithEnrollmentDateAsDateOfBirth(CHILD_SCHEDULE_MEASLES_BOOSTER)
                .shouldEnrollWithEnrollmentDateAsDateOfBirth(CHILD_SCHEDULE_DPT2)
                .shouldFulfillWithFulfillmentDateAsDateOfBirth(CHILD_SCHEDULE_BCG, 1)
                .shouldFulfillWithFulfillmentDateAsDateOfBirth(CHILD_SCHEDULE_OPV, 2)
                .shouldFulfillWithFulfillmentDateAsDateOfBirth(CHILD_SCHEDULE_DPT1, 1)
                .shouldFulfillWithFulfillmentDateAsDateOfBirth(CHILD_SCHEDULE_MEASLES, 1)
                .shouldNotEnrollAndFulfillAnythingElse();
    }

    @Test
    public void shouldEnrollDependentSchedulesEvenIfDependeeIsNotPresentButImmunizationIsGiven() {
        new TestForChildEnrollmentAndUpdate()
                .givenEnrollmentWillHappenIn(CHILD_SCHEDULE_MEASLES_BOOSTER, MEASLES_BOOSTER_COMMCARE_VALUE)
                .givenEnrollmentWillHappenIn(CHILD_SCHEDULE_DPT_BOOSTER2, DPT_BOOSTER_2_COMMCARE_VALUE)
                .whenProvidedWithImmunizations("bcg opv_0 measles dptbooster_1 opvbooster")
                .shouldEnrollWithEnrollmentDateAsImmunizationsProvidedDate(CHILD_SCHEDULE_MEASLES_BOOSTER)
                .shouldEnrollWithEnrollmentDateAsImmunizationsProvidedDate(CHILD_SCHEDULE_DPT_BOOSTER2)
                .shouldNotEnrollAndFulfillAnythingElse();
    }

    @Test
    public void shouldNotEnrollDependentScheduleIfAlreadyEnrolled(){
        new TestForChildEnrollmentAndUpdate()
                .givenEnrollmentIn(CHILD_SCHEDULE_MEASLES_BOOSTER, MEASLES_BOOSTER_COMMCARE_VALUE)
                .whenProvidedWithImmunizations("measles")
                .shouldNotEnrollAndFulfillAnythingElse();
    }

    @Test
    public void shouldNotEnrollDependentSchedulesIfImmunizationForThemIsAlreadyGiven() {
        new TestForChildEnrollmentAndUpdate()
                .givenChildIsAlreadyProvidedWithImmunizations("dpt_2")
                .whenProvidedWithImmunizations("dpt_1")
                .shouldNotEnrollAndFulfillAnythingElse();
    }

    @Test
    public void shouldNotUpdateEnrollmentForAScheduleWhenNotEnrolledInSchedule() {

        new TestForChildEnrollmentAndUpdate()
                .whenProvidedWithImmunizations("bcg opv_0 hepb_1 opv_1 hepb_2 opv_2 hepb_2 hepb_3 opvbooster")
                .shouldNotEnrollAndFulfillAnythingElse();
    }

    @Test
    public void shouldUpdateEnrollmentForBCGOnlyWhenBCGHasBeenProvided() {
        new TestForChildEnrollmentAndUpdate()
                .givenEnrollmentIn(CHILD_SCHEDULE_BCG, BCG_COMMCARE_VALUE)
                .whenProvidedWithImmunizations("bcg")
                .shouldFulfillWithFulfillmentDateAsImmunizationsProvidedDate(CHILD_SCHEDULE_BCG, 1)
                .shouldNotEnrollAndFulfillAnythingElse();

        new TestForChildEnrollmentAndUpdate()
                .givenEnrollmentIn(CHILD_SCHEDULE_BCG, BCG_COMMCARE_VALUE)
                .whenProvidedWithImmunizations("SOME OTHER IMM")
                .shouldNotEnrollAndFulfillAnythingElse();
    }

    @Test
    public void shouldUpdateEnrollmentForDPTWhenDPTHasBeenProvided() {
        new TestForChildEnrollmentAndUpdate()
                .givenEnrollmentIn(CHILD_SCHEDULE_DPT1, DPT_1_COMMCARE_VALUE)
                .givenEnrollmentWillHappenIn(CHILD_SCHEDULE_DPT2, DPT_2_COMMCARE_VALUE)
                .givenEnrollmentWillHappenIn(CHILD_SCHEDULE_DPT3, DPT_3_COMMCARE_VALUE)
                .whenProvidedWithImmunizations("dpt_1 dpt_2")
                .shouldEnrollWithEnrollmentDateAsImmunizationsProvidedDate(CHILD_SCHEDULE_DPT2)
                .shouldEnrollWithEnrollmentDateAsImmunizationsProvidedDate(CHILD_SCHEDULE_DPT3)
                .shouldFulfillWithFulfillmentDateAsImmunizationsProvidedDate(CHILD_SCHEDULE_DPT1, 1)
                .shouldFulfillWithFulfillmentDateAsImmunizationsProvidedDate(CHILD_SCHEDULE_DPT2, 1)
                .shouldNotEnrollAndFulfillAnythingElse();

        new TestForChildEnrollmentAndUpdate()
                .givenEnrollmentIn(CHILD_SCHEDULE_DPT1, DPT_1_COMMCARE_VALUE)
                .whenProvidedWithImmunizations("SOME OTHER IMMUNIZATION")
                .shouldNotEnrollAndFulfillAnythingElse();
    }

    @Test
    public void shouldUpdateEnrollmentForHepatitis() {
        new TestForChildEnrollmentAndUpdate()
                .givenEnrollmentIn(CHILD_SCHEDULE_HEPATITIS, HEPATITIS_0_COMMCARE_VALUE, HEPATITIS_1_COMMCARE_VALUE, HEPATITIS_2_COMMCARE_VALUE, HEPATITIS_3_COMMCARE_VALUE)
                .whenProvidedWithImmunizations("hepb_0 hepb_1 hepb_2 hepb_3")
                .shouldFulfillWithFulfillmentDateAsImmunizationsProvidedDate(CHILD_SCHEDULE_HEPATITIS, 4)
                .shouldNotEnrollAndFulfillAnythingElse();

        new TestForChildEnrollmentAndUpdate()
                .givenEnrollmentIn(CHILD_SCHEDULE_HEPATITIS, HEPATITIS_0_COMMCARE_VALUE)
                .whenProvidedWithImmunizations("SOME OTHER IMMUNIZATION")
                .shouldNotEnrollAndFulfillAnythingElse();
    }

    @Test
    public void shouldUpdateEnrollmentForMeasles() {
        new TestForChildEnrollmentAndUpdate()
                .givenEnrollmentIn(CHILD_SCHEDULE_MEASLES, MEASLES_COMMCARE_VALUE)
                .whenProvidedWithImmunizations("measles")
                .shouldFulfillWithFulfillmentDateAsImmunizationsProvidedDate(CHILD_SCHEDULE_MEASLES, 1)
                .shouldEnrollWithEnrollmentDateAsImmunizationsProvidedDate(CHILD_SCHEDULE_MEASLES_BOOSTER)
                .shouldNotEnrollAndFulfillAnythingElse();

        new TestForChildEnrollmentAndUpdate()
                .givenEnrollmentIn(CHILD_SCHEDULE_MEASLES, MEASLES_COMMCARE_VALUE)
                .whenProvidedWithImmunizations("SOME OTHER IMMUNIZATION")
                .shouldNotEnrollAndFulfillAnythingElse();
    }

    @Test
    public void shouldUpdateEnrollmentForOPV() {
        new TestForChildEnrollmentAndUpdate()
                .givenEnrollmentIn(CHILD_SCHEDULE_OPV, OPV_0_COMMCARE_VALUE, OPV_1_COMMCARE_VALUE, OPV_2_COMMCARE_VALUE, OPV_3_COMMCARE_VALUE)
                .whenProvidedWithImmunizations("opv_0 opv_1 opv_2 opv_3")
                .shouldFulfillWithFulfillmentDateAsImmunizationsProvidedDate(CHILD_SCHEDULE_OPV, 4)
                .shouldNotEnrollAndFulfillAnythingElse();

        new TestForChildEnrollmentAndUpdate()
                .givenEnrollmentIn(CHILD_SCHEDULE_OPV, OPV_2_COMMCARE_VALUE)
                .whenProvidedWithImmunizations("SOME OTHER IMMUNIZATION")
                .shouldNotEnrollAndFulfillAnythingElse();
    }

    @Test
    public void shouldUpdateEnrollmentsWhenMultipleDifferentKindsOfEnrollmentsArePresent() {
        new TestForChildEnrollmentAndUpdate()
                .givenEnrollmentIn(CHILD_SCHEDULE_BCG, BCG_COMMCARE_VALUE)
                .givenEnrollmentIn(CHILD_SCHEDULE_OPV, OPV_1_COMMCARE_VALUE)
                .givenEnrollmentIn(CHILD_SCHEDULE_DPT2, DPT_2_COMMCARE_VALUE)
                .whenProvidedWithImmunizations("dpt_2 hepb_0 measlesbooster opv_1")
                .shouldEnrollWithEnrollmentDateAsImmunizationsProvidedDate(CHILD_SCHEDULE_DPT3)
                .shouldFulfillWithFulfillmentDateAsImmunizationsProvidedDate(CHILD_SCHEDULE_OPV, 1)
                .shouldFulfillWithFulfillmentDateAsImmunizationsProvidedDate(CHILD_SCHEDULE_DPT2, 1)
                .shouldNotEnrollAndFulfillAnythingElse();
    }

    @Test
    public void shouldCloseAllOpenSchedulesWhenAChildIsUnEnrolled() {
        new TestForChildEnrollmentAndUpdate()
                .givenEnrollmentIn(CHILD_SCHEDULE_BCG, BCG_COMMCARE_VALUE)
                .givenEnrollmentIn(CHILD_SCHEDULE_OPV, OPV_1_COMMCARE_VALUE)
                .givenEnrollmentIn(CHILD_SCHEDULE_HEPATITIS, HEPATITIS_0_COMMCARE_VALUE)
                .givenEnrollmentIn(CHILD_SCHEDULE_DPT1, DPT_1_COMMCARE_VALUE)
                .whenUnenrolled()
                .shouldUnEnrollFrom(CHILD_SCHEDULE_BCG, CHILD_SCHEDULE_OPV, CHILD_SCHEDULE_HEPATITIS, CHILD_SCHEDULE_DPT1);
    }

    private class TestForChildEnrollmentAndUpdate {
        private final String caseId = "Case X";
        private final String name = "Asha";
        private final String dateOfBirth = "2012-01-01";
        private final String immunizationsDate = "2012-05-04";

        private final ScheduleTrackingService scheduleTrackingService;

        private List<EnrollmentRecord> allEnrollments;

        public TestForChildEnrollmentAndUpdate() {
            scheduleTrackingService = mock(ScheduleTrackingService.class);
            childSchedulesService = new ChildSchedulesService(scheduleTrackingService, allChildren);
            allEnrollments = new ArrayList<>();
            initCommonExpectations();
        }

        private void initCommonExpectations() {
            Child child = mock(Child.class);
            when(allChildren.findByCaseId(caseId)).thenReturn(child);
            when(child.immunizationsProvided()).thenReturn(asList(""));
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

        public TestForChildEnrollmentAndUpdate givenEnrollmentWillHappenIn(String schedule, String... milestoneNames){
            ArrayList<EnrollmentRecord> records = new ArrayList<>();
            for (String milestoneName : milestoneNames) {
                records.add(new EnrollmentRecord(caseId, schedule, milestoneName, null, null, null, null, null, null, null));
            }

            when(scheduleTrackingService.getEnrollment(caseId,schedule)).thenReturn(null, records.toArray(new EnrollmentRecord[0]));
            allEnrollments.addAll(records);

            return this;
        }

        public TestForChildEnrollmentAndUpdate whenUnenrolled() {
            when(scheduleTrackingService.search(any(EnrollmentsQuery.class))).thenReturn(allEnrollments);

            childSchedulesService.unenrollChild(caseId);

            return this;
        }

        public TestForChildEnrollmentAndUpdate whenProvidedWithImmunizations(String providedImmunizations) {
            childSchedulesService.updateEnrollments(new ChildImmunizationUpdationRequest(caseId, "ANM X", providedImmunizations, immunizationsDate));

            return this;
        }

        public TestForChildEnrollmentAndUpdate whenEnrolledWithImmunizationsProvided(String... immunizationsProvided) {
            setExpectationsForNonDependentSchedules();

            childSchedulesService.enrollChild(new ChildInformation(caseId, null, null, name, null, dateOfBirth, join(asList(immunizationsProvided), " "), null));

            return this;
        }

        private void setExpectationsForNonDependentSchedules() {
            this.givenEnrollmentIn(CHILD_SCHEDULE_BCG, BCG_COMMCARE_VALUE)
                    .givenEnrollmentIn(CHILD_SCHEDULE_DPT1, DPT_1_COMMCARE_VALUE)
                    .givenEnrollmentIn(CHILD_SCHEDULE_HEPATITIS, HEPATITIS_0_COMMCARE_VALUE, HEPATITIS_1_COMMCARE_VALUE, HEPATITIS_2_COMMCARE_VALUE, HEPATITIS_3_COMMCARE_VALUE)
                    .givenEnrollmentIn(CHILD_SCHEDULE_MEASLES, MEASLES_COMMCARE_VALUE)
                    .givenEnrollmentIn(CHILD_SCHEDULE_OPV, OPV_0_COMMCARE_VALUE, OPV_1_COMMCARE_VALUE, OPV_2_COMMCARE_VALUE, OPV_3_COMMCARE_VALUE);
        }

        public TestForChildEnrollmentAndUpdate shouldEnrollWithEnrollmentDateAsDateOfBirth(String... expectedEnrolledSchedules) {
            shouldEnroll(expectedEnrolledSchedules,dateOfBirth);
            return this;
        }

        public TestForChildEnrollmentAndUpdate shouldEnrollWithEnrollmentDateAsImmunizationsProvidedDate(String... expectedEnrolledSchedules) {
            shouldEnroll(expectedEnrolledSchedules,immunizationsDate);
            return this;
        }

        private void shouldEnroll(String[] expectedEnrolledSchedules, String enrollmentDate) {
            for (String expectedEnrolledSchedule : expectedEnrolledSchedules) {
                verify(scheduleTrackingService).enroll(enrollmentFor(caseId, expectedEnrolledSchedule, LocalDate.parse(enrollmentDate)));
            }
        }

        public TestForChildEnrollmentAndUpdate shouldFulfillWithFulfillmentDateAsDateOfBirth(String expectedFulfillment, int numberOfTimes) {
            shouldFulfill(expectedFulfillment, numberOfTimes, dateOfBirth);
            return this;
        }

        public TestForChildEnrollmentAndUpdate shouldFulfillWithFulfillmentDateAsImmunizationsProvidedDate(String expectedFulfillment, int numberOfTimes) {
            shouldFulfill(expectedFulfillment, numberOfTimes, immunizationsDate);
            return this;
        }

        private void shouldFulfill(String expectedFulfillment, int numberOfTimes, String dateOfFulfillment) {
            verify(scheduleTrackingService, times(numberOfTimes)).fulfillCurrentMilestone(caseId, expectedFulfillment, LocalDate.parse(dateOfFulfillment));
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
            when(child.immunizationsProvided()).thenReturn(asList(immunizationsAlreadyProvided.split(" ")));
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
