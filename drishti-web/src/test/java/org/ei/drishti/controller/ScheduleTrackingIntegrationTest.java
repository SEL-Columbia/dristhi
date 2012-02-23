package org.ei.drishti.controller;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.model.Time;
import org.motechproject.scheduletracking.api.service.ScheduleTrackingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;

import static org.motechproject.scheduletracking.api.domain.WindowName.*;
import static org.motechproject.util.DateUtil.newDate;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:spring/applicationContext-drishti-web.xml")
public class ScheduleTrackingIntegrationTest {
    @Autowired
    private ScheduleTrackingService trackingService;

    @Autowired
    private SchedulerFactoryBean schedulerFactoryBean;

    private TestSchedule testSchedule;

    @Before
    public void setUp() throws Exception {
        testSchedule = new TestSchedule(trackingService, schedulerFactoryBean);
    }

    @Test
    public void shouldProvideAlertsForANCAtTheRightTimes() throws Exception {
        testSchedule.enrollFor("Ante Natal Care - Normal", newDate(2012, 1, 1), new Time(14, 0));

        testSchedule.assertNoAlerts("ANC 1", earliest);
        testSchedule.assertAlerts("ANC 1", due, date(2012, 3, 4), date(2012, 3, 11), date(2012, 3, 18), date(2012, 3, 25));
        testSchedule.assertAlerts("ANC 1", late, date(2012, 4, 1), date(2012, 4, 4), date(2012, 4, 8), date(2012, 4, 11), date(2012, 4, 15));
        testSchedule.assertAlerts("ANC 1", max, date(2012, 4, 17), date(2012, 4, 18), date(2012, 4, 19));
    }

    private Date date(int year, int month, int day) {
        return new DateTime(year, month, day, 14, 0).toDate();
    }
}
