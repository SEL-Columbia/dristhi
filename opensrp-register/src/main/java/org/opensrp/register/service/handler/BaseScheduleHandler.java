package org.opensrp.register.service.handler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectWriter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.opensrp.domain.Event;
import org.opensrp.service.formSubmission.handler.EventsHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
abstract class BaseScheduleHandler implements EventsHandler {
	
	private static final String JSON_KEY_CONCEPT = "concept";
	
	private static final String JSON_KEY_OBS = "obs";
	
	private static final String JSON_KEY_FIELD = "field";
	
	private static final String JSON_KEY_VALUE = "value";
	
	private static final String JSON_KEY_MILESTONE = "milestone";
	
	private static final String JSON_KEY_ACTION = "action";
	
	private static final String JSON_KEY_REFDATEFIELDS = "reference_date_fields";
	
	private static final String JSON_KEY_FULFILLMENTDATEFIELDS = "fulfillment_date_fields";
	
	private static final String JSON_KEY_ENROLLMENTFIELDS = "enrollment_fields";
	
	private static final String JSON_KEY_EVENT_CONCEPT = "fieldCode";
	
	private static final String JSON_KEY_NOTEMPTY = "NOT_EMPTY";
	
	protected static Logger logger = LoggerFactory.getLogger(BaseScheduleHandler.class.toString());
	
	protected List<Map<String, String>> getFields(JSONObject scheduleConfigEvent, String key) {
		return null;
	}
	
	protected String getMilestone(JSONObject scheduleConfigEvent) {
		return null;
	}
	
	protected String getAction(JSONObject scheduleConfigEvent) {
		return null;
	}
	
	protected Map<String, String> getReferenceDateFields(JSONObject scheduleConfigEvent) {
		return null;
	}
	
	protected Map<String, String> getFulfillmentDateFields(JSONObject scheduleConfigEvent) {
		return null;
	}
	
	protected String getPassLogic(JSONObject scheduleConfigEvent) {
		return null;
	}
	
	public enum ActionType {
		enroll, unenroll, fulfill
	}
	
	/**
	 * check if event qualifies the client into a schedule
	 * 
	 * @param event
	 * @param scheduleConfigEvent
	 * @return
	 */
	protected boolean evaluateEvent(Event event, JSONObject scheduleConfigEvent) {
		String passLogic = getPassLogic(scheduleConfigEvent);
		String action = getAction(scheduleConfigEvent);
		List<Map<String, String>> fieldsList = new ArrayList<Map<String, String>>();
		if (action.equalsIgnoreCase(ActionType.enroll.toString())) {
			fieldsList = getFields(scheduleConfigEvent, JSON_KEY_ENROLLMENTFIELDS);
		} else if (action.equalsIgnoreCase(ActionType.fulfill.toString())) {
			fieldsList = getFields(scheduleConfigEvent, JSON_KEY_FULFILLMENTDATEFIELDS);
		}
		
		Map<String, String> obs = getEventObs(eventToJson(event));
		boolean result = false;
		for (Map<String, String> enrollmentFields : fieldsList) {
			for (Map.Entry<String, String> entry : enrollmentFields.entrySet()) {
				String key = entry.getKey();
				String value = entry.getValue();
				if (key.equalsIgnoreCase(JSON_KEY_CONCEPT)) {
					//it's a concept search it in the event's obs
					if (obs.containsKey(key)) {
						if (obs.get(key).equalsIgnoreCase(value)
						        || (!obs.get(key).isEmpty() && value.equalsIgnoreCase(JSON_KEY_NOTEMPTY))) {
							result = true;
							//passlogic and means that all the fields must have the specified values in the schedule configs else just return when the first value is true
							if (!passLogic.equalsIgnoreCase("AND"))
								return result;
						}
					}
				}
				
			}
		}
		return result;
	}
	
	private Map<String, String> getEventObs(JSONObject event) {
		Map<String, String> obs = new HashMap<String, String>();
		try {
			if (event.has(JSON_KEY_OBS)) {
				JSONArray obsArray = event.getJSONArray(JSON_KEY_OBS);
				if (obsArray != null && obsArray.length() > 0) {
					for (int i = 0; i < obsArray.length(); i++) {
						JSONObject object = obsArray.getJSONObject(i);
						String key = object.has(JSON_KEY_EVENT_CONCEPT) ? object.getString(JSON_KEY_EVENT_CONCEPT) : null;
						String value = object.has(JSON_KEY_VALUE) ? object.getString(JSON_KEY_VALUE) : null;
						if (key != null && value != null) {
							obs.put(key, value);
						}
					}
				}
			}
		}
		catch (Exception e) {
			logger.error("", e);
		}
		return obs;
	}
	
	private JSONObject eventToJson(Event event) {
		ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
		String json;
		try {
			json = ow.writeValueAsString(event);
			JSONObject eventJson = new JSONObject(json);
			return eventJson;
		}
		catch (IOException e) {
			logger.error("", e);
			return null;
		}
		catch (JSONException e) {
			logger.error("", e);
			return null;
		}
		
	}
	
}
