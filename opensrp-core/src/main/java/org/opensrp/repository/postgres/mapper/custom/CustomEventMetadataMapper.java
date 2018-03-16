package org.opensrp.repository.postgres.mapper.custom;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.opensrp.domain.postgres.Event;
import org.opensrp.domain.postgres.EventMetadataExample;
import org.opensrp.repository.postgres.mapper.EventMetadataMapper;

public interface CustomEventMetadataMapper extends EventMetadataMapper {
	
	Event selectOne(String baseEntityId);
	
	List<Event> selectMany(EventMetadataExample eventMetadataExample);
	
	List<Event> selectManyWithRowBounds(@Param("example") EventMetadataExample example,
	                                    @Param("offset") int offset,@Param("limit") int limit);
	
}
