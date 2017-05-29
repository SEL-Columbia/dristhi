package org.opensrp.service.formSubmission;

import java.util.List;

import org.ektorp.CouchDbConnector;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.opensrp.BaseIntegrationTest;
import org.opensrp.common.AllConstants;
import org.opensrp.domain.AppStateToken;
import org.opensrp.repository.AllAppStateTokens;
import org.opensrp.service.FormSubmissionDataMigrationService;
import org.opensrp.util.Utils;
import org.opensrp.util.Utils.DatabaseConnectionParams;
import org.springframework.beans.factory.annotation.Autowired;

public class FormSubmissionDataMigrationIntegrationTest  {
	@Autowired
	FormSubmissionDataMigrationService dataMigrationService;
	@Autowired
	AllAppStateTokens allAppStateTokens;

	DatabaseConnectionParams sourceDb = new Utils.DatabaseConnectionParams();
	DatabaseConnectionParams targetDb = new Utils.DatabaseConnectionParams();

	@Before
	public void oneTimeSetUp() {
		sourceDb.password = "";
		sourceDb.dbName = "opensrp-form-test";
		sourceDb.portNumber = "5984";
		sourceDb.url = "http://localhost";
		sourceDb.userName = "";

		targetDb.password = "";
		targetDb.dbName = "opensrp-form-test-targetdb";
		targetDb.portNumber = "5984";
		targetDb.url = "http://localhost";
		targetDb.userName = "";
	}

	@After
	public void oneTimeTearDown() {
		targetDb = null;
		sourceDb = null;
	}

	@Test
	@Ignore //FIXME
	public void shouldMigrateFormSubmissions() throws Exception {
		dataMigrationService.migrateFormSubmissions();
		List<AppStateToken> ol = allAppStateTokens.findByName(AllConstants.Config.FORM_ENTITY_PARSER_LAST_MIGRATED_FORM_SUBMISSION.name());

		Assert.assertTrue("AppStateToken shouldn't be empty after a successful migration", !ol.isEmpty());
		Assert.assertTrue("AppStateToken should be greater than 0 all the times after a successful migration", ol.get(0).longValue() > 0);
	}

	

	@Test
	@Ignore//FIXME
	public void shouldMigrateFormSubmissionsAndPopulateTargetDB() throws Exception {

		dataMigrationService.migrateFormSubmissions(sourceDb, targetDb);
		
		CouchDbConnector db = Utils.connectToDB(sourceDb);
		List<AppStateToken> ol = allAppStateTokens.findByName(db,AllConstants.Config.FORM_ENTITY_PARSER_LAST_MIGRATED_FORM_SUBMISSION.name());

		Assert.assertTrue("AppStateToken shouldn't be empty after a successful migration", !ol.isEmpty());
		Assert.assertTrue("AppStateToken should be greater than 0 all the times after a successful migration", ol.get(0).longValue() > 0);
	}


}
