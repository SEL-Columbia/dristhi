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
			String action = getAction(scheduleConfigEvent);
			if (evaluateEvent(event, scheduleConfigEvent)) {
				String milestone = getMilestone(scheduleConfigEvent);

				//String action = getAction(scheduleConfigEvent);
				if (action.equalsIgnoreCase(ActionType.enroll.toString())) {
					if(milestone==null  || milestone.isEmpty()){
						scheduler.enrollIntoSchedule(event.getBaseEntityId(), scheduleName, getReferenceDateForSchedule(event, scheduleConfigEvent, action), event.getId());
					}else{
						scheduler.enrollIntoSchedule(event.getBaseEntityId(), scheduleName, milestone,  getReferenceDateForSchedule(event, scheduleConfigEvent, action),  event.getId());

					}
					logger.info("Enrolled "+ event.getBaseEntityId() +"to schedule "+scheduleName);
					
					
				} else if (action.equalsIgnoreCase(ActionType.fulfill.toString())) {
						
						scheduler.fullfillMilestoneAndCloseAlert(event.getBaseEntityId(), event.getProviderId(), scheduleName.toUpperCase(), LocalDate.parse(getReferenceDateForSchedule(event, scheduleConfigEvent, action)),  event.getId());
						logger.info("Fulfilled schedule"+ scheduleName  +"for schedule "+event.getBaseEntityId());

				}
			}
			
		}
		catch (JSONException e) {
			logger.error("", e);
		}
}
	/**
	 * OPV 0 was not given and OPV 3 was given at least 4 weeks ago
	 * @param baseEntityId
	 * @return
	 */
	private boolean evaluateOpv4(String baseEntityId){
		return false;
	}
	/**
	 * MR or Measles can be given, but not both
	 * @param baseEntityId
	 * @return
	 */
	private boolean evaluateMeasles(String baseEntityId){
		return false;
	}

}
