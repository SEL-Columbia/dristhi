package org.opensrp.connector.openmrs.service;

import static org.junit.Assert.assertEquals;
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
		assertTrue(openMRSConceptParser.getInstanceAttributesForFormFieldAndValue(field, fieldValue, null, formSubmission).equalsIgnoreCase("1065AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"));
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
	
	@Test
	public void shouldFetchCorrectOptionConcept() throws IOException{
		String field = "isThisOnlyDeliveryFacility";
		String fieldVal = "Yes";
		FormSubmission formSubmission = getFormSubmissionFor("repeatform");
		String concept = openMRSConceptParser.getInstanceAttributesForFormFieldAndValue(field, fieldVal, null, formSubmission);
		assertNotNull(concept);
		assertEquals(concept, "1065");
		
		field = "bloodGroup";
		fieldVal = "a_negative";
		String subform = "child_registration";
		concept = openMRSConceptParser.getInstanceAttributesForFormFieldAndValue(field, fieldVal, subform, formSubmission);
		assertNotNull(concept);
		assertEquals(concept, "34134");
	}

	@Test
	public void shouldBringSubformData() throws IOException{
		String subform = "child_registration";

		FormSubmission fs = getFormSubmissionFor("repeatform");
		Map<String, String> attrs = openMRSConceptParser.getAttributesForSubform(subform, fs);
		assertNotNull(attrs.get("openmrs_entity"));
		assertNotNull(attrs.get("openmrs_entity_id"));
		assertEquals(attrs.get("openmrs_entity"), "person");
		assertEquals(attrs.get("openmrs_entity_id"), "new registration");
		
		String field = "premature";
		attrs = openMRSConceptParser.getAttributesForSubform(subform, field, fs);
		assertNotNull(attrs.get("openmrs_entity"));
		assertNotNull(attrs.get("openmrs_entity_id"));
		assertEquals(attrs.get("openmrs_entity"), "concept");
		assertEquals(attrs.get("openmrs_entity_id"), "232323");
		
		attrs.clear();
		attrs.put("openmrs_entity", "person");
		attrs.put("openmrs_entity_id", "gender");
		String fn = openMRSConceptParser.getFieldName(attrs, subform, fs);
		assertNotNull(fn);
		assertEquals(fn, "gender");
	}
}
