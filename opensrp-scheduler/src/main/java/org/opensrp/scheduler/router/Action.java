package org.opensrp.scheduler.router;

import java.util.Map;

public interface Action {
    void invoke(MilestoneEvent event, Map<String, String> extraData);
}
