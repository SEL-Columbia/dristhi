package org.opensrp.repository;

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
	public Provider findByCaseId(String caseId) {
		List<Provider> providers = queryView("by_caseId", caseId);
		if (providers == null || providers.isEmpty()) {
			return null;
		}
		return providers.get(0);
	}

	public boolean exists(String caseId) {
		return findByCaseId(caseId) != null;
	}
	
	@View(name = "all_providers", map = "function(doc) { if (doc.type === 'Provider') { emit(doc.caseId); } }")
	public List<Provider> findAllProviders() {
		return db.queryView(createQuery("all_providers").includeDocs(true),
				Provider.class);
	}

	@View(name = "all_users_by_CaseIDs", map = "function(doc) { if (doc.type === 'Provider' && doc.caseId) { emit(doc.caseId); } }")
	public List<Provider> findAll(List<String> ecIds) {
		return db.queryView(createQuery("all_users_by_CaseIDs").keys(ecIds)
				.includeDocs(true), Provider.class);
	}
}
