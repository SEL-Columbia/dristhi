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
import org.opensrp.register.service.scheduling.ANCSchedulesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ANCScheduleHandler extends BaseScheduleHandler {
	
	@Autowired
	private ANCSchedulesService ancScheduleService;
	
	@Override
	public void handle(Event event, JSONArray scheduleConfigEvents) {
		try {
			
			for (int i = 0; i < scheduleConfigEvents.length(); i++) {
				
				JSONObject scheduleConfigEvent = scheduleConfigEvents.getJSONObject(i);
				String action=getAction(scheduleConfigEvent);
//				List<Map<String, String>> enrollmentFieldsList = getEnrollmentFields(scheduleConfigEvent);
//				for (Map<String, String> enrollmentFields : enrollmentFieldsList) {
//					
//					
//				}
				
			}
			//ancScheduleService.enrollMother(event.getBaseEntityId(), referenceDateForSchedule, event.getProviderId(),
			//    startDate);
			
		}
		catch (JSONException e) {
			logger.error("", e);
		}
	}
}
