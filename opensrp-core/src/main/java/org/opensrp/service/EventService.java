package org.opensrp.service;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.joda.time.DateTime;
import org.json.JSONException;
import org.json.JSONObject;
import org.opensrp.domain.Address;
import org.opensrp.domain.Client;
import org.opensrp.domain.Event;
import org.opensrp.repository.AllEvents;
import org.opensrp.util.DateTimeTypeConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

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
	
	public Event getByBaseEntityAndFormSubmissionId(String baseEntityId, String formSubmissionId)
	{
		List<Event> el = allEvents.findByBaseEntityAndFormSubmissionId(baseEntityId, formSubmissionId);
		if(el.size() > 1){
			throw new IllegalStateException("Multiple events for baseEntityId and formSubmissionId combination ("+baseEntityId+","+formSubmissionId+")");
		}
		if(el.size() == 0){
			return null;
		}
		return el.get(0);
	}
	
	public List<Event> findByBaseEntityId(String baseEntityId) {
		return allEvents.findByBaseEntityId(baseEntityId);
	}
	
	public List<Event> findByFormSubmissionId(String formSubmissionId){
		return allEvents.findByFormSubmissionId(formSubmissionId);
	}
	
	public List<Event> findEventsBy(DateTime from, DateTime to, String eventType, String entityType, String providerId, String locationId) {
		return allEvents.findEvents(from, to, eventType, entityType, providerId, locationId);
	}
	
	public Event addEvent(Event event)
	{
		if(getByEventId(event.getEventId()) != null){
			throw new IllegalArgumentException("An event already exists with given eventId "+event.getEventId()+". Consider updating");
		}
		if(getByBaseEntityAndFormSubmissionId(event.getBaseEntityId(), event.getFormSubmissionId()) != null){
			throw new IllegalArgumentException("An event already exists with given baseEntity and formSubmission combination. Consider updating");
		}

		allEvents.add(event);
		return event;
	}
	
	public void updateEvent(Event updatedEvent)
	{
		// If update is on original entity
		if(updatedEvent.isNew()){
			throw new IllegalArgumentException("Event to be updated is not an existing and persisting domain object. Update database object instead of new pojo");
		}
		
		/*TODO
		 * if(findEvent(updatedEvent) == null){
			throw new IllegalArgumentException("No client found with given list of identifiers. Consider adding new!");
		}*/
		
		updatedEvent.setDateEdited(new Date());
				
		allEvents.update(updatedEvent);					
	}
	
	public Event mergeEvent(Event updatedEvent) 
	{
		return null;
		/*try{
		Client original = findClient(updatedClient);
		if(original == null){
			throw new IllegalArgumentException("No client found with given list of identifiers. Consider adding new!");
		}
		
		Gson gs = new GsonBuilder().registerTypeAdapter(DateTime.class, new DateTimeTypeConverter()).create();
		JSONObject originalJo = new JSONObject(gs.toJson(original));

		JSONObject updatedJo = new JSONObject(gs.toJson(updatedClient));
		List<Field> fn = Arrays.asList(Client.class.getDeclaredFields());

		JSONObject mergedJson = new JSONObject();
		if (originalJo.length() > 0) {
			mergedJson = new JSONObject(originalJo, JSONObject.getNames(originalJo));
		}
		if (updatedJo.length() > 0) {
			for (Field key : fn) {
				String jokey = key.getName();
				if(updatedJo.has(jokey)) mergedJson.put(jokey, updatedJo.get(jokey));
			}
		
			original = gs.fromJson(mergedJson.toString(), Client.class);
			
			for (Address a : updatedClient.getAddresses()) {
				if(original.getAddress(a.getAddressType()) == null) original.addAddress(a);
			}
			for (String k : updatedClient.getIdentifiers().keySet()) {
				original.addIdentifier(k, updatedClient.getIdentifier(k));
			}
			for (String k : updatedClient.getAttributes().keySet()) {
				original.addAttribute(k, updatedClient.getAttribute(k));
			}
		}

		original.setDateEdited(new Date());
		allClients.update(original);
		return original;
		}
		catch(JSONException e){
			throw new RuntimeException(e);
		}*/
	}

}
