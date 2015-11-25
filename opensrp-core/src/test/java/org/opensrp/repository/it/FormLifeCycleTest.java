package org.opensrp.repository.it;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.junit.Assert.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.ektorp.CouchDbConnector;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.motechproject.model.MotechBaseDataObject;
import org.opensrp.api.domain.Client;
import org.opensrp.api.domain.Event;
import org.opensrp.common.AllConstants;
import org.opensrp.form.domain.FormSubmission;
import org.opensrp.scheduler.HealthSchedulerService;
import org.opensrp.service.ClientService;
import org.opensrp.service.EventService;
import org.opensrp.service.formSubmission.FormEntityConverter;
import org.opensrp.service.formSubmission.FormSubmissionProcessor;
import org.opensrp.service.formSubmission.handler.CustomFormSubmissionHandler;
import org.opensrp.service.formSubmission.handler.FormSubmissionRouter;
import org.opensrp.service.formSubmission.handler.HandlerMapper;
import org.opensrp.service.formSubmission.ziggy.EntityDataMap;
import org.opensrp.service.formSubmission.ziggy.ZiggyService;
import org.opensrp.util.TestResourceLoader;
import org.opensrp.util.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.google.gson.Gson;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:test-applicationContext-opensrp.xml")
public class FormLifeCycleTest extends TestResourceLoader{
	public FormLifeCycleTest() throws IOException {
		super();
	}
	
	private List<MotechBaseDataObject> docsToRemove = new ArrayList<>();

	@Autowired
	@Qualifier(AllConstants.OPENSRP_DATABASE_CONNECTOR) 
	private CouchDbConnector db;
	@Autowired
	private FormSubmissionProcessor fsp;
	@Autowired
	private ZiggyService ziggyService;
	@Autowired
	private FormSubmissionRouter formSubmissionRouter;
	@Autowired
	private FormEntityConverter formEntityConverter;
	@Autowired
	private HealthSchedulerService scheduleService;
	@Autowired
	private ClientService clientService;
	@Autowired
	private EventService eventService;
	@Autowired
	private HandlerMapper hmap;
	@Autowired
	private EntityDataMap edmap;
	
	@Before
	public void setup() throws IOException{
		initMocks(this);
		fsp = new FormSubmissionProcessor(getFullPath("/schedule-config.xls"), ziggyService, formSubmissionRouter, 
				formEntityConverter, scheduleService, clientService, eventService);
	}
	
	@Test
	public void shouldCreateClientAndEventAndSchedulesAllDynamic() throws Exception {
		FormSubmission fs = getFormSubmissionFor("child_enrollment",1);
		fsp.processFormSubmission(fs);
		
		assertNotNull(scheduleService.getEnrollment(fs.entityId(), "PENTAVALENT 1"));
		
		fs = getFormSubmissionFor("child_enrollment",2);
		fsp.processFormSubmission(fs);
		
		List<String> asch = scheduleService.findActiveSchedules(fs.entityId());
		assertTrue(asch.isEmpty());
	}
	
	@Test
	public void shouldCreateClientAndEventAndSchedulesWithRouter() throws Exception {
		hmap.addHandler("pnc_1st_registration", new CustomFormSubmissionHandler() {
			@Override
			public void handle(FormSubmission submission) {
				assertEquals("pnc_1st_registration", submission.formName());
				assertEquals("b716d938-1aea-40ae-a081-9ddddddcccc9", submission.entityId());
				assertEquals("admin", submission.anmId());
				assertEquals("demo_mother", submission.bindType());
				System.out.println("I want to call scheduler myself :D");
			}
		});
		FormSubmission fs = getFormSubmissionFor("pnc_1st_registration");
		fsp.processFormSubmission(fs);
		
	}
	
	@Test
	public void shouldCreateClientAndEventAndSchedulesWithZiggy() throws Exception {
		hmap.addHandler("new_household_registration", new CustomFormSubmissionHandler() {
			@Override
			public void handle(FormSubmission submission) {
				assertEquals("new_household_registration", submission.formName());
				assertEquals("b716d938-1aea-40ae-a081-9ddddddcccc9", submission.entityId());
				assertEquals("admin", submission.anmId());
				assertEquals("household", submission.bindType());
				System.out.println("I am Ziggy scheduler");
			}
		});
        edmap.addEntity("household", Client.class);

        FormSubmission fs = getFormSubmissionFor("new_household_registration", 1);
        
        fsp.processFormSubmission(fs);
	}

	
}
