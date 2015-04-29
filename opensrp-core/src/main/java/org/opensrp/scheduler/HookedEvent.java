package org.opensrp.scheduler;

import java.util.Map;

public interface HookedEvent {
    void invoke(MilestoneEvent event, Map<String, String> extraData);
}
