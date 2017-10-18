package org.opensrp.service;

import java.util.List;

import org.opensrp.domain.viewconfiguration.ViewConfiguration;
import org.opensrp.repository.ViewConfigurationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ViewConfigurationService {

	private ViewConfigurationRepository viewConfigurationRepository;

	@Autowired
	public void setViewConfigurationRepository(ViewConfigurationRepository viewConfigurationRepository) {
		this.viewConfigurationRepository = viewConfigurationRepository;
	}

	public List<ViewConfiguration> findAllViewConfigurations() {
		return viewConfigurationRepository.findAllViewConfigurations();
	}

}