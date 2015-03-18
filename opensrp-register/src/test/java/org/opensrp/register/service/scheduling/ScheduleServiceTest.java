package org.opensrp.register.service.scheduling;

import org.opensrp.common.util.DateUtil;
import org.joda.time.Period;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.Mock;
import org.motechproject.scheduletracking.api.domain.Milestone;
import org.motechproject.scheduletracking.api.domain.Schedule;
import org.motechproject.scheduletracking.api.repository.AllSchedules;
import org.motechproject.scheduletracking.api.service.EnrollmentRequest;
import org.motechproject.scheduletracking.api.service.ScheduleTrackingService;

import static org.joda.time.LocalDate.parse;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import org.opensrp.service.scheduling.ScheduleService;

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
        scheduleService = new ScheduleService(scheduleTrackingService, allSchedules, 14);
    }

    @Test
    public void shouldEnrollToFirstMilestoneBasedOnScheduleDatesAndReferenceDate() {
        DateUtil.fakeIt(parse("2012-01-01"));
        when(allSchedules.getByName("my_schedule")).thenReturn(schedule);

        scheduleService.enroll("entity_1", "my_schedule", "2012-01-01");

        verify(scheduleTrackingService).enroll(enrollmentFor("entity_1", "my_schedule", firstMilestone.getName(), "2012-01-01"));
    }

    @Test
    public void shouldEnrollToSecondMilestoneBasedOnScheduleDatesAndReferenceDate() {
        DateUtil.fakeIt(parse("2012-02-07"));
        when(allSchedules.getByName("my_schedule")).thenReturn(schedule);

        scheduleService.enroll("entity_1", "my_schedule", "2012-01-01");

        verify(scheduleTrackingService).enroll(enrollmentFor("entity_1", "my_schedule", secondMilestone.getName(), "2012-01-01"));
    }

    private EnrollmentRequest enrollmentFor(final String entityId, final String scheduleName, final String name, final String referenceDate) {
        return argThat(new ArgumentMatcher<EnrollmentRequest>() {
            @Override
            public boolean matches(Object o) {
                EnrollmentRequest request = (EnrollmentRequest) o;
                return entityId.equals(request.getExternalId()) && parse(referenceDate).equals(request.getReferenceDate())
                        && scheduleName.equals(request.getScheduleName()) && name.equals(request.getStartingMilestoneName());
            }
        });
    }

    private Period weeks(int numberOfWeeks) {
        return new Period(0, 0, numberOfWeeks, 0, 0, 0, 0, 0);
    }
}
