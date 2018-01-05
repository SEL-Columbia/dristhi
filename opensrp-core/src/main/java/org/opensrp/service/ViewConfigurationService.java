package org.opensrp.service;

import java.util.List;

import org.opensrp.domain.viewconfiguration.ViewConfiguration;
import org.opensrp.repository.ViewConfigurationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ViewConfigurationService {
	
	private static Logger logger = LoggerFactory.getLogger(ViewConfigurationService.class.toString());
	
	private ViewConfigurationRepository viewConfigurationRepository;
	
	@Autowired
	public void setViewConfigurationRepository(ViewConfigurationRepository viewConfigurationRepository) {
		this.viewConfigurationRepository = viewConfigurationRepository;
	}
	
	public List<ViewConfiguration> findViewConfigurationsByVersion(Long lastSyncedServerVersion) {
		return viewConfigurationRepository.findViewConfigurationsByVersion(lastSyncedServerVersion);
	}
	
	public void addServerVersion() {
		try {
			List<ViewConfiguration> viewConfigurations = viewConfigurationRepository.findByEmptyServerVersion();
			logger.info("RUNNING addServerVersion viewConfigurations size: " + viewConfigurations.size());
			long currentTimeMillis = System.currentTimeMillis();
			for (ViewConfiguration viewConfiguration : viewConfigurations) {
				try {
					Thread.sleep(1);
					viewConfiguration.setServerVersion(currentTimeMillis);
					viewConfigurationRepository.update(viewConfiguration);
					currentTimeMillis += 1;
				}
				catch (InterruptedException e) {
					logger.error(e.getMessage());
				}
			}
		}
		catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}
	
}
