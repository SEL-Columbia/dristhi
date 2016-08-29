package org.opensrp.register.service.handler;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
	
	private static final String JSON_KEY_EVENT = "event";
	
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
	
	protected static DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	
	/**
	 * Converts values in a json key into key-value pair
	 * 
	 * @param scheduleConfigEvent
	 * @param key
	 * @return
	 */
	protected List<Map<String, Object>> getFields(JSONObject scheduleConfigEvent, String key) {
		Map<String, Object> fieldsMap = new HashMap<String, Object>();
		List<Map<String, Object>> fieldsList = new ArrayList<Map<String, Object>>();
		try {
			if (scheduleConfigEvent.has(key)) {
				JSONArray fieldsArray = scheduleConfigEvent.getJSONArray(key);
				if (fieldsArray != null && fieldsArray.length() > 0) {
					for (int i = 0; i < fieldsArray.length(); i++) {
						JSONObject jsonObject = fieldsArray.getJSONObject(i);
						fieldsMap = jsonObjectToMap(jsonObject);
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
	
	protected Map<String, Object> getReferenceDateFields(JSONObject scheduleConfigEvent) throws JSONException {
		JSONArray jsonArray = scheduleConfigEvent.has(JSON_KEY_REFDATEFIELDS)
		        ? scheduleConfigEvent.getJSONArray(JSON_KEY_REFDATEFIELDS) : null;
		
		return jsonObjectToMap(jsonArray.getJSONObject(0));
	}
	
	/**
	 * Get schedule reference date from the reference_date_fields key
	 * 
	 * @param event
	 * @param scheduleConfigEvent
	 * @param action
	 * @return
	 * @throws JSONException
	 */
	protected String getReferenceDateForSchedule(Event event, JSONObject scheduleConfigEvent, String action)
	    throws JSONException {
		Map<String, Object> refDateFields = new HashMap<String, Object>();
		if (action.equalsIgnoreCase(ActionType.enroll.toString())) {
			refDateFields = getReferenceDateFields(scheduleConfigEvent);
		} else if (action.equalsIgnoreCase(ActionType.fulfill.toString())) {
			refDateFields = getFulfillmentDateFields(scheduleConfigEvent);
		}
		JSONObject eventJson = eventToJson(event);
		Map<String, Object> obs = getEventObs(eventJson);
		String dateStr = "";
		
		for (Map.Entry<String, Object> entry : refDateFields.entrySet()) {
			String key = entry.getKey();//"concept"
			String value = entry.getValue().toString();//"concept value or fieldname"
			if (key.equalsIgnoreCase(JSON_KEY_CONCEPT) && !refDateFields.containsKey(JSON_KEY_EVENT)) {
				//date is a concept and in the current event being processed search it in the event's obs
				if (obs.containsKey(value) && !obs.get(value).toString().isEmpty()) {
					dateStr = getDateValue(obs.get(value));
				}
			} else if (key.equalsIgnoreCase(JSON_KEY_CONCEPT) && refDateFields.containsKey(JSON_KEY_EVENT)) {
				//date is a concept and not in the current event being processed search it in the other event's obs
				//TODO fetch latest event of the type specified here from the db
				if (obs.containsKey(value) && !obs.get(value).toString().isEmpty()) {
					dateStr = getDateValue(obs.get(value));
				}
			} else if (key.equalsIgnoreCase(JSON_KEY_FIELD)) {
				//date is a not a concept but indeed a field in the current event being processed search it in the event's doc
				if (eventJson.has(value) && !eventJson.getString(value).isEmpty()) {
					dateStr = getDateValue(eventJson.get(value));
				}
			}
			
		}
		return dateStr;
	}
	
	/**
	 * sometimes date is in long value convert to the right format
	 * 
	 * @param value
	 * @return
	 */
	private String getDateValue(Object value) {
		String dateStr = "";
		
		if (value instanceof Long) {//sometimes date is in long for some reason
			Date date = new Date(Long.valueOf(value.toString()));
			dateStr = dateFormat.format(date);
		} else if (value.toString().contains("T")) {//sometimes the ref date is the format 2016-08-20T17:45:00.000+03:00
			int substrIndex=value.toString().indexOf("T");
			dateStr=value.toString().substring(0,substrIndex);
		} else {
			dateStr = value.toString();
			
		}
		return dateStr;
	}
	
	protected Map<String, Object> getFulfillmentDateFields(JSONObject scheduleConfigEvent) throws JSONException {
		
		JSONArray jsonArray = scheduleConfigEvent.has(JSON_KEY_FULFILLMENTDATEFIELDS)
		        ? scheduleConfigEvent.getJSONArray(JSON_KEY_FULFILLMENTDATEFIELDS) : null;
		
		return jsonObjectToMap(jsonArray.getJSONObject(0));
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
		List<Map<String, Object>> fieldsList = new ArrayList<Map<String, Object>>();
		if (action.equalsIgnoreCase(ActionType.enroll.toString())) {
			fieldsList = getFields(scheduleConfigEvent, JSON_KEY_ENROLLMENTFIELDS);
		} else if (action.equalsIgnoreCase(ActionType.fulfill.toString())) {
			fieldsList = getFields(scheduleConfigEvent, JSON_KEY_FULFILLMENTDATEFIELDS);
		}
		
		JSONObject eventJson = eventToJson(event);
		Map<String, Object> obs = getEventObs(eventJson);
		boolean result = false;
		for (Map<String, Object> scheduleFields : fieldsList) {
			for (Map.Entry<String, Object> entry : scheduleFields.entrySet()) {
				String key = entry.getKey();//"concept"
				String value = entry.getValue().toString();//"concept value"
				String scheduleValue = scheduleFields.get(JSON_KEY_VALUE).toString();//"value- either not_empty or a concept mapping"
				if (key.equalsIgnoreCase(JSON_KEY_CONCEPT)) {
					//it's a concept search it in the event's obs
					//key="fieldCode";
					if (obs.containsKey(value)) {//check if the concept mapping exists in the obs
						if (obs.get(value).toString().equalsIgnoreCase(scheduleValue)
						        || (!obs.get(value).toString().isEmpty()
						                && scheduleValue.equalsIgnoreCase(JSON_KEY_NOTEMPTY))) {
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
	private Map<String, Object> getEventObs(JSONObject event) {
		Map<String, Object> obs = new HashMap<String, Object>();
		try {
			if (event.has(JSON_KEY_OBS)) {
				JSONArray obsArray = event.getJSONArray(JSON_KEY_OBS);
				if (obsArray != null && obsArray.length() > 0) {
					for (int i = 0; i < obsArray.length(); i++) {
						JSONObject object = obsArray.getJSONObject(i);
						String key = object.has(JSON_KEY_EVENT_CONCEPT) ? object.getString(JSON_KEY_EVENT_CONCEPT) : null;
						String value = object.has(JSON_KEY_VALUE) ? object.getString(JSON_KEY_VALUE) : "";
						// : object.has("values") ? object.get("values").toString() : null;
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
	private Map<String, Object> jsonObjectToMap(JSONObject jsonObject) throws JSONException {
		
		if (jsonObject == null) {
			return null;
		}
		
		Map<String, Object> fieldsMap = new HashMap<String, Object>();
		
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
