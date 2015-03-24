
import static java.util.Arrays.asList;
import static org.mockito.MockitoAnnotations.initMocks;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.opensrp.connector.FormAttributeMapper;
import org.opensrp.form.domain.FormData;
import org.opensrp.form.domain.FormField;
import org.opensrp.form.domain.FormInstance;
import org.opensrp.form.domain.FormSubmission;
import org.opensrp.form.domain.SubFormData;


public class FormAttributeMapperTest {
	
	private String anmId = "anmId";
    private String instanceId = "instance id 1";
    private String entityId = "entity id 1";
    private String formName = "form name 1";
    private String bind_type = "entity 1";
    private String default_bind_path = "bind path 1";
    private String formDataDefinitionVersion = "1";
    private SubFormData subFormData = new SubFormData("sub form name", Collections.<Map<String, String>>emptyList());
    private List<FormField> fields = new ArrayList<>();
    private Long timestamp = 0L;
    private long serverVersion = 0L;
		
	FormAttributeMapper openMRSConceptParser;
    @Before
    public void setUp() throws Exception {
        initMocks(this);
        openMRSConceptParser = new FormAttributeMapper("/home/wahid/Downloads/form_submission_samples/", "/home/wahid/Downloads/form_submission_samples/");
    }
    
    public FormSubmission build() {
        FormInstance formInstance = new FormInstance(new FormData(bind_type, default_bind_path, fields, asList(subFormData)));
        return new FormSubmission(anmId, instanceId, formName, entityId, timestamp, formDataDefinitionVersion, formInstance, serverVersion);
    }
    
	private String fieldName = "didMotherSurvive";
	private FormSubmission formSubmission;
	
	@Test
    public void shouldParseModelXMLDocAndFormDefJSONToGetOpenMRSConcept() {
		
       formSubmission = build();       
       Map<String,String> attributeMap = openMRSConceptParser.getAttributesForField(fieldName, formSubmission);
       openMRSConceptParser.getFieldName(attributeMap, formSubmission);
       
    }
}
