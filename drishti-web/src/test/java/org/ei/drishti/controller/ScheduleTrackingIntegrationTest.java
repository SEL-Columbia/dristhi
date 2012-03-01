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

import java.io.File;
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

    private TestSchedule schedule;
    private ScheduleVisualization visualization;

    @Test
    public void shouldProvideAlertsForANCAtTheRightTimes() throws Exception {
        schedule.enrollFor("Ante Natal Care - Normal", newDate(2012, 1, 1), new Time(14, 0));

        schedule.assertNoAlerts("ANC 1", earliest);
        schedule.assertAlerts("ANC 1", due, date(4, MARCH), date(11, MARCH), date(18, MARCH), date(25, MARCH));
        schedule.assertAlerts("ANC 1", late, date(1, APRIL), date(4, APRIL), date(8, APRIL), date(11, APRIL), date(15, APRIL));
        schedule.assertAlerts("ANC 1", max, date(17, APRIL), date(18, APRIL), date(19, APRIL));

        schedule.assertNoAlerts("ANC 2", earliest);
        schedule.assertAlerts("ANC 2", due, date(27, MAY), date(3, JUNE), date(10, JUNE), date(17, JUNE));
        schedule.assertAlerts("ANC 2", late, date(24, JUNE), date(27, JUNE), date(1, JULY), date(4, JULY), date(8, JULY));
        schedule.assertAlerts("ANC 2", max, date(10, JULY), date(11, JULY), date(12, JULY));

        schedule.assertNoAlerts("ANC 3", earliest);
        schedule.assertAlerts("ANC 3", due, date(22, JULY), date(29, JULY), date(5, AUGUST), date(12, AUGUST));
        schedule.assertAlerts("ANC 3", late, date(19, AUGUST), date(22, AUGUST));
        schedule.assertAlerts("ANC 3", max, date(23, AUGUST), date(24, AUGUST), date(25, AUGUST));

        schedule.assertNoAlerts("ANC 4", earliest);
        schedule.assertAlerts("ANC 4", due, date(26, AUGUST), date(2, SEPTEMBER), date(9, SEPTEMBER));
        schedule.assertAlerts("ANC 4", late, date(16, SEPTEMBER), date(19, SEPTEMBER), date(23, SEPTEMBER), date(26, SEPTEMBER), date(30, SEPTEMBER));
        schedule.assertAlerts("ANC 4", max, date(2, OCTOBER), date(3, OCTOBER), date(4, OCTOBER));

        visualization.outputTo("mother-anc-normal.html");
    }

    @Test
    public void shouldProvideAlertsForTetanusToxoidVaccinationAtTheRightTimes() throws Exception {
        schedule.withFulfillmentDates(date(27, MAY)).enrollFor("Tetatnus Toxoid Vaccination", newDate(2012, 1, 1), new Time(14, 0));

        schedule.assertNoAlerts("TT 1", earliest);
        schedule.assertNoAlerts("TT 1", due);
        schedule.assertAlertsStartWith("TT 1", late, date(15, APRIL), date(18, APRIL), date(22, APRIL), date(25, APRIL), date(29, APRIL), date(2, MAY), date(6, MAY), date(9, MAY), date(13, MAY), date(16, MAY));
        schedule.assertNoAlerts("TT 1", max);

        schedule.assertNoAlerts("TT 2", earliest);
        schedule.assertAlerts("TT 2", due, date(17, JUNE), date(24, JUNE));
        schedule.assertAlerts("TT 2", late, date(27, JUNE), date(1, JULY), date(4, JULY), date(8, JULY), date(11, JULY), date(15, JULY), date(18, JULY), date(22, JULY));
        schedule.assertAlerts("TT 2", max, date(24, JULY), date(25, JULY), date(26, JULY));

        visualization.outputTo("mother-tetanus-toxoid-vaccination.html");
    }

    @Test
    public void shouldProvideAlertsForLabRemindersATheRightTimes() throws Exception {
        schedule.enrollFor("Lab Reminders", newDate(2012, 1, 1), new Time(14, 0));

        schedule.assertNoAlerts("REMINDER", earliest);
        schedule.assertNoAlerts("REMINDER", due);
        schedule.assertAlerts("REMINDER", late, date(29, JULY), date(5, AUGUST), date(12, AUGUST), date(19, AUGUST),
                date(26, AUGUST), date(2, SEPTEMBER), date(9, SEPTEMBER), date(16, SEPTEMBER), date(23, SEPTEMBER), date(30, SEPTEMBER), date(7, OCTOBER));
        schedule.assertAlerts("REMINDER", max, date(9, OCTOBER), date(10, OCTOBER), date(11, OCTOBER));

        visualization.outputTo("mother-lab-reminder.html");
    }

    @Test
    public void shouldProvideAlertsForExpectedDateOfDeliveryAtTheRightTimes() throws Exception {
        schedule.enrollFor("Expected Date Of Delivery", newDate(2012, 1, 1), new Time(14, 0));

        schedule.assertNoAlerts("EDD", earliest);
        schedule.assertAlerts("EDD", due, date(23, SEPTEMBER), date(30, SEPTEMBER), date(7, OCTOBER));
        schedule.assertAlertsStartWith("EDD", late, date(10, OCTOBER), date(14, OCTOBER), date(17, OCTOBER), date(21, OCTOBER), date(24, OCTOBER), date(28, OCTOBER), date(31, OCTOBER), date(4, NOVEMBER));
        schedule.assertNoAlerts("EDD", max);

        visualization.outputTo("mother-expected-date-of-delivery.html");
    }

    @Test
    public void shouldProvideAlertsForBCG() throws Exception {
        schedule.enrollFor("BCG", newDate(2012, 1, 1), new Time(14, 0));

        schedule.assertNoAlerts("REMINDER", earliest);
        schedule.assertAlertsStartWith("REMINDER", due, date(15, JANUARY), date(18, JANUARY), date(22, JANUARY), date(25, JANUARY),
                date(29, JANUARY), date(1, FEBRUARY), date(5, FEBRUARY), date(8, FEBRUARY), date(12, FEBRUARY),
                date(15, FEBRUARY), date(19, FEBRUARY), date(22, FEBRUARY));
        schedule.assertNoAlerts("REMINDER", late);
        schedule.assertNoAlerts("REMINDER", max);

        visualization.outputTo("child-bcg.html");
    }

    @Test
    public void shouldProvideAlertsForOPV() throws Exception {
        schedule.enrollFor("OPV", newDate(2012, 1, 1), new Time(14, 0));

        schedule.assertNoAlerts("OPV 0", earliest);
        schedule.assertAlertsStartWith("OPV 0", due, date(15, JANUARY), date(18, JANUARY), date(22, JANUARY), date(25, JANUARY),
                date(29, JANUARY), date(1, FEBRUARY), date(5, FEBRUARY), date(8, FEBRUARY), date(12, FEBRUARY),
                date(15, FEBRUARY), date(19, FEBRUARY), date(22, FEBRUARY));
        schedule.assertNoAlerts("OPV 0", late);
        schedule.assertNoAlerts("OPV 0", max);

        schedule.assertNoAlerts("OPV 1", earliest);
        schedule.assertAlerts("OPV 1", due, date(5, FEBRUARY), date(12, FEBRUARY));
        schedule.assertAlertsStartWith("OPV 1", late, date(19, FEBRUARY), date(22, FEBRUARY), date(26, FEBRUARY), date(29, FEBRUARY),
                date(4, MARCH), date(7, MARCH));
        schedule.assertNoAlerts("OPV 1", max);

        schedule.assertNoAlerts("OPV 2", earliest);
        schedule.assertAlerts("OPV 2", due, date(4, MARCH), date(11, MARCH));
        schedule.assertAlertsStartWith("OPV 2", late, date(18, MARCH), date(21, MARCH), date(25, MARCH), date(28, MARCH),
                date(1, APRIL), date(4, APRIL));
        schedule.assertNoAlerts("OPV 2", max);

        schedule.assertNoAlerts("OPV 3", earliest);
        schedule.assertAlerts("OPV 3", due, date(1, APRIL), date(8, APRIL));
        schedule.assertAlertsStartWith("OPV 3", late, date(15, APRIL), date(18, APRIL), date(22, APRIL), date(25, APRIL),
                date(29, APRIL), date(2, MAY));
        schedule.assertNoAlerts("OPV 3", max);

        visualization.outputTo("child-opv.html");
    }

    @Test
    public void shouldProvideAlertsForDPT() throws Exception {
        schedule.enrollFor("DPT", newDate(2012, 1, 1), new Time(14, 0));

        schedule.assertNoAlerts("DPT 0", earliest);
        schedule.assertAlertsStartWith("DPT 0", due, date(15, JANUARY), date(18, JANUARY), date(22, JANUARY), date(25, JANUARY),
                date(29, JANUARY), date(1, FEBRUARY), date(5, FEBRUARY), date(8, FEBRUARY), date(12, FEBRUARY),
                date(15, FEBRUARY), date(19, FEBRUARY), date(22, FEBRUARY));
        schedule.assertNoAlerts("DPT 0", late);
        schedule.assertNoAlerts("DPT 0", max);

        schedule.assertNoAlerts("DPT 1", earliest);
        schedule.assertAlerts("DPT 1", due, date(5, FEBRUARY), date(12, FEBRUARY));
        schedule.assertAlertsStartWith("DPT 1", late, date(19, FEBRUARY), date(22, FEBRUARY), date(26, FEBRUARY), date(29, FEBRUARY),
                date(4, MARCH), date(7, MARCH));
        schedule.assertNoAlerts("DPT 1", max);

        schedule.assertNoAlerts("DPT 2", earliest);
        schedule.assertAlerts("DPT 2", due, date(4, MARCH), date(11, MARCH));
        schedule.assertAlertsStartWith("DPT 2", late, date(18, MARCH), date(21, MARCH), date(25, MARCH), date(28, MARCH),
                date(1, APRIL), date(4, APRIL));
        schedule.assertNoAlerts("DPT 2", max);

        schedule.assertNoAlerts("DPT 3", earliest);
        schedule.assertAlerts("DPT 3", due, date(1, APRIL), date(8, APRIL));
        schedule.assertAlertsStartWith("DPT 3", late, date(15, APRIL), date(18, APRIL), date(22, APRIL), date(25, APRIL),
                date(29, APRIL), date(2, MAY));
        schedule.assertNoAlerts("DPT 3", max);

        visualization.outputTo("child-dpt.html");
    }

    @Test
    public void shouldProvideAlertsForHepatitisVaccination() throws Exception {
        schedule.enrollFor("Hepatitis", newDate(2012, 1, 1), new Time(14, 0));

        schedule.assertNoAlerts("Hepatitis B1", earliest);
        schedule.assertAlertsStartWith("Hepatitis B1", due, date(15, JANUARY), date(18, JANUARY), date(22, JANUARY), date(25, JANUARY),
                date(29, JANUARY), date(1, FEBRUARY), date(5, FEBRUARY), date(8, FEBRUARY), date(12, FEBRUARY),
                date(15, FEBRUARY), date(19, FEBRUARY), date(22, FEBRUARY));
        schedule.assertNoAlerts("Hepatitis B1", late);
        schedule.assertNoAlerts("Hepatitis B1", max);

        schedule.assertNoAlerts("Hepatitis B2", earliest);
        schedule.assertAlerts("Hepatitis B2", due, date(5, FEBRUARY), date(12, FEBRUARY));
        schedule.assertAlertsStartWith("Hepatitis B2", late, date(19, FEBRUARY), date(22, FEBRUARY), date(26, FEBRUARY), date(29, FEBRUARY),
                date(4, MARCH), date(7, MARCH));
        schedule.assertNoAlerts("Hepatitis B2", max);

        schedule.assertNoAlerts("Hepatitis B3", earliest);
        schedule.assertAlerts("Hepatitis B3", due, date(4, MARCH), date(11, MARCH));
        schedule.assertAlertsStartWith("Hepatitis B3", late, date(18, MARCH), date(21, MARCH), date(25, MARCH), date(28, MARCH),
                date(1, APRIL), date(4, APRIL));
        schedule.assertNoAlerts("Hepatitis B3", max);

        schedule.assertNoAlerts("Hepatitis B4", earliest);
        schedule.assertAlerts("Hepatitis B4", due, date(1, APRIL), date(8, APRIL));
        schedule.assertAlertsStartWith("Hepatitis B4", late, date(15, APRIL), date(18, APRIL), date(22, APRIL), date(25, APRIL), date(29, APRIL), date(2, MAY), date(6, MAY), date(9, MAY), date(13, MAY), date(16, MAY));
        schedule.assertNoAlerts("Hepatitis B4", max);

        visualization.outputTo("child-hepatitis.html");
    }

    @Test
    public void shouldProvideAlertForMeaslesVaccinationAndVitaminSupplements() throws Exception {
        schedule.enrollFor("Measles Vaccination and Vitamin Supplements", newDate(2012, 1, 1), new Time(14, 0));

        schedule.assertNoAlerts("REMINDER", earliest);
        schedule.assertAlerts("REMINDER", due, date(9, SEPTEMBER));
        schedule.assertNoAlerts("REMINDER", late);
        schedule.assertNoAlerts("REMINDER", max);

        visualization.outputTo("child-measles-and-vitamins.html");
    }

    @Test
    public void shouldProvideAlertForBoosterDoses() throws Exception {
        schedule.enrollFor("Boosters", newDate(2012, 1, 1), new Time(14, 0));

        schedule.assertNoAlerts("REMINDER", earliest);
        schedule.assertAlerts("REMINDER", due, dateWithYear(19, MAY, 2013));
        schedule.assertNoAlerts("REMINDER", late);
        schedule.assertNoAlerts("REMINDER", max);

        visualization.outputTo("child-boosters.html");
    }

    @Before
    public void setUp() throws Exception {
        schedule = new TestSchedule(trackingService, schedulerFactoryBean, new SetDateAction() {
            @Override
            public void setTheDateTo(LocalDate date) {
                mockCurrentDate(date);
            }
        });

        String outputDir = null;
        if (new File("drishti-web").exists()) {
            outputDir = "drishti-web/doc/schedules/";
        }
        else if (new File("doc").exists()) {
            outputDir = "doc/schedules/";
        }
        visualization = new ScheduleVisualization(schedule, outputDir);
    }

    @BeforeClass
    public static void turnOffSpringLogging() {
        Logger logger = Logger.getLogger("org.springframework");
        logger.setLevel(Level.FATAL);
    }

    private Date date(int day, int month) {
        return dateWithYear(day, month, 2012);
    }

    private Date dateWithYear(int day, int month, int year) {
        return new DateTime(year, month, day, 14, 0).toDate();
    }
}
