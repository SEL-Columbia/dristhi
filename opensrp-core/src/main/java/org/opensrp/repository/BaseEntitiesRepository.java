package org.opensrp.repository;

import java.util.List;

import org.opensrp.domain.BaseEntity;

public interface BaseEntitiesRepository extends BaseRepository<BaseEntity> {
	
	BaseEntity findByBaseEntityId(String baseEntityId);
	
	List<BaseEntity> findAllBaseEntities();
	
	List<BaseEntity> findAllByIdentifier(String identifier);
	
	List<BaseEntity> findAllByIdentifier(String identifierType, String identifier);
	
}
