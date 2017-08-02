package org.opensrp.scheduler.service;

import org.joda.time.DateTime;
import org.joda.time.LocalTime;
import org.joda.time.Period;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.motechproject.model.Time;
import org.motechproject.scheduletracking.api.domain.Enrollment;
import org.motechproject.scheduletracking.api.domain.EnrollmentStatus;
import org.motechproject.scheduletracking.api.domain.Milestone;
import org.motechproject.scheduletracking.api.domain.Schedule;
import org.motechproject.scheduletracking.api.repository.AllSchedules;
import org.motechproject.scheduletracking.api.service.EnrollmentRecord;
import org.motechproject.scheduletracking.api.service.EnrollmentsQuery;
import org.motechproject.scheduletracking.api.service.ScheduleTrackingService;
import org.opensrp.common.util.DateUtil;
import org.opensrp.scheduler.HealthSchedulerService;
import org.opensrp.util.ScheduleBuilder;
import org.powermock.api.mockito.PowerMockito;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static junit.framework.Assert.assertEquals;
import static org.joda.time.LocalDate.parse;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;


public class ScheduleServiceTest {

    public static final String ENTITY_ID = "entity_id";
    public static final String MY_SCHEDULE = "my_schedule";
    public static final String ENROLLMENT_ID = "enrollment_di";
    @Mock
    private ScheduleTrackingService scheduleTrackingService;
    @Mock
    private AllSchedules allSchedules;
    @Mock
    private AllEnrollmentWrapper allEnrollments;

    private ScheduleService scheduleService;
    private Milestone firstMilestone;
    private Milestone secondMilestone;
    private Milestone thirdMilestone;
    private Schedule schedule;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        firstMilestone = new Milestone("firstMilestone", weeks(1), weeks(1), weeks(1), weeks(1));
        secondMilestone = new Milestone("secondMilestone", weeks(5), weeks(1), weeks(1), weeks(1));
        thirdMilestone = new Milestone("thirdMilestone", weeks(8), weeks(1), weeks(1), weeks(1));
        schedule = new Schedule("my_schedule");
        schedule.addMilestones(firstMilestone);
        schedule.addMilestones(secondMilestone);
        schedule.addMilestones(thirdMilestone);
        scheduleService = new ScheduleService(scheduleTrackingService, allSchedules, 14, allEnrollments);
    }

    @Test
    public void shouldEnrollToFirstMilestoneBasedOnScheduleDatesAndReferenceDate() {
        DateUtil.fakeIt(parse("2012-01-01"));
        when(allSchedules.getByName("my_schedule")).thenReturn(schedule);

        scheduleService.enroll("entity_1", "my_schedule", "2012-01-01", "formsubmission1");

        verify(scheduleTrackingService).enroll(ScheduleBuilder.enrollmentFor("entity_1", "my_schedule", firstMilestone.getName(), "2012-01-01"));
    }

    @Test
    public void shouldEnrollToSecondMilestoneBasedOnScheduleDatesAndReferenceDate() {
        DateUtil.fakeIt(parse("2012-02-07"));
        when(allSchedules.getByName("my_schedule")).thenReturn(schedule);

        scheduleService.enroll("entity_1", "my_schedule", "2012-01-01", "formsubmission2");

        verify(scheduleTrackingService).enroll(ScheduleBuilder.enrollmentFor("entity_1", "my_schedule", secondMilestone.getName(), "2012-01-01"));
    }

    @Test
    public void shouldEnrollToThirdMilestoneBasedOnScheduleDatesAndReferenceDate() {
        DateUtil.fakeIt(parse("2012-03-01"));
        when(allSchedules.getByName("my_schedule")).thenReturn(schedule);

        scheduleService.enroll("entity_1", "my_schedule", "2012-01-01", "formsubmission3");

        verify(scheduleTrackingService).enroll(ScheduleBuilder.enrollmentFor("entity_1", "my_schedule", thirdMilestone.getName(), "2012-01-01"));
    }

    @Test
    public void testEnrollIntoNullMilestone() {
        DateUtil.fakeIt(parse("2018-03-01"));
        when(allSchedules.getByName("my_schedule")).thenReturn(schedule);

        scheduleService.enroll("entity_1", "my_schedule", "2012-01-01", "formsubmission3");

        verify(scheduleTrackingService).enroll(ScheduleBuilder.enrollmentFor("entity_1", "my_schedule", null, "2012-01-01"));
    }


    @Test(expected = NullPointerException.class)
    public void shouldThrowNullPointerExceptionIfSchduleIsNotFound() {
        scheduleService.enroll("entity_1", "my_schedule", "2012-01-01", "formsubmission2");
    }

    @Test
    public void shouldSuccessfullyFulfilMilestone() {
        Enrollment enrollment = ScheduleBuilder.enrollment("entity_1", "my_schedule", "milestone", new DateTime(0l), new DateTime(1l), EnrollmentStatus.ACTIVE, "formSubmission3");
        when(allEnrollments.getActiveEnrollment("entity_1", "my_schedule")).thenReturn(enrollment);
        LocalTime localTime = new DateTime(3l).toLocalTime();

        ScheduleService spyScheduleService = spy(scheduleService);
        when(spyScheduleService.getCurrentTime()).thenReturn(new Time(localTime));

        spyScheduleService.fulfillMilestone("entity_1", "my_schedule", new DateTime(0l).toLocalDate(), "formSubmission3");

        Map<String, String> metaData = enrollment.getMetadata();
        metaData.put(HealthSchedulerService.MetadataField.fulfillmentEvent.name(), "formSubmission3");
        enrollment.setMetadata(metaData);
        InOrder inOrder = Mockito.inOrder(allEnrollments, scheduleTrackingService);
        inOrder.verify(allEnrollments).update(enrollment);
        inOrder.verify(scheduleTrackingService).fulfillCurrentMilestone(
                "entity_1", "my_schedule", new DateTime(1l).toLocalDate(),
                new Time(localTime));
    }

    @Test
    public void shouldNotTryToFulfilMilestoneIfNoEnrollmentFound() {
        scheduleService.fulfillMilestone("entity_1", "my_schedule", new DateTime(0l).toLocalDate(), "formSubmission3");
        verify(allEnrollments, never()).update(any(Enrollment.class));
        verifyNoMoreInteractions(scheduleTrackingService);
    }

    @Test
    public void shouldUnEnroll() {
        Enrollment enrollment = ScheduleBuilder.enrollment("entity_1", "my_schedule", "milestone", new DateTime(0l), new DateTime(1l), EnrollmentStatus.ACTIVE, "formSubmission3");
        when(allEnrollments.getActiveEnrollment("entity_1", "my_schedule")).thenReturn(enrollment);


        scheduleService.unenroll("entity_1", "my_schedule", "formSubmission3");

        Map<String, String> metaData = enrollment.getMetadata();
        metaData.put(HealthSchedulerService.MetadataField.unenrollmentEvent.name(), "formSubmission3");
        enrollment.setMetadata(metaData);

        InOrder inOrder = Mockito.inOrder(allEnrollments, scheduleTrackingService);
        inOrder.verify(allEnrollments).update(enrollment);
        inOrder.verify(scheduleTrackingService).unenroll(
                "entity_1", asList("my_schedule"));
    }

    @Test
    public void shouldNotTryToUnEnrollIfNoEnrollmentFound() {
        scheduleService.fulfillMilestone("entity_1", "my_schedule", new DateTime(0l).toLocalDate(), "formSubmission3");
        verify(allEnrollments, never()).update(any(Enrollment.class));
        verifyNoMoreInteractions(scheduleTrackingService);
    }

    @Test
    public void shouldUnEnrollGivenScheduleList() {
        Enrollment enrollment_1 = ScheduleBuilder.enrollment("entity_1", "my_schedule",
                "milestone", new DateTime(0l), new DateTime(1l),
                EnrollmentStatus.ACTIVE, "formSubmission3");
        Enrollment enrollment_2 = ScheduleBuilder.enrollment("entity_1", "my_schedule",
                "milestone", new DateTime(0l), new DateTime(1l),
                EnrollmentStatus.ACTIVE, "formSubmission3");
        when(allEnrollments.getActiveEnrollment("entity_1", "schedule_1")).thenReturn(enrollment_1);
        when(allEnrollments.getActiveEnrollment("entity_1", "schedule_2")).thenReturn(enrollment_2);


        scheduleService.unenroll("entity_1", asList("schedule_1", "schedule_2"), "formSubmission3");

        Map<String, String> metaData = enrollment_1.getMetadata();
        metaData.put(HealthSchedulerService.MetadataField.unenrollmentEvent.name(), "formSubmission3");
        enrollment_1.setMetadata(metaData);
        enrollment_2.setMetadata(metaData);

        InOrder inOrder = Mockito.inOrder(allEnrollments, scheduleTrackingService);
        inOrder.verify(allEnrollments).update(enrollment_1);
        inOrder.verify(allEnrollments).update(enrollment_2);
        inOrder.verify(scheduleTrackingService).unenroll(
                "entity_1", asList("schedule_1", "schedule_2"));
    }

    @Test
    public void findOpenEnrollments() throws Exception {
        EnrollmentsQuery enrollmentsQuery = mock(EnrollmentsQuery.class);
        when(enrollmentsQuery.havingExternalId(ENTITY_ID)).thenReturn(enrollmentsQuery);
        when(enrollmentsQuery.havingState(EnrollmentStatus.ACTIVE)).thenReturn(enrollmentsQuery);

        ScheduleService spyScheduleService = spy(scheduleService);
        when(spyScheduleService.createEnrollmentQueryWithActiveExternalId(ENTITY_ID)).thenReturn(enrollmentsQuery);

        spyScheduleService.findOpenEnrollments(ENTITY_ID);

        verify(scheduleTrackingService).search(enrollmentsQuery);
    }

    @Test
    public void shouldFindAllEnrollmentByStatusAndEnrollmentDate() {
        scheduleService.findEnrollmentByStatusAndEnrollmentDate("active", new DateTime(0l), new DateTime(1l));
        verify(allEnrollments).findByEnrollmentDate("active", new DateTime(0l), new DateTime(1l));
    }

    @Test
    public void shouldFindEnrollmentByLastUpDate() {
        scheduleService.findEnrollmentByLastUpDate(new DateTime(0l), new DateTime(1l));
        verify(allEnrollments).findByLastUpDate(new DateTime(0l), new DateTime(1l));
    }


    @Test
    public void shouldUpdateEnrollmentWithMetadata() {
        Enrollment enrollment = ScheduleBuilder.enrollment("entity_1", "my_schedule", "milestone", new DateTime(0l), new DateTime(1l), EnrollmentStatus.ACTIVE, "formSubmission3");
        when(allEnrollments.get("enrollment_id")).thenReturn(enrollment);

        scheduleService.updateEnrollmentWithMetadata("enrollment_id", "key", "value");

        Map<String, String> metaData = enrollment.getMetadata();
        metaData.put("key", "value");
        enrollment.setMetadata(metaData);

        verify(allEnrollments).update(enrollment);

    }

    @Test
    public void shouldFindOpenEnrollmentNames() throws Exception {
        EnrollmentsQuery enrollmentsQuery = PowerMockito.mock(EnrollmentsQuery.class);
        when(enrollmentsQuery.havingExternalId(ENTITY_ID)).thenReturn(enrollmentsQuery);
        when(enrollmentsQuery.havingState(EnrollmentStatus.ACTIVE)).thenReturn(enrollmentsQuery);

        ScheduleService spyScheduleService = spy(scheduleService);
        when(spyScheduleService.createEnrollmentQueryWithActiveExternalId(ENTITY_ID)).thenReturn(enrollmentsQuery);

        List<EnrollmentRecord> enrollmentRecords = new ArrayList<>();
        List<String> expected = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            enrollmentRecords.add(ScheduleBuilder.enrollmentRecord(ENTITY_ID, "schedule" + i, "milestone"));
            expected.add("schedule" + i);
        }

        when(scheduleTrackingService.search(enrollmentsQuery)).thenReturn(enrollmentRecords);
        List<String> actual = spyScheduleService.findOpenEnrollmentNames(ENTITY_ID);

        assertEquals(expected, actual);
    }

    @Test
    public void shouldGetActiveEnrollment() {
        scheduleService.getActiveEnrollment(ENTITY_ID, MY_SCHEDULE);
        verify(allEnrollments).getActiveEnrollment(ENTITY_ID, MY_SCHEDULE);
    }

    @Test
    public void shouldGetEnrollment() {
        scheduleService.getEnrollment(ENTITY_ID, MY_SCHEDULE);
        verify(allEnrollments).getEnrollment(ENTITY_ID, MY_SCHEDULE);
    }

    @Test
    public void shouldGetEnrollmentRecord() {
        scheduleService.getEnrollmentRecord(ENTITY_ID, MY_SCHEDULE);
        verify(scheduleTrackingService).getEnrollment(ENTITY_ID, MY_SCHEDULE);
    }

    @Test
    public void shouldGetEnrollmentById() {
        scheduleService.getEnrollment(ENROLLMENT_ID);
        verify(allEnrollments).get(ENROLLMENT_ID);
    }

    private Period weeks(int numberOfWeeks) {
        return new Period(0, 0, numberOfWeeks, 0, 0, 0, 0, 0);
    }
}
