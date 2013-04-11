package org.ei.drishti.service;

import org.ei.drishti.repository.FormDataRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:test-applicationContext-drishti.xml")
public class DFLServiceIntegrationTest {
    @Autowired
    private FormDataRepository formDataRepository;

    @Autowired
    @Value("#{drishti['form.directory.name']}")
    private String formFilePath;

    @Autowired
    @Value("#{drishti['js.directory.name']}")
    private String jsDirectoryName;

    private DFLFileLoader dflFileLoader;

    @Before
    public void setUp() throws Exception {
        dflFileLoader = new DFLFileLoader(jsDirectoryName, formFilePath);
    }

    @Test
    public void test() throws Exception {
        String params = "{\"id\":null,\"entityId\":\"df8e94dd-91bd-40d2-a82a-fb7402e97f30\",\"formName\":\"fp_complications\"}";
        String formInstance = "{\"form\":{\"bind_type\":\"eligible_couple\",\"default_bind_path\":\"/model/instance/Family_Planning_Complications_EngKan/\",\"fields\":[{\"name\":\"id\",\"source\":\"eligible_couple.id\",\"value\":\"df8e94dd-91bd-40d2-a82a-fb7402e97f30\"},{\"name\":\"case_familyplanning_method\",\"source\":\"eligible_couple.currentMethod\",\"value\":\"iud\"},{\"name\":\"method_still_the_same\",\"source\":\"eligible_couple.isMethodSame\",\"value\":\"no\"},{\"name\":\"iud_removal_date\",\"source\":\"eligible_couple.iudRemovalDate\"},{\"name\":\"iud_removal_place\",\"source\":\"eligible_couple.iudRemovalPlace\"},{\"name\":\"familyplanning_method_1\",\"source\":\"eligible_couple.fpMethodProblem\",\"value\":\"iud\"},{\"name\":\"date_familyplanningstart\",\"source\":\"eligible_couple.familyPlanningMethodChangeDate\",\"value\":\"2013-03-04\"},{\"name\":\"iud_place\",\"source\":\"eligible_couple.iudPlace\",\"value\":\"phc\"},{\"name\":\"iud_person\",\"source\":\"eligible_couple.iudPerson\",\"value\":\"anm\"},{\"name\":\"num_ocp_cycles\",\"source\":\"eligible_couple.numberOfOCPDelivered\"},{\"name\":\"threemonths_from_sterilization_date\",\"source\":\"eligible_couple.threeMonthsFromSterilizationDate\",\"bind\":\"/model/instance/Family_Planning_Complications_EngKan/male_sterilizaton_group/threemonths_from_sterilization_date\",\"value\":\"Sun, 02 Jun 2013 00:00:00 GMT\"},{\"name\":\"familyplanning_method\",\"source\":\"eligible_couple.currentMethod\",\"value\":\"iud\"},{\"name\":\"complication_date\",\"source\":\"eligible_couple.fpComplicationDate\",\"value\":\"2013-04-05\"},{\"name\":\"sterilization_failure\",\"source\":\"eligible_couple.isSterilizationFailure\"},{\"name\":\"iud_sideeffect\",\"source\":\"eligible_couple.iud_SideEffect\",\"value\":\"fever\"},{\"name\":\"ocp_sideeffect\",\"source\":\"eligible_couple.ocpSideEffect\"},{\"name\":\"sterilization_sideeffect\",\"source\":\"eligible_couple.sterilizationSideEffect\"},{\"name\":\"injectable_sideeffect\",\"source\":\"eligible_couple.injectableSideEffect\"}]}}";
        DFLService service = new DFLService(dflFileLoader, formDataRepository);
        service.saveForm(params, formInstance);
    }
}
