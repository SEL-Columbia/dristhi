package org.opensrp.repository;

import java.util.List;

import org.opensrp.domain.viewconfiguration.ViewConfiguration;

public interface ViewConfigurationRepository extends BaseRepository<ViewConfiguration> {
	
	List<ViewConfiguration> findAllViewConfigurations();
	
	List<ViewConfiguration> findViewConfigurationsByVersion(Long lastSyncedServerVersion);
	
	List<ViewConfiguration> findByEmptyServerVersion();
}
