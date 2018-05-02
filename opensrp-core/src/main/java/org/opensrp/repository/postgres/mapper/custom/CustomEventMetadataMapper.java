package org.opensrp.repository.postgres.mapper.custom;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.opensrp.domain.postgres.Event;
import org.opensrp.domain.postgres.EventMetadataExample;
import org.opensrp.repository.postgres.mapper.EventMetadataMapper;

public interface CustomEventMetadataMapper extends EventMetadataMapper {
	
	Event selectByDocumentId(String documentId);
	
	List<Event> selectMany(EventMetadataExample eventMetadataExample);
	
	List<Event> selectManyWithRowBounds(@Param("example") EventMetadataExample example, @Param("offset") int offset,
	                                    @Param("limit") int limit);
	
	List<Event> selectNotInOpenMRSByServerVersion(@Param("from") long serverVersion, @Param("to") long calendar,
	                                              @Param("limit") int limit);
	
	List<Event> selectNotInOpenMRSByServerVersionAndType(@Param("eventType") String type, @Param("from") long serverVersion,
	                                                     @Param("to") long calendar, @Param("limit") int limit);
	
}
