package org.opensrp.repository.postgres.mapper.custom;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.opensrp.domain.postgres.Client;
import org.opensrp.domain.postgres.ClientMetadataExample;
import org.opensrp.repository.postgres.mapper.ClientMetadataMapper;

public interface CustomClientMetadataMapper extends ClientMetadataMapper {
	
	List<Client> selectMany(@Param("example") ClientMetadataExample example, @Param("offset") int offset,
	                        @Param("limit") int limit);
	
	Client selectOne(String baseEntityId);

	Client selectByDocumentId(String documentId);
}
