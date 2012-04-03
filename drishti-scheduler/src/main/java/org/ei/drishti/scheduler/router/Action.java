package org.ei.drishti.scheduler.router;

public interface Action {
    void invoke(MilestoneEvent event);
}
