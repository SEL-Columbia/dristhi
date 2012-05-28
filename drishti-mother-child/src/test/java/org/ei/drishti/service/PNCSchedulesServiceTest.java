package org.ei.drishti.service;

import org.ei.drishti.contract.ChildImmunizationUpdationRequest;
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
import static org.ei.drishti.scheduler.DrishtiSchedules.*;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.util.DateUtil.today;

public class PNCSchedulesServiceTest {
    @Mock
    private ScheduleTrackingService scheduleTrackingService;
    private PNCSchedulesService schedulesService;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        schedulesService = new PNCSchedulesService(scheduleTrackingService);
    }

    @Test
    public void shouldEnrollChildIntoAllChildSchedules() {
        schedulesService.enrollChild("Case X", today());

        verify(scheduleTrackingService).enroll(enrollmentFor("Case X", CHILD_SCHEDULE_BCG, today()));
        verify(scheduleTrackingService).enroll(enrollmentFor("Case X", CHILD_SCHEDULE_DPT, today()));
        verify(scheduleTrackingService).enroll(enrollmentFor("Case X", CHILD_SCHEDULE_HEPATITIS, today()));
        verify(scheduleTrackingService).enroll(enrollmentFor("Case X", CHILD_SCHEDULE_MEASLES, today()));
        verify(scheduleTrackingService).enroll(enrollmentFor("Case X", CHILD_SCHEDULE_OPV, today()));
    }

    @Test
    public void shouldNotUpdateEnrollmentForAnyScheduleWhenNotEnrolledInSchedule() {
        String manyImmunizations = "bcg opv0 hepB0 opv1 dpt1 hepb1 opv2 dpt2 hepb2 dpt3 hepb3 measles MeaslesBooster dptbooster1 dptbooster2 opvbooster";

        new TestForChildEnrollment()
                .whenProvidedWithImmunizations(manyImmunizations)
                .shouldNotFulfillAnythingElse();
    }

    @Test
    public void shouldUpdateEnrollmentForBCGOnlyWhenBCGHasBeenProvided() {
        new TestForChildEnrollment()
                .givenEnrollmentIn(CHILD_SCHEDULE_BCG, "REMINDER")
                .whenProvidedWithImmunizations("bcg")
                .shouldFulfill(CHILD_SCHEDULE_BCG, 1).shouldNotFulfillAnythingElse();

        new TestForChildEnrollment()
                .givenEnrollmentIn(CHILD_SCHEDULE_BCG, "REMINDER")
                .whenProvidedWithImmunizations("SOME OTHER IMM")
                .shouldNotFulfillAnythingElse();
    }

    @Test
    public void shouldUpdateEnrollmentForDPTWhenDPTHasBeenProvided() {
        new TestForChildEnrollment()
                .givenEnrollmentIn(CHILD_SCHEDULE_DPT, "DPT 0", "DPT 1", "DPT 2", "DPT 3")
                .whenProvidedWithImmunizations("dpt0 dpt1 dpt2 dpt3")
                .shouldFulfill(CHILD_SCHEDULE_DPT, 4).shouldNotFulfillAnythingElse();

        new TestForChildEnrollment()
                .givenEnrollmentIn(CHILD_SCHEDULE_DPT, "DPT 0")
                .whenProvidedWithImmunizations("SOME OTHER IMMUNIZATION")
                .shouldNotFulfillAnythingElse();
    }

    @Test
    public void shouldUpdateEnrollmentForHepatitis() {
        new TestForChildEnrollment()
                .givenEnrollmentIn(CHILD_SCHEDULE_HEPATITIS, "Hepatitis B1", "Hepatitis B2", "Hepatitis B3", "Hepatitis B4")
                .whenProvidedWithImmunizations("hepb1 hepb2 hepb3 hepb4")
                .shouldFulfill(CHILD_SCHEDULE_HEPATITIS, 4).shouldNotFulfillAnythingElse();

        new TestForChildEnrollment()
                .givenEnrollmentIn(CHILD_SCHEDULE_HEPATITIS, "Hepatitis B1")
                .whenProvidedWithImmunizations("SOME OTHER IMMUNIZATION")
                .shouldNotFulfillAnythingElse();
    }

    @Test
    public void shouldUpdateEnrollmentForMeasles() {
        new TestForChildEnrollment()
                .givenEnrollmentIn(CHILD_SCHEDULE_MEASLES, "REMINDER")
                .whenProvidedWithImmunizations("measles")
                .shouldFulfill(CHILD_SCHEDULE_MEASLES, 1).shouldNotFulfillAnythingElse();

        new TestForChildEnrollment()
                .givenEnrollmentIn(CHILD_SCHEDULE_MEASLES, "REMINDER")
                .whenProvidedWithImmunizations("SOME OTHER IMMUNIZATION")
                .shouldNotFulfillAnythingElse();
    }

    @Test
    public void shouldUpdateEnrollmentForOPV() {
        new TestForChildEnrollment()
                .givenEnrollmentIn(CHILD_SCHEDULE_OPV, "OPV 0", "OPV 1", "OPV 2", "OPV 3")
                .whenProvidedWithImmunizations("opv0 opv1 opv2 opv3")
                .shouldFulfill(CHILD_SCHEDULE_OPV, 4).shouldNotFulfillAnythingElse();

        new TestForChildEnrollment()
                .givenEnrollmentIn(CHILD_SCHEDULE_OPV, "OPV 2")
                .whenProvidedWithImmunizations("SOME OTHER IMMUNIZATION")
                .shouldNotFulfillAnythingElse();
    }

    @Test
    public void shouldUpdateEnrollmentsWhenMultipleDifferentKindsOfEnrollmentsArePresent() {
        new TestForChildEnrollment()
                .givenEnrollmentIn(CHILD_SCHEDULE_BCG, "REMINDER")
                .givenEnrollmentIn(CHILD_SCHEDULE_OPV, "OPV 1")
                .givenEnrollmentIn(CHILD_SCHEDULE_DPT, "DPT 3")
                .whenProvidedWithImmunizations("dpt1 dpt3 hepB0 hepb3 measles opv1")
                .shouldFulfill(CHILD_SCHEDULE_OPV, 1)
                .shouldFulfill(CHILD_SCHEDULE_DPT, 1)
                .shouldNotFulfillAnythingElse();
    }

    @Test
    public void shouldCloseAllOpenSchedulesWhenAChildIsUnEnrolled() {
        new TestForChildEnrollment()
                .givenEnrollmentIn(CHILD_SCHEDULE_BCG, "REMINDER")
                .givenEnrollmentIn(CHILD_SCHEDULE_OPV, "OPV 1")
                .givenEnrollmentIn(CHILD_SCHEDULE_HEPATITIS, "REMINDER")
                .givenEnrollmentIn(CHILD_SCHEDULE_DPT, "DPT 2")
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

        private final ScheduleTrackingService scheduleTrackingService;

        private final PNCSchedulesService service;
        private List<EnrollmentRecord> allEnrollments;

        public TestForChildEnrollment() {
            scheduleTrackingService = mock(ScheduleTrackingService.class);
            service = new PNCSchedulesService(scheduleTrackingService);
            allEnrollments = new ArrayList<EnrollmentRecord>();
        }

        public TestForChildEnrollment givenEnrollmentIn(String schedule, String... milestoneNames) {
            ArrayList<EnrollmentRecord> records = new ArrayList<EnrollmentRecord>();
            for (String milestoneName : milestoneNames) {
                records.add(new EnrollmentRecord(caseId, schedule, milestoneName, null, null, null, null, null, null, null));
            }

            if (records.size() > 1) {
                when(scheduleTrackingService.getEnrollment(caseId, schedule)).thenReturn(records.get(0), records.subList(1, records.size()).toArray(new EnrollmentRecord[0]));
            }
            else {
                when(scheduleTrackingService.getEnrollment(caseId, schedule)).thenReturn(records.get(0));
            }

            allEnrollments.addAll(records);
            return this;
        }

        public TestForChildEnrollment whenProvidedWithImmunizations(String providedImmunizations) {
            service.updateEnrollments(new ChildImmunizationUpdationRequest(caseId, "ANM X", providedImmunizations));

            return this;
        }

        public TestForChildEnrollment whenUnenrolled() {
            when(scheduleTrackingService.search(any(EnrollmentsQuery.class))).thenReturn(allEnrollments);

            service.unenrollChild("Case X");

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
