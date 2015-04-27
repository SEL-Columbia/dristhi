package org.opensrp.scheduler;

import java.text.MessageFormat;

public class NoRoutesMatchException extends RuntimeException {
    public NoRoutesMatchException(MilestoneEvent event) {
        super(MessageFormat.format("No route found for the Milestone event with scheduleName: {0}, milestoneName: {1}, windowName: {2}",
                event.scheduleName(), event.milestoneName(), event.windowName()));
    }
}
