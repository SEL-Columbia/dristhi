package org.opensrp.repository.postgres;

import java.util.Calendar;
import java.util.List;

import org.joda.time.DateTime;
import org.opensrp.domain.Event;
import org.opensrp.repository.EventsRepository;
import org.springframework.stereotype.Repository;

@Repository
public class EventsRepositoryImpl implements EventsRepository {
	
	@Override
	public Event get(String id) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void add(Event entity) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void update(Event entity) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public List<Event> getAll() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void safeRemove(Event entity) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public List<Event> findAllByIdentifier(String identifier) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public List<Event> findAllByIdentifier(String identifierType, String identifier) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public Event findById(String id) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public List<Event> findByFormSubmissionId(String formSubmissionId) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public List<Event> findByBaseEntityId(String baseEntityId) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public List<Event> findByBaseEntityAndFormSubmissionId(String baseEntityId, String formSubmissionId) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public List<Event> findByBaseEntityAndType(String baseEntityId, String eventType) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public List<Event> findEvents(String baseEntityId, DateTime from, DateTime to, String eventType, String entityType,
	                              String providerId, String locationId, DateTime lastEditFrom, DateTime lastEditTo) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public List<Event> findEventsByDynamicQuery(String query) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public List<Event> findByServerVersion(long serverVersion) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public List<Event> notInOpenMRSByServerVersion(long serverVersion, Calendar calendar) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public List<Event> notInOpenMRSByServerVersionAndType(String type, long serverVersion, Calendar calendar) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public List<Event> findByClientAndConceptAndDate(String baseEntityId, String concept, String conceptValue,
	                                                 String dateFrom, String dateTo) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public List<Event> findByBaseEntityIdAndConceptParentCode(String baseEntityId, String concept, String parentCode) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public List<Event> findByConceptAndValue(String concept, String conceptValue) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public List<Event> findByEmptyServerVersion() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public List<Event> findEvents(String team, String providerId, String locationId, String baseEntityId, Long serverVersion,
	                              String sortBy, String sortOrder, int limit) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public List<Event> findEventByEventTypeBetweenTwoDates(String eventType) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
