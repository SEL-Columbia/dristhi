package org.opensrp.register.service.handler;

import org.json.JSONException;
import org.json.JSONObject;
import org.opensrp.domain.Event;
import org.opensrp.register.service.scheduling.ELCOScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ElcoScheduleHandler extends BaseScheduleHandler {
	
	@Autowired
	private ELCOScheduleService elcoScheduleService;
	
	public static final String ELCO_SCHEDULE_PSRF = "ELCO PSRF";
	
	public static final String MIS_ELCO = "mis_elco";
	
	@Override
	public void handle(Event event, JSONObject scheduleConfigEvent) {
		try {
			
			if (evaluateEvent(event, scheduleConfigEvent)) {
				//	String milestone = getMilestone(scheduleConfigEvent);
				String action = getAction(scheduleConfigEvent);
				if (action.equalsIgnoreCase(ActionType.enroll.toString())) {
					elcoScheduleService.imediateEnrollIntoMilestoneOfPSRF(event.getBaseEntityId(),
					    getReferenceDateForSchedule(event, scheduleConfigEvent, action), event.getProviderId(),
					    ELCO_SCHEDULE_PSRF, event.getId());
				}
			}
			
		}
		catch (JSONException e) {
			logger.error("", e);
		}
	}
}
