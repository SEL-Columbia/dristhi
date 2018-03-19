package org.opensrp.repository;

import java.util.Calendar;
import java.util.List;

import org.joda.time.DateTime;
import org.opensrp.domain.Event;

public interface EventsRepository extends BaseRepository<Event> {
	
	List<Event> findAllByIdentifier(String identifier);
	
	List<Event> findAllByIdentifier(String identifierType, String identifier);
	
	Event findById(String id);
	
	List<Event> findByFormSubmissionId(String formSubmissionId);
	
	List<Event> findByBaseEntityId(String baseEntityId);
	
	List<Event> findByBaseEntityAndFormSubmissionId(String baseEntityId, String formSubmissionId);
	
	List<Event> findByBaseEntityAndType(String baseEntityId, String eventType);
	
	List<Event> findEvents(String baseEntityId, DateTime from, DateTime to, String eventType, String entityType,
	                       String providerId, String locationId, DateTime lastEditFrom, DateTime lastEditTo, String team,
	                       String teamId);
	
	List<Event> findEventsByDynamicQuery(String query);
	
	List<Event> findByServerVersion(long serverVersion);
	
	List<Event> notInOpenMRSByServerVersion(long serverVersion, Calendar calendar);
	
	List<Event> notInOpenMRSByServerVersionAndType(String type, long serverVersion, Calendar calendar);
	
	List<Event> findByClientAndConceptAndDate(String baseEntityId, String concept, String conceptValue, String dateFrom,
	                                          String dateTo);
	
	List<Event> findByBaseEntityIdAndConceptParentCode(String baseEntityId, String concept, String parentCode);
	
	List<Event> findByConceptAndValue(String concept, String conceptValue);
	
	List<Event> findByEmptyServerVersion();
	
	List<Event> findEvents(String team, String teamId, String providerId, String locationId, String baseEntityId,
	                       Long serverVersion, String sortBy, String sortOrder, int limit);
	
	List<Event> findEventByEventTypeBetweenTwoDates(String eventType);
	
}
