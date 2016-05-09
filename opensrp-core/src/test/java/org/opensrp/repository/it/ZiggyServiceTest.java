package org.opensrp.repository.it;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.opensrp.form.domain.FormSubmission;
import org.opensrp.form.service.FormSubmissionService;
import org.opensrp.service.formSubmission.handler.CustomFormSubmissionHandler;
import org.opensrp.service.formSubmission.handler.FormSubmissionRouter;
import org.opensrp.service.formSubmission.handler.HandlerMapper;
import org.opensrp.service.formSubmission.ziggy.EntityDataMap;
import org.opensrp.service.formSubmission.ziggy.ZiggyDataHandler;
import org.opensrp.service.formSubmission.ziggy.ZiggyFileLoader;
import org.opensrp.service.formSubmission.ziggy.ZiggyService;
import org.opensrp.util.TestResourceLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.google.gson.Gson;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:test-applicationContext-opensrp.xml")
public class ZiggyServiceTest extends TestResourceLoader{
    public ZiggyServiceTest() throws IOException {
		super();
	}

	@Mock
    private ZiggyDataHandler ziggyDataHandler;
	@Autowired
	private EntityDataMap edmap;

    @Autowired
    @Value("#{opensrp['form.directory.name']}")
    private String formFilePath;

    @Autowired
    @Value("#{opensrp['js.directory.name']}")
    private String jsDirectoryName;
    
    @Mock
    private FormSubmissionService formSubmissionService;

    private ZiggyFileLoader ziggyFileLoader;
    
    @Mock
	private HandlerMapper handlerMapper;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        ziggyFileLoader = new ZiggyFileLoader(jsDirectoryName, formFilePath);
    }

    @Test
    public void test() throws Exception {
        String params = "{\"instanceId\":\"88c0e824-10b4-44c2-9429-754b8d823776\", \"anmId\":\"demotest\", \"entityId\":\"a3f2abf4-2699-4761-819a-cea739224164\",\"formName\":\"new_household_registration\",\"clientVersion\":\"1430997074596\", \"serverVersion\": \"1430998001293\"}";
        FormSubmission fs = getFormSubmissionFor("new_household_registration",1);
        String formInstance = new Gson().toJson(fs.instance());

        when(formSubmissionService.findByInstanceId("88c0e824-10b4-44c2-9429-754b8d823776")).thenReturn(fs);
        when(ziggyDataHandler.saveEntity(eq("household"), any(String.class))).thenReturn("a3f2abf4-2699-4761-819a-cea739224164");
        
        handlerMapper = new HandlerMapper().addCustomFormSubmissionHandler("new_household_registration", 
        		new CustomFormSubmissionHandler() {
					@Override
					public void handle(FormSubmission submission) {
						System.out.println("CUSTOM FORM CALLED");
						assertEquals("new_household_registration", submission.formName());
						assertEquals("demotest", submission.anmId());
						assertEquals("a3f2abf4-2699-4761-819a-cea739224164", submission.entityId());
						assertEquals("household", submission.bindType());
					}
				});
        
        ZiggyService service = new ZiggyService(ziggyFileLoader, ziggyDataHandler,
                new FormSubmissionRouter(formSubmissionService, handlerMapper)
        );

        service.saveForm(params, formInstance);
    }
}
