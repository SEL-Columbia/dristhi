package org.opensrp.repository.postgres.mapper.custom;

import org.opensrp.domain.postgres.Event;;

public interface CustomEventMapper {
	
	int insertSelectiveAndSetId(Event record);
	
	Event selectByDocumentId(String documentId);
}
