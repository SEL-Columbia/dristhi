package org.opensrp.register.service.scheduling;

import static java.util.Arrays.asList;
import static org.joda.time.LocalDate.parse;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.opensrp.common.util.DateUtil.fakeIt;
import static org.opensrp.common.util.DateUtil.today;
import static org.opensrp.register.DrishtiScheduleConstants.MotherScheduleConstants.SCHEDULE_ANC;
import static org.opensrp.register.DrishtiScheduleConstants.MotherScheduleConstants.SCHEDULE_DELIVERY_PLAN;
import static org.opensrp.register.DrishtiScheduleConstants.MotherScheduleConstants.SCHEDULE_EDD;
import static org.opensrp.register.DrishtiScheduleConstants.MotherScheduleConstants.SCHEDULE_HB_TEST_1;
import static org.opensrp.register.DrishtiScheduleConstants.MotherScheduleConstants.SCHEDULE_IFA_1;
import static org.opensrp.register.DrishtiScheduleConstants.MotherScheduleConstants.SCHEDULE_LAB;
import static org.opensrp.register.DrishtiScheduleConstants.MotherScheduleConstants.SCHEDULE_TT_1;
import static org.powermock.api.mockito.PowerMockito.verifyNoMoreInteractions;
import static org.powermock.api.mockito.PowerMockito.verifyZeroInteractions;
import static org.powermock.api.mockito.PowerMockito.when;

import java.util.List;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.scheduletracking.api.service.EnrollmentRecord;
import org.motechproject.testing.utils.BaseUnitTest;
import org.opensrp.register.service.FastForwardScheduleTestBase;
import org.opensrp.register.service.scheduling.ANCSchedulesService;
import org.opensrp.scheduler.HealthSchedulerService;

public class ANCSchedulesServiceTest extends BaseUnitTest {
    @Mock
    private HealthSchedulerService scheduler;
    
    private ANCSchedulesService ancSchedulesService;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        ancSchedulesService = new ANCSchedulesService(scheduler);
    }

    @Test
    public void shouldEnrollMotherIntoSchedules() {
        fakeIt(parse("2012-01-01"));
        LocalDate lmp = today().minusDays(3);

        ancSchedulesService.enrollMother("Case X", lmp);

        verifyNonANCScheduleEnrollments(lmp);
        verify(scheduler).enrollIntoSchedule("Case X", SCHEDULE_ANC, "ANC 1", lmp.toString());
        verifyNoMoreInteractions(scheduler);
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
        when(scheduler.getEnrollment("entity id 1", "TT 1")).thenReturn(
                new EnrollmentRecord("entity id 1", "TT 1", "TT 1", null, null, null, null, null, null, null));

        ancSchedulesService.ttVisitHasHappened("entity id 1", "ANM 1", "tt1", "2012-01-01");

        verify(scheduler).fullfillMilestoneAndCloseAlert(eq("entity id 1"), eq("ANM 1"), eq("TT 1"), eq("TT 1"), eq(today())/*, any(Time.class)*/);
        verify(scheduler).enrollIntoSchedule("entity id 1", "TT 2", "2012-01-01");
    }

    @Test
    public void shouldFullFillTT1AndTT2IfTTBoosterIsProvided() throws Exception {
        fakeIt(parse("2012-01-01"));
        when(scheduler.getEnrollment("entity id 1", "TT 1")).thenReturn(
                new EnrollmentRecord("entity id 1", "TT 1", "TT 1", null, null, null, null, null, null, null));
        when(scheduler.getEnrollment("entity id 1", "TT 2")).thenReturn(
                new EnrollmentRecord("entity id 1", "TT 2", "TT 2", null, null, null, null, null, null, null));

        ancSchedulesService.ttVisitHasHappened("entity id 1", "ANM 1", "ttbooster", "2012-01-01");

//       / verify(actionService).markAlertAsInactive("ANM 1", "entity id 1", "TT 1");
        verify(scheduler).unEnrollFromSchedule("entity id 1", "ANM 1", "TT 1");
        verifyZeroInteractions(scheduler);
    }

    @Test
    public void shouldNotEnrollMotherInTT2ScheduleIfSomeOtherThingIsProvided() throws Exception {
        ancSchedulesService.ttVisitHasHappened("entity id 1", "ANM 1", "some other", "2012-01-01");

        verifyZeroInteractions(scheduler);
    }

    @Test
    public void shouldNotFulfillANCIfANCScheduleIsAlreadyOver() throws Exception {
        when(scheduler.isNotEnrolled("Entity X", SCHEDULE_ANC)).thenReturn(true);
        when(scheduler.getEnrollment("Entity X", SCHEDULE_ANC)).thenReturn(null);

        ancSchedulesService.ancVisitHasHappened("Entity X", "ANM 1", 2, "2012-01-23");

        verify(scheduler).isNotEnrolled("Entity X", SCHEDULE_ANC);
        verifyNoMoreInteractions(scheduler);
    }

    @Test
    public void shouldfullfillMilestoneAndCloseAlertForGivenExternalIdWhenVisitHasBeenMissed() {
        fakeIt(parse("2012-01-01"));

        ancSchedulesService.forceFulfillMilestone("Case X", "Schedule 1");

        //TODO verify(scheduler).fullfillMilestoneAndCloseAlert(eq("Case X"), eq("Schedule 1"), eq(today()), any(Time.class));
    }

    @Test
    public void shouldUnenrollAMotherFromAllOpenSchedulesAndRaiseDeleteAllAlertActionDuringClose() {
        EnrollmentRecord record1 = new EnrollmentRecord("Case X", "Schedule 1", null, null, null, null, null, null, null, null);
        EnrollmentRecord record2 = new EnrollmentRecord("Case X", "Schedule 2", null, null, null, null, null, null, null, null);
        List<EnrollmentRecord> records = asList(record1, record2);

        when(scheduler.findActiveEnrollments("Case X")).thenReturn(records);

        ancSchedulesService.unEnrollFromAllSchedules("Case X");

        verify(scheduler).unEnrollFromAllSchedules("Case X");
    }

    @Test
    public void shouldFulfillHbTest1WhenHbTestIsDone() {
        when(scheduler.getEnrollment("Case X", "Hb Test 1"))
                .thenReturn(new EnrollmentRecord("Case X", "Hb Test 1", "HB Test 1", null, null, null, null, null, null, null));

        ancSchedulesService.hbTestDone("Case X", "ANM 1", "2013-01-01", null, parse("2012-09-01"));

        verify(scheduler).fullfillMilestoneAndCloseAlert(eq("Case X"), eq("ANM 1"), eq("Hb Test 1"), eq("Hb Test 1"), eq(parse("2013-01-01")));
    }

    @Test
    public void shouldEnrollANCToHbFollowupTestWhenHbTestIsDoneAndSheIsAnaemic() {
        when(scheduler.getEnrollment("Case X", "Hb Test 1"))
                .thenReturn(new EnrollmentRecord("Case X", "Hb Test 1", "HB Test 1", null, null, null, null, null, null, null));

        ancSchedulesService.hbTestDone("Case X", "ANM 1", "2013-01-01", "Anaemic", null);

        verify(scheduler).enrollIntoSchedule("Case X", "Hb Followup Test", "2013-01-01");
    }

    @Test
    public void shouldEnrollANCToHbTest2WhenHbTestIsDoneAndSheIsNotAnaemic() {
        when(scheduler.getEnrollment("Case X", "Hb Test 1"))
                .thenReturn(new EnrollmentRecord("Case X", "Hb Test 1", "HB Test 1", null, null, null, null, null, null, null));

        ancSchedulesService.hbTestDone("Case X", "ANM 1", "2013-01-01", null, parse("2012-09-01"));

        verify(scheduler).enrollIntoSchedule("Case X", "Hb Test 2", "2012-09-01");
    }

    @Test
    public void shouldFulfillHbFollowupTestWhenHbTestIsDone() {
        when(scheduler.isNotEnrolled("Case X", "Hb Test 1")).thenReturn(true);
        when(scheduler.getEnrollment("Case X", "Hb Test 1")).thenReturn(null);
        when(scheduler.getEnrollment("Case X", "Hb Followup Test"))
                .thenReturn(new EnrollmentRecord("Case X", "Hb Followup Test", "Hb Followup Test", null, null, null, null, null, null, null));

        ancSchedulesService.hbTestDone("Case X", "ANM 1", "2013-01-01", "Anaemic", parse("2012-09-01"));

        verify(scheduler).fullfillMilestoneAndCloseAlert(eq("Case X"), eq("ANM 1"), eq("Hb Followup Test"), eq("Hb Followup Test"), eq(parse("2013-01-01")));
    }

    @Test
    public void shouldFulfillHbTest2WhenHbTestIsDoneAndFollowupIsFulfilledAndDateIs28WeeksAfterLMP() {
        when(scheduler.isNotEnrolled("Case X", "Hb Test 1")).thenReturn(true);
        when(scheduler.getEnrollment("Case X", "Hb Test 1")).thenReturn(null);
        when(scheduler.isNotEnrolled("Case X", "Hb Followup Test")).thenReturn(true);
        when(scheduler.getEnrollment("Case X", "Hb Followup Test"))
                .thenReturn(new EnrollmentRecord("Case X", "Hb Followup Test", "Hb Followup Test", null, null, null, null, null, null, null));
        when(scheduler.getEnrollment("Case X", "Hb Test 2"))
                .thenReturn(new EnrollmentRecord("Case X", "Hb Test 2", "Hb Test 2", null, null, null, null, null, null, null));

        ancSchedulesService.hbTestDone("Case X", "ANM 1", "2013-04-16", "Anaemic", parse("2012-09-01"));

        verify(scheduler).fullfillMilestoneAndCloseAlert(eq("Case X"), eq("ANM 1"), eq("Hb Test 2"), eq("Hb Test 2"), eq(parse("2013-04-16")));
    }

    @Test
    public void shouldFulfillScheduleWhenDeliveryHasBeenPlanned() {
        when(scheduler.getEnrollment("Case X", "Delivery Plan"))
                .thenReturn(new EnrollmentRecord("Case X", "Delivery Plan", "Delivery Plan", null, null, null, null, null, null, null));

        ancSchedulesService.deliveryHasBeenPlanned("Case X", "ANM 1", "2013-01-01");

        verify(scheduler).fullfillMilestoneAndCloseAlert(eq("Case X"), eq("ANM 1"), eq("Delivery Plan"), eq("Delivery Plan"), eq(parse("2013-01-01")));
    }

    @Test
    public void shouldEnrollToHbTest2WhenHbTestIsDoneAndFollowupIsFulfilledAndDateIsNot28WeeksAfterLMP() {
        when(scheduler.isNotEnrolled("Case X", "Hb Test 1")).thenReturn(true);
        when(scheduler.getEnrollment("Case X", "Hb Test 1")).thenReturn(null);
        when(scheduler.getEnrollment("Case X", "Hb Followup Test"))
                .thenReturn(new EnrollmentRecord("Case X", "Hb Followup Test", "Hb Followup Test", null, null, null, null, null, null, null));
        when(scheduler.getEnrollment("Case X", "Hb Test 2"))
                .thenReturn(new EnrollmentRecord("Case X", "Hb Test 2", "Hb Test 2", null, null, null, null, null, null, null));

        ancSchedulesService.hbTestDone("Case X", "ANM 1", "2013-02-16", "Anaemic", parse("2012-09-01"));

        verify(scheduler, times(0)).fullfillMilestoneAndCloseAlert(eq("Case X"), eq("ANM 1"), eq("Hb Test 2"), eq(parse("2013-04-16")));
        verify(scheduler).enrollIntoSchedule("Case X", "Hb Test 2", "2012-09-01");
    }

    @Test
    public void shouldFulfillHbTest2WhenHbTestIsDoneAndItsTheActiveMilestone() {
        when(scheduler.isNotEnrolled("Case X", "Hb Test 1")).thenReturn(true);
        when(scheduler.getEnrollment("Case X", "Hb Test 1")).thenReturn(null);
        when(scheduler.isNotEnrolled("Case X", "Hb Followup Test")).thenReturn(true);
        when(scheduler.getEnrollment("Case X", "Hb Followup Test")).thenReturn(null);
        when(scheduler.getEnrollment("Case X", "Hb Test 2"))
                .thenReturn(new EnrollmentRecord("Case X", "Hb Test 2", "Hb Test 2", null, null, null, null, null, null, null));

        ancSchedulesService.hbTestDone("Case X", "ANM 1", "2013-04-16", "Anaemic", parse("2012-09-01"));

        verify(scheduler).fullfillMilestoneAndCloseAlert(eq("Case X"), eq("ANM 1"), eq("Hb Test 2"), eq("Hb Test 2"), eq(parse("2013-04-16")));
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
        verify(scheduler, times(wantedNumberOfInvocations)).enrollIntoSchedule("Case X", SCHEDULE_ANC, expectedMilestone, lmp.toString());
    }

   /* private EnrollmentsQuery queryFor(final String externalId) {
        return argThat(new ArgumentMatcher<EnrollmentsQuery>() {
            @Override
            public boolean matches(Object o) {
                EnrollmentsQuery expectedQuery = new EnrollmentsQuery().havingExternalId(externalId).havingState(ACTIVE);
                return EqualsBuilder.reflectionEquals(expectedQuery.getCriteria(), ((EnrollmentsQuery) o).getCriteria());
            }
        });
    }*/

    private void verifyNonANCScheduleEnrollments(LocalDate lmp) {
        verify(scheduler).enrollIntoSchedule("Case X", SCHEDULE_EDD, lmp);
        verify(scheduler).enrollIntoSchedule("Case X", SCHEDULE_LAB, lmp);
        verify(scheduler).enrollIntoSchedule("Case X", SCHEDULE_TT_1, lmp);
        verify(scheduler).enrollIntoSchedule("Case X", SCHEDULE_IFA_1, lmp);
        verify(scheduler).enrollIntoSchedule("Case X", SCHEDULE_HB_TEST_1, lmp);
        verify(scheduler).enrollIntoSchedule("Case X", SCHEDULE_DELIVERY_PLAN, lmp);
    }
}
