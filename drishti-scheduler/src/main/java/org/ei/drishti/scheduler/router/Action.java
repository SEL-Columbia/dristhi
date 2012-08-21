package org.ei.drishti.scheduler.router;

import java.util.Map;

public interface Action {
    void invoke(MilestoneEvent event, Map<String, String> extraData);
}
