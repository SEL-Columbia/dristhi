package org.opensrp.repository.postgres.mapper.custom;

import java.util.List;

import org.opensrp.domain.postgres.Client;
import org.opensrp.domain.postgres.ClientMetadataExample;
import org.opensrp.repository.postgres.mapper.ClientMetadataMapper;

public interface MyClientMetadataMapper extends ClientMetadataMapper {
	
	List<Client> selectMany(ClientMetadataExample example);
	
	Client selectOne(String baseEntityId);
}
