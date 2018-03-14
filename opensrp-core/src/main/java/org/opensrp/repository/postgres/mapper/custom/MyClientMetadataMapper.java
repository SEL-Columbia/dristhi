package org.opensrp.repository.postgres.mapper.custom;

import java.util.List;

import org.apache.ibatis.session.RowBounds;
import org.opensrp.domain.postgres.Client;
import org.opensrp.domain.postgres.ClientMetadataExample;

public interface MyClientMetadataMapper {
	
	List<Client> selectMany(ClientMetadataExample example);
	
	List<Client> selectManyWithRowBounds(ClientMetadataExample example, RowBounds rowBounds);
	
	Client selectOne(String baseEntityId);
}
