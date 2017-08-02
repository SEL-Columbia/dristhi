package org.opensrp.repository;

import java.util.List;

import org.ektorp.ComplexKey;
import org.ektorp.CouchDbConnector;
import org.ektorp.support.GenerateView;
import org.ektorp.support.View;
import org.motechproject.dao.MotechBaseRepository;
import org.opensrp.common.AllConstants;
import org.opensrp.domain.BaseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

@Repository
public class AllBaseEntities extends MotechBaseRepository<BaseEntity> {

	@Autowired
	protected AllBaseEntities(@Qualifier(AllConstants.OPENSRP_DATABASE_CONNECTOR) CouchDbConnector db) {
		super(BaseEntity.class, db);
	}

	@GenerateView
	public BaseEntity findByBaseEntityId(String baseEntityId) {
		List<BaseEntity> entities = queryView("by_baseEntityId", baseEntityId);
		if (entities == null || entities.isEmpty()) {
			return null;
		}
		return entities.get(0);
	}

	@View(name = "all_entities", map = "function(doc) { if (doc.baseEntityId && doc.identifiers) { emit(doc.id); } }")
	public List<BaseEntity> findAllBaseEntities() {
		return db.queryView(createQuery("all_entities").includeDocs(true), BaseEntity.class);
	}

	@View(name = "all_entities_by_identifier", map = "function(doc) {if (doc.baseEntityId && doc.identifiers) {for(var key in doc.identifiers) {emit(doc.identifiers[key]);}}}")
	public List<BaseEntity> findAllByIdentifier(String identifier) {
		return db.queryView(createQuery("all_entities_by_identifier").key(identifier).includeDocs(true), BaseEntity.class);
	}

	@View(name = "all_entities_by_identifier_of_type", map = "function(doc) {if (doc.baseEntityId && doc.identifiers) {for(var key in doc.identifiers) {emit([key, doc.identifiers[key]]);}}}")
	public List<BaseEntity> findAllByIdentifier(String identifierType, String identifier) {
		ComplexKey ckey = ComplexKey.of(identifierType, identifier);
		return queryView("all_entities_by_identifier_of_type", ckey);
	}
}
