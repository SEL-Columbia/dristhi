package org.opensrp.repository;

import java.util.List;

import org.ektorp.CouchDbConnector;
import org.ektorp.ViewQuery;
import org.ektorp.support.GenerateView;
import org.ektorp.support.View;
import org.motechproject.dao.MotechBaseRepository;
import org.opensrp.common.AllConstants;
import org.opensrp.domain.Event;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

@Repository
public class AllEvents extends MotechBaseRepository<Event>{
	
	@Autowired
	protected AllEvents(@Qualifier(AllConstants.OPENSRP_DATABASE_CONNECTOR) CouchDbConnector db) {
		super(Event.class, db);
	}
	
	@GenerateView
	public Event findByEventId(String eventId) {
		List<Event> events = queryView("by_eventId", eventId);
		if (events == null || events.isEmpty()) {
			return null;
		}
		return events.get(0);
	}
	
	@GenerateView
	public Event findByFormSubmissionId(String formSubmissionId) {
		List<Event> events = queryView("by_formSubmissionId", formSubmissionId);
		if (events == null || events.isEmpty()) {
			return null;
		}
		return events.get(0);
	}
	
	@GenerateView
	public List<Event> findByBaseEntityId(String baseEntityId) {
		return queryView("by_baseEntityId", baseEntityId);
	}
	
	@View(name = "all_events", map = "function(doc) { if (doc.type === 'Event') { emit(doc.eventId); } }")
	public List<Event> findAllEvents() {
		return db.queryView(createQuery("all_events").includeDocs(true), Event.class);
	}
	
	@View(name = "all_events_by_filter", map = "function(doc) {if (doc.type==='Event'){emit([doc.baseEntityId,doc.locationId,doc.eventType,doc.providerId,doc.entityType,doc.version]);}}")
	public List<Event> findEvents(String baseEntityId, String locationId, String eventType,
			String providerId, String entityType, long from, long to) {
		ViewQuery q = createQuery("all_events_by_filter").includeDocs(true);
		if(baseEntityId != null){
			q.queryParam("baseEntityId", baseEntityId);
		}
		if(locationId != null){
			q.queryParam("locationId", locationId);
		}
		if(eventType != null){
			q.queryParam("eventType", eventType);
		}
		if(providerId != null){
			q.queryParam("providerId", providerId);
		}
		if(entityType != null){
			q.queryParam("entityType", entityType);
		}
		
		return db.queryView(q.startKey(from).endKey(to), Event.class);
	}
	
	
}
