package org.ei.drishti.integration.util;

import org.motechproject.scheduletracking.api.domain.WindowName;

import java.util.Date;
import java.util.List;

public class AlertInformation {
    private final String milestoneName;
    private final WindowName window;
    private final List<Date> alertTimes;
    private final boolean hasPartialTimes;

    public AlertInformation(String milestoneName, WindowName window, List<Date> alertTimes, boolean hasPartialTimes) {
        this.milestoneName = milestoneName;
        this.window = window;
        this.alertTimes = alertTimes;
        this.hasPartialTimes = hasPartialTimes;
    }

    public boolean hasPartialTimes() {
        return hasPartialTimes;
    }

    public String description() {
        if (hasPartialTimes) {
            return milestoneName + " - " + window + " (Will continue till service is delivered)";
        }
        return milestoneName + " - " + window;
    }

    public List<Date> times() {
        return alertTimes;
    }

    public String milestoneName() {
        return milestoneName;
    }
}
