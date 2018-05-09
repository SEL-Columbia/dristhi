package org.opensrp.repository.postgres.mapper.custom;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.opensrp.domain.postgres.MultiMedia;
import org.opensrp.domain.postgres.MultiMediaExample;
import org.opensrp.repository.postgres.mapper.MultiMediaMapper;

public interface CustomMultiMediaMapper extends MultiMediaMapper {
	
	List<MultiMedia> selectMany(@Param("example") MultiMediaExample example, @Param("offset") int offset,
	                            @Param("limit") int limit);
}
