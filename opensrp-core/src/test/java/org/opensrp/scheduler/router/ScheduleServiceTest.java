package org.opensrp.scheduler.router;

import static org.joda.time.LocalDate.parse;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import org.joda.time.Period;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.scheduletracking.api.domain.Milestone;
import org.motechproject.scheduletracking.api.domain.Schedule;
import org.motechproject.scheduletracking.api.repository.AllSchedules;
import org.motechproject.scheduletracking.api.service.ScheduleTrackingService;
import org.opensrp.common.util.DateUtil;
import org.opensrp.scheduler.service.ScheduleService;
import org.opensrp.util.ScheduleBuilder;

public class ScheduleServiceTest {

    @Mock
    private ScheduleTrackingService scheduleTrackingService;
    @Mock
    private AllSchedules allSchedules;
    private ScheduleService scheduleService;
    private Milestone firstMilestone;
    private Milestone secondMilestone;
    private Schedule schedule;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        firstMilestone = new Milestone("firstMilestone", weeks(1), weeks(1), weeks(1), weeks(1));
        secondMilestone = new Milestone("secondMilestone", weeks(5), weeks(1), weeks(1), weeks(1));
        schedule = new Schedule("my_schedule");
        schedule.addMilestones(firstMilestone);
        schedule.addMilestones(secondMilestone);
        scheduleService = new ScheduleService(scheduleTrackingService, allSchedules, 14, null);
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

    private Period weeks(int numberOfWeeks) {
        return new Period(0, 0, numberOfWeeks, 0, 0, 0, 0, 0);
    }
}
