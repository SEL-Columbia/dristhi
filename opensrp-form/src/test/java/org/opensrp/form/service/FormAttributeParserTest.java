package org.opensrp.form.service;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.MockitoAnnotations.initMocks;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.opensrp.form.domain.FormSubmission;
import org.xml.sax.SAXException;

import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;


public class FormAttributeParserTest extends TestResourceLoader{
	
	public FormAttributeParserTest() throws IOException {
		super();
	}

	FormAttributeParser fam;

	@Before
    public void setUp() throws Exception {
        initMocks(this);
        fam = new FormAttributeParser(formDirPath);
    }
	
	@SuppressWarnings({ "unchecked" })
	@Test
	public void shouldValidateGeneratedFormSubmissionMap() throws JsonIOException, IOException, JsonSyntaxException, XPathExpressionException, ParserConfigurationException, SAXException {
		FormSubmission fs = getFormSubmissionFor("basic_reg");
		FormSubmissionMap fsm = fam.createFormSubmissionMap(fs);
		assertEquals("/model/instance/register_with_address/", fsm.bindPath());
		assertEquals("register_with_address", fsm.bindType());
		assertEquals(1426830449320L, fsm.clientTimestamp());
		assertEquals("b716d938-1aea-40ae-a081-9ddddddcccc9", fsm.entityId());
		
		testFormField(fsm.fields(), "woman_name", "text", "test woman_name", 
				"/model/instance/register_with_address/woman_name", 
				fsm.getField("woman_name").fieldAttributes(), makeFieldAttributes("person", "first_name"));
		testFormField(fsm.fields(), "last_name", "text", "test last_name", 
				"/model/instance/register_with_address/last_name", 
				fsm.getField("last_name").fieldAttributes(), makeFieldAttributes("person", "last_name"));
		testFormField(fsm.fields(), "gender", "text", "FEMALE", 
				"/model/instance/register_with_address/gender", 
				fsm.getField("gender").fieldAttributes(), makeFieldAttributes("person", "gender"));
		testFormField(fsm.fields(), "birthdate", "date", "2015-02-01", 
				"/model/instance/register_with_address/birthdate", 
				fsm.getField("birthdate").fieldAttributes(), makeFieldAttributes("person", "birthdate"));
		testFormField(fsm.fields(), "location", "select one", "unknown location", 
				"/model/instance/register_with_address/location", 
				fsm.getField("location").fieldAttributes(), makeFieldAttributes("encounter", "location_id"));
		testFormField(fsm.fields(), "anc_visit_date", "date", "2015-02-01", 
				"/model/instance/register_with_address/anc_visit_date", 
				fsm.getField("anc_visit_date").fieldAttributes(), makeFieldAttributes("encounter", "encounter_date"));
		testFormField(fsm.fields(), "pulse_rate", "integer", "55", 
				"/model/instance/register_with_address/pulse_rate", 
				fsm.getField("pulse_rate").fieldAttributes(), makeFieldAttributes("concept", "5087AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"));
		testFormField(fsm.fields(), "temperature", "decimal", "37", 
				"/model/instance/register_with_address/temperature", 
				fsm.getField("temperature").fieldAttributes(), makeFieldAttributes("concept", "5088AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"));
		testFormField(fsm.fields(), "today", "today", "2015-02-01", 
				"/model/instance/register_with_address/today", 
				fsm.getField("today").fieldAttributes(), new HashMap<String, String>());
		testFormField(fsm.fields(), "start", "start", "2015-02-01", 
				"/model/instance/register_with_address/start", 
				fsm.getField("start").fieldAttributes(), new HashMap<String, String>());
		testFormField(fsm.fields(), "end", "end", "2015-02-01", 
				"/model/instance/register_with_address/end", 
				fsm.getField("end").fieldAttributes(), new HashMap<String, String>());
		
		testFormField(fsm.fields(), "birthplace_lat", "text", "test birthplace_lat", 
				"/model/instance/register_with_address/birthplace_address/birthplace_lat", 
				fsm.getField("birthplace_lat").fieldAttributes(), makeFieldAttributes("person_address", "latitude", "birthplace"));
		testFormField(fsm.fields(), "birthplace_lon", "text", "test birthplace_lon", 
				"/model/instance/register_with_address/birthplace_address/birthplace_lon", 
				fsm.getField("birthplace_lon").fieldAttributes(), makeFieldAttributes("person_address", "longitute", "birthplace"));
		testFormField(fsm.fields(), "birthplace_postcode", "text", "test birthplace_postcode", 
				"/model/instance/register_with_address/birthplace_address/birthplace_postcode", 
				fsm.getField("birthplace_postcode").fieldAttributes(), makeFieldAttributes("person_address", "postalcode", "birthplace"));
		testFormField(fsm.fields(), "birthplace_house", "text", "test birthplace_house", 
				"/model/instance/register_with_address/birthplace_address/birthplace_house", 
				fsm.getField("birthplace_house").fieldAttributes(), makeFieldAttributes("person_address", "house_number", "birthplace"));
		testFormField(fsm.fields(), "birthplace_street", "text", "test birthplace_street", 
				"/model/instance/register_with_address/birthplace_address/birthplace_street", 
				fsm.getField("birthplace_street").fieldAttributes(), makeFieldAttributes("person_address", "street", "birthplace"));
		testFormField(fsm.fields(), "birthplace_area", "text", "test birthplace_area", 
				"/model/instance/register_with_address/birthplace_address/birthplace_area", 
				fsm.getField("birthplace_area").fieldAttributes(), makeFieldAttributes("person_address", "area", "birthplace"));
		testFormField(fsm.fields(), "birthplace_town", "text", "test birthplace_town", 
				"/model/instance/register_with_address/birthplace_address/birthplace_town", 
				fsm.getField("birthplace_town").fieldAttributes(), makeFieldAttributes("person_address", "town", "birthplace"));
		testFormField(fsm.fields(), "birthplace_busroute", "text", "test birthplace_busroute", 
				"/model/instance/register_with_address/birthplace_address/birthplace_busroute", 
				fsm.getField("birthplace_busroute").fieldAttributes(), makeFieldAttributes("person_address", "bus_route", "birthplace"));
		testFormField(fsm.fields(), "birthplace_district", "text", "test birthplace_district", 
				"/model/instance/register_with_address/birthplace_address/birthplace_district", 
				fsm.getField("birthplace_district").fieldAttributes(), makeFieldAttributes("person_address", "district", "birthplace"));
		testFormField(fsm.fields(), "birthplace_city", "text", "test birthplace_city", 
				"/model/instance/register_with_address/birthplace_address/birthplace_city", 
				fsm.getField("birthplace_city").fieldAttributes(), makeFieldAttributes("person_address", "city", "birthplace"));
		testFormField(fsm.fields(), "birthplace_state", "text", "test birthplace_state", 
				"/model/instance/register_with_address/birthplace_address/birthplace_state", 
				fsm.getField("birthplace_state").fieldAttributes(), makeFieldAttributes("person_address", "state", "birthplace"));
		testFormField(fsm.fields(), "birthplace_country", "text", "test birthplace_country", 
				"/model/instance/register_with_address/birthplace_address/birthplace_country", 
				fsm.getField("birthplace_country").fieldAttributes(), makeFieldAttributes("person_address", "country", "birthplace"));


		testFormField(fsm.fields(), "usual_residence_lat", "text", "test usual_residence_lat", 
				"/model/instance/register_with_address/usual_residence_address/usual_residence_lat", 
				fsm.getField("usual_residence_lat").fieldAttributes(), makeFieldAttributes("person_address", "latitude", "usual_residence"));
		testFormField(fsm.fields(), "usual_residence_lon", "text", "test usual_residence_lon", 
				"/model/instance/register_with_address/usual_residence_address/usual_residence_lon", 
				fsm.getField("usual_residence_lon").fieldAttributes(), makeFieldAttributes("person_address", "longitute", "usual_residence"));
		testFormField(fsm.fields(), "usual_residence_postcode", "text", "test usual_residence_postcode", 
				"/model/instance/register_with_address/usual_residence_address/usual_residence_postcode", 
				fsm.getField("usual_residence_postcode").fieldAttributes(), makeFieldAttributes("person_address", "postalcode", "usual_residence"));
		testFormField(fsm.fields(), "usual_residence_house", "text", "test usual_residence_house", 
				"/model/instance/register_with_address/usual_residence_address/usual_residence_house", 
				fsm.getField("usual_residence_house").fieldAttributes(), makeFieldAttributes("person_address", "unit", "usual_residence"));
		testFormField(fsm.fields(), "usual_residence_street", "text", "test usual_residence_street", 
				"/model/instance/register_with_address/usual_residence_address/usual_residence_street", 
				fsm.getField("usual_residence_street").fieldAttributes(), makeFieldAttributes("person_address", "lane", "usual_residence"));
		testFormField(fsm.fields(), "usual_residence_area", "text", "test usual_residence_area", 
				"/model/instance/register_with_address/usual_residence_address/usual_residence_area", 
				fsm.getField("usual_residence_area").fieldAttributes(), makeFieldAttributes("person_address", "sector", "usual_residence"));
		testFormField(fsm.fields(), "usual_residence_town", "text", "test usual_residence_town", 
				"/model/instance/register_with_address/usual_residence_address/usual_residence_town", 
				fsm.getField("usual_residence_town").fieldAttributes(), makeFieldAttributes("person_address", "municipality", "usual_residence"));
		testFormField(fsm.fields(), "usual_residence_start", "date", "2015-02-01", 
				"/model/instance/register_with_address/usual_residence_address/usual_residence_start", 
				fsm.getField("usual_residence_start").fieldAttributes(), makeFieldAttributes("person_address", "startdate", "usual_residence"));
		testFormField(fsm.fields(), "usual_residence_district", "text", "test usual_residence_district", 
				"/model/instance/register_with_address/usual_residence_address/usual_residence_district", 
				fsm.getField("usual_residence_district").fieldAttributes(), makeFieldAttributes("person_address", "district", "usual_residence"));
		testFormField(fsm.fields(), "usual_residence_city", "text", "test usual_residence_city", 
				"/model/instance/register_with_address/usual_residence_address/usual_residence_city", 
				fsm.getField("usual_residence_city").fieldAttributes(), makeFieldAttributes("person_address", "city", "usual_residence"));
		testFormField(fsm.fields(), "usual_residence_state", "text", "test usual_residence_state", 
				"/model/instance/register_with_address/usual_residence_address/usual_residence_state", 
				fsm.getField("usual_residence_state").fieldAttributes(), makeFieldAttributes("person_address", "province", "usual_residence"));
		testFormField(fsm.fields(), "usual_residence_country", "text", "test usual_residence_country", 
				"/model/instance/register_with_address/usual_residence_address/usual_residence_country", 
				fsm.getField("usual_residence_country").fieldAttributes(), makeFieldAttributes("person_address", "country", "usual_residence"));

		
		assertThat(fsm.formAttributes(), Matchers.allOf(
				Matchers.hasEntry("encounter_type", "patient_register"),
				Matchers.hasEntry("id", "patient_basic_reg"),
				Matchers.hasEntry("version", "201504030905")));
		assertEquals("basic_reg", fsm.formName());
		assertEquals("1", fsm.formVersion());
		//TODO assertEquals(, fsm.getSubform(entityId, subformName));
		assertEquals("88ceee24-10b4-44c2-9429-754b8d823776", fsm.instanceId());
		assertEquals("admin", fsm.providerId());
		assertEquals(1430998001293L, fsm.serverTimestamp());
		assertEquals(0, fsm.subforms().size());	
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void shouldValidateGeneratedFormSubmissionMapWithSubform() throws JsonIOException, IOException, JsonSyntaxException, XPathExpressionException, ParserConfigurationException, SAXException {
		FormSubmission fs = getFormSubmissionFor("new_household_registration", 7);
		FormSubmissionMap fsm = fam.createFormSubmissionMap(fs);
		assertEquals("/model/instance/FWNewHH", fsm.bindPath());
		assertEquals("household", fsm.bindType());
		assertEquals(1430997074596L, fsm.clientTimestamp());
		assertEquals("a3f2abf4-2699-4761-819a-cea739224164", fsm.entityId());
		
		testFormField(fsm.fields(), "existing_location", "hidden", "KUPTALA", 
				"/model/instance/FWNewHH/existing_location", 
				fsm.getField("existing_location").fieldAttributes(), makeFieldAttributes("encounter", "location_id"));
		testFormField(fsm.fields(), "today", "today", "2015-05-07", 
				"/model/instance/FWNewHH/today", 
				fsm.getField("today").fieldAttributes(), makeFieldAttributes("encounter", "encounter_date") );
		testFormField(fsm.fields(), "start", "start", "2015-05-07T17:07:21.000+06:00", 
				"/model/instance/FWNewHH/start", 
				fsm.getField("start").fieldAttributes(), new HashMap<String, String>());
		testFormField(fsm.fields(), "end", "end", "2015-05-07T17:07:21.000+06:00", 
				"/model/instance/FWNewHH/end", 
				fsm.getField("end").fieldAttributes(), new HashMap<String, String>());
		testFormField(fsm.fields(), "FWNHREGDATE", "date", "2015-05-07", 
				"/model/instance/FWNewHH/FWNHREGDATE", 
				fsm.getField("FWNHREGDATE").fieldAttributes(), makeFieldAttributes("concept", "160753AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"));
		testFormField(fsm.fields(), "FWGOBHHID", "text", "1234", 
				"/model/instance/FWNewHH/FWGOBHHID", 
				fsm.getField("FWGOBHHID").fieldAttributes(), makeFieldAttributes("person_identifier", "GOB HHID"));
		testFormField(fsm.fields(), "FWJIVHHID", "text", "1234", 
				"/model/instance/FWNewHH/FWJIVHHID", 
				fsm.getField("FWJIVHHID").fieldAttributes(), makeFieldAttributes("person_identifier", "JiVitA HHID"));
		testFormField(fsm.fields(), "FWNHNEARTO", "text", "nothing", 
				"/model/instance/FWNewHH/FWNHNEARTO", 
				fsm.getField("FWNHNEARTO").fieldAttributes(), makeFieldAttributes("person_address", "landmark", "usual_residence"));
		testFormField(fsm.fields(), "FWNHHHGPS", "geopoint", "34 34 0 0", 
				"/model/instance/FWNewHH/FWNHHHGPS", 
				fsm.getField("FWNHHHGPS").fieldAttributes(), makeFieldAttributes("person_address", "geopoint", "usual_residence"));
		testFormField(fsm.fields(), "FWHOHFNAME", "text", "test", 
				"/model/instance/FWNewHH/FWHOHFNAME", 
				fsm.getField("FWHOHFNAME").fieldAttributes(), makeFieldAttributes("person", "first_name"));
//TODO all other props		
		assertThat(fsm.formAttributes(), Matchers.allOf(
				Matchers.hasEntry("encounter_type", "New Household Registration"),
				Matchers.hasEntry("id", "FWNewHH"),
				Matchers.hasEntry("version", "201505061341")));
		assertEquals("new_household_registration", fsm.formName());
		assertEquals("1", fsm.formVersion());
		assertEquals("88c0e824-10b4-44c2-9429-754b8d823776", fsm.instanceId());
		assertEquals("admin", fsm.providerId());
		assertEquals(1430998001293L, fsm.serverTimestamp());
		assertEquals(1, fsm.subforms().size());
		SubformMap sf = fsm.subforms().get(0);
		assertEquals("elco", sf.bindType());
		assertEquals("/model/instance/FWNewHH/woman", sf.defaultBindPath());
		assertEquals("babcd9d2-b3e9-4f6d-8a06-2df8f5fbf01f", sf.entityId());
		
		testFormField(sf.fields(), "FWWOMFNAME", "text", "tEST First", 
				"/model/instance/FWNewHH/woman/FWWOMFNAME", 
				sf.getField("FWWOMFNAME").fieldAttributes(), makeFieldAttributes("person", "first_name"));
		testFormField(sf.fields(), "FWWOMLNAME", "calculate", " lastname", 
				"/model/instance/FWNewHH/woman/FWWOMLNAME", 
				sf.getField("FWWOMLNAME").fieldAttributes(), makeFieldAttributes("person", "last_name"));
		testFormField(sf.fields(), "FWWOMNID", "text", "5478549854895", 
				"/model/instance/FWNewHH/woman/FWWOMNID", 
				sf.getField("FWWOMNID").fieldAttributes(), makeFieldAttributes("person_identifier", "NID"));
		testFormField(sf.fields(), "FWWOMBID", "text", "43030293029323", 
				"/model/instance/FWNewHH/woman/FWWOMBID", 
				sf.getField("FWWOMBID").fieldAttributes(), makeFieldAttributes("person_identifier", "Birth Registration ID"));
		
		assertThat(sf.formAttributes(), Matchers.allOf(
				Matchers.hasEntry("openmrs_entity", "person"),
				Matchers.hasEntry("openmrs_entity_id", "Census and New Woman Registration")));		
		assertEquals("elco_registration", sf.name());
	}
	
	@Test
	public void shouldValidateGeneratedFormSubmissionMapWithNoLastName() throws JsonIOException, IOException, JsonSyntaxException, XPathExpressionException, ParserConfigurationException, SAXException {
		FormSubmission fs = getFormSubmissionFor("new_household_registration_with_grouped_subform_data", 1);
		FormSubmissionMap fsm = fam.createFormSubmissionMap(fs);
		assertEquals("/model/instance/FWNewHH", fsm.bindPath());
		assertEquals("household", fsm.bindType());
		assertEquals(1444562091545L, fsm.clientTimestamp());
		assertEquals("4237d267-d438-49f2-7822-8968t555447c", fsm.entityId());
		
		testFormField(fsm.fields(), "existing_location", "hidden", "2fc43738-ace5-g961-8e8f-ab7dg0e5bc63", 
				"/model/instance/FWNewHH/existing_location", 
				fsm.getField("existing_location").fieldAttributes(), makeFieldAttributes("encounter", "location_id"));
		testFormField(fsm.fields(), "today", "today", "2015-10-11", 
				"/model/instance/FWNewHH/today", 
				fsm.getField("today").fieldAttributes(), makeFieldAttributes("encounter", "encounter_date") );
		testFormField(fsm.fields(), "start", "start", "2015-10-11T17:12:34.000+06:00", 
				"/model/instance/FWNewHH/start", 
				fsm.getField("start").fieldAttributes(), new HashMap<String, String>());
		testFormField(fsm.fields(), "end", "end", "2015-10-11T17:12:34.000+06:00", 
				"/model/instance/FWNewHH/end", 
				fsm.getField("end").fieldAttributes(), new HashMap<String, String>());
		testFormField(fsm.fields(), "FWNHREGDATE", "date", "2015-10-11", 
				"/model/instance/FWNewHH/FWNHREGDATE", 
				fsm.getField("FWNHREGDATE").fieldAttributes(), makeFieldAttributes("concept", "160753AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"));
		testFormField(fsm.fields(), "FWGOBHHID", "text", "2322", 
				"/model/instance/FWNewHH/FWGOBHHID", 
				fsm.getField("FWGOBHHID").fieldAttributes(), makeFieldAttributes("person_attribute", "GoB_HHID"));
		testFormField(fsm.fields(), "FWJIVHHID", "text", "9889", 
				"/model/instance/FWNewHH/FWJIVHHID", 
				fsm.getField("FWJIVHHID").fieldAttributes(), makeFieldAttributes("person_attribute", "JiVitA_HHID"));
		testFormField(fsm.fields(), "FWNHHHGPS", "geopoint", "23.8002 90.4068 0 10", 
				"/model/instance/FWNewHH/FWNHHHGPS", 
				fsm.getField("FWNHHHGPS").fieldAttributes(), makeFieldAttributes("person_address", "geopoint", "usual_residence"));
		testFormField(fsm.fields(), "FWHOHFNAME", "text", "mango", 
				"/model/instance/FWNewHH/FWHOHFNAME", 
				fsm.getField("FWHOHFNAME").fieldAttributes(), makeFieldAttributes("person", "first_name"));
//TODO all other props		
		assertThat(fsm.formAttributes(), Matchers.allOf(
				Matchers.hasEntry("encounter_type", "New Household Registration"),
				Matchers.hasEntry("id", "FWNewHH"),
				Matchers.hasEntry("version", "201510181114")));
		assertEquals("new_household_registration_with_grouped_subform_data", fsm.formName());
		assertEquals("12", fsm.formVersion());
		assertEquals("d304dbr7-3998-434f-8c5b-55d6f5fa4252", fsm.instanceId());
		assertEquals("opensrp", fsm.providerId());
		assertEquals(1444734863350L, fsm.serverTimestamp());
		assertEquals(1, fsm.subforms().size());
		SubformMap sf = fsm.subforms().get(0);
		assertEquals("elco", sf.bindType());
		assertEquals("/model/instance/FWNewHH/woman", sf.defaultBindPath());
		assertEquals("ce71572a-8oc5-u32f-9d3b-4a6b568d5g77", sf.entityId());
		
		testFormField(sf.fields(), "FWWOMFNAME", "text", "jackfruit", 
				"/model/instance/FWNewHH/woman/FWWOMFNAME", 
				sf.getField("FWWOMFNAME").fieldAttributes(), makeFieldAttributes("person", "first_name"));
		testFormField(sf.fields(), "FWWOMLNAME", "calculate", ".", 
				"/model/instance/FWNewHH/woman/FWWOMLNAME", 
				sf.getField("FWWOMLNAME").fieldAttributes(), makeFieldAttributes("person", "last_name"));
		testFormField(sf.fields(), "FWWOMRETYPENID", "text", "7675788777775", 
				"/model/instance/FWNewHH/woman/eligible/FWWOMRETYPENID", 
				sf.getField("FWWOMRETYPENID").fieldAttributes(), makeFieldAttributes("person_identifier", "NID"));
		testFormField(sf.fields(), "FWWOMRETYPEBID", "text", "98899998888888888", 
				"/model/instance/FWNewHH/woman/eligible/FWWOMRETYPEBID", 
				sf.getField("FWWOMRETYPEBID").fieldAttributes(), makeFieldAttributes("person_identifier", "Birth Registration ID"));
		
		assertThat(sf.formAttributes(), Matchers.allOf(
				Matchers.hasEntry("openmrs_entity", "person"),
				Matchers.hasEntry("openmrs_entity_id", "New Woman Registration")));		
		assertEquals("elco_registration", sf.name());
	}
	
	@Test
	public void shouldValidateGeneratedFormSubmissionMapWithMultiselect() throws JsonIOException, IOException, JsonSyntaxException, XPathExpressionException, ParserConfigurationException, SAXException {
		FormSubmission fs = getFormSubmissionFor("new_household_registration_with_grouped_subform_data", 1);
		FormSubmissionMap fsm = fam.createFormSubmissionMap(fs);

		SubformMap sbf = fsm.getSubform("ce71572a-8oc5-u32f-9d3b-4a6b568d5g77", "elco_registration");
		String multiselectField = "FWWOMANYID";
		assertNotNull(sbf.getField(multiselectField));
		assertNotNull(sbf.getField(multiselectField).values());
		assertNotNull(sbf.getField(multiselectField).valuesCodes());
		assertEquals(2, sbf.getField(multiselectField).values().size());
		assertEquals(2, sbf.getField(multiselectField).valuesCodes().size());
		assertThat(sbf.getField(multiselectField).values(), Matchers.hasItems("1", "2"));
		assertThat(sbf.getField(multiselectField).valuesCodes().keySet(), Matchers.hasItems("1", "2"));		
		assertThat(sbf.getField(multiselectField).valuesCodes().get("1"), Matchers.hasEntry("openmrs_code", "163084AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"));		
		assertThat(sbf.getField(multiselectField).valuesCodes().get("2"), Matchers.hasEntry("openmrs_code", "163083AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"));		
	}
	
	@SuppressWarnings("serial")
	private Map<String, String> makeFieldAttributes(final String entity, final String entityId) {
		return new HashMap<String, String>(){{
			put("openmrs_entity", entity);
			put("openmrs_entity_id", entityId);
		}};
	}
	
	private Map<String, String> makeFieldAttributes(final String entity, final String entityId, final String parent) {
		Map<String, String> attr = makeFieldAttributes(entity, entityId);
		attr.put("openmrs_entity_parent", parent);
		return attr;
	}
	
	@SuppressWarnings("unchecked")
	private void testFormField(List<FormFieldMap> fields, String name, String type, String value, 
			String bindPath, Map<String, String> actualAttributes, Map<String, String> expectedAttributes) {
		assertThat(fields, Matchers.<FormFieldMap>hasItem(Matchers.<FormFieldMap>allOf(
				Matchers.<FormFieldMap>hasProperty("name",equalTo(name)),
				Matchers.<FormFieldMap>hasProperty("type",equalTo(type)),
				Matchers.<FormFieldMap>hasProperty("values", Matchers.allOf(Matchers.hasItem(value))),
				Matchers.<FormFieldMap>hasProperty("bindPath",equalTo(bindPath)),
				Matchers.<FormFieldMap>hasProperty("fieldAttributes"),
				Matchers.<FormFieldMap>hasProperty("valuesCodes")
				)));
		for (Entry<String, String> at : expectedAttributes.entrySet()) {
			assertThat(actualAttributes, Matchers.hasEntry(at.getKey(), at.getValue()));
		}
	}
    
    @Test
    public void shouldParseFormJSONToGetOpenMRSConcept() throws JsonSyntaxException, JsonIOException, IOException {
		String field = "delivery_skilled";
		FormSubmission formSubmission = getFormSubmissionFor("pnc_1st_registration");
		String fieldValue = formSubmission.getField(field);
		assertTrue(fam.getInstanceAttributesForFormFieldAndValue(field, fieldValue, null, formSubmission).get("openmrs_code").equalsIgnoreCase("1065AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"));
    }
    
	@Test
    public void shouldParseModelXMLDocAndFormDefJSONToGetOpenMRSConcept() throws JsonSyntaxException, JsonIOException, IOException, XPathExpressionException, ParserConfigurationException, SAXException {
       String field = "delivery_skilled";
	   FormSubmission formSubmission = getFormSubmissionFor("pnc_1st_registration");
	   Map<String,String> attributeMap = fam.getAttributesForField(field, formSubmission.formName());
       assertTrue(fam.getFieldName(attributeMap, formSubmission).equalsIgnoreCase(field));
       String etypr = "encounter_type";
       List<String> atl = new ArrayList<>();
       atl.add(etypr);
       assertNotNull(fam.getUniqueAttributeValue(atl, formSubmission).get("encounter_type"));
    }
	
	@Test
	public void shouldMapAddressWithClient() throws IOException, JsonSyntaxException, XPathExpressionException, ParserConfigurationException, SAXException{
		String field = "birthplace_street";
		FormSubmission formSubmission = getFormSubmissionFor("basic_reg");
		assertNotNull(fam.getAttributesForField(field, formSubmission.formName()));
	}
	
	@Test
	public void shouldFetchCorrectOptionConcept() throws IOException{
		String field = "isThisOnlyDeliveryFacility";
		String fieldVal = "Yes";
		FormSubmission formSubmission = getFormSubmissionFor("repeatform");
		Map<String, String> concept = fam.getInstanceAttributesForFormFieldAndValue(field, fieldVal, null, formSubmission);
		assertNotNull(concept);
		assertThat(concept, Matchers.hasEntry("openmrs_code", "1065"));
		
		field = "bloodGroup";
		fieldVal = "a_negative";
		String subform = "child_registration";
		concept = fam.getInstanceAttributesForFormFieldAndValue(field, fieldVal, subform, formSubmission);
		assertNotNull(concept);
		assertThat(concept, Matchers.hasEntry("openmrs_code", "34134"));
	}

	@Test
	public void shouldFetchSubformData() throws IOException, JsonSyntaxException, XPathExpressionException, ParserConfigurationException, SAXException{
		String subform = "child_registration";

		FormSubmission fs = getFormSubmissionFor("repeatform");
		Map<String, String> attrs = fam.getAttributesForSubform(subform, fs);
		assertNotNull(attrs.get("openmrs_entity"));
		assertNotNull(attrs.get("openmrs_entity_id"));
		assertEquals(attrs.get("openmrs_entity"), "person");
		assertEquals(attrs.get("openmrs_entity_id"), "new registration");
		
		String field = "premature";
		attrs = fam.getAttributesForField(field,subform, fs.formName());
		assertNotNull(attrs.get("openmrs_entity"));
		assertNotNull(attrs.get("openmrs_entity_id"));
		assertEquals(attrs.get("openmrs_entity"), "concept");
		assertEquals(attrs.get("openmrs_entity_id"), "232323");
		
		attrs.clear();
		attrs.put("openmrs_entity", "person");
		attrs.put("openmrs_entity_id", "gender");
		String fn = fam.getFieldName(attrs, subform, fs);
		assertNotNull(fn);
		assertEquals(fn, "gender");
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void shouldGetCorrectAttributesFromFormSubmissionMap() throws JsonIOException, IOException, JsonSyntaxException, XPathExpressionException, ParserConfigurationException, SAXException{
		FormSubmission fs = getFormSubmissionFor("repeatform");
		
		FormSubmissionMap fsm = fam.createFormSubmissionMap(fs);
		assertEquals("/model/instance/PNC_Registration_EngKan/", fsm.bindPath());
		assertEquals("mother", fsm.bindType());
		assertEquals(1426830449320L, fsm.clientTimestamp());
		assertEquals("b716d938-1aea-40ae-a081-9ddddddcccc9", fsm.entityId());
		assertThat(fsm.formAttributes(), allOf(
				hasEntry("id", "Delivery_Outcome_EngKan"),
				hasEntry("encounter_type", "PNC Registration"),
				hasEntry("version", "201503200602")));
		assertEquals("repeatform", fsm.formName());
		assertEquals("5", fsm.formVersion());
		assertEquals("f7974258-1aea-40ae-6676-9ddddddcccc9", fsm.instanceId());
		assertEquals("admin", fsm.providerId());
		assertEquals(1426877779320L, fsm.serverTimestamp());
		
		assertTrue(fsm.subforms().size() == 3);
		
		for (SubformMap sf : fsm.subforms()) {
			assertEquals("child", sf.bindType());
			assertEquals("/model/instance/PNC_Registration_EngKan/live_birth_group/child", sf.defaultBindPath());
			assertEquals("child_registration", sf.name());
			assertThat(sf.formAttributes(), allOf(
					hasEntry("openmrs_entity", "person"),
					hasEntry("openmrs_entity_id", "new registration")));
			
			assertThat(sf.entityId(), anyOf(
					equalTo("e9a91c61-0d33-42d3-bf9b-560b4d08c74f"),
					equalTo("c7305d21-0b90-4c15-a88f-b08338d3aed9"),
					equalTo("6c2d772b-7d6a-4a05-a83d-5168c183ef42")
					));
		}
	}

	@Test
	public void shouldMapCorrectAttributesForFieldsInFormSubmissionMap() throws JsonIOException, IOException, JsonSyntaxException, XPathExpressionException, ParserConfigurationException, SAXException{
		FormSubmission fs = getFormSubmissionFor("new_household_registration_with_grouped_subform_data", 1);
		
		FormSubmissionMap fsm = fam.createFormSubmissionMap(fs);
		FormFieldMap fl = fsm.getField("existing_location");
		//TODO
	}

}
