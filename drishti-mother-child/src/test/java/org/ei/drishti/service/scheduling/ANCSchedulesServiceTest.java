package org.ei.drishti.service.scheduling;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.ei.drishti.service.ActionService;
import org.ei.drishti.service.FastForwardScheduleTestBase;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.Mock;
import org.motechproject.model.Time;
import org.motechproject.scheduletracking.api.service.EnrollmentRecord;
import org.motechproject.scheduletracking.api.service.EnrollmentsQuery;
import org.motechproject.scheduletracking.api.service.ScheduleTrackingService;
import org.motechproject.testing.utils.BaseUnitTest;

import java.util.List;

import static java.util.Arrays.asList;
import static org.ei.drishti.common.util.DateUtil.fakeIt;
import static org.ei.drishti.common.util.DateUtil.today;
import static org.ei.drishti.scheduler.DrishtiScheduleConstants.MotherScheduleConstants.*;
import static org.joda.time.LocalDate.parse;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.scheduletracking.api.domain.EnrollmentStatus.ACTIVE;
import static org.powermock.api.mockito.PowerMockito.*;

public class ANCSchedulesServiceTest extends BaseUnitTest {
    @Mock
    private ScheduleTrackingService trackingService;
    @Mock
    private ActionService actionService;
    @Mock
    private ScheduleService scheduleService;

    private ANCSchedulesService ancSchedulesService;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        ancSchedulesService = new ANCSchedulesService(trackingService, actionService, scheduleService);
    }

    @Test
    public void shouldEnrollMotherIntoSchedules() {
        fakeIt(parse("2012-01-01"));
        LocalDate lmp = today().minusDays(3);

        ancSchedulesService.enrollMother("Case X", lmp);

        verify(scheduleService).enroll("Case X", SCHEDULE_ANC, "ANC 1", lmp.toString());
        verifyNonANCScheduleEnrollments(lmp);
        verifyNoMoreInteractions(trackingService);
    }

    @Test
    public void shouldEnrollMotherIntoANC1WhenTheMotherIsEnrolledBefore14WeeksFromLMP() throws Exception {
        assertEnrollmentIntoMilestoneBasedOnDate(new LocalDate(2012, 1, 1), "ANC 1");
        assertEnrollmentIntoMilestoneBasedOnDate(new LocalDate(2012, 4, 6), "ANC 1");
        assertEnrollmentIntoMilestoneBasedOnDate(new LocalDate(2012, 4, 7), "ANC 1");
    }

    @Test
    public void shouldEnrollMotherIntoANC2WhenTheMotherIsEnrolledBetween16And28WeeksFromLMP() throws Exception {
        assertEnrollmentIntoMilestoneBasedOnDate(new LocalDate(2012, 4, 8), "ANC 2");
        assertEnrollmentIntoMilestoneBasedOnDate(new LocalDate(2012, 7, 13), "ANC 2");
        assertEnrollmentIntoMilestoneBasedOnDate(new LocalDate(2012, 7, 14), "ANC 2");
    }

    @Test
    public void shouldEnrollMotherIntoANC3WhenTheMotherIsEnrolledBetween28And34WeeksFromLMP() throws Exception {
        assertEnrollmentIntoMilestoneBasedOnDate(new LocalDate(2012, 7, 15), "ANC 3");
        assertEnrollmentIntoMilestoneBasedOnDate(new LocalDate(2012, 8, 24), "ANC 3");
        assertEnrollmentIntoMilestoneBasedOnDate(new LocalDate(2012, 8, 25), "ANC 3");
        assertEnrollmentIntoMilestoneBasedOnDate(new LocalDate(2012, 9, 8), "ANC 3");
    }

    @Test
    public void shouldEnrollMotherIntoANC3WhenTheMotherIsEnrolledAfter34WeeksFromLMP() throws Exception {
        assertEnrollmentIntoMilestoneBasedOnDate(new LocalDate(2012, 9, 9), "ANC 4");
        assertEnrollmentIntoMilestoneBasedOnDate(new LocalDate(2012, 10, 6), "ANC 4");
        assertEnrollmentIntoMilestoneBasedOnDate(new LocalDate(2012, 10, 7), "ANC 4");
        assertEnrollmentIntoMilestoneBasedOnDate(new LocalDate(2013, 10, 7), "ANC 4");
    }

    @Test
    public void shouldNotEnrollMotherIntoANC1WhenLMPDateIsAfterTodayOrMoreThan40WeeksBeforeToday() throws Exception {
        assertNoEnrollmentIntoMilestone(new LocalDate(2011, 12, 31));
        assertNoEnrollmentIntoMilestone(new LocalDate(2012, 10, 8));
    }

    @Test
    public void shouldFulfillANCScheduleWhenTheExpectedANCVisitHappens() {
        fakeIt(parse("2012-01-01"));
        new FastForwardScheduleTestBase().forANCSchedule().whenExpecting("ANC 1").providedWithVisitNumber(1).willFulfillFor("ANC 1");
    }

    @Test
    public void shouldNotFulfillANCMilestoneWhichHasAlreadyBeenFulfilled() {
        new FastForwardScheduleTestBase().forANCSchedule().whenExpecting("ANC 3").providedWithVisitNumber(1).willNotFulfillAnything();
    }

    @Test
    public void shouldFulfillAllMilestonesBetweenTheCurrentOneAndTheOneCorrespondingToTheVisitNumber() {
        new FastForwardScheduleTestBase().forANCSchedule().whenExpecting("ANC 1").providedWithVisitNumber(3).willFulfillFor("ANC 1", "ANC 2", "ANC 3");
    }

    @Test
    public void shouldFulfillTT1MilestoneWhenTT1IsExpectedDuringANCCare() {
        new FastForwardScheduleTestBase().forTT1Schedule().whenExpecting("TT 1").providedWithVisitNumber(1).willFulfillFor("TT 1");
    }

    @Test
    public void shouldFulfillTT2MilestoneWhenTT2IsExpectedDuringANCCare() {
        new FastForwardScheduleTestBase().forTT2Schedule().whenExpecting("TT 2").providedWithVisitNumber(2).willFulfillFor("TT 2");
    }

    @Test
    public void shouldFulfillIFA1MilestoneWhenIFA1IsExpectedAndIFATabletsGiven() {
        new FastForwardScheduleTestBase().forIFA1Schedule().providedWithNumberOfIFATablets("100").whenExpecting("IFA 1").willFulfillFor("IFA 1");
    }

    @Test
    public void shouldFulfillIFA2MilestoneWhenIFA2IsExpectedAndIFATabletsGiven() {
        new FastForwardScheduleTestBase().forIFA2Schedule().providedWithNumberOfIFATablets("100").whenExpecting("IFA 2").willFulfillFor("IFA 2");
    }

    @Test
    public void shouldFulfillIFA3MilestoneWhenIFA3IsExpectedAndIFATabletsGiven() {
        new FastForwardScheduleTestBase().forIFA3Schedule().providedWithNumberOfIFATablets("100").whenExpecting("IFA 3").willFulfillFor("IFA 3");
    }

    @Test
    public void shouldDoNothingWhenIFATabletsNotGiven() {
        new FastForwardScheduleTestBase().forIFA1Schedule().providedWithNumberOfIFATablets("0").whenExpecting("IFA 1").willNotFulfillAnything();
    }

    @Test
    public void shouldEnrollMotherInTT2ScheduleIfTT1IsProvided() throws Exception {
        fakeIt(parse("2012-01-01"));
        when(trackingService.getEnrollment("entity id 1", "TT 1")).thenReturn(
                new EnrollmentRecord("entity id 1", "TT 1", "TT 1", null, null, null, null, null, null, null));

        ancSchedulesService.ttVisitHasHappened("entity id 1", "ANM 1", "tt1", "2012-01-01");

        verify(trackingService).fulfillCurrentMilestone(eq("entity id 1"), eq("TT 1"), eq(today()), any(Time.class));
        verify(actionService).markAlertAsClosed("entity id 1", "ANM 1", "TT 1", "2012-01-01");
        verify(scheduleService).enroll("entity id 1", "TT 2", "2012-01-01");
    }

    @Test
    public void shouldFullFillTT1AndTT2IfTTBoosterIsProvided() throws Exception {
        fakeIt(parse("2012-01-01"));
        when(trackingService.getEnrollment("entity id 1", "TT 1")).thenReturn(
                new EnrollmentRecord("entity id 1", "TT 1", "TT 1", null, null, null, null, null, null, null));
        when(trackingService.getEnrollment("entity id 1", "TT 2")).thenReturn(
                new EnrollmentRecord("entity id 1", "TT 2", "TT 2", null, null, null, null, null, null, null));

        ancSchedulesService.ttVisitHasHappened("entity id 1", "ANM 1", "ttbooster", "2012-01-01");

        verify(actionService).markAlertAsInactive("ANM 1", "entity id 1", "TT 1");
        verify(trackingService).unenroll("entity id 1", asList("TT 1"));
        verifyZeroInteractions(scheduleService);
    }

    @Test
    public void shouldNotEnrollMotherInTT2ScheduleIfSomeOtherThingIsProvided() throws Exception {
        ancSchedulesService.ttVisitHasHappened("entity id 1", "ANM 1", "some other", "2012-01-01");

        verifyZeroInteractions(trackingService);
    }

    @Test
    public void shouldNotFulfillANCIfANCScheduleIsAlreadyOver() throws Exception {
        when(trackingService.getEnrollment("Entity X", SCHEDULE_ANC)).thenReturn(null);

        ancSchedulesService.ancVisitHasHappened("Entity X", "ANM 1", 2, "2012-01-23");

        verify(trackingService).getEnrollment("Entity X", SCHEDULE_ANC);
        verifyNoMoreInteractions(trackingService);
        verifyZeroInteractions(actionService);
    }

    @Test
    public void shouldFulfillCurrentMilestoneForGivenExternalIdWhenVisitHasBeenMissed() {
        fakeIt(parse("2012-01-01"));

        ancSchedulesService.forceFulfillMilestone("Case X", "Schedule 1");

        verify(trackingService).fulfillCurrentMilestone(eq("Case X"), eq("Schedule 1"), eq(today()), any(Time.class));
    }

    @Test
    public void shouldUnenrollAMotherFromAllOpenSchedulesAndRaiseDeleteAllAlertActionDuringClose() {
        EnrollmentRecord record1 = new EnrollmentRecord("Case X", "Schedule 1", null, null, null, null, null, null, null, null);
        EnrollmentRecord record2 = new EnrollmentRecord("Case X", "Schedule 2", null, null, null, null, null, null, null, null);
        List<EnrollmentRecord> records = asList(record1, record2);

        when(trackingService.search(queryFor("Case X"))).thenReturn(records);

        ancSchedulesService.unEnrollFromAllSchedules("Case X");

        verify(trackingService).unenroll("Case X", asList("Schedule 1"));
        verify(trackingService).unenroll("Case X", asList("Schedule 2"));
        verify(actionService).markAllAlertsAsInactive("Case X");
    }

    @Test
    public void shouldFulfillHbTest1WhenHbTestIsDone() {
        when(trackingService.getEnrollment("Case X", "Hb Test 1"))
                .thenReturn(new EnrollmentRecord("Case X", "Hb Test 1", "HB Test 1", null, null, null, null, null, null, null));

        ancSchedulesService.hbTestDone("Case X", "ANM 1", "2013-01-01", null, parse("2012-09-01"));

        verify(trackingService).fulfillCurrentMilestone(eq("Case X"), eq("Hb Test 1"), eq(parse("2013-01-01")), any(Time.class));
    }

    @Test
    public void shouldEnrollANCToHbFollowupTestWhenHbTestIsDoneAndSheIsAnaemic() {
        when(trackingService.getEnrollment("Case X", "Hb Test 1"))
                .thenReturn(new EnrollmentRecord("Case X", "Hb Test 1", "HB Test 1", null, null, null, null, null, null, null));

        ancSchedulesService.hbTestDone("Case X", "ANM 1", "2013-01-01", "Anaemic", null);

        verify(scheduleService).enroll("Case X", "Hb Followup Test", "2013-01-01");
    }

    @Test
    public void shouldEnrollANCToHbTest2WhenHbTestIsDoneAndSheIsNotAnaemic() {
        when(trackingService.getEnrollment("Case X", "Hb Test 1"))
                .thenReturn(new EnrollmentRecord("Case X", "Hb Test 1", "HB Test 1", null, null, null, null, null, null, null));

        ancSchedulesService.hbTestDone("Case X", "ANM 1", "2013-01-01", null, parse("2012-09-01"));

        verify(scheduleService).enroll("Case X", "Hb Test 2", "2012-09-01");
    }

    @Test
    public void shouldFulfillHbFollowupTestWhenHbTestIsDone() {
        when(trackingService.getEnrollment("Case X", "Hb Test 1")).thenReturn(null);
        when(trackingService.getEnrollment("Case X", "Hb Followup Test"))
                .thenReturn(new EnrollmentRecord("Case X", "Hb Followup Test", "Hb Followup Test", null, null, null, null, null, null, null));

        ancSchedulesService.hbTestDone("Case X", "ANM 1", "2013-01-01", "Anaemic", parse("2012-09-01"));

        verify(trackingService).fulfillCurrentMilestone(eq("Case X"), eq("Hb Followup Test"), eq(parse("2013-01-01")), any(Time.class));
    }

    @Test
    public void shouldFulfillHbTest2WhenHbTestIsDoneAndFollowupIsFulfilledAndDateIs28WeeksAfterLMP() {
        when(trackingService.getEnrollment("Case X", "Hb Test 1")).thenReturn(null);
        when(trackingService.getEnrollment("Case X", "Hb Followup Test"))
                .thenReturn(new EnrollmentRecord("Case X", "Hb Followup Test", "Hb Followup Test", null, null, null, null, null, null, null));
        when(trackingService.getEnrollment("Case X", "Hb Test 2"))
                .thenReturn(new EnrollmentRecord("Case X", "Hb Test 2", "Hb Test 2", null, null, null, null, null, null, null));

        ancSchedulesService.hbTestDone("Case X", "ANM 1", "2013-04-16", "Anaemic", parse("2012-09-01"));

        verify(trackingService).fulfillCurrentMilestone(eq("Case X"), eq("Hb Test 2"), eq(parse("2013-04-16")), any(Time.class));
    }

    @Test
    public void shouldFulfillScheduleWhenDeliveryHasBeenPlanned() {
        when(trackingService.getEnrollment("Case X", "Delivery Plan"))
                .thenReturn(new EnrollmentRecord("Case X", "Delivery Plan", "Delivery Plan", null, null, null, null, null, null, null));

        ancSchedulesService.deliveryHasBeenPlanned("Case X", "ANM id 1", "2013-01-01");

        verify(trackingService).fulfillCurrentMilestone(eq("Case X"), eq("Delivery Plan"), eq(parse("2013-01-01")), any(Time.class));
    }

    @Test
    public void shouldEnrollToHbTest2WhenHbTestIsDoneAndFollowupIsFulfilledAndDateIsNot28WeeksAfterLMP() {
        when(trackingService.getEnrollment("Case X", "Hb Test 1")).thenReturn(null);
        when(trackingService.getEnrollment("Case X", "Hb Followup Test"))
                .thenReturn(new EnrollmentRecord("Case X", "Hb Followup Test", "Hb Followup Test", null, null, null, null, null, null, null));
        when(trackingService.getEnrollment("Case X", "Hb Test 2"))
                .thenReturn(new EnrollmentRecord("Case X", "Hb Test 2", "Hb Test 2", null, null, null, null, null, null, null));

        ancSchedulesService.hbTestDone("Case X", "ANM 1", "2013-02-16", "Anaemic", parse("2012-09-01"));

        verify(trackingService, times(0)).fulfillCurrentMilestone(eq("Case X"), eq("Hb Test 2"), eq(parse("2013-04-16")), any(Time.class));
        verify(scheduleService).enroll("Case X", "Hb Test 2", "2012-09-01");
    }

    @Test
    public void shouldFulfillHbTest2WhenHbTestIsDoneAndItsTheActiveMilestone() {
        when(trackingService.getEnrollment("Case X", "Hb Test 1")).thenReturn(null);
        when(trackingService.getEnrollment("Case X", "Hb Followup Test")).thenReturn(null);
        when(trackingService.getEnrollment("Case X", "Hb Test 2"))
                .thenReturn(new EnrollmentRecord("Case X", "Hb Test 2", "Hb Test 2", null, null, null, null, null, null, null));

        ancSchedulesService.hbTestDone("Case X", "ANM 1", "2013-04-16", "Anaemic", parse("2012-09-01"));

        verify(trackingService).fulfillCurrentMilestone(eq("Case X"), eq("Hb Test 2"), eq(parse("2013-04-16")), any(Time.class));
    }

    private void assertEnrollmentIntoMilestoneBasedOnDate(LocalDate enrollmentDate, String expectedMilestone) throws Exception {
        assertEnrollmentIntoMilestoneBasedOnDate(enrollmentDate, expectedMilestone, 1);
    }

    private void assertNoEnrollmentIntoMilestone(LocalDate enrollmentDate) throws Exception {
        assertEnrollmentIntoMilestoneBasedOnDate(enrollmentDate, "", 0);
    }

    private void assertEnrollmentIntoMilestoneBasedOnDate(LocalDate enrollmentDate, String expectedMilestone, int wantedNumberOfInvocations) throws Exception {
        setUp();
        LocalDate lmp = new LocalDate(2012, 1, 1);
        fakeIt(enrollmentDate);

        ancSchedulesService.enrollMother("Case X", lmp);

        verifyNonANCScheduleEnrollments(lmp);
        verify(scheduleService, times(wantedNumberOfInvocations)).enroll("Case X", SCHEDULE_ANC, expectedMilestone, lmp.toString());
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

    private void verifyNonANCScheduleEnrollments(LocalDate lmp) {
        verify(scheduleService).enroll("Case X", SCHEDULE_EDD, lmp.toString());
        verify(scheduleService).enroll("Case X", SCHEDULE_LAB, lmp.toString());
        verify(scheduleService).enroll("Case X", SCHEDULE_TT_1, lmp.toString());
        verify(scheduleService).enroll("Case X", SCHEDULE_IFA_1, lmp.toString());
        verify(scheduleService).enroll("Case X", SCHEDULE_HB_TEST_1, lmp.toString());
    }
}
