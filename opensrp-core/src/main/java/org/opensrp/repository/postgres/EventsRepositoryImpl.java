package org.opensrp.repository.postgres;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.opensrp.common.AllConstants;
import org.opensrp.common.AllConstants.BaseEntity;
import org.opensrp.domain.Event;
import org.opensrp.domain.postgres.EventMetadata;
import org.opensrp.domain.postgres.EventMetadataExample;
import org.opensrp.domain.postgres.EventMetadataExample.Criteria;
import org.opensrp.repository.EventsRepository;
import org.opensrp.repository.postgres.mapper.custom.CustomEventMapper;
import org.opensrp.repository.postgres.mapper.custom.CustomEventMetadataMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository("eventsRepositoryPostgres")
public class EventsRepositoryImpl extends BaseRepositoryImpl<Event> implements EventsRepository {
	
	public static String SERVER_VERSION = "server_version";
	
	@Autowired
	private CustomEventMapper eventMapper;
	
	@Autowired
	private CustomEventMetadataMapper eventMetadataMapper;
	
	@Override
	public Event get(String id) {
		if (StringUtils.isBlank(id)) {
			return null;
		}
		org.opensrp.domain.postgres.Event pgEvent = eventMetadataMapper.selectByDocumentId(id);
		
		return convert(pgEvent);
	}
	
	@Override
	public void add(Event entity) {
		if (entity == null || entity.getBaseEntityId() == null) {
			return;
		}
		
		if (retrievePrimaryKey(entity) != null) { //Event already added
			return;
		}
		
		org.opensrp.domain.postgres.Event pgEvent = convert(entity, null);
		if (pgEvent == null) {
			return;
		}
		
		int rowsAffected = eventMapper.insertSelectiveAndSetId(pgEvent);
		if (rowsAffected < 1 || pgEvent.getId() == null) {
			return;
		}
		
		EventMetadata eventMetadata = createMetadata(entity, pgEvent.getId());
		if (eventMetadata != null) {
			eventMetadataMapper.insertSelective(eventMetadata);
		}
		
	}
	
	@Override
	public void update(Event entity) {
		if (entity == null || entity.getBaseEntityId() == null) {
			return;
		}
		
		Long id = retrievePrimaryKey(entity);
		if (id == null) { // Event not added
			return;
		}
		
		org.opensrp.domain.postgres.Event pgEvent = convert(entity, id);
		if (pgEvent == null) {
			return;
		}
		
		EventMetadata eventMetadata = createMetadata(entity, id);
		if (eventMetadata == null) {
			return;
		}
		
		int rowsAffected = eventMapper.updateByPrimaryKeySelective(pgEvent);
		if (rowsAffected < 1) {
			return;
		}
		
		EventMetadataExample eventMetadataExample = new EventMetadataExample();
		eventMetadataExample.createCriteria().andEventIdEqualTo(id);
		eventMetadataMapper.updateByExampleSelective(eventMetadata, eventMetadataExample);
		
	}
	
	@Override
	public List<Event> getAll() {
		List<org.opensrp.domain.postgres.Event> events = eventMetadataMapper.selectMany(new EventMetadataExample());
		return convert(events);
	}
	
	@Override
	public void safeRemove(Event entity) {
		if (entity == null || entity.getBaseEntityId() == null) {
			return;
		}
		
		Long id = retrievePrimaryKey(entity);
		if (id == null) {
			return;
		}
		
		EventMetadataExample eventMetadataExample = new EventMetadataExample();
		eventMetadataExample.createCriteria().andEventIdEqualTo(id);
		int rowsAffected = eventMetadataMapper.deleteByExample(eventMetadataExample);
		if (rowsAffected < 1) {
			return;
		}
		
		eventMapper.deleteByPrimaryKey(id);
		
	}
	
	@Override
	public List<Event> findAllByIdentifier(String identifier) {
		List<org.opensrp.domain.postgres.Event> events = eventMapper.selectByIdentifier(identifier);
		return convert(events);
	}
	
	@Override
	public List<Event> findAllByIdentifier(String identifierType, String identifier) {
		List<org.opensrp.domain.postgres.Event> events = eventMapper.selectByIdentifierOfType(identifierType, identifier);
		return convert(events);
	}
	
	@Override
	public Event findById(String id) {
		return get(id);
	}
	
	@Override
	public Event findByFormSubmissionId(String formSubmissionId) {
		EventMetadataExample example = new EventMetadataExample();
		example.createCriteria().andFormSubmissionIdEqualTo(formSubmissionId);
		List<org.opensrp.domain.postgres.Event> events = eventMetadataMapper.selectMany(example);
		if (events != null && !events.isEmpty())
			return convert(events.get(0));
		else
			return null;
	}
	
	@Override
	public List<Event> findByBaseEntityId(String baseEntityId) {
		EventMetadataExample example = new EventMetadataExample();
		example.createCriteria().andBaseEntityIdEqualTo(baseEntityId);
		return convert(eventMetadataMapper.selectMany(example));
	}
	
	@Override
	public List<Event> findByBaseEntityAndFormSubmissionId(String baseEntityId, String formSubmissionId) {
		EventMetadataExample example = new EventMetadataExample();
		example.createCriteria().andBaseEntityIdEqualTo(baseEntityId).andFormSubmissionIdEqualTo(formSubmissionId);
		return convert(eventMetadataMapper.selectMany(example));
	}
	
	@Override
	public List<Event> findByBaseEntityAndType(String baseEntityId, String eventType) {
		EventMetadataExample example = new EventMetadataExample();
		example.createCriteria().andBaseEntityIdEqualTo(baseEntityId).andEventTypeEqualTo(eventType);
		return convert(eventMetadataMapper.selectMany(example));
	}
	
	@Override
	public List<Event> findEvents(String baseEntityId, DateTime from, DateTime to, String eventType, String entityType,
	                              String providerId, String locationId, DateTime lastEditFrom, DateTime lastEditTo,
	                              String team, String teamId) {
		EventMetadataExample example = new EventMetadataExample();
		Criteria criteria = example.createCriteria();
		if (StringUtils.isNotEmpty(baseEntityId))
			criteria.andBaseEntityIdEqualTo(baseEntityId);
		if (from != null && to != null)
			criteria.andEventDateBetween(from.toDate(), to.toDate());
		if (StringUtils.isNotEmpty(eventType))
			criteria.andEventTypeEqualTo(eventType);
		if (StringUtils.isNotEmpty(providerId))
			criteria.andProviderIdEqualTo(providerId);
		if (StringUtils.isNotEmpty(locationId))
			criteria.andProviderIdEqualTo(locationId);
		if (lastEditFrom != null && lastEditTo != null)
			criteria.andDateEditedBetween(from.toDate(), to.toDate());
		if (StringUtils.isNotEmpty(team))
			criteria.andTeamEqualTo(team);
		if (StringUtils.isNotEmpty(teamId))
			criteria.andTeamIdEqualTo(teamId);
		return convert(eventMetadataMapper.selectManyWithRowBounds(example, 0, DEFAULT_FETCH_SIZE));
	}
	
	@Override
	public List<Event> findEventsByDynamicQuery(String query) {
		throw new RuntimeException("Dynamic query feature not supported");
	}
	
	@Override
	public List<Event> findByServerVersion(long serverVersion) {
		EventMetadataExample example = new EventMetadataExample();
		example.createCriteria().andServerVersionGreaterThanOrEqualTo(serverVersion + 1);
		return convert(eventMetadataMapper.selectManyWithRowBounds(example, 0, DEFAULT_FETCH_SIZE));
	}
	
	@Override
	public List<Event> notInOpenMRSByServerVersion(long serverVersion, Calendar calendar) {
		EventMetadataExample example = new EventMetadataExample();
		example.createCriteria().andServerVersionBetween(serverVersion + 1, calendar.getTimeInMillis())
		        .andOpenmrsUuidIsNotNull().andOpenmrsUuidNotEqualTo("");
		return convert(eventMetadataMapper.selectManyWithRowBounds(example, 0, DEFAULT_FETCH_SIZE));
	}
	
	@Override
	public List<Event> notInOpenMRSByServerVersionAndType(String type, long serverVersion, Calendar calendar) {
		EventMetadataExample example = new EventMetadataExample();
		example.createCriteria().andEventTypeEqualTo(type)
		        .andServerVersionBetween(serverVersion + 1, calendar.getTimeInMillis()).andOpenmrsUuidIsNotNull()
		        .andOpenmrsUuidNotEqualTo("");
		return convert(eventMetadataMapper.selectManyWithRowBounds(example, 0, DEFAULT_FETCH_SIZE));
	}
	
	@Override
	public List<Event> findByClientAndConceptAndDate(String baseEntityId, String concept, String conceptValue,
	                                                 String dateFrom, String dateTo) {
		return convert(
		    eventMapper.selectByBaseEntityIdConceptAndDate(baseEntityId, concept, conceptValue, dateFrom, dateTo));
	}
	
	@Override
	public List<Event> findByBaseEntityIdAndConceptParentCode(String baseEntityId, String concept, String parentCode) {
		return convert(eventMapper.selectByBaseEntityIdAndConceptParentCode(baseEntityId, concept, parentCode));
	}
	
	@Override
	public List<Event> findByConceptAndValue(String concept, String conceptValue) {
		return convert(eventMapper.selectByConceptAndValue(concept, conceptValue));
	}
	
	@Override
	public List<Event> findByEmptyServerVersion() {
		EventMetadataExample example = new EventMetadataExample();
		example.createCriteria().andServerVersionIsNull();
		example.or(example.createCriteria().andServerVersionNotEqualTo(0l));
		return convert(eventMetadataMapper.selectManyWithRowBounds(example, 0, DEFAULT_FETCH_SIZE));
	}
	
	@Override
	public List<Event> findEvents(String team, String teamId, String providerId, String locationId, String baseEntityId,
	                              Long serverVersion, String sortBy, String sortOrder, int limit) {
		EventMetadataExample example = new EventMetadataExample();
		Criteria criteria = example.createCriteria();
		
		if (StringUtils.isNotEmpty(team)) {
			if (team.contains(",")) {
				String[] teamsArray = org.apache.commons.lang.StringUtils.split(team, ",");
				criteria.andTeamIn(Arrays.asList(teamsArray));
			} else {
				criteria.andTeamEqualTo(team);
			}
		}
		
		if (StringUtils.isNotEmpty(teamId)) {
			if (teamId.contains(",")) {
				String[] teamsArray = org.apache.commons.lang.StringUtils.split(teamId, ",");
				criteria.andTeamIdIn(Arrays.asList(teamsArray));
			} else {
				criteria.andTeamIdEqualTo(teamId);
			}
		}
		
		if (StringUtils.isNotEmpty(providerId)) {
			if (providerId.contains(",")) {
				String[] providersArray = org.apache.commons.lang.StringUtils.split(providerId, ",");
				criteria.andProviderIdIn(Arrays.asList(providersArray));
			} else {
				criteria.andProviderIdEqualTo(providerId);
			}
		}
		if (StringUtils.isNotEmpty(locationId)) {
			if (locationId.contains(",")) {
				String[] locationArray = org.apache.commons.lang.StringUtils.split(locationId, ",");
				criteria.andLocationIdIn(Arrays.asList(locationArray));
			} else {
				criteria.andLocationIdEqualTo(locationId);
			}
		}
		if (StringUtils.isNotEmpty(baseEntityId)) {
			if (baseEntityId.contains(",")) {
				String[] idsArray = org.apache.commons.lang.StringUtils.split(baseEntityId, ",");
				criteria.andBaseEntityIdIn(Arrays.asList(idsArray));
			} else {
				criteria.andBaseEntityIdEqualTo(baseEntityId);
			}
		}
		if (serverVersion != null)
			criteria.andServerVersionGreaterThanOrEqualTo(serverVersion);
		sortBy = sortBy == BaseEntity.SERVER_VERSIOIN ? SERVER_VERSION : sortBy;
		example.setOrderByClause(sortBy + " " + sortOrder);
		return convert(eventMetadataMapper.selectManyWithRowBounds(example, 0, limit));
	}
	
	@Override
	public List<Event> findEventByEventTypeBetweenTwoDates(String eventType) {
		EventMetadataExample example = new EventMetadataExample();
		example.createCriteria().andEventTypeEqualTo(eventType);
		return convert(eventMetadataMapper.selectManyWithRowBounds(example, 0, DEFAULT_FETCH_SIZE));
	}
	
	@Override
	protected Long retrievePrimaryKey(Event t) {
		Object uniqueId = getUniqueField(t);
		if (uniqueId == null) {
			return null;
		}
		
		String documentId = uniqueId.toString();
		
		EventMetadataExample eventMetadataExample = new EventMetadataExample();
		eventMetadataExample.createCriteria().andDocumentIdEqualTo(documentId);
		
		org.opensrp.domain.postgres.Event pgClient = eventMetadataMapper.selectByDocumentId(documentId);
		if (pgClient == null) {
			return null;
		}
		return pgClient.getId();
	}
	
	@Override
	protected Object getUniqueField(Event t) {
		if (t == null) {
			return null;
		}
		return t.getId();
	}
	
	// Private Methods	
	private Event convert(org.opensrp.domain.postgres.Event event) {
		if (event == null || event.getJson() == null || !(event.getJson() instanceof Event)) {
			return null;
		}
		return (Event) event.getJson();
	}
	
	private org.opensrp.domain.postgres.Event convert(Event event, Long primaryKey) {
		if (event == null) {
			return null;
		}
		
		org.opensrp.domain.postgres.Event pgEvent = new org.opensrp.domain.postgres.Event();
		pgEvent.setId(primaryKey);
		pgEvent.setJson(event);
		
		return pgEvent;
	}
	
	private List<Event> convert(List<org.opensrp.domain.postgres.Event> events) {
		if (events == null || events.isEmpty()) {
			return new ArrayList<>();
		}
		
		List<Event> convertedClients = new ArrayList<>();
		for (org.opensrp.domain.postgres.Event event : events) {
			Event convertedEvent = convert(event);
			if (convertedEvent != null) {
				convertedClients.add(convertedEvent);
			}
		}
		
		return convertedClients;
	}
	
	private EventMetadata createMetadata(Event event, Long eventId) {
		try {
			EventMetadata eventMetadata = new EventMetadata();
			eventMetadata.setBaseEntityId(event.getBaseEntityId());
			eventMetadata.setEventId(eventId);
			eventMetadata.setDocumentId(event.getId());
			eventMetadata.setBaseEntityId(event.getBaseEntityId());
			eventMetadata.setFormSubmissionId(event.getFormSubmissionId());
			eventMetadata.setOpenmrsUuid(event.getIdentifier(AllConstants.Client.OPENMRS_UUID_IDENTIFIER_TYPE));
			eventMetadata.setEventType(event.getEntityType());
			eventMetadata.setEventDate(event.getEventDate().toDate());
			eventMetadata.setProviderId(event.getProviderId());
			eventMetadata.setLocationId(event.getLocationId());
			eventMetadata.setTeam(event.getTeam());
			eventMetadata.setTeamId(event.getTeamId());
			eventMetadata.setServerVersion(event.getServerVersion());
			return eventMetadata;
		}
		catch (Exception e) {
			logger.error(e.getMessage(), e);
			return null;
		}
	}
	
}
