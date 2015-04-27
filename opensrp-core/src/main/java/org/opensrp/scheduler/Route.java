package org.opensrp.scheduler;

import java.util.HashMap;
import java.util.Map;

public class Route {
    private final Matcher scheduleMatcher;
    private final Matcher milestoneMatcher;
    private final Matcher windowMatcher;
    private final HookedEvent action;
    private Map<String, String> extraData;

    public Route(Matcher scheduleMatcher, Matcher milestoneMatcher, Matcher windowMatcher, HookedEvent action) {
        this(scheduleMatcher, milestoneMatcher, windowMatcher, action, new HashMap<String, String>());
    }

    public Route(Matcher scheduleMatcher, Matcher milestoneMatcher, Matcher windowMatcher, HookedEvent action, Map<String, String> extraData) {
        this.scheduleMatcher = scheduleMatcher;
        this.milestoneMatcher = milestoneMatcher;
        this.windowMatcher = windowMatcher;
        this.action = action;
        this.extraData = extraData;
        if(extraData == null){
        	this.extraData = new HashMap<String, String>();
        }
    }
    
    public boolean isSatisfiedBy(String scheduleName, String milestoneName, String windowName) {
        return scheduleMatcher.matches(scheduleName) && milestoneMatcher.matches(milestoneName) && windowMatcher.matches(windowName);
    }

    public void invokeAction(MilestoneEvent event) {
        action.invoke(event, extraData);
    }

    public Route addExtraData(String key, String value) {
        this.extraData.put(key, value);
        return this;
    }
}
