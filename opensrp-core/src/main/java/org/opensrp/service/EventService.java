package org.opensrp.service;

import java.util.List;

import org.opensrp.domain.Event;
import org.opensrp.repository.AllEvents;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EventService {

	private final AllEvents allEvents;
	
	@Autowired
	public EventService(AllEvents allEvents)
	{
		this.allEvents = allEvents;
	}
	
	public Event getByEventId(String eventId)
	{
		return allEvents.findByEventId(eventId);
	}
	
	public List<Event> findByBaseEntityId(String baseEntityId) {
		return allEvents.findByBaseEntityId(baseEntityId);
	}
	
	public Event findByFormSubmissionId(String formSubmissionId){
		return allEvents.findByFormSubmissionId(formSubmissionId);
	}
	
	public List<Event> findEventsBy(String baseEntityId, String locationId, String eventType,
			String providerId, String entityType, long from, long to) {
		return allEvents.findEvents(baseEntityId, locationId, eventType, providerId, entityType, from, to);
	}
	
	public void addEvent(Event event)
	{
		allEvents.add(event);				
	}
	
	public void updateEvent(Event event)
	{
		allEvents.update(event);					
	}
}
