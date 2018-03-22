package org.opensrp.repository.postgres.mapper.custom;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.opensrp.domain.postgres.Alert;
import org.opensrp.domain.postgres.AlertMetadataExample;
import org.opensrp.repository.postgres.mapper.AlertMetadataMapper;

public interface CustomAlertMetadataMapper extends AlertMetadataMapper {
	
	Alert selectByDocumentId(String documentId);
	
	List<Alert> selectMany(@Param("example") AlertMetadataExample example, @Param("offset") int offset,
	                       @Param("limit") int limit);
}
