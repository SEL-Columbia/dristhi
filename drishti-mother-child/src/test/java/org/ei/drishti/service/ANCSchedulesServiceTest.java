package org.ei.drishti.service;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.hamcrest.Description;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.Mock;
import org.motechproject.model.Time;
import org.motechproject.scheduletracking.api.service.EnrollmentRecord;
import org.motechproject.scheduletracking.api.service.EnrollmentRequest;
import org.motechproject.scheduletracking.api.service.EnrollmentsQuery;
import org.motechproject.scheduletracking.api.service.ScheduleTrackingService;
import org.motechproject.testing.utils.BaseUnitTest;
import org.motechproject.util.DateUtil;

import java.util.Arrays;
import java.util.List;

import static org.ei.drishti.service.ANCSchedulesService.*;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.scheduletracking.api.domain.EnrollmentStatus.ACTIVE;
import static org.powermock.api.mockito.PowerMockito.when;

public class ANCSchedulesServiceTest extends BaseUnitTest {
    @Mock
    private ScheduleTrackingService scheduleTrackingService;
    private ANCSchedulesService schedulesService;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        schedulesService = new ANCSchedulesService(scheduleTrackingService);
    }

    @Test
    public void shouldEnrollMotherIntoSchedules() {
        LocalDate lmp = DateUtil.today().minusDays(3);

        schedulesService.enrollMother("Case X", lmp, new Time(14, 0));

        verify(scheduleTrackingService).enroll(ancEnrollmentFor("Case X", SCHEDULE_ANC, lmp, "ANC 1"));
        verify(scheduleTrackingService).enroll(enrollmentFor("Case X", SCHEDULE_EDD, lmp));
        verify(scheduleTrackingService).enroll(enrollmentFor("Case X", SCHEDULE_IFA, lmp));
        verify(scheduleTrackingService).enroll(enrollmentFor("Case X", SCHEDULE_LAB, lmp));
        verify(scheduleTrackingService).enroll(enrollmentFor("Case X", SCHEDULE_TT, lmp));
        verifyNoMoreInteractions(scheduleTrackingService);
    }

    @Test
    public void shouldEnrollMotherIntoANC1WhenTheMotherIsEnrolledBefore16WeeksFromLMP() throws Exception {
        assertEnrollmentIntoMilestoneBasedOnDate(new LocalDate(2012, 1, 1), "ANC 1");
        assertEnrollmentIntoMilestoneBasedOnDate(new LocalDate(2012, 4, 20), "ANC 1");
        assertEnrollmentIntoMilestoneBasedOnDate(new LocalDate(2012, 4, 21), "ANC 1");
    }

    @Test
    public void shouldEnrollMotherIntoANC2WhenTheMotherIsEnrolledBetween16And28WeeksFromLMP() throws Exception {
        assertEnrollmentIntoMilestoneBasedOnDate(new LocalDate(2012, 4, 22), "ANC 2");
        assertEnrollmentIntoMilestoneBasedOnDate(new LocalDate(2012, 7, 13), "ANC 2");
        assertEnrollmentIntoMilestoneBasedOnDate(new LocalDate(2012, 7, 14), "ANC 2");
    }

    @Test
    public void shouldEnrollMotherIntoANC3WhenTheMotherIsEnrolledBetween28And34WeeksFromLMP() throws Exception {
        assertEnrollmentIntoMilestoneBasedOnDate(new LocalDate(2012, 7, 15), "ANC 3");
        assertEnrollmentIntoMilestoneBasedOnDate(new LocalDate(2012, 8, 24), "ANC 3");
        assertEnrollmentIntoMilestoneBasedOnDate(new LocalDate(2012, 8, 25), "ANC 3");
    }

    @Test
    public void shouldEnrollMotherIntoANC3WhenTheMotherIsEnrolledAfter34WeeksFromLMP() throws Exception {
        assertEnrollmentIntoMilestoneBasedOnDate(new LocalDate(2012, 8, 26), "ANC 4");
        assertEnrollmentIntoMilestoneBasedOnDate(new LocalDate(2012, 10, 6), "ANC 4");
        assertEnrollmentIntoMilestoneBasedOnDate(new LocalDate(2012, 10, 7), "ANC 4");
    }

    @Test
    public void shouldEnrollMotherIntoANC1WhenLMPDateIsAfterTodayOrMoreThan40WeeksBeforeToday() throws Exception {
        assertEnrollmentIntoMilestoneBasedOnDate(new LocalDate(2012, 2, 1), "ANC 1");
        assertEnrollmentIntoMilestoneBasedOnDate(new LocalDate(2012, 10, 8), "ANC 1");
    }

    @Test
    public void shouldFulfillANCScheduleWhenTheExpectedANCVisitHappens() {
        new FastForwardScheduleTestBase(scheduleTrackingService).forANCSchedule().whenExpecting("ANC 1").providedWithVisitNumber(1).willFulFillTimes(1);
    }

    @Test
    public void shouldNotFulfillANCMilestoneWhichHasAlreadyBeenFulfilled() {
        new FastForwardScheduleTestBase(scheduleTrackingService).forANCSchedule().whenExpecting("ANC 3").providedWithVisitNumber(1).willFulFillTimes(0);
    }

    @Test
    public void shouldFulfillAllMilestonesBetweenTheCurrentOneAndTheOneCorrespondingToTheVisitNumber() {
        new FastForwardScheduleTestBase(scheduleTrackingService).forANCSchedule().whenExpecting("ANC 1").providedWithVisitNumber(3).willFulFillTimes(3);
    }

    @Test
    public void shouldFulfillTT1MilestoneWhenTT1IsExpectedDuringANCCare() {
        new FastForwardScheduleTestBase(scheduleTrackingService).forTTSchedule().whenExpecting("TT 1").providedWithVisitNumber(1).willFulFillTimes(1);
    }

    @Test
    public void shouldFulfillTT2MilestoneWhenTT2IsExpectedDuringANCCare() {
        new FastForwardScheduleTestBase(scheduleTrackingService).forTTSchedule().whenExpecting("TT 2").providedWithVisitNumber(2).willFulFillTimes(1);
    }

    @Test
    public void shouldFulfillBothTT1AndTT2MilestoneWhenTT1IsExpectedDuringANCCareAndTT2IsProvided() {
        new FastForwardScheduleTestBase(scheduleTrackingService).forTTSchedule().whenExpecting("TT 1").providedWithVisitNumber(2).willFulFillTimes(2);
    }

    @Test
    public void shouldFulfillIFA1MilestoneWhenIFA1IsExpectedDuringANCCare() {
        new FastForwardScheduleTestBase(scheduleTrackingService).forIFASchedule().whenExpecting("IFA 1").providedWithVisitNumber(1).willFulFillTimes(1);
    }

    @Test
    public void shouldFulfillIFA2MilestoneWhenIFA2IsExpectedDuringANCCare() {
        new FastForwardScheduleTestBase(scheduleTrackingService).forIFASchedule().whenExpecting("IFA 2").providedWithVisitNumber(2).willFulFillTimes(1);
    }

    @Test
    public void shouldFulfillBothIFA1AndIFA2MilestoneWhenIFA1IsExpectedDuringANCCareAndIFA2IsProvided() {
        new FastForwardScheduleTestBase(scheduleTrackingService).forIFASchedule().whenExpecting("IFA 1").providedWithVisitNumber(2).willFulFillTimes(2);
    }

    @Test
    public void shouldUnEnrollAMotherFromAllOpenSchedulesDuringClose() {
        EnrollmentRecord record1 = new EnrollmentRecord("Case X", "Schedule 1", null, null, null, null, null, null, null, null);
        EnrollmentRecord record2 = new EnrollmentRecord("Case X", "Schedule 2", null, null, null, null, null, null, null, null);
        List<EnrollmentRecord> records = Arrays.asList(record1, record2);

        when(scheduleTrackingService.search(queryFor("Case X"))).thenReturn(records);

        schedulesService.closeCase("Case X");

        verify(scheduleTrackingService).unenroll("Case X", Arrays.asList("Schedule 1"));
        verify(scheduleTrackingService).unenroll("Case X", Arrays.asList("Schedule 2"));
    }

    private void assertEnrollmentIntoMilestoneBasedOnDate(LocalDate enrollmentDate, String expectedMilestone) throws Exception {
        setUp();
        LocalDate lmp = new LocalDate(2012, 1, 1);
        mockCurrentDate(enrollmentDate);

        schedulesService.enrollMother("Case X", lmp, new Time(14, 0));

        verify(scheduleTrackingService).enroll(ancEnrollmentFor("Case X", SCHEDULE_ANC, lmp, expectedMilestone));
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

    private EnrollmentRequest ancEnrollmentFor(final String caseId, final String scheduleName, final LocalDate lmp, final String expectedMilestoneThePersonWillBeEnrolledInto) {
        return argThat(new ArgumentMatcher<EnrollmentRequest>() {
            @Override
            public boolean matches(Object o) {
                EnrollmentRequest request = (EnrollmentRequest) o;
                return caseId.equals(request.getExternalId())
                        && lmp.equals(request.getReferenceDate())
                        && scheduleName.equals(request.getScheduleName())
                        && expectedMilestoneThePersonWillBeEnrolledInto.equals(request.getStartingMilestoneName());
            }

            @Override
            public void describeTo(Description description) {
                description.appendValue("Case: " + caseId + ", Schedule: " + scheduleName + ", Milstone: " + expectedMilestoneThePersonWillBeEnrolledInto);
            }
        });
    }

    private EnrollmentsQuery queryFor(final String externalId) {
        return argThat(new ArgumentMatcher<EnrollmentsQuery>() {
            @Override
            public boolean matches(Object o) {
                EnrollmentsQuery expectedQuery = new EnrollmentsQuery().havingExternalId(externalId).havingState(ACTIVE.toString());
                return EqualsBuilder.reflectionEquals(expectedQuery.getCriteria(), ((EnrollmentsQuery) o).getCriteria());
            }
        });
    }

    private EnrollmentRecord ancEnrollmentRecord(String currentMilestone) {
        return new EnrollmentRecord("Case X", SCHEDULE_ANC, currentMilestone, null, null, null, null, null, null, null);
    }

    private EnrollmentRecord ttEnrollmentRecord(String currentMilestone) {
        return new EnrollmentRecord("Case X", SCHEDULE_TT, currentMilestone, null, null, null, null, null, null, null);
    }
}
