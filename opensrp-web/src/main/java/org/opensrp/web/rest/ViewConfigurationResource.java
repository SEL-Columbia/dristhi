package org.opensrp.web.rest;

import static org.opensrp.web.rest.RestUtils.getStringFilter;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.opensrp.common.AllConstants.BaseEntity;
import org.opensrp.domain.viewconfiguration.ViewConfiguration;
import org.opensrp.service.ViewConfigurationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value = "/rest/viewconfiguration")
public class ViewConfigurationResource {
	
	private ViewConfigurationService viewConfigurationService;
	
	@Autowired
	public void setViewConfigurationService(ViewConfigurationService viewConfigurationService) {
		this.viewConfigurationService = viewConfigurationService;
	}
	
	@RequestMapping(method = RequestMethod.GET, value = "/sync")
	@ResponseBody
	public List<ViewConfiguration> findViewConfigurationsByVersion(HttpServletRequest request) {
		String serverVersion = getStringFilter(BaseEntity.SERVER_VERSIOIN, request);
		Long lastSyncedServerVersion = null;
		if (serverVersion != null) {
			lastSyncedServerVersion = Long.valueOf(serverVersion) + 1;
		}
		return viewConfigurationService.findViewConfigurationsByVersion(lastSyncedServerVersion);
	}
}
