package org.opensrp.web.controller;
import java.io.IOException;
import java.net.URISyntaxException;

import org.junit.Test;
import org.mockito.Mock;
import org.springframework.mock.web.MockHttpServletResponse;

public class FormDownloadTest 
{
	@Mock
	MockHttpServletResponse resp = new MockHttpServletResponse();
	
	@Test
	public void shouldReturnAvailableVersion() throws URISyntaxException, IOException
	{
        String filename = "form/";

		FormDownLoadController formDownLoadController = new FormDownLoadController(filename, "model.xml,form.xml,form_definition.json");
		
		String versions = formDownLoadController.getAllAvailableVersion();
		System.out.println("available version:::"+versions);
	}	
}
