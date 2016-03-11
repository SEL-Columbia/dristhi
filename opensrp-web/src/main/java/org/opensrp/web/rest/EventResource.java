package org.opensrp.web.rest;

import static org.opensrp.common.AllConstants.Event.*;
import static org.opensrp.web.rest.RestUtils.getDateFilter;
import static org.opensrp.web.rest.RestUtils.getStringFilter;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.joda.time.DateTime;
import org.opensrp.domain.Client;
import org.opensrp.domain.Event;
import org.opensrp.service.ClientService;
import org.opensrp.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.mysql.jdbc.StringUtils;

@Controller
@RequestMapping(value = "/rest/event")
public class EventResource extends RestResource<Event>{
	private EventService eventService;
	
	@Autowired
	public EventResource(EventService eventService) {
		this.eventService = eventService;
	}

	@Override
	public Event getByUniqueId(String uniqueId) {
		return eventService.getByEventId(uniqueId);
	}
	
	@RequestMapping(method=RequestMethod.GET)
	@ResponseBody
	public Event getByBaseEntityAndFormSubmissionId(@RequestParam String baseEntityId, @RequestParam String formSubmissionId) {
		return eventService.getByBaseEntityAndFormSubmissionId(baseEntityId, formSubmissionId);
	}
	
	@Override
    public Event create(Event o) {
		return eventService.addEvent(o);
	}

	@Override
	public List<String> requiredProperties() {
		List<String> p = new ArrayList<>();
		p.add(BASE_ENTITY_ID);
		p.add(FORM_SUBMISSION_ID);
		p.add(EVENT_TYPE);
		p.add(LOCATION_ID);
		p.add(EVENT_DATE);
		p.add(PROVIDER_ID);
		p.add(ENTITY_TYPE);
		return p;
	}

	@Override
	public Event update(Event entity) {
		return eventService.mergeEvent(entity);
	}
	
	@Override
	public List<Event> search(HttpServletRequest request) throws ParseException {
		DateTime eventDate = getDateFilter(EVENT_DATE, request);//TODO
		String eventType = getStringFilter(EVENT_TYPE, request);
		String location = getStringFilter(LOCATION_ID, request);
		String provider = getStringFilter(PROVIDER_ID, request);
		String entityType = getStringFilter(ENTITY_TYPE, request);
		
		return eventService.findEventsBy(eventDate, eventDate, eventType, entityType, provider, location);
	}

}
