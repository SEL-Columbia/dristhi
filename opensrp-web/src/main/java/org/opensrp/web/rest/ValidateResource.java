package org.opensrp.web.rest;

import static java.text.MessageFormat.format;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.json.JSONObject;
import org.opensrp.domain.Client;
import org.opensrp.domain.Event;
import org.opensrp.service.ClientService;
import org.opensrp.service.EventService;
import org.opensrp.util.DateTimeTypeConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;

@Controller
@RequestMapping(value = "/rest/validate/")
public class ValidateResource {
	
	private static Logger logger = LoggerFactory.getLogger(ValidateResource.class.toString());
	
	private ClientService clientService;
	
	private EventService eventService;
	
	Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
	        .registerTypeAdapter(DateTime.class, new DateTimeTypeConverter()).create();
	
	@Autowired
	public ValidateResource(ClientService clientService, EventService eventService) {
		this.clientService = clientService;
		this.eventService = eventService;
	}
	
	/**
	 * Validate that the client and event ids reference actual documents
	 * 
	 * @param data
	 * @return
	 */
	@RequestMapping(headers = { "Accept=application/json" }, method = POST, value = "/sync")
	public ResponseEntity<String> validateSync(@RequestBody String data) {
		Map<String, Object> response = new HashMap<String, Object>();
		
		try {
			if (StringUtils.isBlank(data)) {
				return new ResponseEntity<>(BAD_REQUEST);
			}
			JSONObject syncData = new JSONObject(data);
			if (!syncData.has("clients") && !syncData.has("events")) {
				return new ResponseEntity<>(BAD_REQUEST);
			}
			
			List<String> missingClientIds = new ArrayList<>();
			if (syncData.has("clients")) {
				
				List<String> clientIds = gson.fromJson(syncData.getString("clients"),
				    new TypeToken<ArrayList<String>>() {}.getType());
				for (String clientId : clientIds) {
					try {
						Client client = clientService.getByBaseEntityId(clientId);
						if (client == null) {
							missingClientIds.add(clientId);
						}
					}
					catch (Exception e) {
						logger.error("Client Sync Valiation Failed, BaseEntityId: " + clientId, e);
					}
				}
			}
			
			List<String> missingEventIds = new ArrayList<>();
			if (syncData.has("events")) {
				List<String> eventIds = gson.fromJson(syncData.getString("events"),
				    new TypeToken<ArrayList<String>>() {}.getType());
				for (String eventId : eventIds) {
					try {
						List<Event> events = eventService.findByFormSubmissionId(eventId);
						if (events == null || events.isEmpty()) {
							missingEventIds.add(eventId);
						}
						
					}
					catch (Exception e) {
						logger.error("Event Sync Valiation Failed, FormSubmissionId: " + eventId, e);
					}
				}
			}
			
			JsonArray clientsArray = (JsonArray) gson.toJsonTree(missingClientIds,
			    new TypeToken<List<String>>() {}.getType());
			
			JsonArray eventsArray = (JsonArray) gson.toJsonTree(missingEventIds, new TypeToken<List<String>>() {}.getType());
			
			response.put("events", eventsArray);
			response.put("clients", clientsArray);
			
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.OK);
			
		}
		catch (Exception e) {
			logger.error(format("Validation Sync failed data processing failed with exception {0}.- ", e));
			response.put("msg", "Error occurred");
			return new ResponseEntity<>(new Gson().toJson(response), HttpStatus.INTERNAL_SERVER_ERROR);
			
		}
	}
}
