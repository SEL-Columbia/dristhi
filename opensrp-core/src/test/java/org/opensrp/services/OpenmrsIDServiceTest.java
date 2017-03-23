package org.opensrp.services;

import java.sql.SQLException;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Test;
import org.opensrp.SpringApplicationContextProvider;
import org.opensrp.domain.Client;
import org.opensrp.service.OpenmrsIDService;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertNotNull;;

public class OpenmrsIDServiceTest extends SpringApplicationContextProvider {
	@Autowired
	OpenmrsIDService openmrsIDService;
	
	@After
	public void tearDown() {
		openmrsIDService.clearRecords(true);
	}
	
	@Test
	public void testDownloadOpenmrsIds() throws SQLException {
		DateTime dateOfBirth = new DateTime();
		Client client = new Client("12345", "First", "", "Last", dateOfBirth, null, false, false, "Male", null, null, null);
		
		Client updatedClient = openmrsIDService.assignOpenmrsIdToClient("12345-1", client, true);
		assertNotNull(updatedClient.getIdentifier(OpenmrsIDService.ZEIR_IDENTIFIER));
	}
}
