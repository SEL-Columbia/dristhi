package org.opensrp.repository;

import java.util.List;

import org.ektorp.ComplexKey;
import org.ektorp.CouchDbConnector;
import org.ektorp.UpdateConflictException;
import org.ektorp.ViewQuery;
import org.ektorp.support.GenerateView;
import org.ektorp.support.View;
import org.ektorp.util.Assert;
import org.ektorp.util.Documents;
import org.joda.time.DateTime;
import org.motechproject.dao.MotechBaseRepository;
import org.opensrp.common.AllConstants;
import org.opensrp.domain.Event;
import org.opensrp.form.domain.FormSubmission;
import org.opensrp.repository.lucene.LuceneEventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

@Repository
public class AllEvents extends MotechBaseRepository<Event>{
	private LuceneEventRepository ler;

	@Autowired
	protected AllEvents(@Qualifier(AllConstants.OPENSRP_DATABASE_CONNECTOR) CouchDbConnector db,
			LuceneEventRepository ler) {
		super(Event.class, db);
		this.ler = ler;
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
		return db.queryView(createQuery("all_events_by_base_entity_and_form_submission").key(ComplexKey.of(baseEntityId, formSubmissionId)).includeDocs(true), Event.class);
	}
	@View(name = "all_events_by_base_entity_and_form_submission", map = "function(doc) { if (doc.type === 'Event'){  emit([doc.baseEntityId, doc.eventType], doc); } }")
	public List<Event> findByBaseEntityAndType(String baseEntityId, String eventType) {
		return db.queryView(createQuery("all_events_by_base_entity_and_type").key(ComplexKey.of(baseEntityId, eventType)).includeDocs(true), Event.class);
	}
	@View(name = "all_events_by_base_entity_and_form_submission", map = "function(doc) { if (doc.type === 'Event'){  emit([doc.baseEntityId, doc.formSubmissionId], doc); } }")
	public List<Event> findByBaseEntityAndFormSubmissionId(CouchDbConnector targetDb,String baseEntityId, String formSubmissionId) {
		return targetDb.queryView(createQuery("all_events_by_base_entity_and_form_submission").key(ComplexKey.of(baseEntityId, formSubmissionId)).includeDocs(true), Event.class);
	}
	
	public List<Event> findEvents(String baseEntityId, DateTime from, DateTime to, String eventType, String entityType,
			String providerId, String locationId, DateTime lastEditFrom, DateTime lastEditTo) {
		return ler.getByCriteria(baseEntityId, from, to, eventType, entityType, providerId, locationId, lastEditFrom, lastEditTo);
	}
	
	public List<Event> findEventsByDynamicQuery(String query){
		return ler.getByCriteria(query);
	}
	/**
	 * Save event to the specified db
	 * @throws UpdateConflictException if there was an update conflict.
	 */
	public void add(CouchDbConnector targetDb,Event event) {
		Assert.isTrue(Documents.isNew(event), "entity must be new");
		targetDb.create(event);
	}
	 @View(name = "events_by_server_version", map = "function(doc) { if (doc.type === 'Event') { emit([doc.serverVersion], null); } }")
	    public List<Event> findByServerVersion(long serverVersion) {
	        ComplexKey startKey = ComplexKey.of(serverVersion + 1);
	        ComplexKey endKey = ComplexKey.of(Long.MAX_VALUE);
	        return db.queryView(createQuery("events_by_server_version").startKey(startKey).endKey(endKey).includeDocs(true), Event.class);
	    }

	
	
}
