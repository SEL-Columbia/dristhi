package org.opensrp.repository.couch;

import java.util.List;

import org.ektorp.CouchDbConnector;
import org.ektorp.support.GenerateView;
import org.ektorp.support.View;
import org.motechproject.dao.MotechBaseRepository;
import org.opensrp.common.AllConstants;
import org.opensrp.domain.Provider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

@Repository
public class AllProviders extends MotechBaseRepository<Provider> {

	@Autowired
	protected AllProviders(
			@Qualifier(AllConstants.OPENSRP_DATABASE_CONNECTOR) CouchDbConnector db) {
		super(Provider.class, db);
	}

	@GenerateView
	public Provider findByBaseEntityId(String baseEntityId) {
		List<Provider> providers = queryView("by_baseEntityId", baseEntityId);
		if (providers == null || providers.isEmpty()) {
			return null;
		}
		return providers.get(0);
	}

	public boolean exists(String baseEntityId) {
		return findByBaseEntityId(baseEntityId) != null;
	}
	
	@View(name = "all_providers", map = "function(doc) { if (doc.type === 'Provider') { emit(doc.baseEntityId); } }")
	public List<Provider> findAllProviders() {
		return db.queryView(createQuery("all_providers").includeDocs(true),
				Provider.class);
	}

	@View(name = "all_providers_by_baseEntityIDs", map = "function(doc) { if (doc.type === 'Provider' && doc.baseEntityId) { emit(doc.baseEntityId); } }")
	public List<Provider> findAllProviderByIds(List<String> Ids) {
		return db.queryView(createQuery("all_providers_by_baseEntityIDs").keys(Ids)
				.includeDocs(true), Provider.class);
	}
}
