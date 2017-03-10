package org.opensrp.register.service.handler;

import org.joda.time.LocalDate;
import org.json.JSONException;
import org.json.JSONObject;
import org.opensrp.domain.Event;
import org.opensrp.scheduler.HealthSchedulerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class VaccinesScheduleHandler extends BaseScheduleHandler {
	
	@Autowired
	private HealthSchedulerService scheduler;

	@Override
	public void handle(Event event, JSONObject scheduleConfigEvent, String scheduleName) {
		try {
			
			if (evaluateEvent(event, scheduleConfigEvent)) {
				String milestone = getMilestone(scheduleConfigEvent);
				String action = getAction(scheduleConfigEvent);
				if (action.equalsIgnoreCase(ActionType.enroll.toString())) {
					if(milestone==null  || milestone.isEmpty()){
						scheduler.enrollIntoSchedule(event.getBaseEntityId(), scheduleName, getReferenceDateForSchedule(event, scheduleConfigEvent, action), event.getId());
					}else{
						scheduler.enrollIntoSchedule(event.getBaseEntityId(), scheduleName, milestone,  getReferenceDateForSchedule(event, scheduleConfigEvent, action),  event.getId());

					}
					
					
				} else if (action.equalsIgnoreCase(ActionType.fulfill.toString())) {
					if(milestone==null  || milestone.isEmpty()){
						scheduler.fullfillMilestoneAndCloseAlert(event.getBaseEntityId(), event.getProviderId(), scheduleName, LocalDate.parse(getReferenceDateForSchedule(event, scheduleConfigEvent, action)),  event.getId());

					}else{
						scheduler.fullfillMilestoneAndCloseAlert(event.getBaseEntityId(), event.getProviderId(), milestone, LocalDate.parse(getReferenceDateForSchedule(event, scheduleConfigEvent, action)), event.getId());

					}
				}
			}
			
		}
		catch (JSONException e) {
			logger.error("", e);
		}
}

}
