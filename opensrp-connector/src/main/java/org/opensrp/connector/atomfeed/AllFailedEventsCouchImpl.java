package org.opensrp.connector.atomfeed;
import java.util.ArrayList;
import java.util.List;

import org.ektorp.ComplexKey;
import org.ektorp.CouchDbConnector;
import org.ektorp.support.View;
import org.ict4h.atomfeed.client.domain.FailedEvent;
import org.ict4h.atomfeed.client.domain.FailedEventRetryLog;
import org.ict4h.atomfeed.client.repository.AllFailedEvents;
import org.motechproject.dao.MotechBaseRepository;
import org.opensrp.connector.openmrs.constants.OpenmrsConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

@Repository
public class AllFailedEventsCouchImpl  implements AllFailedEvents {
	private AllFailedEventInnerRepository repository;

	@Autowired
	public AllFailedEventsCouchImpl(@Value("#{opensrp['couchdb.atomfeed-db.revision-limit']}") int revisionLimit,
			@Qualifier(OpenmrsConstants.ATOMFEED_DATABASE_CONNECTOR) CouchDbConnector db) {
		db.setRevisionLimit(revisionLimit);
		this.repository = new AllFailedEventInnerRepository(db);
	}
    
    @Override
    public FailedEvent get(String feedUri, String id) {
       return repository.findByFeedUriAndEventId(feedUri, id).toFailedEvent();
    }

    @Override
    public void addOrUpdate(FailedEvent failedEvent) {
    	org.opensrp.connector.atomfeed.domain.FailedEvent existing = repository.findByEventId(failedEvent.getEventId());
        if(existing != null){
        	existing.setErrorHashCode(failedEvent.hashCode());
        	existing.setErrorMessage(failedEvent.getErrorMessage());
        	existing.setEventContent(failedEvent.getEvent().getContent());
        	existing.setEventId(failedEvent.getEventId());
        	existing.setFailedAt(failedEvent.getFailedAt());
        	existing.setFeedUri(failedEvent.getFeedUri());
        	existing.setRetries(failedEvent.getRetries());
        	existing.setTitle(failedEvent.getEvent().getTitle());
        	
        	repository.update(existing);
        }
        else {
        	repository.add(existing);
        }
    }

    @Override
    public List<FailedEvent> getOldestNFailedEvents(String feedUri, int numberOfFailedEvents, int failedEventMaxRetry) {
        if (numberOfFailedEvents < 1) throw new IllegalArgumentException("Number of failed events should at least be one.");

        List<FailedEvent> lastNFailedEvents = new ArrayList<FailedEvent>();
        List<org.opensrp.connector.atomfeed.domain.FailedEvent> fel = repository.findByFeedUri(feedUri);
        for (int i = fel.size() - 1; i >= 0 ; i--) {
            if (lastNFailedEvents.size() == numberOfFailedEvents) break;

            lastNFailedEvents.add(fel.get(i).toFailedEvent());
        }

        return lastNFailedEvents;
    }

    @Override
    public int getNumberOfFailedEvents(String feedUri) {
        return repository.findByFeedUri(feedUri).size();
    }

    @Override
    public void remove(FailedEvent failedEvent) {
    	org.opensrp.connector.atomfeed.domain.FailedEvent fe = repository.findByEventId(failedEvent.getEventId());
    	if(fe != null){
    		repository.remove(fe);
    	}
    	else {
    		throw new RuntimeException("Failed Event not found");
    	}
    }


    @Override
    public void insert(FailedEventRetryLog failedEventRetryLog) {

    }
    
    public class AllFailedEventInnerRepository extends MotechBaseRepository<org.opensrp.connector.atomfeed.domain.FailedEvent>{
    	AllFailedEventInnerRepository(CouchDbConnector db) {
            super(org.opensrp.connector.atomfeed.domain.FailedEvent.class, db);
		}
    	
    	@View(name = "all_failed_events_by_feed_uri_and_event_id", map = "function(doc) {if (doc.type === 'FailedEvent') {emit([doc.feedUri, doc.eventId]);}}")
    	public org.opensrp.connector.atomfeed.domain.FailedEvent findByFeedUriAndEventId(String feedUri, String eventId) {
    		ComplexKey ckey = ComplexKey.of(feedUri, eventId);
    		List<org.opensrp.connector.atomfeed.domain.FailedEvent> ol = db.queryView(createQuery("all_failed_events_by_feed_uri_and_event_id").key(ckey).includeDocs(true), org.opensrp.connector.atomfeed.domain.FailedEvent.class);
    		
    		if(ol.size() == 0){
    			return null;
    		}
    		
    		return ol.get(0);
    	}
    	
    	@View(name = "all_failed_events_by_feed_uri", map = "function(doc) {if (doc.type === 'FailedEvent') {emit(doc.feedUri);}}")
    	public List<org.opensrp.connector.atomfeed.domain.FailedEvent> findByFeedUri(String feedUri) {
    		List<org.opensrp.connector.atomfeed.domain.FailedEvent> ol = db.queryView(createQuery("all_failed_events_by_feed_uri").key(feedUri).includeDocs(true), org.opensrp.connector.atomfeed.domain.FailedEvent.class);
    		return ol;
    	}
    	
    	@View(name = "all_failed_events_event_id", map = "function(doc) {if (doc.type === 'FailedEvent') {emit(doc.eventId);}}")
    	public org.opensrp.connector.atomfeed.domain.FailedEvent findByEventId(String eventId) {
    		List<org.opensrp.connector.atomfeed.domain.FailedEvent> ol = db.queryView(createQuery("all_failed_events_event_id").key(eventId).includeDocs(true), org.opensrp.connector.atomfeed.domain.FailedEvent.class);
    		
    		if(ol.size() == 0){
    			return null;
    		}
    		
    		return ol.get(0);
    	}
    }

}