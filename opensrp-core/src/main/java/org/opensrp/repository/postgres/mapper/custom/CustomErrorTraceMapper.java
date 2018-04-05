package org.opensrp.repository.postgres.mapper.custom;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.opensrp.domain.postgres.ErrorTrace;
import org.opensrp.domain.postgres.ErrorTraceExample;
import org.opensrp.repository.postgres.mapper.ErrorTraceMapper;

public interface CustomErrorTraceMapper extends ErrorTraceMapper {
	
	List<ErrorTrace> selectMany(@Param("example") ErrorTraceExample example, @Param("offset") int offset,
	                            @Param("limit") int limit);
	
}
