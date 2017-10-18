package org.opensrp.service;

import java.util.List;

import org.opensrp.domain.ViewConfiguration;
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

	public List<ViewConfiguration> findAllByOrganizationAndIdentifier(String organization) {
		return viewConfigurationRepository.findAllByOrganizationAndIdentifier(organization);
	}

}