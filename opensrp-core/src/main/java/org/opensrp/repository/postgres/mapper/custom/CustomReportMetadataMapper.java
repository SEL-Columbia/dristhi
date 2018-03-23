package org.opensrp.repository.postgres.mapper.custom;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.opensrp.domain.postgres.Report;
import org.opensrp.domain.postgres.ReportMetadataExample;
import org.opensrp.repository.postgres.mapper.ReportMetadataMapper;

public interface CustomReportMetadataMapper extends ReportMetadataMapper {
	
	Report selectByDocumentId(String documentId);
	
	List<Report> selectMany(@Param("example") ReportMetadataExample example, @Param("offset") int offset,
	                        @Param("limit") int limit);
	
}
