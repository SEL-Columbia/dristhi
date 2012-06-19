package org.ei.drishti.util;

import org.joda.time.DateTime;
import org.motechproject.scheduler.domain.MotechEvent;
import org.motechproject.scheduletracking.api.domain.MilestoneAlert;
import org.motechproject.scheduletracking.api.domain.WindowName;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.motechproject.scheduletracking.api.events.constants.EventDataKeys.*;

public class EventBuilder {
    private String scheduleName;
    private String milestone;
    private WindowName window;
    private String externalID;
    private DateTime dueDate;

    public EventBuilder withSchedule(String scheduleName) {
        this.scheduleName = scheduleName;
        return this;
    }

    public EventBuilder withMilestone(String milestone) {
        this.milestone = milestone;
        return this;
    }

    public EventBuilder withWindow(WindowName window) {
        this.window = window;
        return this;
    }

    public EventBuilder withExternalId(String externalID) {
        this.externalID = externalID;
        return this;
    }

    public EventBuilder withDueDate(DateTime dueDate) {
        this.dueDate = dueDate;
        return this;
    }

    public MotechEvent build() {
        MilestoneAlert alert = mock(MilestoneAlert.class);
        when(alert.getMilestoneName()).thenReturn(milestone);
        when(alert.getDueDateTime()).thenReturn(dueDate);

        Map<String, Object> parameters = new HashMap<String, Object>();

        parameters.put(SCHEDULE_NAME, scheduleName);
        parameters.put(MILESTONE_NAME, alert);
        parameters.put(WINDOW_NAME, window.toString());
        parameters.put(EXTERNAL_ID, externalID);

        return new MotechEvent("Subject", parameters);
    }
}
