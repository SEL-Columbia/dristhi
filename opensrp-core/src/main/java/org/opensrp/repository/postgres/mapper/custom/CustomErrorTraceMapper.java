package org.opensrp.repository.postgres.mapper.custom;

import java.util.List;

import org.opensrp.domain.postgres.ErrorTrace;
import org.opensrp.domain.postgres.ErrorTraceExample;
import org.opensrp.repository.postgres.mapper.ErrorTraceMapper;


public interface CustomErrorTraceMapper extends ErrorTraceMapper {

	List<ErrorTrace> selectMany(ErrorTraceExample example, int i, int dEFAULT_FETCH_SIZE);
	
}
