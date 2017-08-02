package org.opensrp.service.formSubmission;

import org.ektorp.CouchDbConnector;
import org.ektorp.CouchDbInstance;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.opensrp.BaseIntegrationTest;
import org.opensrp.common.AllConstants;
import org.opensrp.domain.AppStateToken;
import org.opensrp.form.domain.FormSubmission;
import org.opensrp.form.repository.AllFormSubmissions;
import org.opensrp.repository.AllAppStateTokens;
import org.opensrp.service.FormSubmissionDataMigrationService;
import org.opensrp.util.Utils;
import org.opensrp.util.Utils.DatabaseConnectionParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.net.MalformedURLException;
import java.util.List;

public class FormSubmissionDataMigrationIntegrationTest extends BaseIntegrationTest{
	@Autowired
	FormSubmissionDataMigrationService dataMigrationService;
	@Autowired
	AllAppStateTokens allAppStateTokens;
	@Autowired
    private FormSubmissionProcessor fsp;
	@Autowired
    AllFormSubmissions allFormSubmissions;

	StubAllFormSubmission stubAllFormSubmission;

	DatabaseConnectionParams sourceDb = new Utils.DatabaseConnectionParams();
	DatabaseConnectionParams targetDb = new Utils.DatabaseConnectionParams();

    @Value("#{opensrp['couchdb.username']}")
    String username;
    @Value("#{opensrp['couchdb.password']}")
    String password;

	@Before
	public void oneTimeSetUp() throws MalformedURLException {
		sourceDb.password = password;
		sourceDb.dbName = "opensrp-form-test";
		sourceDb.portNumber = "5984";
		sourceDb.url = "http://localhost";
		sourceDb.userName = username;



		targetDb.password = password;
		targetDb.dbName = "opensrp-form-test-targetdb";
		targetDb.portNumber = "5984";
		targetDb.url = "http://localhost";
		targetDb.userName = username;

        stubAllFormSubmission = new StubAllFormSubmission(Utils.connectToDB(sourceDb));
        new StubAllAppStateToken(Utils.connectToDB(sourceDb));
        new StubAllFormSubmission(Utils.connectToDB(targetDb));
        new StubAllAppStateToken(Utils.connectToDB(targetDb));
	}

	@After
	public void oneTimeTearDown() throws MalformedURLException {
        CouchDbInstance couchDbInstance = Utils.getDbInstance(sourceDb);
        couchDbInstance.deleteDatabase(sourceDb.dbName);
        couchDbInstance.deleteDatabase(targetDb.dbName);
		targetDb = null;
		sourceDb = null;

	}


	@Test
	public void shouldMigrateFormSubmissions() throws Exception {
        FormSubmission fs = getFormSubmissionFor("new_household_registration", 1);

        allFormSubmissions.add(fs);

        dataMigrationService.migrateFormSubmissions();
		List<AppStateToken> ol = allAppStateTokens.findByName(AllConstants.Config.FORM_ENTITY_PARSER_LAST_MIGRATED_FORM_SUBMISSION.name());

		Assert.assertTrue("AppStateToken shouldn't be empty after a successful migration", !ol.isEmpty());
		Assert.assertTrue("AppStateToken should be greater than 0 all the times after a successful migration", ol.get(0).longValue() > 0);


	}

	

	@Test
	public void shouldMigrateFormSubmissionsAndPopulateTargetDB() throws Exception {
        FormSubmission fs = getFormSubmissionFor("new_household_registration", 1);


        stubAllFormSubmission.add(fs);

        dataMigrationService.migrateFormSubmissions(sourceDb, targetDb);

		List<AppStateToken> ol = allAppStateTokens.findByName(Utils.connectToDB(sourceDb), AllConstants.Config.FORM_ENTITY_PARSER_LAST_MIGRATED_FORM_SUBMISSION.name());

		Assert.assertTrue("AppStateToken shouldn't be empty after a successful migration", !ol.isEmpty());
		Assert.assertTrue("AppStateToken should be greater than 0 all the times after a successful migration", ol.get(0).longValue() > 0);

	}


}
