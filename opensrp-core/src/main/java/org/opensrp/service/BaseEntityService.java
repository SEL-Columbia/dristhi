package org.opensrp.service;

import java.util.List;

import org.opensrp.domain.BaseEntity;
import org.opensrp.repository.BaseEntitiesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BaseEntityService {

	private final BaseEntitiesRepository allBaseEntities;

	@Autowired
	public BaseEntityService(BaseEntitiesRepository allBaseEntities ) {
		this.allBaseEntities = allBaseEntities;
	}
	
	public List<BaseEntity> getAllBaseEntities() {
		return allBaseEntities.findAllBaseEntities();
	}
	
	public BaseEntity findByBaseEntityId(String baseEntityId) {
		return allBaseEntities.findByBaseEntityId(baseEntityId);
	}

	public List<BaseEntity> findByIdentifier(String identifier) {
		return allBaseEntities.findAllByIdentifier(identifier);
	}

	public List<BaseEntity> findByIdentifier(String identifierType,
			String identifier) {
		return allBaseEntities.findAllByIdentifier(identifierType, identifier);
	}
}
