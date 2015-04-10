
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.MockitoAnnotations.initMocks;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.opensrp.connector.FormAttributeMapper;
import org.opensrp.form.domain.FormSubmission;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ResourceLoader;

import com.google.gson.Gson;


public class FormAttributeMapperTest {
	
    private FormSubmission formSubmission;
	FormAttributeMapper openMRSConceptParser;
	String formDirPath = "form/";

	@Before
    public void setUp() throws Exception {
        initMocks(this);
        openMRSConceptParser = new FormAttributeMapper(formDirPath);
        
        String fsStr = "{\"anmId\": \"admin\",   \"clientVersion\": \"1427951221623\", "
    			+ " \"entityId\": \"b6d2751f-3c44-48d0-822c-734ee03c8aa2\","
    			+ " \"formDataDefinitionVersion\": \"1\","
    			+ " \"formInstance\": {\"form_data_definition_version\":\"1\",\"form\":{\"default_bind_path\":\"/model/instance/pnc_first_registration_motherdetails\",\"bind_type\":\"demo_mother\",\"fields\":[{\"name\":\"id\",\"shouldLoadValue\":true,\"source\":\"demo_mother.id\",\"value\":\"b6d2751f-3c44-48d0-822c-734ee03c8aa2\"},{\"name\":\"location\",\"bind\":\"/model/instance/pnc_first_registration_motherdetails/location\",\"source\":\"demo_mother.location\",\"value\":\"tejgaon\"},{\"name\":\"today\",\"bind\":\"/model/instance/pnc_first_registration_motherdetails/today\",\"source\":\"demo_mother.today\",\"value\":\"2015-04-02\"},{\"name\":\"start\",\"bind\":\"/model/instance/pnc_first_registration_motherdetails/start\",\"source\":\"demo_mother.start\",\"value\":\"2015-04-02T05:04:52.000-00:00\"},{\"name\":\"end\",\"bind\":\"/model/instance/pnc_first_registration_motherdetails/end\",\"source\":\"demo_mother.end\",\"value\":\"2015-04-02T05:04:52.000-00:00\"},{\"name\":\"mother_id\",\"bind\":\"/model/instance/pnc_first_registration_motherdetails/mother_id\",\"source\":\"demo_mother.mother_id\",\"value\":\"145\"},{\"name\":\"gender\",\"bind\":\"/model/instance/pnc_first_registration_motherdetails/gender\",\"source\":\"demo_mother.gender\",\"value\":\"male\"},{\"name\":\"mother_birthdate\",\"bind\":\"/model/instance/pnc_first_registration_motherdetails/mother_birthdate\",\"source\":\"demo_mother.mother_birthdate\",\"value\":\"1989-01-10\"},{\"name\":\"mother_first_name\",\"bind\":\"/model/instance/pnc_first_registration_motherdetails/mother_first_name\",\"source\":\"demo_mother.mother_first_name\",\"value\":\"jannat\"},{\"name\":\"mother_last_name\",\"bind\":\"/model/instance/pnc_first_registration_motherdetails/mother_last_name\",\"source\":\"demo_mother.mother_last_name\",\"value\":\"rehana\"},{\"name\":\"delivery_date\",\"bind\":\"/model/instance/pnc_first_registration_motherdetails/delivery_date\",\"source\":\"demo_mother.delivery_date\",\"value\":\"2015-03-30\"},{\"name\":\"delivery_facility_name\",\"bind\":\"/model/instance/pnc_first_registration_motherdetails/delivery_facility_name\",\"source\":\"demo_mother.delivery_facility_name\",\"value\":\"memon hospital\"},{\"name\":\"delivery_skilled\",\"bind\":\"/model/instance/pnc_first_registration_motherdetails/delivery_skilled\",\"source\":\"demo_mother.delivery_skilled\",\"value\":\"yes\"},{\"name\":\"delivery_type\",\"bind\":\"/model/instance/pnc_first_registration_motherdetails/delivery_type\",\"source\":\"demo_mother.delivery_type\",\"value\":\"cesarean\"},{\"name\":\"delivery_outcome\",\"bind\":\"/model/instance/pnc_first_registration_motherdetails/delivery_outcome\",\"source\":\"demo_mother.delivery_outcome\",\"value\":\"live_birth\"},{\"name\":\"parity\",\"bind\":\"/model/instance/pnc_first_registration_motherdetails/parity\",\"source\":\"demo_mother.parity\",\"value\":\"1\"},{\"name\":\"num_livebirths\",\"bind\":\"/model/instance/pnc_first_registration_motherdetails/num_livebirths\",\"source\":\"demo_mother.num_livebirths\",\"value\":\"1\"},{\"name\":\"num_stillbirths\",\"bind\":\"/model/instance/pnc_first_registration_motherdetails/num_stillbirths\",\"source\":\"demo_mother.num_stillbirths\"},{\"name\":\"complications\",\"bind\":\"/model/instance/pnc_first_registration_motherdetails/complications\",\"source\":\"demo_mother.complications\"},{\"name\":\"woman_survived\",\"bind\":\"/model/instance/pnc_first_registration_motherdetails/woman_survived\",\"source\":\"demo_mother.woman_survived\",\"value\":\"yes\"}]}},"
    			+ " \"formName\": \"pnc_1st_registration\",  \"instanceId\": \"759b4790-47eb-457f-a5b9-3015bc89253c\"  }";
	
		formSubmission = new Gson().fromJson(fsStr, FormSubmission.class);
    }
    
    @Test
    public void shouldParseFormJSONToGetOpenMRSConcept() {
		String field = "delivery_skilled";
		String fieldValue = formSubmission.getField(field);
		System.out.println(openMRSConceptParser.getInstanceAttributesForFormFieldAndValue(field, fieldValue, formSubmission));
    }
    
	@Test
    public void shouldParseModelXMLDocAndFormDefJSONToGetOpenMRSConcept() {
		
       String field = "delivery_skilled";
       Map<String,String> attributeMap = openMRSConceptParser.getAttributesForField(field, formSubmission);
       assertTrue(openMRSConceptParser.getFieldName(attributeMap, formSubmission).equalsIgnoreCase(field));
       String etypr = "encounter_type";
       List<String> atl = new ArrayList<>();
       atl.add(etypr);
       assertNotNull(openMRSConceptParser.getUniqueAttributeValue(atl, formSubmission).get("encounter_type"));
    }
	
	@Test
	public void shouldMapAddressWithClient() throws IOException{
		String field = "birthplace_street";

		ResourceLoader loader=new DefaultResourceLoader();
		formDirPath = loader.getResource(formDirPath).getURI().getPath();
		File fsfile = new File(formDirPath+"basic_reg/formSubmission.json");
		FormSubmission fs = new Gson().fromJson(new FileReader(fsfile), FormSubmission.class);
		assertNotNull(openMRSConceptParser.getAttributesForField(field, fs));
	}
}
