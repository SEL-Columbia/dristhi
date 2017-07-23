package org.opensrp.scheduler;


import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.motechproject.scheduler.MotechSchedulerService;
import org.motechproject.scheduler.domain.CronSchedulableJob;
import org.motechproject.scheduler.domain.MotechEvent;
import org.motechproject.scheduler.domain.RepeatingSchedulableJob;
import org.motechproject.scheduler.gateway.OutboundEventGateway;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;


public class TaskSchedulerServiceTest {
    public static final String SUBJECT = "subject";
    @Mock
    MotechSchedulerService motechSchedulerService;
    @Mock
    OutboundEventGateway outboundEventGateway;
    @Mock
    AlertRouter alertRouter;

    @InjectMocks
    TaskSchedulerService taskSchedulerService = new TaskSchedulerService(motechSchedulerService,outboundEventGateway, alertRouter);

    @Before
    public void setUp() {
        initMocks(this);
    }

    @Test
    public void testStartJobWithRepeatingSchedule() {
        Map<String, Object> extraData = new HashMap<>();
        extraData.put("key1", "value1");
        RepeatingSchedule repeatingSchedule = new RepeatingSchedule(SUBJECT, 5, TimeUnit.MILLISECONDS, 4, TimeUnit.MILLISECONDS, new Date(1l), extraData);
        TaskSchedulerService spyTaskSchedulingService = spy(taskSchedulerService);
        when(spyTaskSchedulingService.getCurrentTime()).thenReturn(new DateTime(0l));

        spyTaskSchedulingService.startJob(repeatingSchedule);

        MotechEvent motechEvent = new MotechEvent(SUBJECT, extraData);
        verify(spyTaskSchedulingService).createRepeatingSchedulableJob(motechEvent,new DateTime(0l).plusMillis(5).toDate(), new Date(1l), 4l);
    }

    @Test
    public void testStartJobWithRepeatingScheduleWithNullData() {
        RepeatingSchedule repeatingSchedule = new RepeatingSchedule(SUBJECT, 5, TimeUnit.MILLISECONDS, 4, TimeUnit.MILLISECONDS, new Date(1l), null);
        TaskSchedulerService spyTaskSchedulingService = spy(taskSchedulerService);
        when(spyTaskSchedulingService.getCurrentTime()).thenReturn(new DateTime(0l));

        spyTaskSchedulingService.startJob(repeatingSchedule);

        MotechEvent motechEvent = new MotechEvent(SUBJECT, new HashMap<String, Object>());
        verify(spyTaskSchedulingService).createRepeatingSchedulableJob(motechEvent,new DateTime(0l).plusMillis(5).toDate(), new Date(1l), 4l);
    }

    @Test
    public void testStartJobWithCornSchedule() {
        Map<String, Object> extraData = new HashMap<>();
        extraData.put("key1", "value1");
        String cronExpression = "corn";
        RepeatingCronSchedule repeatingCronSchedule = new RepeatingCronSchedule(SUBJECT, 5, TimeUnit.MILLISECONDS, cronExpression, new Date(1l), extraData);
        TaskSchedulerService spyTaskSchedulingService = spy(taskSchedulerService);
        when(spyTaskSchedulingService.getCurrentTime()).thenReturn(new DateTime(0l));

        spyTaskSchedulingService.startJob(repeatingCronSchedule);

        MotechEvent motechEvent = new MotechEvent(SUBJECT, extraData);
        CronSchedulableJob cronSchedulableJob = new CronSchedulableJob(motechEvent, cronExpression,new DateTime(0l).plusMillis(5).toDate(), new Date(1l));

        verify(motechSchedulerService).safeScheduleJob(cronSchedulableJob);
    }

    @Test
    public void testStartJobWithCornScheduleWithNullData() {
        String cronExpression = "corn";
        RepeatingCronSchedule repeatingCronSchedule = new RepeatingCronSchedule(SUBJECT, 5, TimeUnit.MILLISECONDS, cronExpression, new Date(1l), null);
        TaskSchedulerService spyTaskSchedulingService = spy(taskSchedulerService);
        when(spyTaskSchedulingService.getCurrentTime()).thenReturn(new DateTime(0l));

        spyTaskSchedulingService.startJob(repeatingCronSchedule);

        MotechEvent motechEvent = new MotechEvent(SUBJECT, new HashMap<String, Object>());
        CronSchedulableJob cronSchedulableJob = new CronSchedulableJob(motechEvent, cronExpression,new DateTime(0l).plusMillis(5).toDate(), new Date(1l));

        verify(motechSchedulerService).safeScheduleJob(cronSchedulableJob);
    }

    @Test
    public void testNotifyEvent() {
        SystemEvent systemEvent = new SystemEvent(SUBJECT, "data");

        taskSchedulerService.notifyEvent(systemEvent);

        verify(outboundEventGateway).sendEventMessage(systemEvent.toMotechEvent());
    }
}
