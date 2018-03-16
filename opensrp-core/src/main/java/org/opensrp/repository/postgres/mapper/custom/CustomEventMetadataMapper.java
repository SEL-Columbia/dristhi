package org.opensrp.repository.postgres.mapper.custom;

import java.util.List;

import org.opensrp.domain.postgres.Event;
import org.opensrp.domain.postgres.EventMetadataExample;

public interface CustomEventMetadataMapper {
	
	Event selectOne(String baseEntityId);
	
	List<Event> selectMany(EventMetadataExample eventMetadataExample);
	
}
