package org.opensrp.service.formSubmission;

import org.junit.Assert;
import org.junit.Test;
import org.opensrp.BaseIntegrationTest;
import org.opensrp.service.FormSubmissionDataMigrationService;
import org.springframework.beans.factory.annotation.Autowired;

public class FormSubmissionDataMigrationIntegrationTest extends BaseIntegrationTest {
	@Autowired
	FormSubmissionDataMigrationService dataMigrationService;

	@Test
	public void shouldMigrateFormSubmissions() throws Exception {
		//dataMigrationService.migrateFormSubmissions();
		Assert.fail("implement this");
	}
	
	@Test
	public void migrateFormSubmissionsShouldUpdatedAppStateToken() throws Exception {
		//dataMigrationService.migrateFormSubmissions();
		Assert.fail("implement this");
	}
	
	@Test
	public void shouldMigrateFormSubmissionsAndPopulateTargetDB() throws Exception {
		//dataMigrationService.migrateFormSubmissions(null,null);
		Assert.fail("implement this");
	}
	
	@Test
	public void migrateFormSubmissionsAndPopulateTargetDBShouldUpdateAppStateToken() throws Exception {
		//dataMigrationService.migrateFormSubmissions(null,null);
		Assert.fail("implement this");
	}

}
