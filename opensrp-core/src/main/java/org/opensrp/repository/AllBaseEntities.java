package org.opensrp.repository;

import java.util.List;

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
	protected AllBaseEntities(
			@Qualifier(AllConstants.OPENSRP_DATABASE_CONNECTOR) CouchDbConnector db) {
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

	@View(name = "all_entities", map = "function(doc) { if (doc.type === 'BaseEntity') { emit(doc.id); } }")
	public List<BaseEntity> findAllBaseEntities() {
		return db.queryView(createQuery("all_entities").includeDocs(true),
				BaseEntity.class);
	}

	@View(name = "all_entities_by_IDs", map = "function(doc) { if (doc.type === 'BaseEntity' && doc.id) { emit(doc.id); } }")
	public List<BaseEntity> findAll(List<String> Ids) {
		return db.queryView(createQuery("all_entities_by_IDs").keys(Ids)
				.includeDocs(true), BaseEntity.class);
	}

	@View(name = "all_entities_by_IDs", map = "function(doc) { if (doc.type === 'BaseEntity' && doc.id) { emit(doc.id); } }")
	public List<BaseEntity> findByBaseEntityIds(List<String> Ids) {
		return db.queryView(createQuery("all_entities_by_IDs").keys(Ids)
				.includeDocs(true), BaseEntity.class);
	}
}
