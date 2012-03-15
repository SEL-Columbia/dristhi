package org.ei.drishti.scheduler.router;

import org.motechproject.model.MotechEvent;

public interface Action {
    void invoke(MilestoneEvent event);
}
