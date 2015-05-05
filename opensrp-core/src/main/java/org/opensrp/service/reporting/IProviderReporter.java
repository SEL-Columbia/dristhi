package org.opensrp.service.reporting;

import java.util.List;

import org.opensrp.common.domain.ANMReport;

public interface IProviderReporter {
	
	public void processReports(List<ANMReport> reports);
	public void reportFromEntityData();
}
