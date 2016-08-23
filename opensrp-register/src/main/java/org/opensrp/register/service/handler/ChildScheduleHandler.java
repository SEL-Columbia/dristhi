package org.opensrp.register.service.handler;

import org.joda.time.LocalDate;
import org.json.JSONException;
import org.json.JSONObject;
import org.opensrp.domain.Event;
import org.opensrp.register.service.scheduling.ENCCSchedulesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ChildScheduleHandler extends BaseScheduleHandler {
	
	@Autowired
	private ENCCSchedulesService enccSchedulesService;
	
	@Override
	public void handle(Event event, JSONObject scheduleConfigEvent) {
		try {
			
			if (evaluateEvent(event, scheduleConfigEvent)) {
				String action = getAction(scheduleConfigEvent);
				String milestone=getMilestone(scheduleConfigEvent);
				if (action.equalsIgnoreCase(ActionType.enroll.toString())) {
					enccSchedulesService.enrollIntoCorrectMilestoneOfENCCCare(event.getBaseEntityId(), LocalDate.parse(getReferenceDateForSchedule(event, scheduleConfigEvent, action)), milestone, event.getId());
				}
			}
			
		}
		catch (JSONException e) {
			logger.error("", e);
		}
	}
}
