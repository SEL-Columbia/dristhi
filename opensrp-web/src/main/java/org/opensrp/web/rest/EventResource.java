package org.opensrp.web.rest;

import static org.opensrp.common.AllConstants.BaseEntity.BASE_ENTITY_ID;
import static org.opensrp.common.AllConstants.BaseEntity.LAST_UPDATE;
import static org.opensrp.common.AllConstants.Event.ENTITY_TYPE;
import static org.opensrp.common.AllConstants.Event.EVENT_DATE;
import static org.opensrp.common.AllConstants.Event.EVENT_TYPE;
import static org.opensrp.common.AllConstants.Event.LOCATION_ID;
import static org.opensrp.common.AllConstants.Event.PROVIDER_ID;
import static org.opensrp.web.rest.RestUtils.getDateRangeFilter;
import static org.opensrp.web.rest.RestUtils.getStringFilter;
import static org.opensrp.web.rest.RestUtils.getIntegerFilter;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.joda.time.DateTime;
import org.opensrp.common.AllConstants.BaseEntity;
import org.opensrp.domain.Client;
import org.opensrp.domain.Event;
import org.opensrp.service.ClientService;
import org.opensrp.service.EventService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.mysql.jdbc.StringUtils;

@Controller
@RequestMapping(value = "/rest/event")
public class EventResource extends RestResource<Event> {
	
	private static Logger logger = LoggerFactory.getLogger(EventResource.class.toString());
	
	private EventService eventService;
	
	private ClientService clientService;
	
	@Autowired
	public EventResource(ClientService clientService, EventService eventService) {
		this.clientService = clientService;
		this.eventService = eventService;
	}
	
	@Override
	public Event getByUniqueId(String uniqueId) {
		return eventService.find(uniqueId);
	}
	
	@RequestMapping(value = "/getall", method = RequestMethod.GET)
	@ResponseBody
	protected List<Event> getAll() {
		return eventService.getAll();
	}
	
	/**
	 * Fetch events ordered by serverVersion ascending order and return the clients associated with
	 * the events
	 * 
	 * @param request
	 * @return a map response with events, clients and optionally msg when an error occurs
	 */
	@RequestMapping(value = "/sync", method = RequestMethod.GET)
	@ResponseBody
	protected Map<String, Object> sync(HttpServletRequest request) {
		Map<String, Object> response = new HashMap<String, Object>();
		
		try {
			String providerId = getStringFilter(PROVIDER_ID, request);
			String locationId = getStringFilter(LOCATION_ID, request);
			Long lastSyncedServerVersion = Long.valueOf(getStringFilter(BaseEntity.SERVER_VERSIOIN, request)) + 1;
			String team = getStringFilter("team", request);
			Integer limit = getIntegerFilter("limit", request);
			if(limit == null || limit.intValue() == 0){
				limit = 25;
			}
			
			List<Event> events = new ArrayList<Event>();
			List<String> clientIds = new ArrayList<String>();
			List<Client> clients = new ArrayList<Client>();
			if (team != null || providerId != null || locationId != null) {
				events = eventService.findEvents(team, providerId, locationId, lastSyncedServerVersion,
				    BaseEntity.SERVER_VERSIOIN, "asc", limit);
				if (!events.isEmpty()) {
					for (Event event : events) {
						clientIds.add(event.getBaseEntityId());
					}
					clients = clientService.findByFieldValue(BaseEntity.BASE_ENTITY_ID, clientIds);
				}
			}
			response.put("events", events);
			response.put("clients", clients);
			response.put("no_of_events", events.size());
		}
		catch (Exception e) {
			response.put("msg", "Error occurred");
			logger.error("", e);
		}
		return response;
	}
	/*	@RequestMapping(method=RequestMethod.GET)
		@ResponseBody
		public Event getByBaseEntityAndFormSubmissionId(@RequestParam String baseEntityId, @RequestParam String formSubmissionId) {
			return eventService.getByBaseEntityAndFormSubmissionId(baseEntityId, formSubmissionId);
		}*/
	
	@Override
	public Event create(Event o) {
		return eventService.addEvent(o);
	}
	
	@Override
	public List<String> requiredProperties() {
		List<String> p = new ArrayList<>();
		p.add(BASE_ENTITY_ID);
		//p.add(FORM_SUBMISSION_ID);
		p.add(EVENT_TYPE);
		//p.add(LOCATION_ID);
		//p.add(EVENT_DATE);
		p.add(PROVIDER_ID);
		//p.add(ENTITY_TYPE);
		return p;
	}
	
	@Override
	public Event update(Event entity) {
		return eventService.mergeEvent(entity);
	}
	
	public static void main(String[] args) {
		
	}
	
	@Override
	public List<Event> search(HttpServletRequest request) throws ParseException {
		String clientId = getStringFilter("identifier", request);
		DateTime[] eventDate = getDateRangeFilter(EVENT_DATE, request);//TODO
		String eventType = getStringFilter(EVENT_TYPE, request);
		String location = getStringFilter(LOCATION_ID, request);
		String provider = getStringFilter(PROVIDER_ID, request);
		String entityType = getStringFilter(ENTITY_TYPE, request);
		DateTime[] lastEdit = getDateRangeFilter(LAST_UPDATE, request);
		
		if (!StringUtils.isEmptyOrWhitespaceOnly(clientId)) {
			Client c = clientService.find(clientId);
			if (c == null) {
				return new ArrayList<>();
			}
			
			clientId = c.getBaseEntityId();
		}
		
		return eventService.findEventsBy(clientId, eventDate == null ? null : eventDate[0],
		    eventDate == null ? null : eventDate[1], eventType, entityType, provider, location,
		    lastEdit == null ? null : lastEdit[0], lastEdit == null ? null : lastEdit[1]);
	}
	
	@Override
	public List<Event> filter(String query) {
		return eventService.findEventsByDynamicQuery(query);
	}
	
}
