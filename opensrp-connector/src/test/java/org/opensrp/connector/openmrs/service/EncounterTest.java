
package org.opensrp.connector.openmrs.service;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import org.hamcrest.Matchers;
import org.json.JSONException;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.opensrp.api.domain.Client;
import org.opensrp.api.domain.Event;
import org.opensrp.api.domain.Obs;
import org.opensrp.connector.FormAttributeMapper;
import org.opensrp.connector.OpenmrsConnector;
import org.opensrp.connector.openmrs.constants.OpenmrsHouseHold;
import org.opensrp.form.domain.FormSubmission;


public class EncounterTest extends TestResourceLoader{
	public EncounterTest() throws IOException {
		super();
	}

	EncounterService s;
	OpenmrsConnector oc;
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
		FormAttributeMapper fam = new FormAttributeMapper(formDirPath);
		oc = new OpenmrsConnector(fam);
	}
	
	@Test
	public void testEncounter() throws JSONException, ParseException, IOException {
		FormSubmission fs = getFormSubmissionFor("basic_reg");
		
		Client c = oc.getClientFromFormSubmission(fs);
		assertEquals(c.getBaseEntityId(), "b716d938-1aea-40ae-a081-9ddddddcccc9");
		assertEquals(c.getBaseEntity().getFirstName(), "test woman_name");
		assertEquals(c.getBaseEntity().getGender(), "FEMALE");
		assertEquals(c.getBaseEntity().getAddresses().get(0).getAddressType(), "birthplace");
		assertEquals(c.getBaseEntity().getAddresses().get(1).getAddressType(), "usual_residence");
		assertEquals(c.getBaseEntity().getAddresses().get(2).getAddressType(), "previous_residence");
		assertEquals(c.getBaseEntity().getAddresses().get(3).getAddressType(), "deathplace");
		assertTrue(c.getBaseEntity().getAttributes().isEmpty());
		
		Event e = oc.getEventFromFormSubmission(fs);
		assertEquals(e.getEventType(), "patient_register");
		assertEquals(e.getEventDate(), sd.parse("2015-02-01"));
		assertEquals(e.getLocationId(), "unknown location");
	}
	
	@Ignore @Test
	public void shouldHandleSubform() throws IOException, ParseException, JSONException{
		FormSubmission fs = getFormSubmissionFor("new_household_registration", 1);

		assertTrue(oc.isOpenmrsForm(fs));
		
		Client c = oc.getClientFromFormSubmission(fs);
		assertEquals(c.getBaseEntityId(), "a3f2abf4-2699-4761-819a-cea739224164");
		assertEquals(c.getBaseEntity().getFirstName(), "test");
		assertEquals(c.getBaseEntity().getGender(), "male");
		assertEquals(c.getBaseEntity().getBirthdate(), sd.parse("1900-01-01"));
		assertEquals(c.getBaseEntity().getAddresses().get(0).getAddressField("landmark"), "nothing");
		assertEquals(c.getBaseEntity().getAddresses().get(0).getAddressType(), "usual_residence");
		assertEquals(c.getIdentifiers().get("GOB HHID"), "1234");
		assertEquals(c.getIdentifiers().get("JiVitA HHID"), "1234");
		
		Event e = oc.getEventFromFormSubmission(fs);
		assertEquals(e.getBaseEntityId(), "a3f2abf4-2699-4761-819a-cea739224164");
		assertEquals(e.getEventDate(), sd.parse("2015-05-07"));
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

		assertTrue(oc.isOpenmrsForm(fs));
		
		Client c = oc.getClientFromFormSubmission(fs);
		assertEquals(c.getBaseEntityId(), "a3f2abf4-2699-4761-819a-cea739224164");
		assertEquals(c.getBaseEntity().getFirstName(), "test");
		assertEquals(c.getBaseEntity().getGender(), "male");
		assertEquals(c.getBaseEntity().getBirthdate(), sd.parse("1900-01-01"));
		assertEquals(c.getBaseEntity().getAddresses().get(0).getAddressField("landmark"), "nothing");
		assertEquals(c.getBaseEntity().getAddresses().get(0).getAddressType(), "usual_residence");
		assertEquals(c.getIdentifiers().get("GOB HHID"), "1234");
		assertEquals(c.getIdentifiers().get("JiVitA HHID"), "1234");
		
		Event e = oc.getEventFromFormSubmission(fs);
		assertEquals(e.getBaseEntityId(), "a3f2abf4-2699-4761-819a-cea739224164");
		assertEquals(e.getEventDate(), sd.parse("2015-05-07"));
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

		assertTrue(oc.isOpenmrsForm(fs));
		
		Client c = oc.getClientFromFormSubmission(fs);
		assertEquals(c.getBaseEntity().getBirthdate(), sd.parse("1900-01-01"));
		assertTrue(c.getBaseEntity().getBirthdateApprox());
		
		Map<String, Map<String, Object>> dc = oc.getDependentClientsFromFormSubmission(fs);
		for (String id : dc.keySet()) {
			Client cl = (Client) dc.get(id).get("client");
			assertEquals(cl.getBaseEntity().getBirthdate(), sd.parse("2000-05-07"));
			assertFalse(cl.getBaseEntity().getBirthdateApprox());
		}
	}	
	
	@Test
	public void shouldGetBirthdateNotEstimatedForMainAndRepeatGroupIfNotSpecified() throws IOException, ParseException, JSONException{
		FormSubmission fs = getFormSubmissionFor("new_household_registration", 8);

		assertTrue(oc.isOpenmrsForm(fs));
		
		Client c = oc.getClientFromFormSubmission(fs);
		assertEquals(c.getBaseEntity().getBirthdate(), sd.parse("1900-01-01"));
		assertFalse(c.getBaseEntity().getBirthdateApprox());
		
		Map<String, Map<String, Object>> dc = oc.getDependentClientsFromFormSubmission(fs);
		for (String id : dc.keySet()) {
			Client cl = (Client) dc.get(id).get("client");
			assertEquals(cl.getBaseEntity().getBirthdate(), sd.parse("2000-05-07"));
			assertFalse(cl.getBaseEntity().getBirthdateApprox());
		}
	}	
	
	@SuppressWarnings("unchecked")
	@Test
	public void shouldGetDataSpecifiedInGroupInsideSubform() throws IOException, ParseException, JSONException{
		FormSubmission fs = getFormSubmissionFor("new_household_registration_with_grouped_subform_data", 1);

		assertTrue(oc.isOpenmrsForm(fs));
		
		Client c = oc.getClientFromFormSubmission(fs);
		assertEquals(c.getBaseEntity().getBirthdate(), sd.parse("1900-01-01"));
		assertFalse(c.getBaseEntity().getBirthdateApprox());
		assertThat(c.getBaseEntity().getAttributes(), Matchers.<String, Object>hasEntry(equalTo("GoB_HHID"), equalTo((Object)"2322")));
		assertThat(c.getBaseEntity().getAttributes(), Matchers.<String, Object>hasEntry(equalTo("JiVitA_HHID"), equalTo((Object)"9889")));
		
		Event e = (Event) oc.getEventFromFormSubmission(fs);
		assertEquals(e.getBaseEntityId(), c.getBaseEntityId());
		assertEquals(e.getEventType(), "New Household Registration");
		assertEquals(e.getEventDate(), new SimpleDateFormat("yyyy-M-dd").parse("2015-10-11"));
		assertEquals(e.getLocationId(), "2fc43738-ace5-g961-8e8f-ab7dg0e5bc63");
		
		assertThat(e.getObs(), Matchers.<Obs>hasItem(Matchers.<Obs>allOf(
				Matchers.<Obs>hasProperty("fieldCode",equalTo("5611AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA")),
				Matchers.<Obs>hasProperty("value",equalTo("23")),
				Matchers.<Obs>hasProperty("formSubmissionField",equalTo("FWNHHMBRNUM"))
				)));
		
		Map<String, Map<String, Object>> dc = oc.getDependentClientsFromFormSubmission(fs);
		for (String id : dc.keySet()) {
			Client cl = (Client) dc.get(id).get("client");
			assertEquals(cl.getBaseEntity().getBirthdate(), sd.parse("1988-10-08"));
			assertFalse(cl.getBaseEntity().getBirthdateApprox());
			assertEquals(cl.getBaseEntity().getFirstName(), "jackfruit");
			assertEquals(cl.getBaseEntity().getAddresses().get(0).getCountry(), "Bangladesh");
			assertEquals(cl.getBaseEntity().getAddresses().get(0).getAddressType(), "usual_residence");
			assertEquals(cl.getBaseEntity().getAddresses().get(0).getState(), "RANGPUR");
			assertThat(cl.getIdentifiers(), Matchers.<String, String>hasEntry(equalTo("NID"), equalTo("7675777777775")));
			assertThat(cl.getIdentifiers(), Matchers.<String, String>hasEntry(equalTo("Birth Registration ID"), equalTo("99999998888888888")));
			assertThat(cl.getBaseEntity().getAttributes(), Matchers.<String, Object>hasEntry(equalTo("GoB_HHID"), equalTo((Object)"2322")));
			assertThat(cl.getBaseEntity().getAttributes(), Matchers.<String, Object>hasEntry(equalTo("JiVitA_HHID"), equalTo((Object)"9889")));
		
			Event ev = (Event) dc.get(id).get("event");
			assertEquals(ev.getBaseEntityId(), cl.getBaseEntityId());
			assertEquals(ev.getEventType(), "New Woman Registration");
			assertEquals(ev.getEventDate(), new SimpleDateFormat("yyyy-M-dd").parse("2015-10-11"));
			assertEquals(ev.getLocationId(), "2fc43738-ace5-g961-8e8f-ab7dg0e5bc63");
			
			assertThat(ev.getObs(), Matchers.<Obs>hasItem(Matchers.<Obs>allOf(
					Matchers.<Obs>hasProperty("fieldCode",equalTo("161135AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA")),
					Matchers.<Obs>hasProperty("value",equalTo("zoom")),
					Matchers.<Obs>hasProperty("formSubmissionField",equalTo("FWHUSNAME"))
					)));
			assertThat(ev.getObs(), Matchers.<Obs>hasItem(Matchers.<Obs>allOf(
					Matchers.<Obs>hasProperty("fieldCode",equalTo("163087AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA")),
					Matchers.<Obs>hasProperty("value",equalTo("1 2")),
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
}