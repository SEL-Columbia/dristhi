package org.ei.drishti.scheduler.router;

import org.motechproject.scheduler.domain.MotechEvent;
import org.motechproject.scheduletracking.api.events.constants.EventSubjects;
import org.motechproject.server.event.annotations.MotechListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class AlertRouter {
    private List<Route> routes;

    public AlertRouter() {
        routes = new ArrayList<>();
    }

    public Route addRoute(Matcher scheduleMatcher, Matcher milestoneMatcher, Matcher windowMatcher, Action action) {
        Route route = new Route(scheduleMatcher, milestoneMatcher, windowMatcher, action);
        routes.add(route);
        return route;
    }

    @MotechListener(subjects = {EventSubjects.MILESTONE_ALERT})
    public void handle(MotechEvent realEvent) {
        MilestoneEvent event = new MilestoneEvent(realEvent);

        for (Route route : routes) {
            if (route.isSatisfiedBy(event.scheduleName(), event.milestoneName(), event.windowName())) {
                route.invokeAction(event);
                return;
            }
        }

        throw new NoRoutesMatchException();
    }
}
