package org.ei.drishti.reporting;

import org.ei.drishti.common.util.DateTimeUtil;
import org.ei.drishti.scheduler.AggregateReportsScheduler;
import org.joda.time.DateTimeConstants;
import org.joda.time.LocalDateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.motechproject.scheduler.MotechSchedulerService;
import org.motechproject.scheduler.domain.RepeatingSchedulableJob;

import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class AggregateReportsSchedulerTest {

    @Mock
    private MotechSchedulerService schedulerService;
    private AggregateReportsScheduler scheduler;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        scheduler = new AggregateReportsScheduler(schedulerService);
    }

    @Test
    public void shouldScheduleARepeatingJob() throws Exception {
        DateTimeUtil.fakeIt(LocalDateTime.parse("2013-01-01T12:00:00.000"));

        scheduler.startTimedScheduler();

        verify(schedulerService).safeScheduleRepeatingJob(verifyJob());
    }

    private RepeatingSchedulableJob verifyJob() {
        return Matchers.argThat(new ArgumentMatcher<RepeatingSchedulableJob>() {
            @Override
            public boolean matches(Object o) {
                RepeatingSchedulableJob job = (RepeatingSchedulableJob) o;
                return LocalDateTime.parse("2013-01-01T12:00:00.000").plusMinutes(10).toDate()
                        .equals(job.getStartTime())
                        && DateTimeConstants.MILLIS_PER_HOUR == job.getRepeatIntervalInMilliSeconds()
                        && "REPORT_AGGREGATOR_SCHEDULE".equals(job.getMotechEvent().getSubject());
            }
        });
    }
}
