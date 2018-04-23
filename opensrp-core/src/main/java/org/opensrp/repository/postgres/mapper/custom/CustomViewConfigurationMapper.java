package org.opensrp.repository.postgres.mapper.custom;

import org.opensrp.domain.postgres.ViewConfiguration;
import org.opensrp.repository.postgres.mapper.ViewConfigurationMapper;

public interface CustomViewConfigurationMapper extends ViewConfigurationMapper {
	
	int insertSelectiveAndSetId(ViewConfiguration viewConfiguration);
}
