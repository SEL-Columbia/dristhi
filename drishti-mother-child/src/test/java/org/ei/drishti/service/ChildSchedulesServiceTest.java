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
import org.motechproject.util.DateUtil;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static org.apache.commons.lang.StringUtils.join;
import static org.ei.drishti.scheduler.DrishtiScheduleConstants.ChildScheduleConstants.*;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class ChildSchedulesServiceTest {
    @Mock
    private ScheduleTrackingService scheduleTrackingService;

    private ChildSchedulesService childSchedulesService;

    private final String immunizationsDate = "2012-05-04";


    @Before
    public void setUp() throws Exception {
        initMocks(this);
        childSchedulesService = new ChildSchedulesService(scheduleTrackingService);
    }

    @Test
    public void shouldEnrollChildIntoSchedulesAndShouldUpdateEnrollments() {

        new TestForChildEnrollment()
                .whenEnrolledWithImmunizationsProvided("bcg", "opv_0", "dpt_2", "opv_2")
                .shouldEnroll(CHILD_SCHEDULE_BCG, CHILD_SCHEDULE_DPT, CHILD_SCHEDULE_HEPATITIS, CHILD_SCHEDULE_MEASLES, CHILD_SCHEDULE_OPV)
                .shouldNotEnroll(CHILD_SCHEDULE_MEASLES_BOOSTER)
                .shouldFulfill(CHILD_SCHEDULE_BCG, 1)
                .shouldFulfill(CHILD_SCHEDULE_OPV, 2)
                .shouldFulfill(CHILD_SCHEDULE_DPT, 1)
                .shouldNotFulfillAnythingElse();
    }

    @Test
    public void shouldEnrollChildIntoDependentModulesIfRequiredAndShouldUpdateEnrollments(){

        new TestForChildEnrollment()
                .whenEnrolledWithImmunizationsProvided("bcg", "opv_0", "dpt_2", "opv_2", "measles")
                .shouldEnroll(CHILD_SCHEDULE_BCG, CHILD_SCHEDULE_DPT, CHILD_SCHEDULE_HEPATITIS, CHILD_SCHEDULE_MEASLES, CHILD_SCHEDULE_OPV)
                .shouldEnroll(LocalDate.now(), CHILD_SCHEDULE_MEASLES_BOOSTER)
                .shouldFulfill(CHILD_SCHEDULE_BCG, 1)
                .shouldFulfill(CHILD_SCHEDULE_OPV, 2)
                .shouldFulfill(CHILD_SCHEDULE_DPT, 1)
                .shouldFulfill(CHILD_SCHEDULE_MEASLES, 1)
                .shouldNotFulfillAnythingElse();
    }

    @Test
    public void shouldNotUpdateEnrollmentForAScheduleWhenNotEnrolledInSchedule() {
        String manyImmunizations = "bcg opv_0 hepb_1 opv_1 dpt_1 hepb_2 opv_2 dpt_2 hepb_2 dpt_3 hepb_3 dptbooster1 dptbooster2 opvbooster";

        new TestForChildEnrollment()
                .whenProvidedWithImmunizations(manyImmunizations)
                .shouldNotFulfillAnythingElse();
    }

    @Test
    public void shouldEnrollInDependentSchedulesAndUpdateThemEvenIfDependyIsNotPresentButImmunizationIsGiven() {
        String manyImmunizations = "bcg opv_0 measles measlesbooster dptbooster2 opvbooster";

        new TestForChildEnrollment()
                .givenEnrollmentWillHappenIn(CHILD_SCHEDULE_MEASLES_BOOSTER, CHILD_SCHEDULE_MEASLES_BOOSTER_MILESTONE)
                .whenProvidedWithImmunizations(manyImmunizations)
                .shouldEnroll(LocalDate.parse(immunizationsDate),CHILD_SCHEDULE_MEASLES_BOOSTER)
                .shouldFulfill(CHILD_SCHEDULE_MEASLES_BOOSTER,1)
                .shouldNotFulfillAnythingElse();
    }

    @Test
    public void shouldNotEnrollDependentScheduleIfAlreadyEnrolled(){
        new TestForChildEnrollment()
                .givenEnrollmentIn(CHILD_SCHEDULE_MEASLES_BOOSTER, "Measles Booster")
                .whenProvidedWithImmunizations("measles")
                .shouldNotFulfillAnythingElse();
    }

    @Test
    public void shouldUpdateEnrollmentForBCGOnlyWhenBCGHasBeenProvided() {
        new TestForChildEnrollment()
                .givenEnrollmentIn(CHILD_SCHEDULE_BCG, CHILD_SCHEDULE_BCG_MILESTONE)
                .whenProvidedWithImmunizations("bcg")
                .shouldFulfill(CHILD_SCHEDULE_BCG, 1)
                .shouldNotFulfillAnythingElse();

        new TestForChildEnrollment()
                .givenEnrollmentIn(CHILD_SCHEDULE_BCG, CHILD_SCHEDULE_BCG_MILESTONE)
                .whenProvidedWithImmunizations("SOME OTHER IMM")
                .shouldNotFulfillAnythingElse();
    }

    @Test
    public void shouldUpdateEnrollmentForDPTWhenDPTHasBeenProvided() {
        new TestForChildEnrollment()
                .givenEnrollmentIn(CHILD_SCHEDULE_DPT, CHILD_SCHEDULE_DPT_MILESTONE_0,CHILD_SCHEDULE_DPT_MILESTONE_1, CHILD_SCHEDULE_DPT_MILESTONE_2, CHILD_SCHEDULE_DPT_MILESTONE_3)
                .whenProvidedWithImmunizations("dpt_0 dpt_1 dpt_2 dpt_3")
                .shouldFulfill(CHILD_SCHEDULE_DPT, 4)
                .shouldNotFulfillAnythingElse();

        new TestForChildEnrollment()
                .givenEnrollmentIn(CHILD_SCHEDULE_DPT, CHILD_SCHEDULE_DPT_MILESTONE_1)
                .whenProvidedWithImmunizations("SOME OTHER IMMUNIZATION")
                .shouldNotFulfillAnythingElse();
    }

    @Test
    public void shouldUpdateEnrollmentForHepatitis() {
        new TestForChildEnrollment()
                .givenEnrollmentIn(CHILD_SCHEDULE_HEPATITIS, CHILD_SCHEDULE_HEPATITIS_MILESTONE_1, CHILD_SCHEDULE_HEPATITIS_MILESTONE_2, CHILD_SCHEDULE_HEPATITIS_MILESTONE_3, CHILD_SCHEDULE_HEPATITIS_MILESTONE_4)
                .whenProvidedWithImmunizations("hepb_1 hepb_2 hepb_3 hepb_4")
                .shouldFulfill(CHILD_SCHEDULE_HEPATITIS, 4)
                .shouldNotFulfillAnythingElse();

        new TestForChildEnrollment()
                .givenEnrollmentIn(CHILD_SCHEDULE_HEPATITIS, CHILD_SCHEDULE_HEPATITIS_MILESTONE_1)
                .whenProvidedWithImmunizations("SOME OTHER IMMUNIZATION")
                .shouldNotFulfillAnythingElse();
    }

    @Test
    public void shouldUpdateEnrollmentForMeasles() {
        new TestForChildEnrollment()
                .givenEnrollmentIn(CHILD_SCHEDULE_MEASLES, CHILD_SCHEDULE_MEASLES_MILESTONE)
                .whenProvidedWithImmunizations("measles")
                .shouldFulfill(CHILD_SCHEDULE_MEASLES, 1)
                .shouldEnroll(LocalDate.parse(immunizationsDate), CHILD_SCHEDULE_MEASLES_BOOSTER)
                .shouldNotFulfillAnythingElse();

        new TestForChildEnrollment()
                .givenEnrollmentIn(CHILD_SCHEDULE_MEASLES, CHILD_SCHEDULE_MEASLES_MILESTONE)
                .whenProvidedWithImmunizations("SOME OTHER IMMUNIZATION")
                .shouldNotFulfillAnythingElse();
    }

    @Test
    public void shouldUpdateEnrollmentForOPV() {
        new TestForChildEnrollment()
                .givenEnrollmentIn(CHILD_SCHEDULE_OPV, CHILD_SCHEDULE_OPV_MILESTONE_0,CHILD_SCHEDULE_OPV_MILESTONE_1, CHILD_SCHEDULE_OPV_MILESTONE_2, CHILD_SCHEDULE_OPV_MILESTONE_3)
                .whenProvidedWithImmunizations("opv_0 opv_1 opv_2 opv_3")
                .shouldFulfill(CHILD_SCHEDULE_OPV, 4)
                .shouldNotFulfillAnythingElse();

        new TestForChildEnrollment()
                .givenEnrollmentIn(CHILD_SCHEDULE_OPV, CHILD_SCHEDULE_OPV_MILESTONE_2)
                .whenProvidedWithImmunizations("SOME OTHER IMMUNIZATION")
                .shouldNotFulfillAnythingElse();
    }

    @Test
    public void shouldUpdateEnrollmentsWhenMultipleDifferentKindsOfEnrollmentsArePresent() {
        new TestForChildEnrollment()
                .givenEnrollmentIn(CHILD_SCHEDULE_BCG, CHILD_SCHEDULE_BCG_MILESTONE)
                .givenEnrollmentIn(CHILD_SCHEDULE_OPV, CHILD_SCHEDULE_OPV_MILESTONE_1)
                .givenEnrollmentIn(CHILD_SCHEDULE_DPT, CHILD_SCHEDULE_DPT_MILESTONE_3)
                .whenProvidedWithImmunizations("dpt_1 dpt_3 hepb_0 hepb_3 measlesbooster opv_1")
                .shouldFulfill(CHILD_SCHEDULE_OPV, 1)
                .shouldFulfill(CHILD_SCHEDULE_DPT, 1)
                .shouldNotFulfillAnythingElse();
    }

    @Test
    public void shouldCloseAllOpenSchedulesWhenAChildIsUnEnrolled() {
        new TestForChildEnrollment()
                .givenEnrollmentIn(CHILD_SCHEDULE_BCG, CHILD_SCHEDULE_BCG_MILESTONE)
                .givenEnrollmentIn(CHILD_SCHEDULE_OPV, CHILD_SCHEDULE_OPV_MILESTONE_1)
                .givenEnrollmentIn(CHILD_SCHEDULE_HEPATITIS, CHILD_SCHEDULE_HEPATITIS_MILESTONE_1)
                .givenEnrollmentIn(CHILD_SCHEDULE_DPT, CHILD_SCHEDULE_DPT_MILESTONE_0)
                .whenUnenrolled()
                .shouldUnEnrollFrom(CHILD_SCHEDULE_BCG, CHILD_SCHEDULE_OPV, CHILD_SCHEDULE_HEPATITIS, CHILD_SCHEDULE_DPT);
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

    private class TestForChildEnrollment {
        private final String caseId = "Case X";
        private final String name = "Asha";
        private final String dateOfBirth = "2012-01-01";

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

            childSchedulesService.unenrollChild("Case X");

            return this;
        }

        public TestForChildEnrollment whenProvidedWithImmunizations(String providedImmunizations) {
            childSchedulesService.updateEnrollments(new ChildImmunizationUpdationRequest(caseId, "ANM X", providedImmunizations, immunizationsDate));

            return this;
        }

        public TestForChildEnrollment whenEnrolledWithImmunizationsProvided(String... immunizationsProvided) {
            setExpectationsOnScheduleTrackingService();

            childSchedulesService.enrollChild(new ChildInformation(caseId, null, null, name, null, dateOfBirth, join(asList(immunizationsProvided), " "), null));

            return this;
        }

        private void setExpectationsOnScheduleTrackingService() {
            this.givenEnrollmentIn(CHILD_SCHEDULE_BCG, CHILD_SCHEDULE_BCG_MILESTONE)
                    .givenEnrollmentIn(CHILD_SCHEDULE_DPT, CHILD_SCHEDULE_DPT_MILESTONE_0, CHILD_SCHEDULE_DPT_MILESTONE_1, CHILD_SCHEDULE_DPT_MILESTONE_2, CHILD_SCHEDULE_DPT_MILESTONE_3)
                    .givenEnrollmentIn(CHILD_SCHEDULE_HEPATITIS, CHILD_SCHEDULE_HEPATITIS_MILESTONE_1, CHILD_SCHEDULE_HEPATITIS_MILESTONE_2, CHILD_SCHEDULE_HEPATITIS_MILESTONE_3, CHILD_SCHEDULE_HEPATITIS_MILESTONE_4)
                    .givenEnrollmentIn(CHILD_SCHEDULE_MEASLES, CHILD_SCHEDULE_MEASLES_MILESTONE)
                    .givenEnrollmentIn(CHILD_SCHEDULE_OPV, CHILD_SCHEDULE_OPV_MILESTONE_0, CHILD_SCHEDULE_OPV_MILESTONE_1, CHILD_SCHEDULE_OPV_MILESTONE_2, CHILD_SCHEDULE_OPV_MILESTONE_3);
        }

        public TestForChildEnrollment shouldEnroll(String... expectedEnrolledSchedules) {
            for (String expectedEnrolledSchedule : expectedEnrolledSchedules) {
                verify(scheduleTrackingService).enroll(enrollmentFor("Case X", expectedEnrolledSchedule, LocalDate.parse(dateOfBirth)));
            }

            return this;
        }

        public TestForChildEnrollment shouldEnroll(LocalDate referenceDate,String... expectedEnrolledSchedules) {
            for (String expectedEnrolledSchedule : expectedEnrolledSchedules) {
                verify(scheduleTrackingService).enroll(enrollmentFor("Case X", expectedEnrolledSchedule, referenceDate));
            }

            return this;
        }

        public TestForChildEnrollment shouldNotEnroll(String... schedules){
            for (String schedule : schedules) {
                verify(scheduleTrackingService, times(0)).enroll(enrollmentFor("Case X", schedule, LocalDate.parse(dateOfBirth)));
            }
            return this;
        }

        public TestForChildEnrollment shouldFulfill(String expectedFulfillment, int numberOfTimes) {
            verify(scheduleTrackingService, times(numberOfTimes)).fulfillCurrentMilestone(caseId, expectedFulfillment, DateUtil.today());

            return this;
        }

        public void shouldNotFulfillAnythingElse() {
            verify(scheduleTrackingService, atLeastOnce()).getEnrollment(eq("Case X"), any(String.class));
            verifyNoMoreInteractions(scheduleTrackingService);
        }

        public TestForChildEnrollment shouldUnEnrollFrom(String... schedules) {
            verify(scheduleTrackingService).search(any(EnrollmentsQuery.class));
            for (String schedule : schedules) {
                verify(scheduleTrackingService).unenroll("Case X", asList(schedule));
            }
            verifyNoMoreInteractions(scheduleTrackingService);

            return this;
        }
    }
}
