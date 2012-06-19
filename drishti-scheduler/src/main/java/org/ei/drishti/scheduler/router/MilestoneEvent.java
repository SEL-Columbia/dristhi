package org.ei.drishti.scheduler.router;

import org.joda.time.DateTime;
import org.motechproject.scheduler.domain.MotechEvent;
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
        return alert().getMilestoneName();
    }

    public String externalId() {
        return event.getParameters().get(EventDataKeys.EXTERNAL_ID).toString();
    }

    public DateTime due() {
        return alert().getDueDateTime();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MilestoneEvent that = (MilestoneEvent) o;

        return !(event != null ? !event.equals(that.event) : that.event != null);
    }

    @Override
    public int hashCode() {
        return event != null ? event.hashCode() : 0;
    }

    private MilestoneAlert alert() {
        return (MilestoneAlert) event.getParameters().get(MILESTONE_NAME);
    }
}
