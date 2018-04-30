package org.opensrp.repository.postgres.mapper.custom;

import org.opensrp.domain.postgres.Alert;
import org.opensrp.repository.postgres.mapper.AlertMapper;


public interface CustomAlertMapper extends AlertMapper {

	int insertSelectiveAndSetId(Alert pgAlert);
	
}
