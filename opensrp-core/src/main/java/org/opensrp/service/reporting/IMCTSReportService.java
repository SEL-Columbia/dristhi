package org.opensrp.service.reporting;

import org.opensrp.form.domain.FormSubmission;

public interface IMCTSReportService {
	
	public void reportFor(FormSubmission formSubmission) throws Exception;

}
