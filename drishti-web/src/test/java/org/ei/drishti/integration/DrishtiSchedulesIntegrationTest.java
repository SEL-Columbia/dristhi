package org.ei.drishti.integration;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.delivery.schedule.util.FakeSchedule;
import org.motechproject.delivery.schedule.util.ScheduleVisualization;
import org.motechproject.delivery.schedule.util.ScheduleWithCapture;
import org.motechproject.delivery.schedule.util.SetDateAction;
import org.motechproject.model.Time;
import org.motechproject.scheduletracking.api.service.ScheduleTrackingService;
import org.motechproject.testing.utils.BaseUnitTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.File;
import java.util.Date;

import static org.ei.drishti.common.AllConstants.ChildImmunizationCommCareFields.*;
import static org.ei.drishti.common.util.DateUtil.fakeIt;
import static org.ei.drishti.scheduler.DrishtiScheduleConstants.ChildScheduleConstants.*;
import static org.motechproject.scheduletracking.api.domain.WindowName.*;
import static org.motechproject.util.DateUtil.newDate;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:spring/applicationContext-drishti-web.xml")
public class DrishtiSchedulesIntegrationTest extends BaseUnitTest {
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
    private static final int DECEMBER = 12;

    @Autowired
    private ScheduleTrackingService trackingService;
    @Autowired
    private SchedulerFactoryBean schedulerFactoryBean;

    private ScheduleWithCapture schedule;
    private ScheduleVisualization visualization;

    @Test
    public void shouldProvideAlertsForANCAtTheRightTimes() throws Exception {
        schedule.enrollFor("Ante Natal Care - Normal", newDate(2012, 1, 1), new Time(14, 0));

        schedule.assertNoAlerts("ANC 1", earliest);
        schedule.assertAlerts("ANC 1", due, date(1, JANUARY), date(8, JANUARY), date(15, JANUARY), date(22, JANUARY), date(29, JANUARY), date(5, FEBRUARY), date(12, FEBRUARY), date(19, FEBRUARY),
                date(26, FEBRUARY), date(4, MARCH), date(11, MARCH), date(18, MARCH));
        schedule.assertAlerts("ANC 1", late, date(25, MARCH), date(28, MARCH), date(1, APRIL), date(4, APRIL));
        schedule.assertNoAlerts("ANC 1", max);

        schedule.assertNoAlerts("ANC 2", earliest);
        schedule.assertAlerts("ANC 2", due, date(8, APRIL), date(15, APRIL), date(22, APRIL), date(29, APRIL), date(6, MAY), date(13, MAY), date(20, MAY), date(27, MAY),
                date(3, JUNE), date(10, JUNE), date(17, JUNE), date(24, JUNE));
        schedule.assertAlerts("ANC 2", late, date(1, JULY), date(4, JULY), date(8, JULY), date(11, JULY));
        schedule.assertNoAlerts("ANC 2", max);

        schedule.assertNoAlerts("ANC 3", earliest);
        schedule.assertAlerts("ANC 3", due, date(15, JULY), date(22, JULY), date(29, JULY), date(5, AUGUST), date(12, AUGUST), date(19, AUGUST));
        schedule.assertAlerts("ANC 3", late, date(26, AUGUST), date(29, AUGUST), date(2, SEPTEMBER), date(5, SEPTEMBER));
        schedule.assertNoAlerts("ANC 3", max);

        schedule.assertNoAlerts("ANC 4", earliest);
        schedule.assertAlerts("ANC 4", due, date(9, SEPTEMBER), date(16, SEPTEMBER), date(23, SEPTEMBER));
        schedule.assertAlertsStartWith("ANC 4", late, date(30, SEPTEMBER), date(3, OCTOBER), date(7, OCTOBER), date(10, OCTOBER));
        schedule.assertNoAlerts("ANC 4", max);

        visualization.outputTo("mother-anc-normal.html", 3);
    }

    @Test
    public void shouldProvideAlertsForLabRemindersATheRightTimes() throws Exception {
        schedule.enrollFor("Lab Reminders", newDate(2012, 1, 1), new Time(14, 0));

        schedule.assertNoAlerts("REMINDER", earliest);
        schedule.assertNoAlerts("REMINDER", due);
        schedule.assertAlerts("REMINDER", late, date(29, JULY), date(5, AUGUST), date(12, AUGUST), date(19, AUGUST),
                date(26, AUGUST), date(2, SEPTEMBER), date(9, SEPTEMBER), date(16, SEPTEMBER), date(23, SEPTEMBER), date(30, SEPTEMBER), date(7, OCTOBER));
        schedule.assertAlerts("REMINDER", max, date(9, OCTOBER), date(10, OCTOBER), date(11, OCTOBER));

        visualization.outputTo("mother-lab-reminder.html", 3);
    }

    @Test
    public void shouldProvideAlertsForExpectedDateOfDeliveryAtTheRightTimes() throws Exception {
        schedule.enrollFor("Expected Date Of Delivery", newDate(2012, 1, 1), new Time(14, 0));

        schedule.assertNoAlerts("EDD", earliest);
        schedule.assertAlerts("EDD", due, date(23, SEPTEMBER), date(30, SEPTEMBER), date(7, OCTOBER));
        schedule.assertAlertsStartWith("EDD", late, date(10, OCTOBER), date(14, OCTOBER), date(17, OCTOBER), date(21, OCTOBER), date(24, OCTOBER), date(28, OCTOBER), date(31, OCTOBER), date(4, NOVEMBER));
        schedule.assertNoAlerts("EDD", max);

        visualization.outputTo("mother-expected-date-of-delivery.html", 3);
    }

    @Test
    public void shouldProvideAlertsForBCG() throws Exception {
        schedule.enrollFor(CHILD_SCHEDULE_BCG, newDate(2012, 1, 1), new Time(14, 0));

        schedule.assertNoAlerts(BCG_COMMCARE_VALUE, earliest);
        schedule.assertAlertsStartWith(BCG_COMMCARE_VALUE, due, date(15, JANUARY), date(18, JANUARY), date(22, JANUARY), date(25, JANUARY),
                date(29, JANUARY), date(1, FEBRUARY), date(5, FEBRUARY), date(8, FEBRUARY), date(12, FEBRUARY),
                date(15, FEBRUARY), date(19, FEBRUARY), date(22, FEBRUARY));
        schedule.assertNoAlerts(BCG_COMMCARE_VALUE, late);
        schedule.assertNoAlerts(BCG_COMMCARE_VALUE, max);

        visualization.outputTo("child-bcg.html", 1);
    }

    @Test
    public void shouldProvideAlertsForOPV() throws Exception {
        schedule.enrollFor(CHILD_SCHEDULE_OPV, newDate(2012, 1, 1), new Time(14, 0));

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

        visualization.outputTo("child-opv.html", 1);
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

        visualization.outputTo("child-dpt.html", 1);
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

        visualization.outputTo("child-hepatitis.html", 1);
    }

    @Test
    public void shouldProvideAlertForMeaslesVaccination() throws Exception {
        schedule.enrollFor(CHILD_SCHEDULE_MEASLES, newDate(2012, 1, 1), new Time(14, 0));

        schedule.assertNoAlerts(MEASLES_COMMCARE_VALUE, earliest);
        schedule.assertAlertsStartWith(MEASLES_COMMCARE_VALUE, due, date(1, OCTOBER), date(4, OCTOBER), date(8, OCTOBER), date(11, OCTOBER), date(15, OCTOBER));
        schedule.assertAlertsStartWith(MEASLES_COMMCARE_VALUE, late, dateWithYear(1, JANUARY, 2013), dateWithYear(4, JANUARY, 2013), dateWithYear(8, JANUARY, 2013), dateWithYear(11, JANUARY, 2013));
        schedule.assertNoAlerts(MEASLES_COMMCARE_VALUE, max);

        visualization.outputTo("child-measles.html", 4);
    }

    @Test
    public void shouldProvideAlertForMeaslesBoosterVaccination() throws Exception {
        schedule.enrollFor(CHILD_SCHEDULE_MEASLES_BOOSTER, newDate(2012, 1, 1), new Time(14, 0));

        schedule.assertNoAlerts(MEASLES_BOOSTER_COMMCARE_VALUE, earliest);
        schedule.assertAlertsStartWith(MEASLES_BOOSTER_COMMCARE_VALUE, due, date(1, AUGUST), date(4, AUGUST), date(8, AUGUST), date(11, AUGUST));
        schedule.assertAlertsStartWith(MEASLES_BOOSTER_COMMCARE_VALUE, late, dateWithYear(1, FEBRUARY, 2013), dateWithYear(4, FEBRUARY, 2013), dateWithYear(8, FEBRUARY, 2013));
        schedule.assertNoAlerts(MEASLES_BOOSTER_COMMCARE_VALUE, max);

        visualization.outputTo("child-measles-booster.html", 4);
    }

    @Test
    public void shouldProvideAlertForDPT1Vaccination() throws Exception {
        schedule.enrollFor(CHILD_SCHEDULE_DPT1, newDate(2012, 1, 1), new Time(14, 0));

        schedule.assertNoAlerts(DPT_1_COMMCARE_VALUE, earliest);
        schedule.assertAlerts(DPT_1_COMMCARE_VALUE, due, date(1, JULY), date(4, JULY));
        schedule.assertAlertsStartWith(DPT_1_COMMCARE_VALUE, late, dateWithYear(8, JULY, 2012), dateWithYear(11, JULY, 2012), dateWithYear(15, JULY, 2012));
        schedule.assertNoAlerts(DPT_1_COMMCARE_VALUE, max);

        visualization.outputTo("child-dpt1.html", 4);
    }

    @Test
    public void shouldProvideAlertForDPT2Vaccination() throws Exception {
        schedule.enrollFor(CHILD_SCHEDULE_DPT2, newDate(2012, 1, 1), new Time(14, 0));

        schedule.assertNoAlerts(DPT_2_COMMCARE_VALUE, earliest);
        schedule.assertAlerts(DPT_2_COMMCARE_VALUE, due, date(29, APRIL), date(2, MAY));
        schedule.assertAlertsStartWith(DPT_2_COMMCARE_VALUE, late, dateWithYear(6, MAY, 2012), dateWithYear(9, MAY, 2012), dateWithYear(13, MAY, 2012));
        schedule.assertNoAlerts(DPT_2_COMMCARE_VALUE, max);

        visualization.outputTo("child-dpt2.html", 4);
    }

    @Test
    public void shouldProvideAlertForDPT3Vaccination() throws Exception {
        schedule.enrollFor(CHILD_SCHEDULE_DPT3, newDate(2012, 1, 1), new Time(14, 0));

        schedule.assertNoAlerts(DPT_3_COMMCARE_VALUE, earliest);
        schedule.assertAlerts(DPT_3_COMMCARE_VALUE, due, date(29, APRIL), date(02, MAY));
        schedule.assertAlertsStartWith(DPT_3_COMMCARE_VALUE, late, dateWithYear(6, MAY, 2012), dateWithYear(9, MAY, 2012), dateWithYear(13, MAY, 2012));
        schedule.assertNoAlerts(DPT_3_COMMCARE_VALUE, max);

        visualization.outputTo("child-dpt3.html", 4);
    }

    @Test
    public void shouldProvideAlertForDPTBooster1Vaccination() throws Exception {
        schedule.enrollFor(CHILD_SCHEDULE_DPT_BOOSTER1, newDate(2012, 1, 1), new Time(14, 0));

        schedule.assertNoAlerts(DPT_BOOSTER_1_COMMCARE_VALUE, earliest);
        schedule.assertAlertsStartWith(DPT_BOOSTER_1_COMMCARE_VALUE, due, dateWithYear(1, FEBRUARY, 2013), dateWithYear(4, FEBRUARY, 2013), dateWithYear(8, FEBRUARY, 2013));
        schedule.assertAlertsStartWith(DPT_BOOSTER_1_COMMCARE_VALUE, late, dateWithYear(1, OCTOBER, 2013), dateWithYear(4, OCTOBER, 2013), dateWithYear(8, OCTOBER, 2013));
        schedule.assertNoAlerts(DPT_BOOSTER_1_COMMCARE_VALUE, max);

        visualization.outputTo("child-dpt-booster1.html", 4);
    }

    @Ignore
    public void shouldProvideAlertForDPTBooster2Vaccination() throws Exception {
        schedule.enrollFor(CHILD_SCHEDULE_DPT_BOOSTER2, newDate(2012, 1, 1), new Time(14, 0));

        schedule.assertNoAlerts(DPT_BOOSTER_2_COMMCARE_VALUE, earliest);
        schedule.assertAlertsStartWith(DPT_BOOSTER_2_COMMCARE_VALUE, due, dateWithYear(1, JANUARY, 2015), dateWithYear(4, JANUARY, 2015), dateWithYear(8, JANUARY, 2015));
        schedule.assertAlertsStartWith(DPT_BOOSTER_2_COMMCARE_VALUE, late, dateWithYear(1, OCTOBER, 2015), dateWithYear(4, OCTOBER, 2015), dateWithYear(8, OCTOBER, 2015));
        schedule.assertNoAlerts(DPT_BOOSTER_2_COMMCARE_VALUE, max);

        visualization.outputTo("child-dpt-booster2.html", 4);
    }

    @Test
    public void shouldProvideAlertForBoosterDoses() throws Exception {
        schedule.enrollFor("Boosters", newDate(2012, 1, 1), new Time(14, 0));

        schedule.assertNoAlerts("REMINDER", earliest);
        schedule.assertAlerts("REMINDER", due, dateWithYear(19, MAY, 2013));
        schedule.assertNoAlerts("REMINDER", late);
        schedule.assertNoAlerts("REMINDER", max);

        visualization.outputTo("child-measles-boosters.html", 4);
    }

    @Test
    public void shouldProvideAlertsForPNCCloseAtTheRightTimes() throws Exception {
        schedule.enrollFor("Auto Close PNC", newDate(2012, 1, 1), new Time(14, 0));

        schedule.assertNoAlerts("Auto Close PNC", earliest);
        schedule.assertAlertsStartWith("Auto Close PNC", due, dateWithYear(26, FEBRUARY, 2012), dateWithYear(29, FEBRUARY, 2012));
        schedule.assertNoAlerts("Auto Close PNC", late);
        schedule.assertNoAlerts("Auto Close PNC", max);

        visualization.outputTo("mother-auto-close-pnc.html", 1);
    }

    @Test
    public void shouldProvideAlertsForDMPAInjectableRefillAtTheRightTimes() throws Exception {
        schedule.enrollFor("DMPA Injectable Refill", newDate(2012, JANUARY, 1), new Time(14, 0));

        schedule.assertAlertsStartWith("DMPA Injectable Refill", earliest,
                dateWithYear(1, JANUARY, 2012), dateWithYear(2, JANUARY, 2012), dateWithYear(3, JANUARY, 2012), dateWithYear(4, JANUARY, 2012));
        schedule.assertAlerts("DMPA Injectable Refill", due, dateWithYear(25, MARCH, 2012));
        schedule.assertAlertsStartWith("DMPA Injectable Refill", late, dateWithYear(1, APRIL, 2012), dateWithYear(8, APRIL, 2012), dateWithYear(15, APRIL, 2012), dateWithYear(22, APRIL, 2012));
        schedule.assertNoAlerts("DMPA Injectable Refill", max);

        visualization.outputTo("ec-dmpa-injectable-refill.html", 1);
    }

    @Test
    public void shouldProvideAlertsForOCPRefillAtTheRightTimes() throws Exception {
        schedule.enrollFor("OCP Refill", newDate(2012, JANUARY, 1), new Time(14, 0));

        schedule.assertNoAlerts("OCP Refill", earliest);
        schedule.assertAlerts("OCP Refill", due, dateWithYear(1, JANUARY, 2012));
        schedule.assertAlertsStartWith("OCP Refill", late, dateWithYear(8, JANUARY, 2012), dateWithYear(15, JANUARY, 2012), dateWithYear(22, JANUARY, 2012), dateWithYear(29, JANUARY, 2012));
        schedule.assertNoAlerts("OCP Refill", max);

        visualization.outputTo("ec-ocp-refill.html", 1);
    }

    @Test
    public void shouldProvideAlertsForCondomRefillAtTheRightTimes() throws Exception {
        schedule.enrollFor("Condom Refill", newDate(2012, JANUARY, 1), new Time(14, 0));

        schedule.assertNoAlerts("Condom Refill", earliest);
        schedule.assertAlerts("Condom Refill", due, dateWithYear(1, JANUARY, 2012));
        schedule.assertAlertsStartWith("Condom Refill", late, dateWithYear(8, JANUARY, 2012), dateWithYear(15, JANUARY, 2012), dateWithYear(22, JANUARY, 2012), dateWithYear(29, JANUARY, 2012));
        schedule.assertNoAlerts("Condom Refill", max);

        visualization.outputTo("ec-condom-refill.html", 1);
    }

    @Test
    public void shouldProvideAlertsForFemaleSterilizationFollowupAtTheRightTimes() throws Exception {
        schedule.enrollFor("Female sterilization Followup", newDate(2012, JANUARY, 1), new Time(14, 0));

        schedule.assertNoAlerts("Female sterilization Followup 1", earliest);
        schedule.assertAlerts("Female sterilization Followup 1", due, dateWithYear(1, JANUARY, 2012), dateWithYear(2, JANUARY, 2012));
        schedule.assertAlerts("Female sterilization Followup 1", late, dateWithYear(3, JANUARY, 2012), dateWithYear(4, JANUARY, 2012), dateWithYear(5, JANUARY, 2012), dateWithYear(6, JANUARY, 2012), dateWithYear(7, JANUARY, 2012));
        schedule.assertNoAlerts("Female sterilization Followup 1", max);

        schedule.assertNoAlerts("Female sterilization Followup 2", earliest);
        schedule.assertAlerts("Female sterilization Followup 2", due, dateWithYear(8, JANUARY, 2012), dateWithYear(9, JANUARY, 2012));
        schedule.assertAlertsStartWith("Female sterilization Followup 2", late, dateWithYear(10, JANUARY, 2012), dateWithYear(13, JANUARY, 2012), dateWithYear(16, JANUARY, 2012));
        schedule.assertNoAlerts("Female sterilization Followup 2", max);

        schedule.assertNoAlerts("Female sterilization Followup 3", earliest);
        schedule.assertAlertsStartWith("Female sterilization Followup 3", due, dateWithYear(1, FEBRUARY, 2012), dateWithYear(2, FEBRUARY, 2012));
        schedule.assertAlertsStartWith("Female sterilization Followup 3", late, dateWithYear(8, FEBRUARY, 2012), dateWithYear(15, FEBRUARY, 2012), dateWithYear(22, FEBRUARY, 2012));
        schedule.assertNoAlerts("Female sterilization Followup 3", max);

        visualization.outputTo("ec-female-sterilization-followup.html", 1);
    }

    @Test
    public void shouldProvideAlertsForMaleSterilizationFollowupAtTheRightTimes() throws Exception {
        schedule.enrollFor("Male sterilization Followup", newDate(2012, JANUARY, 1), new Time(14, 0));

        schedule.assertNoAlerts("Male sterilization Followup 1", earliest);
        schedule.assertAlerts("Male sterilization Followup 1", due, dateWithYear(1, JANUARY, 2012), dateWithYear(2, JANUARY, 2012));
        schedule.assertAlertsStartWith("Male sterilization Followup 1", late, dateWithYear(3, JANUARY, 2012), dateWithYear(14, JANUARY, 2012), dateWithYear(25, JANUARY, 2012));
        schedule.assertNoAlerts("Male sterilization Followup 1", max);

        schedule.assertNoAlerts("Male sterilization Followup 2", earliest);
        schedule.assertAlertsStartWith("Male sterilization Followup 2", due, dateWithYear(1, APRIL, 2012), dateWithYear(2, APRIL, 2012), dateWithYear(3, APRIL, 2012));
        schedule.assertAlertsStartWith("Male sterilization Followup 2", late, dateWithYear(8, APRIL, 2012), dateWithYear(15, APRIL, 2012), dateWithYear(22, APRIL, 2012));
        schedule.assertNoAlerts("Male sterilization Followup 2", max);

        visualization.outputTo("ec-male-sterilization-followup.html", 1);
    }

    @Test
    public void shouldProvideAlertsForIUDFollowupAtTheRightTimes() throws Exception {
        schedule.enrollFor("IUD Followup", newDate(2012, JANUARY, 1), new Time(14, 0));

        schedule.assertAlertsStartWith("IUD Followup 1", earliest, dateWithYear(1, JANUARY, 2012), dateWithYear(2, JANUARY, 2012), dateWithYear(3, JANUARY, 2012));
        schedule.assertAlertsStartWith("IUD Followup 1", due, dateWithYear(1, FEBRUARY, 2012), dateWithYear(2, FEBRUARY, 2012), dateWithYear(3, FEBRUARY, 2012));
        schedule.assertAlertsStartWith("IUD Followup 1", late, dateWithYear(8, FEBRUARY, 2012), dateWithYear(15, FEBRUARY, 2012), dateWithYear(22, FEBRUARY, 2012));
        schedule.assertNoAlerts("IUD Followup 1", max);

        schedule.assertNoAlerts("IUD Followup 2", earliest);
        schedule.assertAlertsStartWith("IUD Followup 2", due, dateWithYear(1, MAY, 2012), dateWithYear(2, MAY, 2012), dateWithYear(3, MAY, 2012));
        schedule.assertAlertsStartWith("IUD Followup 2", late, dateWithYear(8, MAY, 2012), dateWithYear(15, MAY, 2012), dateWithYear(22, MAY, 2012));
        schedule.assertNoAlerts("IUD Followup 2", max);

        visualization.outputTo("ec-iud-followup.html", 1);
    }

    @Test
    public void shouldProvideAlertsForFPFollowupAtTheRightTimes() throws Exception {
        schedule.enrollFor("FP Followup", newDate(2012, JANUARY, 1), new Time(14, 0));

        schedule.assertAlerts("FP Followup", earliest, dateWithYear(1, JANUARY, 2012), dateWithYear(2, JANUARY, 2012), dateWithYear(3, JANUARY, 2012));
        schedule.assertAlerts("FP Followup", due, dateWithYear(4, JANUARY, 2012), dateWithYear(5, JANUARY, 2012), dateWithYear(6, JANUARY, 2012), dateWithYear(7, JANUARY, 2012));
        schedule.assertAlertsStartWith("FP Followup", late, dateWithYear(8, JANUARY, 2012), dateWithYear(15, JANUARY, 2012), dateWithYear(22, JANUARY, 2012), dateWithYear(29, JANUARY, 2012));
        schedule.assertNoAlerts("FP Followup", max);

        visualization.outputTo("ec-fp-followup.html", 1);
    }

    @Test
    public void shouldProvideAlertsForFPReferralFollowupAtTheRightTimes() throws Exception {
        schedule.enrollFor("FP Referral Followup", newDate(2012, JANUARY, 1), new Time(14, 0));

        schedule.assertAlerts("FP Referral Followup", earliest, dateWithYear(1, JANUARY, 2012));
        schedule.assertAlerts("FP Referral Followup", due, dateWithYear(2, JANUARY, 2012));
        schedule.assertAlertsStartWith("FP Referral Followup", late, dateWithYear(3, JANUARY, 2012), dateWithYear(10, JANUARY, 2012), dateWithYear(17, JANUARY, 2012), dateWithYear(24, JANUARY, 2012));
        schedule.assertNoAlerts("FP Referral Followup", max);

        visualization.outputTo("ec-fp-referral-followup.html", 1);
    }

    @Test
    public void shouldProvideAlertsForTT1AtTheRightTimes() throws Exception {
        schedule.enrollFor("TT 1", newDate(2012, JANUARY, 1), new Time(14, 0));

        schedule.assertNoAlerts("TT 1", earliest);
        schedule.assertAlertsStartWith("TT 1", due, dateWithYear(1, JANUARY, 2012), dateWithYear(8, JANUARY, 2012), dateWithYear(15, JANUARY, 2012));
        schedule.assertAlertsStartWith("TT 1", late, dateWithYear(25, MARCH, 2012), dateWithYear(1, APRIL, 2012), dateWithYear(8, APRIL, 2012));
        schedule.assertNoAlerts("TT 1", max);

        visualization.outputTo("mother-tt-1.html", 1);
    }

    @Test
    public void shouldProvideAlertsForTT2AtTheRightTimes() throws Exception {
        schedule.enrollFor("TT 2", newDate(2012, JANUARY, 1), new Time(14, 0));

        schedule.assertAlertsStartWith("TT 2", earliest, dateWithYear(1, JANUARY, 2012), dateWithYear(4, JANUARY, 2012), dateWithYear(7, JANUARY, 2012));
        schedule.assertAlerts("TT 2", due, dateWithYear(29, JANUARY, 2012), dateWithYear(5, FEBRUARY, 2012));
        schedule.assertAlertsStartWith("TT 2", late, dateWithYear(12, FEBRUARY, 2012), dateWithYear(19, FEBRUARY, 2012), dateWithYear(26, FEBRUARY, 2012));
        schedule.assertNoAlerts("TT 2", max);

        visualization.outputTo("mother-tt-2.html", 1);
    }

    @Test
    public void shouldProvideAlertsForIFA1() throws Exception {
        schedule.enrollFor("IFA 1", newDate(2012, 1, 1), new Time(14, 0));

        schedule.assertAlertsStartWith("IFA 1", earliest, date(1, JANUARY), date(2, JANUARY), date(3, JANUARY));
        schedule.assertAlertsStartWith("IFA 1", due, date(8, APRIL), date(9, APRIL), date(10, APRIL), date(11, APRIL));
        schedule.assertAlertsStartWith("IFA 1", late, date(22, APRIL), date(25, APRIL), date(28, APRIL), date(1, MAY));
        schedule.assertNoAlerts("IFA 1", max);

        visualization.outputTo("mother-ifa-1.html", 1);
    }

    @Test
    public void shouldProvideAlertsForIFA2() throws Exception {
        schedule.enrollFor("IFA 2", newDate(2012, 1, 1), new Time(14, 0));

        schedule.assertAlertsStartWith("IFA 2", earliest, date(1, JANUARY), date(2, JANUARY), date(3, JANUARY));
        schedule.assertAlertsStartWith("IFA 2", due, date(1, FEBRUARY), date(2, FEBRUARY), date(3, FEBRUARY), date(4, FEBRUARY));
        schedule.assertAlertsStartWith("IFA 2", late, date(15, FEBRUARY), date(18, FEBRUARY), date(21, FEBRUARY), date(24, FEBRUARY));
        schedule.assertNoAlerts("IFA 2", max);

        visualization.outputTo("mother-ifa-2.html", 1);
    }

    @Test
    public void shouldProvideAlertsForIFA3() throws Exception {
        schedule.enrollFor("IFA 3", newDate(2012, 1, 1), new Time(14, 0));

        schedule.assertAlertsStartWith("IFA 3", earliest, date(1, JANUARY), date(2, JANUARY), date(3, JANUARY));
        schedule.assertAlertsStartWith("IFA 3", due, date(1, FEBRUARY), date(2, FEBRUARY), date(3, FEBRUARY), date(4, FEBRUARY));
        schedule.assertAlertsStartWith("IFA 3", late, date(15, FEBRUARY), date(18, FEBRUARY), date(21, FEBRUARY), date(24, FEBRUARY));
        schedule.assertNoAlerts("IFA 3", max);

        visualization.outputTo("mother-ifa-3.html", 1);
    }

    @Test
    public void shouldProvideAlertsForHbTest1AtTheRightTimes() throws Exception {
        schedule.enrollFor("Hb Test 1", newDate(2012, JANUARY, 1), new Time(14, 0));

        schedule.assertNoAlerts("Hb Test 1", earliest);
        schedule.assertAlertsStartWith("Hb Test 1", due, dateWithYear(1, JANUARY, 2012), dateWithYear(8, JANUARY, 2012), dateWithYear(15, JANUARY, 2012));
        schedule.assertAlertsStartWith("Hb Test 1", late, dateWithYear(25, MARCH, 2012), dateWithYear(1, APRIL, 2012), dateWithYear(8, APRIL, 2012));
        schedule.assertNoAlerts("Hb Test 1", max);

        visualization.outputTo("mother-hb-test-1.html", 1);
    }

    @Test
    public void shouldProvideAlertsForHbFollowupTestAtTheRightTimes() throws Exception {
        schedule.enrollFor("Hb Followup Test", newDate(2012, JANUARY, 1), new Time(14, 0));

        schedule.assertAlerts("Hb Followup Test", earliest, dateWithYear(1, JANUARY, 2012), dateWithYear(8, JANUARY, 2012),
                dateWithYear(15, JANUARY, 2012), dateWithYear(22, JANUARY, 2012));
        schedule.assertAlerts("Hb Followup Test", due, dateWithYear(1, FEBRUARY, 2012), dateWithYear(4, FEBRUARY, 2012),
                dateWithYear(7, FEBRUARY, 2012), dateWithYear(10, FEBRUARY, 2012));
        schedule.assertAlertsStartWith("Hb Followup Test", late, dateWithYear(15, FEBRUARY, 2012), dateWithYear(22, FEBRUARY, 2012), dateWithYear(29, FEBRUARY, 2012));
        schedule.assertNoAlerts("Hb Followup Test", max);

        visualization.outputTo("mother-hb-followup-test.html", 1);
    }

    @Test
    public void shouldProvideAlertsForHbTest2AtTheRightTimes() throws Exception {
        schedule.enrollFor("Hb Test 2", newDate(2012, JANUARY, 1), new Time(14, 0));

        schedule.assertNoAlerts("Hb Test 2", earliest);
        schedule.assertAlerts("Hb Test 2", due, dateWithYear(15, JULY, 2012), dateWithYear(22, JULY, 2012));
        schedule.assertAlertsStartWith("Hb Test 2", late, dateWithYear(29, JULY, 2012), dateWithYear(5, AUGUST, 2012), dateWithYear(12, AUGUST, 2012));
        schedule.assertNoAlerts("Hb Test 2", max);

        visualization.outputTo("mother-hb-test-2.html", 1);
    }

    @Before
    public void setUp() throws Exception {
        FakeSchedule fakeSchedule = new FakeSchedule(trackingService, schedulerFactoryBean, new SetDateAction() {
            @Override
            public void setTheDateTo(LocalDate date) {
                mockCurrentDate(date);
                fakeIt(date);
            }
        });

        String outputDir = null;
        if (new File("drishti-web").exists()) {
            outputDir = "drishti-web/doc/schedules/";
        } else if (new File("doc").exists()) {
            outputDir = "doc/schedules/";
        }
        visualization = new ScheduleVisualization(fakeSchedule, outputDir);

        schedule = new ScheduleWithCapture(fakeSchedule, visualization);
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
