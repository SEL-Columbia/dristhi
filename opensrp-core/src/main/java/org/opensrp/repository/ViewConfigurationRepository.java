package org.opensrp.repository;

import java.util.List;

import org.ektorp.CouchDbConnector;
import org.ektorp.support.CouchDbRepositorySupport;
import org.ektorp.support.View;
import org.opensrp.common.AllConstants;
import org.opensrp.domain.ViewConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

@Repository
public class ViewConfigurationRepository extends CouchDbRepositorySupport<ViewConfiguration> {

	@Autowired
	protected ViewConfigurationRepository(@Qualifier(AllConstants.OPENSRP_DATABASE_CONNECTOR) CouchDbConnector db) {
		super(ViewConfiguration.class,  db);
		initStandardDesignDocument();
	}

	@View(name = "all_view_configuration_by_organization", map = "function(doc) { if (doc.type==='ViewConfiguration' && doc.organization) { emit(doc.organization);}}")
	public List<ViewConfiguration> findAllByOrganizationAndIdentifier(String organization) {
		return db.queryView(createQuery("all_view_configuration_by_organization").key(organization).includeDocs(true),
				ViewConfiguration.class);
	}

}
