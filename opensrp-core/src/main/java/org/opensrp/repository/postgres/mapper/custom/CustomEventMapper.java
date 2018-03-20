package org.opensrp.repository.postgres.mapper.custom;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.opensrp.domain.postgres.Event;
import org.opensrp.repository.postgres.mapper.EventMapper;;

public interface CustomEventMapper extends EventMapper {
	
	int insertSelectiveAndSetId(Event record);
	
	Event selectByDocumentId(String documentId);
	
	List<Event> selectByIdentifier(String identifier);
	
	List<Event> selectByIdentifierOfType(@Param("identifierType") String identifierType,
	                                     @Param("identifier") String identifier);
	
	List<Event> selectByBaseEntityIdConceptAndDate(@Param("baseEntityId") String baseEntityId,
	                                               @Param("concept") String concept,
	                                               @Param("conceptValue") String conceptValue,
	                                               @Param("dateFrom") Date dateFrom, @Param("dateTo") Date dateTo);
	
	List<Event> selectByBaseEntityIdAndConceptParentCode(@Param("baseEntityId") String baseEntityId,
	                                                     @Param("concept") String concept,
	                                                     @Param("parentCode") String parentCode);
	
	List<Event> selectByConceptAndValue(@Param("concept") String concept, @Param("conceptValue") String conceptValue);
	
}
