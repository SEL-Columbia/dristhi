package org.opensrp.service;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ektorp.CouchDbConnector;
import org.opensrp.common.AllConstants;
import org.opensrp.domain.AppStateToken;
import org.opensrp.form.domain.FormSubmission;
import org.opensrp.form.service.FormSubmissionService;
import org.opensrp.service.formSubmission.FormSubmissionProcessor;
import org.opensrp.util.Utils;
import org.opensrp.util.Utils.DatabaseConnectionParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 
 * @author onamacuser This service fetches existing form submissions data from
 *         couchdb and converts it events and clients
 *
 */
@Service
public class FormSubmissionDataMigrationService {
	protected final Log logger = LogFactory.getLog(getClass());

	@Autowired
	FormSubmissionService formSubmissionService;
	@Autowired
	FormSubmissionProcessor processor;
	@Autowired
	private ConfigService configService;

	/**
	 * Based on the last processed form submission version process any newly
	 * added or updated formsubmissions. This method assumes the data is in the
	 * db the app is currently running on
	 * 
	 * @return
	 */
	public void migrateFormSubmissions() {
		try {
			long lastMigratedFsVersion = getVersion();
			
			//TODO Implement batch processing  
			List<FormSubmission> formSubmissions = formSubmissionService.getAllSubmissions(lastMigratedFsVersion, null);

			if (formSubmissions != null && !formSubmissions.isEmpty()) {
				// break down the submissions to ec model
				for (FormSubmission submission : formSubmissions) {

					processor.processFormSubmission(submission);
					configService.updateAppStateToken(AllConstants.Config.FORM_ENTITY_PARSER_LAST_MIGRATED_FORM_SUBMISSION, submission.serverVersion());
				}

			}
		} catch (Exception e) {
			logger.error("", e);
		}
	}

	/**
	 * This method pulls form submissions from the source db and saves the
	 * broken down submissions (to EC model) to the target db
	 * 
	 * @param sourceDbParams
	 * @param targetDbParams
	 */
	public void migrateFormSubmissions(DatabaseConnectionParams sourceDbParams, DatabaseConnectionParams targetDbParams) {
		try {
			long lastMigratedFsVersion = getVersion();
			CouchDbConnector sourceDb = Utils.connectToDB(sourceDbParams);
			CouchDbConnector targetDb = Utils.connectToDB(targetDbParams);
			
			//TODO Implement batch processing  
			List<FormSubmission> formSubmissions = formSubmissionService.getAllSubmissions(sourceDb, lastMigratedFsVersion, null);
			
			if (formSubmissions != null && !formSubmissions.isEmpty()) {
				// break down the submissions to ec model
				for (FormSubmission submission : formSubmissions) {

					processor.makeModelEntities(targetDb,submission);
					//TODO update target db  
					configService.updateAppStateToken(AllConstants.Config.FORM_ENTITY_PARSER_LAST_MIGRATED_FORM_SUBMISSION, submission.serverVersion());
				}

			}

		} catch (Exception e) {
			logger.error("", e);
		}

	}

	private long getVersion() {
		AppStateToken token = configService.getAppStateTokenByName(AllConstants.Config.FORM_ENTITY_PARSER_LAST_MIGRATED_FORM_SUBMISSION);
		return token == null ? 0L : token.longValue();
	}
}
