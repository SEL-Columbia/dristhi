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
	
	private static final String JSON_KEY_PASSLOGIC = "pass_logic";
	
	protected static Logger logger = LoggerFactory.getLogger(BaseScheduleHandler.class.toString());
	
	/**
	 * Converts values in a json key into key-value pair
	 * 
	 * @param scheduleConfigEvent
	 * @param key
	 * @return
	 */
	protected List<Map<String, String>> getFields(JSONObject scheduleConfigEvent, String key) {
		Map<String, String> fieldsMap = new HashMap<String, String>();
		List<Map<String, String>> fieldsList = new ArrayList<Map<String, String>>();
		try {
			if (scheduleConfigEvent.has(key)) {
				JSONArray fieldsArray = scheduleConfigEvent.getJSONArray(key);
				if (fieldsArray != null && fieldsArray.length() > 0) {
					for (int i = 0; i < fieldsArray.length(); i++) {
						JSONObject jsonObject = fieldsArray.getJSONObject(i);
						fieldsMap=jsonObjectToMap(jsonObject);
						fieldsList.add(fieldsMap);
						
					}
				}
			}
		}
		catch (Exception e) {
			logger.error("", e);
		}
		return fieldsList;
	}
	
	protected String getMilestone(JSONObject scheduleConfigEvent) throws JSONException {
		String milestone = scheduleConfigEvent.has(JSON_KEY_MILESTONE) ? scheduleConfigEvent.getString(JSON_KEY_MILESTONE)
		        : "";
		return milestone;
	}
	
	protected String getAction(JSONObject scheduleConfigEvent) throws JSONException {
		String action = scheduleConfigEvent.has(JSON_KEY_ACTION) ? scheduleConfigEvent.getString(JSON_KEY_ACTION) : "";
		return action;
	}
	
	protected Map<String, String> getReferenceDateFields(JSONObject scheduleConfigEvent) throws JSONException {
		JSONObject jsonObject = scheduleConfigEvent.has(JSON_KEY_REFDATEFIELDS)
		        ? scheduleConfigEvent.getJSONObject(JSON_KEY_REFDATEFIELDS) : null;
		
		return jsonObjectToMap(jsonObject);
	}
	protected String getReferenceDateForSchedule(JSONObject scheduleConfigEvent) throws JSONException {
		Map<String, String> refDateFields=getReferenceDateFields(scheduleConfigEvent);
		for (Map.Entry<String, String> entry : refDateFields.entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue();
			
		}
		return null;
	}
	
	protected Map<String, String> getFulfillmentDateFields(JSONObject scheduleConfigEvent) throws JSONException {
		JSONObject jsonObject = scheduleConfigEvent.has(JSON_KEY_FULFILLMENTDATEFIELDS)
		        ? scheduleConfigEvent.getJSONObject(JSON_KEY_FULFILLMENTDATEFIELDS) : null;
		
		return jsonObjectToMap(jsonObject);
	}
	
	protected String getPassLogic(JSONObject scheduleConfigEvent) throws JSONException {
		String passLogic = scheduleConfigEvent.has(JSON_KEY_PASSLOGIC) ? scheduleConfigEvent.getString(JSON_KEY_PASSLOGIC)
		        : "";
		return passLogic;
	}
	
	
	protected enum ActionType {
		enroll, unenroll, fulfill
	}
	
	/**
	 * check if event qualifies the client into a schedule by comparing the enrollment_fields and
	 * fulfillment_date_fields values with the values in the event (from the db)
	 * 
	 * @param event
	 * @param scheduleConfigEvent
	 * @return
	 * @throws JSONException
	 */
	protected boolean evaluateEvent(Event event, JSONObject scheduleConfigEvent) throws JSONException {
		String passLogic = getPassLogic(scheduleConfigEvent);
		String action = getAction(scheduleConfigEvent);
		List<Map<String, String>> fieldsList = new ArrayList<Map<String, String>>();
		if (action.equalsIgnoreCase(ActionType.enroll.toString())) {
			fieldsList = getFields(scheduleConfigEvent, JSON_KEY_ENROLLMENTFIELDS);
		} else if (action.equalsIgnoreCase(ActionType.fulfill.toString())) {
			fieldsList = getFields(scheduleConfigEvent, JSON_KEY_FULFILLMENTDATEFIELDS);
		}
		
		JSONObject eventJson = eventToJson(event);
		Map<String, String> obs = getEventObs(eventJson);
		boolean result = false;
		for (Map<String, String> scheduleFields : fieldsList) {
			for (Map.Entry<String, String> entry : scheduleFields.entrySet()) {
				String key = entry.getKey();//"concept"
				String value = entry.getValue();//"concept value"
				String scheduleValue=scheduleFields.get(JSON_KEY_VALUE);//"value- either not_empty or a concept mapping"
				if (key.equalsIgnoreCase(JSON_KEY_CONCEPT)) {
					//it's a concept search it in the event's obs
					//key="fieldCode";
					if (obs.containsKey(value)) {//check if the concept mapping exists in the obs
						if (obs.get(value).equalsIgnoreCase(scheduleValue)
						        || (!obs.get(value).isEmpty() && scheduleValue.equalsIgnoreCase(JSON_KEY_NOTEMPTY))) {
							result = true;
							//passlogic AND means that all the fields must have the specified values in the schedule configs else just return when the first value is true
							if (!passLogic.equalsIgnoreCase("AND"))
								return result;
						}
					}
				} else if (key.equalsIgnoreCase(JSON_KEY_FIELD)) { //not a concept so get the value from the main doc e.g eventDate
					String fieldName = eventJson.has(value) ? eventJson.getString(value) : "";
					String fieldValue = eventJson.has(fieldName) ? eventJson.getString(fieldName) : "";
					if (fieldValue.equalsIgnoreCase(scheduleValue)
					        || (!fieldValue.isEmpty() && scheduleValue.equalsIgnoreCase(JSON_KEY_NOTEMPTY))) {
						result = true;
						//passlogic AND means that all the fields must have the specified values in the schedule configs else just return when the first value is true
						if (!passLogic.equalsIgnoreCase("AND"))
							return result;
					}
					
				}
				
			}
		}
		return result;
	}
	
	/**
	 * Put all obs into a key(concept), value (concept value) pair for easier searching
	 * 
	 * @param event
	 * @return
	 */
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
	
	/**
	 * Convert event pojo to a jsonobject
	 * 
	 * @param event
	 * @return
	 */
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
	
	/**
	 * Convert a jsonobject into a map -key/value
	 * 
	 * @param jsonObject
	 * @return
	 * @throws JSONException
	 */
	private Map<String, String> jsonObjectToMap(JSONObject jsonObject) throws JSONException {
		
		if (jsonObject == null) {
			return null;
		}
		
		Map<String, String> fieldsMap = new HashMap<String, String>();
		
		for (int j = 0; j < jsonObject.names().length(); j++) {
			String jsonObjectKey = jsonObject.names().getString(j);
			String jsonObjectValue = jsonObject.get(jsonObject.names().getString(j)).toString();
			if (jsonObjectKey != null && jsonObjectValue != null) {
				fieldsMap.put(jsonObjectKey, jsonObjectValue);
			}
		}
		return fieldsMap;
	}
	
}
