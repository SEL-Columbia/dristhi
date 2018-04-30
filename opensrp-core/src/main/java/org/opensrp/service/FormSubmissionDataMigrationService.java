package org.opensrp.service;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ektorp.CouchDbConnector;
import org.json.JSONException;
import org.opensrp.common.AllConstants;
import org.opensrp.domain.AppStateToken;
import org.opensrp.form.domain.FormSubmission;
import org.opensrp.form.service.FormSubmissionService;
import org.opensrp.repository.couch.AllAppStateTokens;
import org.opensrp.service.formSubmission.FormSubmissionProcessor;
import org.opensrp.util.Utils;
import org.opensrp.util.Utils.DatabaseConnectionParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.mysql.jdbc.StringUtils;

/**
 * @author onamacuser This service fetches existing form submissions data from couchdb and converts
 *         them to events and clients
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
	
	@Autowired
	@Qualifier("couchAppStateTokensRepository")
	private AllAppStateTokens allAppStateTokens;
	
	private volatile int BATCH_SIZE = 100;
	
	//private static final int THREADS_COUNT = 5;
	
	private enum MigrationType {
		TO_LOCAL_DB, TO_REMOTE_DB
	}
	
	/**
	 * Based on the last processed form submission version process any newly added or updated
	 * formsubmissions. This method assumes the data is in the db the app is currently running on
	 * 
	 * @return
	 */
	public void migrateFormSubmissions() {
		//ExecutorService executor = Executors.newFixedThreadPool(THREADS_COUNT);
		
		try {
			configService.registerAppStateToken(AllConstants.Config.FORM_ENTITY_PARSER_LAST_MIGRATED_FORM_SUBMISSION, 0,
			    "Token to keep track of forms processed for client n event parsing and schedule handling", true);
			
			boolean processSubmissions = true;
			
			while (processSubmissions) {
				long lastMigratedFsVersion = getVersion();
				
				List<FormSubmission> formSubmissions = formSubmissionService.getAllSubmissions(lastMigratedFsVersion,
				    BATCH_SIZE);
				processSubmissions = formSubmissions != null && !formSubmissions.isEmpty();
				
				if (processSubmissions) {
					
					Runnable migrateFormSubmissionsTask = new MigrateFormSubmissionsTask(MigrationType.TO_LOCAL_DB,
					        formSubmissions);
					//executor.execute(migrateFormSubmissionsTask);
					migrateFormSubmissionsTask.run();
				}
			}
			// This will make the executor accept no new threads
			// and finish all existing threads in the queue
			//executor.shutdown();
		}
		catch (Exception e) {
			logger.error("", e);
			//executor.shutdown();
		}
	}
	
	/**
	 * This method pulls form submissions from the source db and saves the broken down submissions
	 * (to EC model) to the target db
	 * 
	 * @param sourceDbParams
	 * @param targetDbParams
	 */
	public void migrateFormSubmissions(DatabaseConnectionParams sourceDbParams, DatabaseConnectionParams targetDbParams) {
		//ExecutorService executor = Executors.newFixedThreadPool(THREADS_COUNT);
		
		try {
			
			CouchDbConnector sourceDb = Utils.connectToDB(sourceDbParams);
			CouchDbConnector targetDb = Utils.connectToDB(targetDbParams);
			
			registerAppStateToken(sourceDb,
			    AllConstants.Config.FORM_ENTITY_PARSER_LAST_MIGRATED_FORM_SUBMISSION, 0,
			    "Token to keep track of forms processed for client n event parsing and schedule handling", true);
			
			boolean processSubmissions = true;
			
			while (processSubmissions) {
				long lastMigratedFsVersion = getVersion(sourceDb);
				List<FormSubmission> formSubmissions = formSubmissionService.getAllSubmissions(sourceDb,
				    lastMigratedFsVersion, BATCH_SIZE);
				processSubmissions = formSubmissions != null && !formSubmissions.isEmpty();
				
				if (processSubmissions) {
					
					Runnable migrateFormSubmissionsTask = new MigrateFormSubmissionsTask(sourceDb, targetDb,
					        MigrationType.TO_REMOTE_DB, formSubmissions);
					//executor.execute(migrateFormSubmissionsTask);
					migrateFormSubmissionsTask.run();
				}
			}
			// This will make the executor accept no new threads
			// and finish all existing threads in the queue
			//executor.shutdown();
			
		}
		catch (Exception e) {
			logger.error("", e);
			//executor.shutdown();
		}
		
	}
	
	private long getVersion() {
		AppStateToken token = configService
		        .getAppStateTokenByName(AllConstants.Config.FORM_ENTITY_PARSER_LAST_MIGRATED_FORM_SUBMISSION);
		return token == null ? 0L : token.longValue();
	}
	
	/**
	 * Gets appstatetoken from the specified database
	 * 
	 * @param db
	 * @param tokenName
	 * @return AppStateToken with given name. Since model is supposed to keep track of system`s
	 *         state at any given time it throws IllegalStateException incase multiple Tokens found
	 *         with same name.
	 */
	public AppStateToken getAppStateTokenByName(CouchDbConnector db, Enum<?> tokenName) {
		List<AppStateToken> ol = allAppStateTokens.findByName(db, tokenName.name());
		if (ol.size() > 1) {
			throw new IllegalStateException("System was found to have multiple token with same name (" + tokenName.name()
			        + "). This can lead to potential critical inconsistencies.");
		}
		
		return ol.size() == 0 ? null : ol.get(0);
	}
	

	public void updateAppStateToken(CouchDbConnector db,Enum<?> tokenName, Object value) {
		List<AppStateToken> ol = allAppStateTokens.findByName(db,tokenName.name());
		if(ol.size() > 1){
			throw new IllegalStateException("System was found to have multiple token with same name ("+tokenName.name()+"). This can lead to potential critical inconsistencies.");
		}
		
		if(ol.size() == 0){
			throw new IllegalStateException("Property with name ("+tokenName.name()+") not found.");
		}
		
		AppStateToken ast = ol.get(0);
		ast.setValue(value);
		ast.setLastEditDate(System.currentTimeMillis());
		db.update(ast);
	}
	
	/**
	 * Registers a new token to manage the specified variable state (by token name) of App. The token is registered in the specified db
	 * @param db
	 * @param tokenName
	 * @param defaultValue
	 * @param description
	 * @param suppressExceptionIfExists
	 * @return
	 */
	public AppStateToken registerAppStateToken(CouchDbConnector db,Enum<?> tokenName, Object defaultValue, String description, boolean suppressExceptionIfExists) {
		if(tokenName == null || StringUtils.isEmptyOrWhitespaceOnly(description)){
			throw new IllegalArgumentException("Token name and description must be provided");
		}
		
		List<AppStateToken> atl = allAppStateTokens.findByName(db,tokenName.name());
		if(atl.size() > 0){
			if(!suppressExceptionIfExists){
				throw new IllegalArgumentException("Token with given name ("+tokenName.name()+") already exists.");
			}
			return atl.get(0);
		}
		
		AppStateToken token = new AppStateToken(tokenName.name(), defaultValue, 0L, description);
		allAppStateTokens.add(db,token);
		return token;
	}
	
	private long getVersion(CouchDbConnector db) {
		AppStateToken token = getAppStateTokenByName(db,
		    AllConstants.Config.FORM_ENTITY_PARSER_LAST_MIGRATED_FORM_SUBMISSION);
		return token == null ? 0L : token.longValue();
	}
	
	/**
	 * Worker thread to break down formsubmissions to ec data.
	 * 
	 * @author onamacuser
	 */
	class MigrateFormSubmissionsTask implements Runnable {
		
		MigrationType type;
		
		List<FormSubmission> formSubmissions;
		
		CouchDbConnector sourceDb;
		
		CouchDbConnector targetDb;
		
		MigrateFormSubmissionsTask(MigrationType _type, List<FormSubmission> _formSubmissions) {
			type = _type;
			formSubmissions = _formSubmissions;
		}
		
		MigrateFormSubmissionsTask(CouchDbConnector _sourceDb, CouchDbConnector _targetDb, MigrationType _type,
		    List<FormSubmission> _formSubmissions) {
			type = _type;
			formSubmissions = _formSubmissions;
			sourceDb = _sourceDb;
			targetDb = _targetDb;
		}
		
		@Override
		public void run() {
			switch (type) {
				case TO_LOCAL_DB:
					
					try {
						// break down the submissions to ec model
						for (FormSubmission submission : formSubmissions) {
							
							configService.updateAppStateToken(
							    AllConstants.Config.FORM_ENTITY_PARSER_LAST_MIGRATED_FORM_SUBMISSION,
							    submission.serverVersion());
							processor.makeModelEntities(submission);
						}
					}
					catch (Exception e) {
						logger.error("", e);
						
					}
					break;
				
				case TO_REMOTE_DB:
					
					try {
						// break down the submissions to ec model
						for (FormSubmission submission : formSubmissions) {
							updateAppStateToken(sourceDb,
							    AllConstants.Config.FORM_ENTITY_PARSER_LAST_MIGRATED_FORM_SUBMISSION,
							    submission.serverVersion());
							processor.makeModelEntities(targetDb, submission);
							
						}
					}
					catch (JSONException e) {
						logger.error("", e);
						
					}
					break;
				default:
					logger.debug("Unknown migration type");
					break;
				
			}
		}
		
	}
}
