package org.opensrp.repository.postgres.mapper.custom;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.opensrp.domain.postgres.Client;
import org.opensrp.domain.postgres.ClientMetadataExample;
import org.opensrp.repository.postgres.mapper.ClientMetadataMapper;
import org.opensrp.search.AddressSearchBean;
import org.opensrp.search.ClientSearchBean;

public interface CustomClientMetadataMapper extends ClientMetadataMapper {
	
	List<Client> selectMany(@Param("example") ClientMetadataExample example, @Param("offset") int offset,
	                        @Param("limit") int limit);
	
	Client selectOne(String baseEntityId);
	
	Client selectByDocumentId(String documentId);
	
	List<Client> selectBySearchBean(@Param("clientBean") ClientSearchBean searchBean,
	                                @Param("addressBean") AddressSearchBean addressSearchBean, @Param("offset") int offset,
	                                @Param("limit") int limit);
}
