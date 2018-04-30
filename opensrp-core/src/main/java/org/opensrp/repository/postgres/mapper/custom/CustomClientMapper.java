package org.opensrp.repository.postgres.mapper.custom;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.opensrp.domain.postgres.Client;
import org.opensrp.repository.postgres.mapper.ClientMapper;

public interface CustomClientMapper extends ClientMapper {
	
	int insertSelectiveAndSetId(Client record);
	
	Client selectByDocumentId(String documentId);
	
	List<Client> selectByIdentifier(String identifier);
	
	List<Client> selectByIdentifierOfType(@Param("identifierType") String identifierType,
	                                      @Param("identifier") String identifier);
	
	List<Client> selectByAttributeOfType(@Param("attributeType") String attributeType, @Param("attribute") String attribute);
	
	List<Client> selectByRelationshipIdAndDateCreated(@Param("relationalId") String relationalId,
	                                                  @Param("dateFrom") Date date, @Param("dateTo") Date date2);
	
	List<Client> selectByRelationshipIdOfType(@Param("relationshipType") String relationshipType,
	                                          @Param("relationshipId") String relationshipId);
	
	List<Client> selectByRelationShip(String relationshipId);
}
