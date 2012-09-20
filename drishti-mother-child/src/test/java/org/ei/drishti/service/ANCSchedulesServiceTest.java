package org.ei.drishti.service;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.ei.drishti.contract.AnteNatalCareInformation;
import org.ei.drishti.dto.AlertPriority;
import org.ei.drishti.dto.BeneficiaryType;
import org.hamcrest.Description;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
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

import static org.ei.drishti.scheduler.DrishtiSchedules.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.scheduletracking.api.domain.EnrollmentStatus.ACTIVE;
import static org.motechproject.util.DateUtil.today;
import static org.powermock.api.mockito.PowerMockito.verifyZeroInteractions;
import static org.powermock.api.mockito.PowerMockito.when;

public class ANCSchedulesServiceTest extends BaseUnitTest {
    @Mock
    private ScheduleTrackingService scheduleTrackingService;
    @Mock
    private ActionService actionService;
    private ANCSchedulesService schedulesService;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        schedulesService = new ANCSchedulesService(scheduleTrackingService, actionService);
    }

    @Test
    public void shouldEnrollMotherIntoSchedules() {
        LocalDate lmp = DateUtil.today().minusDays(3);

        schedulesService.enrollMother("Case X", lmp, new Time(15, 0), new Time(14, 0));

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
        new FastForwardScheduleTestBase().forANCSchedule().whenExpecting("ANC 1").providedWithVisitNumber(1).willFulfillFor("ANC 1");
    }

    @Test
    public void shouldNotFulfillANCMilestoneWhichHasAlreadyBeenFulfilled() {
        new FastForwardScheduleTestBase().forANCSchedule().whenExpecting("ANC 3").providedWithVisitNumber(1);
    }

    @Test
    public void shouldFulfillAllMilestonesBetweenTheCurrentOneAndTheOneCorrespondingToTheVisitNumber() {
        new FastForwardScheduleTestBase().forANCSchedule().whenExpecting("ANC 1").providedWithVisitNumber(3).willFulfillFor("ANC 1", "ANC 2", "ANC 3");
    }

    @Test
    public void shouldFulfillTT1MilestoneWhenTT1IsExpectedDuringANCCare() {
        new FastForwardScheduleTestBase().forTTSchedule().whenExpecting("TT 1").providedWithVisitNumber(1).willFulfillFor("TT 1");
    }

    @Test
    public void shouldFulfillTT2MilestoneWhenTT2IsExpectedDuringANCCare() {
        new FastForwardScheduleTestBase().forTTSchedule().whenExpecting("TT 2").providedWithVisitNumber(2).willFulfillFor("TT 2");
    }

    @Test
    public void shouldFulfillBothTT1AndTT2MilestoneWhenTT1IsExpectedDuringANCCareAndTT2IsProvided() {
        new FastForwardScheduleTestBase().forTTSchedule().whenExpecting("TT 1").providedWithVisitNumber(2).willFulfillFor("TT 1", "TT 2");
    }

    @Test
    public void shouldFulfillIFA1IfItIsTheCurrentMilestoneWhenIFAIsProvided() {
        new FastForwardScheduleTestBase().forIFASchedule().whenExpecting("IFA 1").willFulfillFor("IFA 1");
    }

    @Test
    public void shouldFulfillIFA2IfItIsTheCurrentMilestoneWhenIFAIsProvided() {
        new FastForwardScheduleTestBase().forIFASchedule().whenExpecting("IFA 2").willFulfillFor("IFA 2");
    }

    @Test
    public void shouldNotFulfillIFAIfIFAScheduleIsAlreadyOver() throws Exception {
        when(scheduleTrackingService.getEnrollment("Case X", SCHEDULE_IFA)).thenReturn(null);

        schedulesService.ifaVisitHasHappened(new AnteNatalCareInformation("Case X", "ANM 1", 0, "2012-01-23"));

        verify(scheduleTrackingService).getEnrollment("Case X", SCHEDULE_IFA);
        verifyNoMoreInteractions(scheduleTrackingService);
        verifyZeroInteractions(actionService);
    }

    @Test
    public void shouldNotFulfillANCIfANCScheduleIsAlreadyOver() throws Exception {
        when(scheduleTrackingService.getEnrollment("Case X", SCHEDULE_ANC)).thenReturn(null);

        schedulesService.ancVisitHasHappened(new AnteNatalCareInformation("Case X", "ANM 1", 2, "2012-01-23"));

        verify(scheduleTrackingService).getEnrollment("Case X", SCHEDULE_ANC);
        verifyNoMoreInteractions(scheduleTrackingService);
        verifyZeroInteractions(actionService);
    }

    @Test
    public void shouldFulfillCurrentMilestoneForGivenExternalIdWhenVisitHasBeenMissed() {
        schedulesService.forceFulfillMilestone("Case X", "Schedule 1");

        verify(scheduleTrackingService).fulfillCurrentMilestone(eq("Case X"), eq("Schedule 1"), eq(today()), any(Time.class));
    }

    @Test
    public void shouldUnenrollAMotherFromAllOpenSchedulesAndRaiseDeleteAllAlertActionDuringClose() {
        EnrollmentRecord record1 = new EnrollmentRecord("Case X", "Schedule 1", null, null, null, null, null, null, null, null);
        EnrollmentRecord record2 = new EnrollmentRecord("Case X", "Schedule 2", null, null, null, null, null, null, null, null);
        List<EnrollmentRecord> records = Arrays.asList(record1, record2);

        when(scheduleTrackingService.search(queryFor("Case X"))).thenReturn(records);

        schedulesService.unEnrollFromSchedules("Case X");

        verify(scheduleTrackingService).unenroll("Case X", Arrays.asList("Schedule 1"));
        verify(scheduleTrackingService).unenroll("Case X", Arrays.asList("Schedule 2"));
        verify(actionService).deleteAllAlertsForMother("Case X");
    }

    private void assertEnrollmentIntoMilestoneBasedOnDate(LocalDate enrollmentDate, String expectedMilestone) throws Exception {
        setUp();
        LocalDate lmp = new LocalDate(2012, 1, 1);
        mockCurrentDate(enrollmentDate);

        schedulesService.enrollMother("Case X", lmp, new Time(15, 0), new Time(14, 0));

        verify(scheduleTrackingService).enroll(ancEnrollmentFor("Case X", SCHEDULE_ANC, lmp, expectedMilestone));
        verify(actionService).alertForBeneficiary(BeneficiaryType.mother, "Case X", expectedMilestone, AlertPriority.normal, lmp.toDateTime(new LocalTime(14, 0)), lmp.plusWeeks(12).toDateTime(new LocalTime(14, 0)));
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
                        && expectedMilestoneThePersonWillBeEnrolledInto.equals(request.getStartingMilestoneName())
                        && new Time(14, 0).equals(request.getPreferredAlertTime())
                        && new Time(15, 0).equals(request.getReferenceTime());
            }

            @Override
            public void describeTo(Description description) {
                description.appendValue("Case: " + caseId + ", Schedule: " + scheduleName + ", Milestone: " + expectedMilestoneThePersonWillBeEnrolledInto);
            }
        });
    }

    private EnrollmentsQuery queryFor(final String externalId) {
        return argThat(new ArgumentMatcher<EnrollmentsQuery>() {
            @Override
            public boolean matches(Object o) {
                EnrollmentsQuery expectedQuery = new EnrollmentsQuery().havingExternalId(externalId).havingState(ACTIVE);
                return EqualsBuilder.reflectionEquals(expectedQuery.getCriteria(), ((EnrollmentsQuery) o).getCriteria());
            }
        });
    }

}
