package org.ei.drishti.controller;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.model.Time;
import org.motechproject.scheduletracking.api.service.ScheduleTrackingService;
import org.motechproject.testing.utils.BaseUnitTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;

import static org.motechproject.scheduletracking.api.domain.WindowName.*;
import static org.motechproject.util.DateUtil.newDate;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:spring/applicationContext-drishti-web.xml")
public class ScheduleTrackingIntegrationTest extends BaseUnitTest {
    private static final int JANUARY = 1;
    private static final int FEBRUARY = 2;
    private static final int MARCH = 3;
    private static final int APRIL = 4;
    private static final int MAY = 5;
    private static final int JUNE = 6;
    private static final int JULY = 7;
    private static final int AUGUST = 8;
    private static final int SEPTEMBER = 9;
    private static final int OCTOBER = 10;
    private static final int NOVEMBER = 11;

    @Autowired
    private ScheduleTrackingService trackingService;
    @Autowired
    private SchedulerFactoryBean schedulerFactoryBean;

    private TestSchedule testSchedule;

    @Test
    public void shouldProvideAlertsForANCAtTheRightTimes() throws Exception {
        testSchedule.enrollFor("Ante Natal Care - Normal", newDate(2012, 1, 1), new Time(14, 0));

        testSchedule.assertNoAlerts("ANC 1", earliest);
        testSchedule.assertAlerts("ANC 1", due, date(4, MARCH), date(11, MARCH), date(18, MARCH), date(25, MARCH));
        testSchedule.assertAlerts("ANC 1", late, date(1, APRIL), date(4, APRIL), date(8, APRIL), date(11, APRIL), date(15, APRIL));
        testSchedule.assertAlerts("ANC 1", max, date(17, APRIL), date(18, APRIL), date(19, APRIL));

        testSchedule.assertNoAlerts("ANC 2", earliest);
        testSchedule.assertAlerts("ANC 2", due, date(27, MAY), date(3, JUNE), date(10, JUNE), date(17, JUNE));
        testSchedule.assertAlerts("ANC 2", late, date(24, JUNE), date(27, JUNE), date(1, JULY), date(4, JULY), date(8, JULY));
        testSchedule.assertAlerts("ANC 2", max, date(10, JULY), date(11, JULY), date(12, JULY));

        testSchedule.assertNoAlerts("ANC 3", earliest);
        testSchedule.assertAlerts("ANC 3", due, date(22, JULY), date(29, JULY), date(5, AUGUST), date(12, AUGUST));
        testSchedule.assertAlerts("ANC 3", late, date(19, AUGUST), date(22, AUGUST));
        testSchedule.assertAlerts("ANC 3", max, date(23, AUGUST), date(24, AUGUST), date(25, AUGUST));

        testSchedule.assertNoAlerts("ANC 4", earliest);
        testSchedule.assertAlerts("ANC 4", due, date(26, AUGUST), date(2, SEPTEMBER), date(9, SEPTEMBER));
        testSchedule.assertAlerts("ANC 4", late, date(16, SEPTEMBER), date(19, SEPTEMBER), date(23, SEPTEMBER), date(26, SEPTEMBER), date(30, SEPTEMBER));
        testSchedule.assertAlerts("ANC 4", max, date(2, OCTOBER), date(3, OCTOBER), date(4, OCTOBER));
    }

    @Test
    public void shouldProvideAlertsForTetanusToxoidVaccinationAtTheRightTimes() throws Exception {
        testSchedule.withFulfillmentDates(date(15, JANUARY)).enrollFor("Tetatnus Toxoid Vaccination", newDate(2012, 1, 1), new Time(14, 0));

        testSchedule.assertNoAlerts("TT 1", earliest);
        testSchedule.assertNoAlerts("TT 1", due);
        testSchedule.assertAlertsStartWith("TT 1", late, date(15, APRIL), date(18, APRIL), date(22, APRIL), date(25, APRIL), date(29, APRIL), date(2, MAY), date(6, MAY), date(9, MAY), date(13, MAY), date(16, MAY));
        testSchedule.assertNoAlerts("TT 1", max);

        testSchedule.assertNoAlerts("TT 2", earliest);
        testSchedule.assertAlerts("TT 2", due, date(5, FEBRUARY), date(12, FEBRUARY));
        testSchedule.assertAlerts("TT 2", late, date(15, FEBRUARY), date(19, FEBRUARY), date(22, FEBRUARY), date(26, FEBRUARY), date(29, FEBRUARY), date(4, MARCH), date(7, MARCH), date(11, MARCH));
        testSchedule.assertAlerts("TT 2", max, date(13, MARCH), date(14, MARCH), date(15, MARCH));
    }

    @Test
    public void shouldProvideAlertsForLabRemindersATheRightTimes() throws Exception {
        testSchedule.enrollFor("Lab Reminders", newDate(2012, 1, 1), new Time(14, 0));

        testSchedule.assertNoAlerts("REMINDER", earliest);
        testSchedule.assertNoAlerts("REMINDER", due);
        testSchedule.assertAlerts("REMINDER", late, date(29, JULY), date(5, AUGUST), date(12, AUGUST), date(19, AUGUST),
                date(26, AUGUST), date(2, SEPTEMBER), date(9, SEPTEMBER), date(16, SEPTEMBER), date(23, SEPTEMBER), date(30, SEPTEMBER), date(7, OCTOBER));
        testSchedule.assertAlerts("REMINDER", max, date(9, OCTOBER), date(10, OCTOBER), date(11, OCTOBER));
    }

    @Test
    public void shouldProvideAlertsForExpectedDateOfDeliveryAtTheRightTimes() throws Exception {
        testSchedule.enrollFor("Expected Date Of Delivery", newDate(2012, 1, 1), new Time(14, 0));

        testSchedule.assertNoAlerts("EDD", earliest);
        testSchedule.assertAlerts("EDD", due, date(23, SEPTEMBER), date(30, SEPTEMBER), date(7, OCTOBER));
        testSchedule.assertAlertsStartWith("EDD", late, date(10, OCTOBER), date(14, OCTOBER), date(17, OCTOBER), date(21, OCTOBER), date(24, OCTOBER), date(28, OCTOBER), date(31, OCTOBER), date(4, NOVEMBER));
        testSchedule.assertNoAlerts("EDD", max);
    }

    @Test
    public void shouldProvideAlertsForBCG() throws Exception {
        testSchedule.enrollFor("BCG", newDate(2012, 1, 1), new Time(14, 0));

        testSchedule.assertNoAlerts("REMINDER", earliest);
        testSchedule.assertAlertsStartWith("REMINDER", due, date(15, JANUARY), date(18, JANUARY), date(22, JANUARY), date(25, JANUARY),
                date(29, JANUARY), date(1, FEBRUARY), date(5, FEBRUARY), date(8, FEBRUARY), date(12, FEBRUARY),
                date(15, FEBRUARY), date(19, FEBRUARY), date(22, FEBRUARY));
        testSchedule.assertNoAlerts("REMINDER", late);
        testSchedule.assertNoAlerts("REMINDER", max);
    }

    @Test
    public void shouldProvideAlertsForOPV() throws Exception {
        testSchedule.enrollFor("OPV", newDate(2012, 1, 1), new Time(14, 0));

        testSchedule.assertNoAlerts("OPV 0", earliest);
        testSchedule.assertAlertsStartWith("OPV 0", due, date(15, JANUARY), date(18, JANUARY), date(22, JANUARY), date(25, JANUARY),
                date(29, JANUARY), date(1, FEBRUARY), date(5, FEBRUARY), date(8, FEBRUARY), date(12, FEBRUARY),
                date(15, FEBRUARY), date(19, FEBRUARY), date(22, FEBRUARY));
        testSchedule.assertNoAlerts("OPV 0", late);
        testSchedule.assertNoAlerts("OPV 0", max);

        testSchedule.assertNoAlerts("OPV 1", earliest);
        testSchedule.assertAlerts("OPV 1", due, date(5, FEBRUARY), date(12, FEBRUARY));
        testSchedule.assertAlertsStartWith("OPV 1", late, date(19, FEBRUARY), date(22, FEBRUARY), date(26, FEBRUARY), date(29, FEBRUARY),
                date(4, MARCH), date(7, MARCH));
        testSchedule.assertNoAlerts("OPV 1", max);

        testSchedule.assertNoAlerts("OPV 2", earliest);
        testSchedule.assertAlerts("OPV 2", due, date(4, MARCH), date(11, MARCH));
        testSchedule.assertAlertsStartWith("OPV 2", late, date(18, MARCH), date(21, MARCH), date(25, MARCH), date(28, MARCH),
                date(1, APRIL), date(4, APRIL));
        testSchedule.assertNoAlerts("OPV 2", max);

        testSchedule.assertNoAlerts("OPV 3", earliest);
        testSchedule.assertAlerts("OPV 3", due, date(1, APRIL), date(8, APRIL));
        testSchedule.assertAlertsStartWith("OPV 3", late, date(15, APRIL), date(18, APRIL), date(22, APRIL), date(25, APRIL),
                date(29, APRIL), date(2, MAY));
        testSchedule.assertNoAlerts("OPV 3", max);
    }

    @Before
    public void setUp() throws Exception {
        testSchedule = new TestSchedule(trackingService, schedulerFactoryBean, new SetDateAction() {
            @Override
            public void setTheDateTo(LocalDate date) {
                mockCurrentDate(date);
            }
        });
    }

    @BeforeClass
    public static void turnOffSpringLogging() {
        Logger logger = Logger.getLogger("org.springframework");
        logger.setLevel(Level.FATAL);
    }

    private Date date(int day, int month) {
        return new DateTime(2012, month, day, 14, 0).toDate();
    }
}
