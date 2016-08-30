package org.opensrp.register.service.handler;

import org.joda.time.LocalDate;
import org.json.JSONException;
import org.json.JSONObject;
import org.opensrp.domain.Event;
import org.opensrp.register.service.scheduling.PNCSchedulesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PNCScheduleHandler extends BaseScheduleHandler {
	
	@Autowired
	private PNCSchedulesService pncSchedulesService;
	private static final String scheduleName="Post Natal Care Reminder Visit";
	@Override
	public void handle(Event event, JSONObject scheduleConfigEvent) {
		try {
			
			if (evaluateEvent(event, scheduleConfigEvent)) {
				String action = getAction(scheduleConfigEvent);
				String milestone=getMilestone(scheduleConfigEvent);
				if (action.equalsIgnoreCase(ActionType.enroll.toString())) {
					pncSchedulesService.enrollPNCRVForMother(event.getBaseEntityId(),scheduleName, LocalDate.parse(getReferenceDateForSchedule(event, scheduleConfigEvent, action)), milestone,event.getId());
				}else if (action.equalsIgnoreCase(ActionType.fulfill.toString())) {
					pncSchedulesService.fullfillMilestone(event.getBaseEntityId(), event.getProviderId(), scheduleName, LocalDate.parse(getReferenceDateForSchedule(event, scheduleConfigEvent, action)), event.getId());
				}
			}
			
		}
		catch (JSONException e) {
			logger.error("", e);
		}
	}
}
