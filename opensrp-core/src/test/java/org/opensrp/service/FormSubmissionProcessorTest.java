package org.opensrp.service;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.opensrp.domain.Client;
import org.opensrp.form.domain.FormSubmission;
import org.opensrp.form.service.FormAttributeParser;
import org.opensrp.scheduler.HealthSchedulerService;
import org.opensrp.service.formSubmission.FormEntityConverter;
import org.opensrp.service.formSubmission.FormSubmissionProcessor;
import org.opensrp.service.formSubmission.handler.FormSubmissionRouter;
import org.opensrp.service.formSubmission.ziggy.ZiggyService;
import org.opensrp.util.TestResourceLoader;

public class FormSubmissionProcessorTest extends TestResourceLoader{

	public FormSubmissionProcessorTest() throws IOException {
		super();
	}

	@Mock
	private FormSubmissionProcessor fsp;
	@Mock
	private ZiggyService ziggyService;
	@Mock
	private FormSubmissionRouter formSubmissionRouter;
	@Mock
	private FormEntityConverter formEntityConverter;
	@Mock
	private HealthSchedulerService scheduleService;
	@Mock
	private ClientService clientService;
	@Mock
	private EventService eventService;
	
	@Before
	public void setup() throws IOException{
		initMocks(this);
		FormEntityConverter fec = new FormEntityConverter(new FormAttributeParser("/form"));
		fsp = new FormSubmissionProcessor(ziggyService, formSubmissionRouter, 
				fec, scheduleService, clientService, eventService);
		
	}
	
	@Test
	public void testFormSubmission() throws Exception{
		doNothing().when(clientService).addClient(any(Client.class));
		FormSubmission submission = getFormSubmissionFor("pnc_reg_form");
		fsp.processFormSubmission(submission);
	}
}
