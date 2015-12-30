package org.opensrp.scheduler;

import java.util.ArrayList;
import java.util.List;

import org.motechproject.scheduler.domain.MotechEvent;
import org.motechproject.scheduletracking.api.events.constants.EventSubjects;
import org.motechproject.scheduletracking.api.service.ScheduleTrackingService;
import org.motechproject.server.event.annotations.MotechListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * The class that maintains the actions against alerts by {@link ScheduleTrackingService}
 */
@Component
public class AlertRouter {
    private List<Route> routes;
    private static Logger logger = LoggerFactory.getLogger(AlertRouter.class.toString());


    public AlertRouter() {
        routes = new ArrayList<>();
    }

    public Route addRoute(Matcher scheduleMatcher, Matcher milestoneMatcher, Matcher windowMatcher, HookedEvent action) {
        Route route = new Route(scheduleMatcher, milestoneMatcher, windowMatcher, action);
        routes.add(route);
        return route;
    }

    public Route addRoute(Route route) {
    	logger.info("ADDED ROUTE:"+route);
        routes.add(route);
        return route;
    }
    
    @MotechListener(subjects = {EventSubjects.MILESTONE_ALERT, EventSubjects.DEFAULTMENT_CAPTURE})
    public void handleAlerts(MotechEvent realEvent) {
        logger.debug("Handling motech milestone alerts: " + realEvent);
        MilestoneEvent event = new MilestoneEvent(realEvent);

        for (Route route : routes) {
            if (route.isSatisfiedBy(event.scheduleName(), event.milestoneName(), event.windowName())) {
                route.invokeAction(event);
                return;
            }
        }

        throw new NoRoutesMatchException(event);
    }
}
