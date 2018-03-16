package org.opensrp.repository.postgres;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.opensrp.common.AllConstants;
import org.opensrp.domain.Event;
import org.opensrp.domain.postgres.EventMetadata;
import org.opensrp.domain.postgres.EventMetadataExample;
import org.opensrp.repository.EventsRepository;
import org.opensrp.repository.postgres.mapper.EventMapper;
import org.opensrp.repository.postgres.mapper.EventMetadataMapper;
import org.opensrp.repository.postgres.mapper.custom.CustomEventMapper;
import org.opensrp.repository.postgres.mapper.custom.CustomEventMetadataMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class EventsRepositoryImpl extends BaseRepositoryImpl<Event> implements EventsRepository {
	
	@Autowired
	private EventMapper eventMapper;
	
	@Autowired
	private CustomEventMapper customEventMapper;
	
	@Autowired
	private EventMetadataMapper eventMetadataMapper;
	
	@Autowired
	private CustomEventMetadataMapper customEventMetadataMapper;
	
	@Override
	public Event get(String id) {
		if (StringUtils.isBlank(id)) {
			return null;
		}
		org.opensrp.domain.postgres.Event pgEvent = customEventMapper.selectByDocumentId(id);
		if (pgEvent == null) {
			return null;
		}
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
		
		int rowsAffected = customEventMapper.insertSelectiveAndSetId(pgEvent);
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
		List<org.opensrp.domain.postgres.Event> events = customEventMetadataMapper.selectMany(new EventMetadataExample());
		return convert(events);
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
	
	@Override
	protected Long retrievePrimaryKey(Event t) {
		Object uniqueId = getUniqueField(t);
		if (uniqueId == null) {
			return null;
		}
		
		String baseEntityId = uniqueId.toString();
		
		EventMetadataExample eventMetadataExample = new EventMetadataExample();
		eventMetadataExample.createCriteria().andBaseEntityIdEqualTo(baseEntityId);
		
		org.opensrp.domain.postgres.Event pgClient = customEventMetadataMapper.selectOne(baseEntityId);
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
		return t.getBaseEntityId();
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
			eventMetadata.setBaseEntityId(event.getBaseEntityId());
			eventMetadata.setFormSubmissionId(event.getFormSubmissionId());
			eventMetadata.setOpenmrsUuid(event.getIdentifier(AllConstants.Client.OPENMRS_UUID_IDENTIFIER_TYPE));
			eventMetadata.setEventType(event.getEntityType());
			eventMetadata.setProviderId(event.getProviderId());
			eventMetadata.setLocationId(event.getLocationId());
			//TODO merge with PR that added team and teamid
			//eventMetadata.setTeam(event.getTeam());
			//eventMetadata.setTeamId(event.getTeamId());
			eventMetadata.setServerVersion(event.getServerVersion());
			return eventMetadata;
		}
		catch (Exception e) {
			logger.error(e.getMessage(), e);
			return null;
		}
	}
	
}
