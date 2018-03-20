package org.opensrp.repository.couch;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.ektorp.ComplexKey;
import org.ektorp.CouchDbConnector;
import org.ektorp.UpdateConflictException;
import org.ektorp.support.GenerateView;
import org.ektorp.support.View;
import org.ektorp.util.Assert;
import org.ektorp.util.Documents;
import org.joda.time.DateTime;
import org.motechproject.dao.MotechBaseRepository;
import org.opensrp.common.AllConstants;
import org.opensrp.domain.Event;
import org.opensrp.repository.EventsRepository;
import org.opensrp.repository.lucene.LuceneEventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

@Repository("couchEventsRepository")
@Primary
public class AllEvents extends MotechBaseRepository<Event> implements EventsRepository {
	
	private LuceneEventRepository ler;
	
	@Autowired
	protected AllEvents(@Qualifier(AllConstants.OPENSRP_DATABASE_CONNECTOR) CouchDbConnector db, LuceneEventRepository ler) {
		super(Event.class, db);
		this.ler = ler;
	}
	
	@View(name = "all_events_by_identifier", map = "function(doc) {if (doc.type === 'Event') {for(var key in doc.identifiers) {emit(doc.identifiers[key]);}}}")
	public List<Event> findAllByIdentifier(String identifier) {
		return db.queryView(createQuery("all_events_by_identifier").key(identifier).includeDocs(true), Event.class);
	}
	
	@View(name = "all_events_by_identifier_of_type", map = "function(doc) {if (doc.type === 'Event') {for(var key in doc.identifiers) {emit([key, doc.identifiers[key]]);}}}")
	public List<Event> findAllByIdentifier(String identifierType, String identifier) {
		ComplexKey ckey = ComplexKey.of(identifierType, identifier);
		return db.queryView(createQuery("all_events_by_identifier_of_type").key(ckey).includeDocs(true), Event.class);
	}
	
	public Event findById(String id) {
		Event event = db.get(Event.class, id);
		return event;
	}
	
	@GenerateView
	public Event findByFormSubmissionId(String formSubmissionId) {
		List<Event> events = queryView("by_formSubmissionId", formSubmissionId);
		if (events == null || events.isEmpty())
			return null;
		else if (events.size() > 1) {
			throw new IllegalStateException("Multiple events for formSubmissionId " + formSubmissionId);
		} else
			return events.get(0);
	}
	
	@GenerateView
	public List<Event> findByBaseEntityId(String baseEntityId) {
		return queryView("by_baseEntityId", baseEntityId);
	}
	
	@View(name = "all_events_by_base_entity_and_form_submission", map = "function(doc) { if (doc.type === 'Event'){  emit([doc.baseEntityId, doc.formSubmissionId], doc); } }")
	public Event findByBaseEntityAndFormSubmissionId(String baseEntityId, String formSubmissionId) {
		List<Event> events = db.queryView(createQuery("all_events_by_base_entity_and_form_submission")
		        .key(ComplexKey.of(baseEntityId, formSubmissionId)).includeDocs(true),
		    Event.class);
		if (events == null || events.isEmpty())
			return null;
		else if (events.size() > 1) {
			throw new IllegalStateException("Multiple events for baseEntityId and formSubmissionId combination ("
			        + baseEntityId + "," + formSubmissionId + ")");
		} else
			return events.get(0);
		
	}
	
	@View(name = "all_events_by_base_entity_and_type", map = "function(doc) { if (doc.type === 'Event'){  emit([doc.baseEntityId, doc.eventType], doc); } }")
	public List<Event> findByBaseEntityAndType(String baseEntityId, String eventType) {
		return db.queryView(
		    createQuery("all_events_by_base_entity_and_type").key(ComplexKey.of(baseEntityId, eventType)).includeDocs(true),
		    Event.class);
	}
	
	@View(name = "all_events_by_base_entity_and_form_submission", map = "function(doc) { if (doc.type === 'Event'){  emit([doc.baseEntityId, doc.formSubmissionId], doc); } }")
	public List<Event> findByBaseEntityAndFormSubmissionId(CouchDbConnector targetDb, String baseEntityId,
	                                                       String formSubmissionId) {
		return targetDb.queryView(createQuery("all_events_by_base_entity_and_form_submission")
		        .key(ComplexKey.of(baseEntityId, formSubmissionId)).includeDocs(true),
		    Event.class);
	}
	
	public List<Event> findEvents(String baseEntityId, DateTime from, DateTime to, String eventType, String entityType,
	                              String providerId, String locationId, DateTime lastEditFrom, DateTime lastEditTo,
	                              String team, String teamId) {
		return ler.getByCriteria(baseEntityId, from, to, eventType, entityType, providerId, locationId, lastEditFrom,
		    lastEditTo, team, teamId);
	}
	
	public List<Event> findEventsByDynamicQuery(String query) {
		return ler.getByCriteria(query);
	}
	
	/**
	 * Save event to the specified db
	 * 
	 * @throws UpdateConflictException if there was an update conflict.
	 */
	public void add(CouchDbConnector targetDb, Event event) {
		Assert.isTrue(Documents.isNew(event), "entity must be new");
		targetDb.create(event);
	}
	
	@View(name = "events_by_version", map = "function(doc) { if (doc.type === 'Event') { emit([doc.serverVersion], null); } }")
	public List<Event> findByServerVersion(long serverVersion) {
		ComplexKey startKey = ComplexKey.of(serverVersion + 1);
		ComplexKey endKey = ComplexKey.of(Long.MAX_VALUE);
		return db.queryView(createQuery("events_by_version").startKey(startKey).endKey(endKey).limit(1000).includeDocs(true),
		    Event.class);
	}
	
	@View(name = "events_not_in_OpenMRS", map = "function(doc) { if (doc.type === 'Event' && doc.serverVersion) { var noId = true; for(var key in doc.identifiers) {if(key == 'OPENMRS_UUID') {noId = false;}}if(noId){emit([doc.serverVersion],  null); }} }")
	public List<Event> notInOpenMRSByServerVersion(long serverVersion, Calendar calendar) {
		long serverStartKey = serverVersion + 1;
		long serverEndKey = calendar.getTimeInMillis();
		if (serverStartKey < serverEndKey) {
			ComplexKey startKey = ComplexKey.of(serverStartKey);
			ComplexKey endKey = ComplexKey.of(serverEndKey);
			return db.queryView(
			    createQuery("events_not_in_OpenMRS").startKey(startKey).endKey(endKey).limit(1000).includeDocs(true),
			    Event.class);
		}
		return new ArrayList<>();
	}
	
	@View(name = "events_by_type_not_in_OpenMRS", map = "function(doc) { if (doc.type === 'Event' && doc.serverVersion) { var noId = true; for(var key in doc.identifiers) {if(key == 'OPENMRS_UUID') {noId = false;}}if(noId){emit([doc.eventType, doc.serverVersion], null); }} }")
	public List<Event> notInOpenMRSByServerVersionAndType(String type, long serverVersion, Calendar calendar) {
		long serverStartKey = serverVersion + 1;
		long serverEndKey = calendar.getTimeInMillis();
		if (serverStartKey < serverEndKey) {
			ComplexKey startKey = ComplexKey.of(type, serverStartKey);
			ComplexKey endKey = ComplexKey.of(type, serverEndKey);
			return db.queryView(
			    createQuery("events_by_type_not_in_OpenMRS").startKey(startKey).endKey(endKey).limit(1000).includeDocs(true),
			    Event.class);
		}
		return new ArrayList<>();
	}
	
	/**
	 * Find an event based on a concept and between a range of date created dates
	 * 
	 * @param concept
	 * @param conceptValue
	 * @param dateFrom
	 * @param dateTo
	 * @return
	 */
	@View(name = "event_by_concept_and_date_created", map = "function(doc) {if (doc.type === 'Event' && doc.obs) {for (var obs in doc.obs) {var fieldCode = doc.obs[obs].fieldCode;var value = doc.obs[obs].values[0];emit([doc.baseEntityId,fieldCode,value,doc.dateCreated.substring(0, 10)],null);}}}")
	public List<Event> findByClientAndConceptAndDate(String baseEntityId, String concept, String conceptValue,
	                                                 String dateFrom, String dateTo) {
		ComplexKey startKey = ComplexKey.of(baseEntityId, concept, conceptValue, dateFrom);
		ComplexKey endKey = ComplexKey.of(baseEntityId, concept, conceptValue, dateTo);
		List<Event> events = db.queryView(
		    createQuery("event_by_concept_and_date_created").startKey(startKey).endKey(endKey).includeDocs(true),
		    Event.class);
		return events;
	}
	
	@View(name = "event_by_concept_parent_code_and_base_entity_id", map = "function(doc) {if (doc.type === 'Event' && doc.obs) {for (var obs in doc.obs) {var fieldCode = doc.obs[obs].fieldCode;var parentCode = doc.obs[obs].parentCode;emit([doc.baseEntityId, fieldCode, parentCode], null);}}}")
	public List<Event> findByBaseEntityIdAndConceptParentCode(String baseEntityId, String concept, String parentCode) {
		ComplexKey startKey = ComplexKey.of(baseEntityId, concept, parentCode);
		ComplexKey endKey = ComplexKey.of(baseEntityId, concept, parentCode);
		List<Event> events = db.queryView(createQuery("event_by_concept_parent_code_and_base_entity_id").startKey(startKey)
		        .endKey(endKey).includeDocs(true),
		    Event.class);
		return events;
	}
	
	@View(name = "event_by_concept_and_value", map = "function(doc) {if (doc.type === 'Event' && doc.obs) {for (var obs in doc.obs) {var fieldCode = doc.obs[obs].fieldCode;var value = doc.obs[obs].values[0];emit([fieldCode,value],null);}}}")
	public List<Event> findByConceptAndValue(String concept, String conceptValue) {
		List<Event> events = db.queryView(
		    createQuery("event_by_concept_and_value").key(ComplexKey.of(concept, conceptValue)).includeDocs(true),
		    Event.class);
		return events;
	}
	
	@View(name = "events_by_empty_server_version", map = "function(doc) { if (doc.type == 'Event' && !doc.serverVersion) { emit(doc._id, doc); } }")
	public List<Event> findByEmptyServerVersion() {
		return db.queryView(createQuery("events_by_empty_server_version").limit(200).includeDocs(true), Event.class);
	}
	
	@GenerateView
	public List<Event> getAll() {
		return super.getAll();
	}
	
	public List<Event> findEvents(String team, String teamId, String providerId, String locationId, String baseEntityId,
	                              Long serverVersion, String sortBy, String sortOrder, int limit) {
		return ler.getByCriteria(team, teamId, providerId, locationId, baseEntityId, serverVersion, sortBy, sortOrder,
		    limit);
	}
	
	@View(name = "all_events_by_event_type_and_version", map = "function(doc) { if (doc.type === 'Event'){  emit([doc.eventType, doc.version], null); } }")
	public List<Event> findEventByEventTypeBetweenTwoDates(String eventType) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.DATE, 1);
		System.err.println("calendar.getTime():" + calendar.getTime().getTime());
		ComplexKey start = ComplexKey.of(eventType, calendar.getTime().getTime());
		ComplexKey end = ComplexKey.of(eventType, System.currentTimeMillis());
		List<Event> events = db.queryView(
		    createQuery("all_events_by_event_type_and_version").startKey(start).endKey(end).includeDocs(true), Event.class);
		
		return events;
	}
}
