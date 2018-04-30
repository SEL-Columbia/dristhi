package org.opensrp.repository.postgres.mapper.custom;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.opensrp.domain.postgres.Report;
import org.opensrp.repository.postgres.mapper.ReportMapper;

public interface CustomReportMapper extends ReportMapper {
	
	int insertSelectiveAndSetId(Report report);
	
	List<Report> selectByIdentifier(@Param("identifier") String identifier, @Param("offset") int offset,
	                                @Param("limit") int limit);
	
}
