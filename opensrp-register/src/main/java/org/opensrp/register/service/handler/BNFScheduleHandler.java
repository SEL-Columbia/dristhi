package org.opensrp.register.service.handler;

import org.joda.time.LocalDate;
import org.json.JSONException;
import org.json.JSONObject;
import org.opensrp.domain.Event;
import org.opensrp.register.service.scheduling.BNFSchedulesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BNFScheduleHandler extends BaseScheduleHandler {
	
	@Autowired
	private BNFSchedulesService bnfSchedulesService;
	
	@Override
	public void handle(Event event, JSONObject scheduleConfigEvent) {
		try {
			
			if (evaluateEvent(event, scheduleConfigEvent)) {
				String action = getAction(scheduleConfigEvent);
				if (action.equalsIgnoreCase(ActionType.enroll.toString())) {
					bnfSchedulesService.enrollBNF(event.getBaseEntityId(), LocalDate.parse(getReferenceDateForSchedule(event, scheduleConfigEvent, action)), event.getProviderId(), event.getEventId(), "2016-01-01");
				}
			}
			
		}
		catch (JSONException e) {
			logger.error("", e);
		}
	}
}
