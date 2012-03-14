package org.ei.drishti.scheduler.router;

import org.motechproject.model.MotechEvent;
import org.motechproject.scheduletracking.api.domain.MilestoneAlert;
import org.motechproject.scheduletracking.api.events.constants.EventSubjects;
import org.motechproject.server.event.annotations.MotechListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static org.motechproject.scheduletracking.api.events.constants.EventDataKeys.*;

@Component
public class AlertRouter {
    private List<Route> routes;

    public AlertRouter() {
        routes = new ArrayList<Route>();
    }

    public void addRoute(Matcher scheduleMatcher, Matcher milestoneMatcher, Matcher windowMatcher, Action action) {
        routes.add(new Route(scheduleMatcher, milestoneMatcher, windowMatcher, action));
    }

    @MotechListener(subjects = {EventSubjects.MILESTONE_ALERT})
    public void handle(MotechEvent event) {
        String scheduleName = (String) event.getParameters().get(SCHEDULE_NAME);
        String windowName = (String) event.getParameters().get(WINDOW_NAME);
        MilestoneAlert alert = (MilestoneAlert) event.getParameters().get(MILESTONE_NAME);

        for (Route route : routes) {
            if (route.isSatisfiedBy(scheduleName, alert.getMilestoneName(), windowName)) {
                route.invokeAction(event);
                return;
            }
        }

        throw new NoRoutesMatchException();
    }

    private class Route {
        private final Matcher scheduleMatcher;
        private final Matcher milestoneMatcher;
        private final Matcher windowMatcher;
        private final Action action;

        public Route(Matcher scheduleMatcher, Matcher milestoneMatcher, Matcher windowMatcher, Action action) {
            this.scheduleMatcher = scheduleMatcher;
            this.milestoneMatcher = milestoneMatcher;
            this.windowMatcher = windowMatcher;
            this.action = action;
        }

        public boolean isSatisfiedBy(String scheduleName, String milestoneName, String windowName) {
            return scheduleMatcher.matches(scheduleName) && milestoneMatcher.matches(milestoneName) && windowMatcher.matches(windowName);
        }

        public void invokeAction(MotechEvent event) {
            action.invoke(event);
        }
    }
}
