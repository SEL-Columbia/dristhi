package org.ei.drishti.service;

import org.ei.drishti.repository.AllFormSubmissions;
import org.ei.drishti.repository.FormDataRepository;
import org.ei.drishti.service.formSubmissionHandler.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:test-applicationContext-drishti.xml")
public class ZiggyServiceIntegrationTest {
    @Autowired
    private FormDataRepository formDataRepository;

    @Autowired
    @Value("#{drishti['form.directory.name']}")
    private String formFilePath;

    @Autowired
    @Value("#{drishti['js.directory.name']}")
    private String jsDirectoryName;

    @Autowired
    private AllFormSubmissions formSubmissionsRepository;

    @Mock
    private ECRegistrationHandler ecRegistrationHandler;
    @Mock
    private FPComplicationsHandler fpComplicationsHandler;
    @Mock
    private FPChangeHandler fpChangeHandler;
    @Mock
    private RenewFPProductHandler renewFPProductHandler;

    private ZiggyFileLoader ziggyFileLoader;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        ziggyFileLoader = new ZiggyFileLoader(jsDirectoryName, formFilePath);
    }

    @Test
    public void test() throws Exception {
        String params = "{\"instanceId\":\"someId\", \"anmId\":\"anm\", \"entityId\":\"df8e94dd-91bd-40d2-a82a-fb7402e97f30\",\"formName\":\"fp_complications\",\"timeStamp\":\"1357225856630\", \"serverVersion\": \"1\"}";
        String formInstance = "{\"form\":{\"bind_type\":\"eligible_couple\",\"default_bind_path\":\"/model/instance/Family_Planning_Complications_EngKan/\",\"fields\":[{\"name\":\"id\",\"source\":\"eligible_couple.id\",\"value\":\"df8e94dd-91bd-40d2-a82a-fb7402e97f30\"},{\"name\":\"case_familyplanning_method\",\"source\":\"eligible_couple.currentMethod\",\"value\":\"iud\"},{\"name\":\"method_still_the_same\",\"source\":\"eligible_couple.isMethodSame\",\"value\":\"no\"},{\"name\":\"iud_removal_date\",\"source\":\"eligible_couple.iudRemovalDate\"},{\"name\":\"iud_removal_place\",\"source\":\"eligible_couple.iudRemovalPlace\"},{\"name\":\"familyplanning_method_1\",\"source\":\"eligible_couple.fpMethodProblem\",\"value\":\"iud\"},{\"name\":\"date_familyplanningstart\",\"source\":\"eligible_couple.familyPlanningMethodChangeDate\",\"value\":\"2013-03-04\"},{\"name\":\"iud_place\",\"source\":\"eligible_couple.iudPlace\",\"value\":\"phc\"},{\"name\":\"iud_person\",\"source\":\"eligible_couple.iudPerson\",\"value\":\"anm\"},{\"name\":\"num_ocp_cycles\",\"source\":\"eligible_couple.numberOfOCPDelivered\"},{\"name\":\"threemonths_from_sterilization_date\",\"source\":\"eligible_couple.threeMonthsFromSterilizationDate\",\"bind\":\"/model/instance/Family_Planning_Complications_EngKan/male_sterilizaton_group/threemonths_from_sterilization_date\",\"value\":\"Sun, 02 Jun 2013 00:00:00 GMT\"},{\"name\":\"familyplanning_method\",\"source\":\"eligible_couple.currentMethod\",\"value\":\"iud\"},{\"name\":\"complication_date\",\"source\":\"eligible_couple.fpComplicationDate\",\"value\":\"2013-04-05\"},{\"name\":\"sterilization_failure\",\"source\":\"eligible_couple.isSterilizationFailure\"},{\"name\":\"iud_sideeffect\",\"source\":\"eligible_couple.iud_SideEffect\",\"value\":\"fever\"},{\"name\":\"ocp_sideeffect\",\"source\":\"eligible_couple.ocpSideEffect\"},{\"name\":\"sterilization_sideeffect\",\"source\":\"eligible_couple.sterilizationSideEffect\"},{\"name\":\"injectable_sideeffect\",\"source\":\"eligible_couple.injectableSideEffect\"}]}}";
        ZiggyService service = new ZiggyService(ziggyFileLoader, formDataRepository, new FormSubmissionRouter(formSubmissionsRepository, ecRegistrationHandler, fpComplicationsHandler, fpChangeHandler, renewFPProductHandler));
        service.saveForm(params, formInstance);
    }
}
