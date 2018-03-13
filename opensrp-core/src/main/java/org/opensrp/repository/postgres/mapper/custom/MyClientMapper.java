package org.opensrp.repository.postgres.mapper.custom;

import org.opensrp.domain.postgres.Client;
import org.opensrp.repository.postgres.mapper.ClientMapper;

public interface MyClientMapper extends ClientMapper {
	
	int insertSelectiveAndSetId(Client record);
	
}
