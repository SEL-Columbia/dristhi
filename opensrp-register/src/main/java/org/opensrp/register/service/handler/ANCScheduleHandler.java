package org.opensrp.register.service.handler;

import java.util.List;
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectWriter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.opensrp.domain.Event;
import org.opensrp.domain.Obs;
import org.opensrp.register.service.scheduling.AnteNatalCareSchedulesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ANCScheduleHandler extends BaseScheduleHandler {
	
	@Autowired
	private AnteNatalCareSchedulesService ancScheduleService;
	
	@Override
	public void handle(Event event, JSONObject scheduleConfigEvent) {
		try {
			
			if (evaluateEvent(event, scheduleConfigEvent)) {
				String milestone = getMilestone(scheduleConfigEvent);
				String action = getAction(scheduleConfigEvent);
				if (action.equalsIgnoreCase(ActionType.enroll.toString())) {
//					ancScheduleService.enrollMother(event.getBaseEntityId(), referenceDateForSchedule, event.getProviderId(),
//					    startDate);
				}
			}
			
		}
		catch (JSONException e) {
			logger.error("", e);
		}
	}
}
