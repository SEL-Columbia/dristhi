package org.ei.drishti.scheduler.router;

import org.motechproject.model.MotechEvent;
import org.motechproject.scheduletracking.api.domain.MilestoneAlert;
import org.motechproject.scheduletracking.api.events.constants.EventDataKeys;

import static org.motechproject.scheduletracking.api.events.constants.EventDataKeys.MILESTONE_NAME;
import static org.motechproject.scheduletracking.api.events.constants.EventDataKeys.SCHEDULE_NAME;
import static org.motechproject.scheduletracking.api.events.constants.EventDataKeys.WINDOW_NAME;

public class MilestoneEvent {
    private final MotechEvent event;

    public MilestoneEvent(MotechEvent event) {
        this.event = event;
    }

    public String scheduleName() {
        return (String) event.getParameters().get(SCHEDULE_NAME);
    }

    public String windowName() {
        return (String) event.getParameters().get(WINDOW_NAME);
    }

    public String milestoneName() {
        return ((MilestoneAlert) event.getParameters().get(MILESTONE_NAME)).getMilestoneName();
    }

    public String externalId() {
        return event.getParameters().get(EventDataKeys.EXTERNAL_ID).toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MilestoneEvent that = (MilestoneEvent) o;

        if (event != null ? !event.equals(that.event) : that.event != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return event != null ? event.hashCode() : 0;
    }
}
