package org.opensrp.repository.postgres.mapper.custom;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.opensrp.domain.postgres.Event;
import org.opensrp.repository.postgres.mapper.EventMapper;;

public interface CustomEventMapper extends EventMapper {
	
	int insertSelectiveAndSetId(Event record);
	
	Event selectByDocumentId(String documentId);
	
	List<Event> selectByIdentifier(String identifier);
	
	List<Event> selectByIdentifierOfType(@Param("identifierType") String identifierType, @Param("identifier") String identifier);

}
