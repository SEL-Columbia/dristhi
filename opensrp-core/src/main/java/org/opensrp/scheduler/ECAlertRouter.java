package org.opensrp.scheduler;

import static org.opensrp.dto.AlertStatus.expired;

import java.util.ArrayList;
import java.util.List;

import org.motechproject.scheduler.domain.MotechEvent;
import org.motechproject.scheduletracking.api.domain.Enrollment;
import org.motechproject.scheduletracking.api.domain.WindowName;
import org.motechproject.scheduletracking.api.events.DefaultmentCaptureEvent;
import org.motechproject.scheduletracking.api.events.constants.EventSubjects;
import org.motechproject.scheduletracking.api.repository.AllEnrollments;
import org.motechproject.scheduletracking.api.service.ScheduleTrackingService;
import org.motechproject.server.event.annotations.MotechListener;
import org.opensrp.domain.Event;
import org.opensrp.dto.ActionData;
import org.opensrp.dto.AlertStatus;
import org.opensrp.scheduler.HealthSchedulerService.MetadataField;
import org.opensrp.scheduler.service.ActionService;
import org.opensrp.scheduler.service.ScheduleService;
import org.opensrp.service.EventService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * The class that maintains the actions against alerts by {@link ScheduleTrackingService}
 */
@Component
public class ECAlertRouter {
	
	private List<Route> routes;
	
	private static Logger logger = LoggerFactory.getLogger(ECAlertRouter.class.toString());
	
	@Autowired
	ScheduleService scheduleService;

	
	@Autowired
	ActionService actionService;
	
	@Autowired
	EventService eventService;
	
	public ECAlertRouter() {
		routes = new ArrayList<>();
	}
	
	public Route addRoute(Matcher scheduleMatcher, Matcher milestoneMatcher, Matcher windowMatcher, HookedEvent action) {
		Route route = new Route(scheduleMatcher, milestoneMatcher, windowMatcher, action);
		routes.add(route);
		return route;
	}
	
	public Route addRoute(Route route) {
		logger.info("ADDED ROUTE:" + route);
		routes.add(route);
		return route;
	}
	
	@MotechListener(subjects = { EventSubjects.MILESTONE_ALERT, EventSubjects.DEFAULTMENT_CAPTURE })
	public void handleAlerts(MotechEvent realEvent) {
		logger.debug("Handling motech milestone alerts: " + realEvent);
		MilestoneEvent milestoneEvent = new MilestoneEvent(realEvent);
		
		try {
//			if (realEvent instanceof DefaultmentCaptureEvent) {
//				
//			} else {
				for (Route route : routes) {
					if (route.isSatisfiedBy(milestoneEvent.scheduleName(), milestoneEvent.milestoneName(),
					    milestoneEvent.windowName())) {
						route.invokeAction(milestoneEvent);
						return;
					}
				}
			//}
		}
		catch (NoRoutesMatchException e) {
			logger.error("", e.getMessage());
		}
		catch (Exception e) {
			String externalId = milestoneEvent.externalId();
			DefaultmentCaptureEvent defaultmentEvent = new DefaultmentCaptureEvent(realEvent);
			String enrollmentId = defaultmentEvent.getEnrollmentId();//9be0cca1be4969d7d104e14706ef81de
			Enrollment enrollment = scheduleService.getEnrollment(enrollmentId);
			
			String eventId = enrollment.getMetadata().get(MetadataField.enrollmentEvent.name());
			Event event = eventService.getById(eventId);
			String entityType = event.getEntityType();
			ActionData actionData = ActionData.createAlert(entityType, enrollment.getScheduleName(),
			    enrollment.getCurrentMilestoneName(), AlertStatus.defaulted, enrollment.getCurrentMilestoneStartDate(),
			    enrollment.getStartOfWindowForCurrentMilestone(WindowName.max));
			Action action = new Action(externalId, event.getProviderId(), actionData);
			actionService.alertForBeneficiary(action);
			
			logger.error("", e.getMessage());
		}
		
	}
}
