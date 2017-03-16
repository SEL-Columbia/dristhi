package org.opensrp.web.controller;

import static org.mockito.MockitoAnnotations.initMocks;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.opensrp.service.ClientService;
import org.opensrp.service.EventService;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;

import static org.junit.Assert.assertTrue;

public class XlsDataImportControllerTest {
	@Mock
	ClientService clientService;
	
	@Mock
	EventService eventService;
	
	@Before
	public void setUp() {
		initMocks(this);
	}
	
	@Test
	public void shouldCreateClientsFromCSVFile() throws IOException {
		File csvFile = new File("src/test/java/org/opensrp/fixtures/csv_to_import.csv");
		FileInputStream fileInputStream = new FileInputStream(csvFile);
		MockMultipartFile file = new MockMultipartFile("file", IOUtils.toByteArray(fileInputStream));
		
		XlsDataImportController controller = new XlsDataImportController(clientService, eventService);
		ResponseEntity<String> response = controller.importXlsData(file);
		assertTrue(response.getBody().contains("4"));
	}
}
