package org.opensrp.repository;

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
import org.opensrp.repository.lucene.LuceneEventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

@Repository
public class AllEvents extends MotechBaseRepository<Event> {
	
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
	public List<Event> findByFormSubmissionId(String formSubmissionId) {
		List<Event> events = queryView("by_formSubmissionId", formSubmissionId);
		return events;
	}
	
	@GenerateView
	public List<Event> findByBaseEntityId(String baseEntityId) {
		return queryView("by_baseEntityId", baseEntityId);
	}
	
	@View(name = "all_events_by_base_entity_and_form_submission", map = "function(doc) { if (doc.type === 'Event'){  emit([doc.baseEntityId, doc.formSubmissionId], doc); } }")
	public List<Event> findByBaseEntityAndFormSubmissionId(String baseEntityId, String formSubmissionId) {
		return db.queryView(createQuery("all_events_by_base_entity_and_form_submission")
		        .key(ComplexKey.of(baseEntityId, formSubmissionId)).includeDocs(true),
		    Event.class);
	}
	
	@View(name = "all_events_by_base_entity_and_form_submission", map = "function(doc) { if (doc.type === 'Event'){  emit([doc.baseEntityId, doc.eventType], doc); } }")
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
	                              String providerId, String locationId, DateTime lastEditFrom, DateTime lastEditTo) {
		return ler.getByCriteria(baseEntityId, from, to, eventType, entityType, providerId, locationId, lastEditFrom,
		    lastEditTo);
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
		return db.queryView(createQuery("events_by_version").startKey(startKey).endKey(endKey).includeDocs(true),
		    Event.class);
	}
	
	@View(name = "events_by_empty_server_version", map = "function(doc) { if (doc.type == 'Client' && !doc.serverVersion) { emit(doc._id, doc); } }")
	public List<Event> findByEmptyServerVersion() {
		return db.queryView(createQuery("events_by_empty_server_version").limit(200).includeDocs(true), Event.class);
	}
	
}
