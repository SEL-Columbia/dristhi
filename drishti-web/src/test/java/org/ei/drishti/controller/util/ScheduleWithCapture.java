package org.ei.drishti.controller.util;

import org.joda.time.LocalDate;
import org.motechproject.model.Time;
import org.motechproject.scheduletracking.api.domain.WindowName;

import java.util.Date;

public class ScheduleWithCapture {
    private final FakeSchedule fakeSchedule;
    private final ScheduleVisualization visualization;

    public ScheduleWithCapture(FakeSchedule fakeSchedule, ScheduleVisualization visualization) {
        this.fakeSchedule = fakeSchedule;
        this.visualization = visualization;
    }

    public ScheduleWithCapture withFulfillmentDates(Date... dates) {
        fakeSchedule.withFulfillmentDates(dates);
        return this;
    }

    public void enrollFor(String scheduleName, LocalDate referenceDate, Time preferredAlertTime) throws Exception {
        fakeSchedule.enrollFor(scheduleName, referenceDate, preferredAlertTime);
    }

    public void assertNoAlerts(String milestoneName, WindowName windowName) {
        fakeSchedule.assertNoAlerts(milestoneName, windowName);
    }

    public void assertAlerts(String milestoneName, WindowName windowName, Date... dates) {
        fakeSchedule.assertAlerts(milestoneName, windowName, dates);
        visualization.addAlerts(milestoneName, windowName, dates);
    }

    public void assertAlertsStartWith(String milestoneName, WindowName windowName, Date... dates) {
        fakeSchedule.assertAlertsStartWith(milestoneName, windowName, dates);
        visualization.addPartialAlerts(milestoneName, windowName, dates);
    }
}
