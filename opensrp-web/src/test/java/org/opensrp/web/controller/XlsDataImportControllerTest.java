package org.opensrp.web.controller;

import static org.mockito.MockitoAnnotations.initMocks;
import static org.mockito.Matchers.any;
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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.opensrp.domain.Client;
import org.opensrp.domain.Event;
import org.opensrp.service.ClientService;
import org.opensrp.service.EventService;
import org.opensrp.service.OpenmrsIDService;
import org.opensrp.web.controller.XlsDataImportController;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

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
	public void shouldCreateClientsFromCSVFile() throws IOException, SQLException, JSONException {
		File csvFile = new File("src/test/java/org/opensrp/fixtures/csv_to_import.csv");
		FileInputStream fileInputStream = new FileInputStream(csvFile);
		MockMultipartFile file = new MockMultipartFile("file", IOUtils.toByteArray(fileInputStream));
		List<String> openmrsIds = new ArrayList<String>();
		openmrsIds.add("12345-1");
		openmrsIds.add("12345-2");
		

		when(this.openmrsIDService.downloadOpenmrsIds(anyInt())).thenReturn(openmrsIds);
		
		XlsDataImportController controller = new XlsDataImportController(clientService, eventService, openmrsIDService);
		ResponseEntity<String> response = controller.importXlsData(file);
		String responseBody = response.getBody();
		JSONObject responseJson = new JSONObject(responseBody);
		
		int summaryClientCount = responseJson.getInt("summary_client_count");
		int summaryEventCount = responseJson.getInt("summary_event_count");
		
		assertEquals(summaryClientCount, 4);
		assertEquals(summaryEventCount, 26);
		verify(clientService, times(4)).addClient(any(Client.class));
		verify(eventService, times(26)).addEvent(any(Event.class));
	}
}
