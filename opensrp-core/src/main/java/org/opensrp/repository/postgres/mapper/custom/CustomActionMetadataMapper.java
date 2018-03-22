package org.opensrp.repository.postgres.mapper.custom;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.opensrp.domain.postgres.Action;
import org.opensrp.domain.postgres.ActionMetadataExample;
import org.opensrp.repository.postgres.mapper.ActionMetadataMapper;

public interface CustomActionMetadataMapper extends ActionMetadataMapper {
	
	Action selectByDocumentId(String documentId);
	
	List<Action> selectMany(@Param("example") ActionMetadataExample example, @Param("offset") int offset,
	                        @Param("limit") int limit);
	
	List<Action> selectManyBySchedule(@Param("example") ActionMetadataExample example,
	                                  @Param("scheduleName") String scheduleName, @Param("offset") int offset,
	                                  @Param("limit") int limit);
	
	Long countAll(@Param("example") ActionMetadataExample example);
}
