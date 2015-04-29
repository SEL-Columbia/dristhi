
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.MockitoAnnotations.initMocks;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.opensrp.connector.FormAttributeMapper;
import org.opensrp.form.domain.FormSubmission;

import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;


public class FormAttributeMapperTest extends TestResourceLoader{
	
	public FormAttributeMapperTest() throws IOException {
		super();
	}

	FormAttributeMapper openMRSConceptParser;

	@Before
    public void setUp() throws Exception {
        initMocks(this);
        openMRSConceptParser = new FormAttributeMapper(formDirPath);
    }
    
    @Test
    public void shouldParseFormJSONToGetOpenMRSConcept() throws JsonSyntaxException, JsonIOException, IOException {
		String field = "delivery_skilled";
		FormSubmission formSubmission = getFormSubmissionFor("pnc_1st_registration");
		String fieldValue = formSubmission .getField(field);
		assertTrue(openMRSConceptParser.getInstanceAttributesForFormFieldAndValue(field, fieldValue, formSubmission).equalsIgnoreCase("1065AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"));
    }
    
	@Test
    public void shouldParseModelXMLDocAndFormDefJSONToGetOpenMRSConcept() throws JsonSyntaxException, JsonIOException, IOException {
       String field = "delivery_skilled";
	   FormSubmission formSubmission = getFormSubmissionFor("pnc_1st_registration");
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
		FormSubmission formSubmission = getFormSubmissionFor("basic_reg");
		assertNotNull(openMRSConceptParser.getAttributesForField(field, formSubmission));
	}
}
