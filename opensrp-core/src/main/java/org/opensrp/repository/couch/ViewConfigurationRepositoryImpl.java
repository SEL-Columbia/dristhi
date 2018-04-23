package org.opensrp.repository.couch;

import java.util.List;

import org.ektorp.ComplexKey;
import org.ektorp.CouchDbConnector;
import org.ektorp.support.CouchDbRepositorySupport;
import org.ektorp.support.View;
import org.opensrp.common.AllConstants;
import org.opensrp.domain.viewconfiguration.ViewConfiguration;
import org.opensrp.repository.ViewConfigurationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

@Repository("couchViewConfigurationRepository")
@Primary
public class ViewConfigurationRepositoryImpl extends CouchDbRepositorySupport<ViewConfiguration> implements ViewConfigurationRepository {
	
	@Autowired
	protected ViewConfigurationRepositoryImpl(@Qualifier(AllConstants.OPENSRP_DATABASE_CONNECTOR) CouchDbConnector db) {
		super(ViewConfiguration.class, db);
		initStandardDesignDocument();
	}
	
	@View(name = "all_view_configurations", map = "function(doc) { if (doc.type==='ViewConfiguration') { emit(doc.identifier); }}")
	public List<ViewConfiguration> findAllViewConfigurations() {
		return db.queryView(createQuery("all_view_configurations").includeDocs(true), ViewConfiguration.class);
	}
	
	@View(name = "view_configurations_by_version", map = "function(doc) { if (doc.type==='ViewConfiguration') { emit([doc.serverVersion], null); }}")
	public List<ViewConfiguration> findViewConfigurationsByVersion(Long lastSyncedServerVersion) {
		ComplexKey startKey = ComplexKey.of(lastSyncedServerVersion);
		ComplexKey endKey = ComplexKey.of(Long.MAX_VALUE);
		return db.queryView(
		    createQuery("view_configurations_by_version").includeDocs(true).startKey(startKey).endKey(endKey),
		    ViewConfiguration.class);
	}
	
	/**
	 * Get all ViewConfigurations without a server version
	 *
	 * @return view configuration
	 */
	@View(name = "view_configurations_by_empty_server_version", map = "function(doc) { if ( doc.type == 'ViewConfiguration' && !doc.serverVersion) { emit(doc._id, doc); } }")
	public List<ViewConfiguration> findByEmptyServerVersion() {
		return db.queryView(createQuery("view_configurations_by_empty_server_version").limit(200).includeDocs(true),
		    ViewConfiguration.class);
	}
	
	@Override
	public void safeRemove(ViewConfiguration entity) {
		remove(entity);
	}
	
}
