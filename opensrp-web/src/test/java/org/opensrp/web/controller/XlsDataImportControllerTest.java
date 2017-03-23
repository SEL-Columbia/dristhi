package org.opensrp.web.controller;

import static org.mockito.MockitoAnnotations.initMocks;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.opensrp.domain.Client;
import org.opensrp.service.ClientService;
import org.opensrp.service.EventService;
import org.opensrp.service.OpenmrsIDService;
import org.opensrp.web.controller.XlsDataImportController;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;

import static org.junit.Assert.assertTrue;

public class XlsDataImportControllerTest {
	@Mock
	ClientService clientService;
	
	@Mock
	EventService eventService;
	
	@Mock
	OpenmrsIDService openmrsIDService;
	
	@Before
	public void setUp() {
		initMocks(this);
	}

	@Test
	public void shouldCreateClientsFromCSVFile() throws IOException, SQLException {
		File csvFile = new File("src/test/java/org/opensrp/fixtures/csv_to_import.csv");
		FileInputStream fileInputStream = new FileInputStream(csvFile);
		MockMultipartFile file = new MockMultipartFile("file", IOUtils.toByteArray(fileInputStream));
		List<String> openmrsIds = new ArrayList<String>();
		openmrsIds.add("12345-1");
		openmrsIds.add("12345-2");
		
		DateTime date = new DateTime();
		
		Client client1 = new Client("1234", "Peter", "", "Lesa", date, null, false, false, "Male", null, null, null);
		Client client2 = new Client("5678", "Mark", "", "Donald", date, null, false, false, "Male", null, null, null);
		
		when(this.openmrsIDService.downloadOpenmrsIds(anyInt())).thenReturn(openmrsIds);
		when(openmrsIDService.assignOpenmrsIdToClient(anyString(), any(Client.class), anyBoolean())).thenReturn(client1, client2);
		
		XlsDataImportController controller = new XlsDataImportController(clientService, eventService, openmrsIDService);
		ResponseEntity<String> response = controller.importXlsData(file);
		assertTrue(response.getBody().contains("4"));
		verify(clientService, times(4)).addClient(any(Client.class));
	}
}
