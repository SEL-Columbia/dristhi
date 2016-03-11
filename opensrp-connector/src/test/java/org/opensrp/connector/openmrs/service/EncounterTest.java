
package org.opensrp.connector.openmrs.service;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Map;

import org.hamcrest.Matchers;
import org.joda.time.DateTime;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.opensrp.domain.Client;
import org.opensrp.domain.Event;
import org.opensrp.domain.Obs;
import org.opensrp.connector.openmrs.constants.OpenmrsHouseHold;
import org.opensrp.form.domain.FormSubmission;
import org.opensrp.form.service.FormAttributeParser;
import org.opensrp.service.formSubmission.FormEntityConverter;

import com.google.gson.JsonIOException;


public class EncounterTest extends TestResourceLoader{
	public EncounterTest() throws IOException {
		super();
	}

	EncounterService s;
	FormEntityConverter oc;
	PatientService ps;
	OpenmrsUserService us;
	HouseholdService hhs;

	SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd");
	
	@Before
	public void setup() throws IOException{
		ps = new PatientService(openmrsOpenmrsUrl, openmrsUsername, openmrsPassword);
		us = new OpenmrsUserService(openmrsOpenmrsUrl, openmrsUsername, openmrsPassword);
		s = new EncounterService(openmrsOpenmrsUrl, openmrsUsername, openmrsPassword);
		s.setPatientService(ps);
		s.setUserService(us);
		hhs = new HouseholdService(openmrsOpenmrsUrl, openmrsUsername, openmrsPassword);
		hhs.setPatientService(ps);
		hhs.setEncounterService(s);
		FormAttributeParser fam = new FormAttributeParser(formDirPath);
		oc = new FormEntityConverter(fam);
	}
	
	@Test
	public void testEncounter() throws JSONException, ParseException, IOException {
		FormSubmission fs = getFormSubmissionFor("basic_reg");
		
		Client c = oc.getClientFromFormSubmission(fs);
		assertEquals(c.getBaseEntityId(), "b716d938-1aea-40ae-a081-9ddddddcccc9");
		assertEquals(c.getFirstName(), "test woman_name");
		assertEquals(c.getGender(), "FEMALE");
		assertEquals(c.getAddresses().get(0).getAddressType(), "birthplace");
		assertEquals(c.getAddresses().get(1).getAddressType(), "usual_residence");
		assertEquals(c.getAddresses().get(2).getAddressType(), "previous_residence");
		assertEquals(c.getAddresses().get(3).getAddressType(), "deathplace");
		assertTrue(c.getAttributes().isEmpty());
		
		Event e = oc.getEventFromFormSubmission(fs);
		assertEquals(e.getEventType(), "patient_register");
		assertEquals(e.getEventDate(), new DateTime(sd.parse("2015-02-01")));
		assertEquals(e.getLocationId(), "unknown location");
		
		if(pushToOpenmrsForTest){
			JSONObject p = ps.getPatientByIdentifier(c.getBaseEntityId());
			if(p == null){
				p = ps.createPatient(c);
			}
			JSONObject en = s.createEncounter(e);
			System.out.println(en);
		}
	}
	
	@Test
	public void testGroupedEncounter() throws JSONException, ParseException, IOException {
		FormSubmission fs = getFormSubmissionFor("repeatform");
		
		Client c = oc.getClientFromFormSubmission(fs);
//TODO		
		Event e = oc.getEventFromFormSubmission(fs);
//TODO
		/*if(true){
			JSONObject p = ps.getPatientByIdentifier(c.getBaseEntityId());
			if(p == null){
				p = ps.createPatient(c);
			}
			JSONObject en = s.createEncounter(e);
			System.out.println(en);
		}*/
	}
	
	@Ignore @Test
	public void shouldHandleSubform() throws IOException, ParseException, JSONException{
		FormSubmission fs = getFormSubmissionFor("new_household_registration", 1);

		Client c = oc.getClientFromFormSubmission(fs);
		assertEquals(c.getBaseEntityId(), "a3f2abf4-2699-4761-819a-cea739224164");
		assertEquals(c.getFirstName(), "test");
		assertEquals(c.getGender(), "male");
		assertEquals(c.getBirthdate(), sd.parse("1900-01-01"));
		assertEquals(c.getAddresses().get(0).getAddressField("landmark"), "nothing");
		assertEquals(c.getAddresses().get(0).getAddressType(), "usual_residence");
		assertEquals(c.getIdentifiers().get("GOB HHID"), "1234");
		assertEquals(c.getIdentifiers().get("JiVitA HHID"), "1234");
		
		Event e = oc.getEventFromFormSubmission(fs);
		assertEquals(e.getBaseEntityId(), "a3f2abf4-2699-4761-819a-cea739224164");
		assertEquals(e.getEventDate(), new DateTime(sd.parse("2015-05-07")));
		assertEquals(e.getLocationId(), "KUPTALA");
		assertEquals(e.getFormSubmissionId(), "88c0e824-10b4-44c2-9429-754b8d823776");

		assertEquals(e.getObs().get(0).getFieldCode(), "160753AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
		assertEquals(e.getObs().get(0).getFormSubmissionField(), "FWNHREGDATE");
		assertEquals(e.getObs().get(0).getValue(), "2015-05-07");

		assertEquals(e.getObs().get(1).getFieldCode(), "5611AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
		assertEquals(e.getObs().get(1).getFormSubmissionField(), "FWNHHMBRNUM");
		assertEquals(e.getObs().get(1).getValue(), "2");
				
		Map<String, Map<String, Object>> dc = oc.getDependentClientsFromFormSubmission(fs);
		for (String id : dc.keySet()) {
			Client cl = (Client) dc.get(id).get("client");
			Event ev = (Event) dc.get(id).get("event");
			assertEquals(cl.getBaseEntityId(), id);
			assertEquals(ev.getBaseEntityId(), id);
		}
	}	
	
	@Ignore @Test
	public void shouldHandleEmptyRepeatGroup() throws IOException, ParseException, JSONException{
		FormSubmission fs = getFormSubmissionFor("new_household_registration", 5);

		Client c = oc.getClientFromFormSubmission(fs);
		assertEquals(c.getBaseEntityId(), "a3f2abf4-2699-4761-819a-cea739224164");
		assertEquals(c.getFirstName(), "test");
		assertEquals(c.getGender(), "male");
		assertEquals(c.getBirthdate(), sd.parse("1900-01-01"));
		assertEquals(c.getAddresses().get(0).getAddressField("landmark"), "nothing");
		assertEquals(c.getAddresses().get(0).getAddressType(), "usual_residence");
		assertEquals(c.getIdentifiers().get("GOB HHID"), "1234");
		assertEquals(c.getIdentifiers().get("JiVitA HHID"), "1234");
		
		Event e = oc.getEventFromFormSubmission(fs);
		assertEquals(e.getBaseEntityId(), "a3f2abf4-2699-4761-819a-cea739224164");
		assertEquals(e.getEventDate(), new DateTime(sd.parse("2015-05-07")));
		assertEquals(e.getLocationId(), "KUPTALA");
		assertEquals(e.getFormSubmissionId(), "88c0e824-10b4-44c2-9429-754b8d823776");

		assertEquals(e.getObs().get(0).getFieldCode(), "160753AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
		assertEquals(e.getObs().get(0).getFormSubmissionField(), "FWNHREGDATE");
		assertEquals(e.getObs().get(0).getValue(), "2015-05-07");

		assertEquals(e.getObs().get(1).getFieldCode(), "5611AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
		assertEquals(e.getObs().get(1).getFormSubmissionField(), "FWNHHMBRNUM");
		assertEquals(e.getObs().get(1).getValue(), "2");
				
		Map<String, Map<String, Object>> dc = oc.getDependentClientsFromFormSubmission(fs);
		assertTrue(dc.isEmpty());
	}	
	
	@Test
	public void shouldGetBirthdateNotEstimatedForMainAndApproxForRepeatGroup() throws IOException, ParseException, JSONException{
		FormSubmission fs = getFormSubmissionFor("new_household_registration", 7);

		Client c = oc.getClientFromFormSubmission(fs);
		assertEquals(c.getBirthdate(), sd.parse("1900-01-01"));
		assertTrue(c.getBirthdateApprox());
		
		Map<String, Map<String, Object>> dc = oc.getDependentClientsFromFormSubmission(fs);
		for (String id : dc.keySet()) {
			Client cl = (Client) dc.get(id).get("client");
			assertEquals(cl.getBirthdate(), sd.parse("2000-05-07"));
			assertFalse(cl.getBirthdateApprox());
		}
	}	
	
	@Test
	public void shouldGetBirthdateNotEstimatedForMainAndRepeatGroupIfNotSpecified() throws IOException, ParseException, JSONException{
		FormSubmission fs = getFormSubmissionFor("new_household_registration", 8);

		Client c = oc.getClientFromFormSubmission(fs);
		assertEquals(c.getBirthdate(), sd.parse("1900-01-01"));
		assertFalse(c.getBirthdateApprox());
		
		Map<String, Map<String, Object>> dc = oc.getDependentClientsFromFormSubmission(fs);
		for (String id : dc.keySet()) {
			Client cl = (Client) dc.get(id).get("client");
			assertEquals(cl.getBirthdate(), sd.parse("2000-05-07"));
			assertFalse(cl.getBirthdateApprox());
		}
	}	
	
	@SuppressWarnings("unchecked")
	@Test
	public void shouldGetDataSpecifiedInGroupInsideSubform() throws IOException, ParseException, JSONException{
		FormSubmission fs = getFormSubmissionFor("new_household_registration_with_grouped_subform_data", 1);

		Client c = oc.getClientFromFormSubmission(fs);
		assertEquals(c.getBirthdate(), sd.parse("1900-01-01"));
		assertFalse(c.getBirthdateApprox());
		assertThat(c.getAttributes(), Matchers.<String, Object>hasEntry(equalTo("GoB_HHID"), equalTo((Object)"2322")));
		assertThat(c.getAttributes(), Matchers.<String, Object>hasEntry(equalTo("JiVitA_HHID"), equalTo((Object)"9889")));
		
		Event e = (Event) oc.getEventFromFormSubmission(fs);
		assertEquals(e.getBaseEntityId(), c.getBaseEntityId());
		assertEquals(e.getEventType(), "New Household Registration");
		assertEquals(e.getEventDate(), new DateTime(new SimpleDateFormat("yyyy-M-dd").parse("2015-10-11")));
		assertEquals(e.getLocationId(), "2fc43738-ace5-g961-8e8f-ab7dg0e5bc63");
		
		assertThat(e.getObs(), Matchers.<Obs>hasItem(Matchers.<Obs>allOf(
				Matchers.<Obs>hasProperty("fieldCode",equalTo("5611AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA")),
				Matchers.<Obs>hasProperty("value",equalTo("23")),
				Matchers.<Obs>hasProperty("formSubmissionField",equalTo("FWNHHMBRNUM"))
				)));
		
		Map<String, Map<String, Object>> dc = oc.getDependentClientsFromFormSubmission(fs);
		for (String id : dc.keySet()) {
			Client cl = (Client) dc.get(id).get("client");
			assertEquals(cl.getBirthdate(), sd.parse("1988-10-08"));
			assertFalse(cl.getBirthdateApprox());
			assertEquals(cl.getFirstName(), "jackfruit");
			assertEquals(cl.getAddresses().get(0).getCountry(), "Bangladesh");
			assertEquals(cl.getAddresses().get(0).getAddressType(), "usual_residence");
			assertEquals(cl.getAddresses().get(0).getStateProvince(), "RANGPUR");
			assertThat(cl.getIdentifiers(), Matchers.<String, String>hasEntry(equalTo("NID"), equalTo("7675788777775")));
			assertThat(cl.getIdentifiers(), Matchers.<String, String>hasEntry(equalTo("Birth Registration ID"), equalTo("98899998888888888")));
			assertThat(cl.getAttributes(), Matchers.<String, Object>hasEntry(equalTo("GoB_HHID"), equalTo((Object)"2322")));
			assertThat(cl.getAttributes(), Matchers.<String, Object>hasEntry(equalTo("JiVitA_HHID"), equalTo((Object)"9889")));
		
			Event ev = (Event) dc.get(id).get("event");
			assertEquals(ev.getBaseEntityId(), cl.getBaseEntityId());
			assertEquals(ev.getEventType(), "New Woman Registration");
			assertEquals(ev.getEventDate(), new DateTime(new SimpleDateFormat("yyyy-M-dd").parse("2015-10-11")));
			assertEquals(ev.getLocationId(), "2fc43738-ace5-g961-8e8f-ab7dg0e5bc63");
			
			assertThat(ev.getObs(), Matchers.<Obs>hasItem(Matchers.<Obs>allOf(
					Matchers.<Obs>hasProperty("fieldCode",equalTo("161135AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA")),
					Matchers.<Obs>hasProperty("value",equalTo("zoom")),
					Matchers.<Obs>hasProperty("formSubmissionField",equalTo("FWHUSNAME"))
					)));
			assertThat(ev.getObs(), Matchers.<Obs>hasItem(Matchers.<Obs>allOf(
					Matchers.<Obs>hasProperty("fieldCode",equalTo("163087AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA")),
					Matchers.<Obs>hasProperty("values",hasItems(equalTo("163084AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"),equalTo("163083AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"))),
					Matchers.<Obs>hasProperty("formSubmissionField",equalTo("FWWOMANYID"))
					)));
		}
		
		if(pushToOpenmrsForTest){
			OpenmrsHouseHold hh = new OpenmrsHouseHold(c, e);
			for (Map<String, Object> cm : dc.values()) {
				hh.addHHMember((Client)cm.get("client"), (Event)cm.get("event"));
			}
			
			hhs.saveHH(hh, true);
		}
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void shouldGetDataSpecifiedInMultiselect() throws IOException, ParseException, JSONException{
		FormSubmission fs = getFormSubmissionFor("new_household_registration_with_grouped_subform_data", 1);

		Client c = oc.getClientFromFormSubmission(fs);
		Event e = (Event) oc.getEventFromFormSubmission(fs);
		
		Map<String, Map<String, Object>> dc = oc.getDependentClientsFromFormSubmission(fs);
		for (String id : dc.keySet()) {
			Client cl = (Client) dc.get(id).get("client");
			Event ev = (Event) dc.get(id).get("event");
			
			assertThat(ev.getObs(), Matchers.<Obs>hasItem(Matchers.<Obs>allOf(
					Matchers.<Obs>hasProperty("fieldCode",equalTo("163087AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA")),
					Matchers.<Obs>hasProperty("values",hasItems(equalTo("163084AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"),equalTo("163083AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"))),
					Matchers.<Obs>hasProperty("formSubmissionField",equalTo("FWWOMANYID")),
					Matchers.<Obs>hasProperty("fieldType",equalTo("concept")),
					Matchers.<Obs>hasProperty("fieldDataType",startsWith("select all"))
					)));
		}
		
		if(pushToOpenmrsForTest){
			OpenmrsHouseHold hh = new OpenmrsHouseHold(c, e);
			for (Map<String, Object> cm : dc.values()) {
				hh.addHHMember((Client)cm.get("client"), (Event)cm.get("event"));
			}
			
			hhs.saveHH(hh, true);
		}
	}
	
	@Test
	public void parentChildObsTest() throws JsonIOException, IOException, JSONException {
		FormSubmission fs = getFormSubmissionFor("psrf_form");
		
		//Client c = oc.getClientFromFormSubmission(fs);
		Event e = (Event) oc.getEventFromFormSubmission(fs);
		
		pushToOpenmrsForTest = true;
		if(pushToOpenmrsForTest){
			s.createEncounter(e);
		}

	}
}