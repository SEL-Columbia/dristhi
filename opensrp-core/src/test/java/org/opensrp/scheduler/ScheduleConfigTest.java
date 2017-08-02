package org.opensrp.scheduler;

import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hamcrest.Matchers;
import org.json.JSONException;
import org.junit.Test;
import org.opensrp.scheduler.Schedule.ActionType;

public class ScheduleConfigTest {
	private ScheduleConfig schconfig;

	public ScheduleConfigTest() throws IOException, JSONException {
		schconfig = new ScheduleConfig("/schedules/schedule-config.xls");
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void shouldReadAllPropertiesForSingleTriggerSingleForm() throws JSONException {
		Schedule sch = new Schedule("{\"milestone\":\"pentavalent_1\",\"passLogic\":\"${fs.pentavalent_1} == empty && ${fs.pentavalent_1_retro} == empty\",\"schedule\":\"PENTAVALENT 1\",\"form\":\"child_enrollment\",\"triggerDateField\":\"child_birth_date\",\"action\":\"enroll\",\"entityType\":\"pkchild\"}");
		assertThat(sch, Matchers.<Schedule>allOf(
				Matchers.<Schedule>hasProperty("schedule",equalTo("PENTAVALENT 1")),
				Matchers.<Schedule>hasProperty("milestone",equalTo("pentavalent_1")),
				Matchers.<Schedule>hasProperty("action",equalTo(ActionType.enroll)),
				Matchers.<Schedule>hasProperty("entityType",equalTo("pkchild")),
				Matchers.<Schedule>hasProperty("triggerDateFields",hasItem("child_birth_date")),
				Matchers.<Schedule>hasProperty("forms",hasItem("child_enrollment"))
				));
		
		assertEquals("PENTAVALENT 1", sch.schedule());
		assertEquals("pentavalent_1", sch.milestone());
		assertEquals(ActionType.enroll, sch.action());
		assertEquals("pkchild", sch.entityType());
		assertThat(sch.triggerDateFields(), hasItem("child_birth_date"));
		assertThat(sch.forms(), hasItem("child_enrollment"));
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void shouldReadAllPropertiesForMultipleTriggerMultipleForm() throws JSONException {
		Schedule sch = new Schedule("{\"milestone\":\"pentavalent_2\","
				+ "\"passLogic\":\"${fs.pentavalent_2} == empty && ${fs.pentavalent_2_retro} == empty\","
				+ "\"schedule\":\"PENTAVALENT 2\",\"form\":\"child_enrollment,child_followup\","
				+ "\"triggerDateField\":\"pentavalent_1, pentavalent_1_retro\","
				+ "\"action\":\"enroll\",\"entityType\":\"pkchild\"}");
		assertThat(sch, Matchers.<Schedule>allOf(
				Matchers.<Schedule>hasProperty("schedule",equalTo("PENTAVALENT 2")),
				Matchers.<Schedule>hasProperty("milestone",equalTo("pentavalent_2")),
				Matchers.<Schedule>hasProperty("action",equalTo(ActionType.enroll)),
				Matchers.<Schedule>hasProperty("entityType",equalTo("pkchild")),
				Matchers.<Schedule>hasProperty("passLogic", anything()),
				Matchers.<Schedule>hasProperty("triggerDateFields",hasItems("pentavalent_1","pentavalent_1_retro")),
				Matchers.<Schedule>hasProperty("forms",hasItems("child_enrollment","child_followup"))
				));
		
		assertEquals("PENTAVALENT 2", sch.schedule());
		assertEquals("pentavalent_2", sch.milestone());
		assertEquals(ActionType.enroll, sch.action());
		assertEquals("pkchild", sch.entityType());
		assertThat(sch.triggerDateFields(), hasItems("pentavalent_1","pentavalent_1_retro"));
		assertThat(sch.forms(), hasItems("child_enrollment","child_followup"));
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void shouldLoadScheduleByFormName() {
		List<Schedule> sch = schconfig.searchSchedules("child_enrollment");
		assertThat(sch, hasItem(Matchers.<Schedule>allOf(
				Matchers.<Schedule>hasProperty("schedule",equalTo("PENTAVALENT 1")),
				Matchers.<Schedule>hasProperty("milestone",equalTo("penta1")),
				Matchers.<Schedule>hasProperty("action",equalTo(ActionType.enroll)),
				Matchers.<Schedule>hasProperty("entityType",equalTo("pkchild")),
				Matchers.<Schedule>hasProperty("passLogic", anything()),
				Matchers.<Schedule>hasProperty("triggerDateFields",hasItem("dob"))
				)));
		
		assertThat(sch, hasItem(Matchers.<Schedule>allOf(
				Matchers.<Schedule>hasProperty("schedule",equalTo("PENTAVALENT 2")),
				Matchers.<Schedule>hasProperty("milestone",equalTo("penta2")),
				Matchers.<Schedule>hasProperty("action",equalTo(ActionType.enroll)),
				Matchers.<Schedule>hasProperty("entityType",equalTo("pkchild")),
				Matchers.<Schedule>hasProperty("passLogic", anything()),
				Matchers.<Schedule>hasProperty("triggerDateFields",hasItems("pentavalent_1","pentavalent_1_retro")),
				Matchers.<Schedule>hasProperty("forms",hasItems("child_enrollment"))
				)));
		
		List<Schedule> schf = schconfig.searchSchedules("child_followup");
		assertThat(schf, hasItem(Matchers.<Schedule>allOf(
				Matchers.<Schedule>hasProperty("schedule",equalTo("PENTAVALENT 2")),
				Matchers.<Schedule>hasProperty("milestone",equalTo("penta2")),
				Matchers.<Schedule>hasProperty("action",equalTo(ActionType.enroll)),
				Matchers.<Schedule>hasProperty("entityType",equalTo("pkchild")),
				Matchers.<Schedule>hasProperty("passLogic", anything()),
				Matchers.<Schedule>hasProperty("triggerDateFields",hasItems("pentavalent_1","pentavalent_1_retro")),
				Matchers.<Schedule>hasProperty("forms",hasItems("child_followup"))
				)));
		assertThat(schf, not(hasItem(Matchers.<Schedule>allOf(
				Matchers.<Schedule>hasProperty("schedule",equalTo("PENTAVALENT 1")),
				Matchers.<Schedule>hasProperty("milestone",equalTo("penta1")),
				Matchers.<Schedule>hasProperty("action",equalTo(ActionType.enroll)),
				Matchers.<Schedule>hasProperty("entityType",equalTo("pkchild")),
				Matchers.<Schedule>hasProperty("passLogic", anything()),
				Matchers.<Schedule>hasProperty("triggerDateFields",hasItem("dob"))
				))));
	}
	
	@Test
	public void shouldReturnCorrectResultForValidation() {
		List<Schedule> sch = schconfig.searchSchedules("child_enrollment", "PENTAVALENT 2", "penta2", ActionType.enroll);
		assertEquals(1, sch.size());
		
		Schedule s = sch.get(0);
		
		Map<String, String> flvl = new HashMap<>();
		flvl.put("pentavalent_2", null);
		flvl.put("pentavalent_2_retro", null);
		assertTrue(s.passesValidations(flvl));
		
		flvl.put("pentavalent_2", "");
		flvl.put("pentavalent_2_retro", "");
		assertTrue(s.passesValidations(flvl));
		
		flvl.put("pentavalent_2", "a val");
		flvl.put("pentavalent_2_retro", "");
		assertFalse(s.passesValidations(flvl));
		
		flvl.put("pentavalent_2", "a val");
		flvl.put("pentavalent_2_retro", "a vale");
		assertFalse(s.passesValidations(flvl));
		
		sch = schconfig.searchSchedules("pnc_reg_form", "Boosters", "REMINDER", ActionType.enroll);
		assertEquals(1, sch.size());
		
		s = sch.get(0);
		
		flvl = new HashMap<>();

		assertTrue(s.passesValidations(flvl));

		flvl.put("pentavalent_2", null);
		flvl.put("pentavalent_2_retro", null);
		assertTrue(s.passesValidations(flvl));
		
		flvl.put("pentavalent_2", "");
		flvl.put("pentavalent_2_retro", "");
		assertTrue(s.passesValidations(flvl));
		
		flvl.put("pentavalent_2", "a val");
		flvl.put("pentavalent_2_retro", "");
		assertTrue(s.passesValidations(flvl));
		
		flvl.put("pentavalent_2", "a val");
		flvl.put("pentavalent_2_retro", "a vale");
		assertTrue(s.passesValidations(flvl));
	}
	
	@Test
	public void shouldReturnCorrectResultForApplicableEntity() {
		List<Schedule> sch = schconfig.searchSchedules("child_enrollment", "PENTAVALENT 2", "penta2", ActionType.enroll);
		assertEquals(1, sch.size());

		Schedule s = sch.get(0);
		assertTrue(s.applicableForEntity("pkchild"));
		assertTrue(s.applicableForEntity("  pkchild  "));
		assertFalse(s.applicableForEntity("pk child"));
		assertFalse(s.applicableForEntity("otherstring"));
	}
	
	@Test
	public void shouldReturnCorrectResultForApplicableForm() {
		List<Schedule> sch = schconfig.searchSchedules("child_enrollment", "PENTAVALENT 2", "penta2", ActionType.enroll);
		assertEquals(1, sch.size());

		Schedule s = sch.get(0);
		assertTrue(s.hasForm("child_enrollment"));
		assertFalse(s.hasForm("other form"));
	}
	
	@Test
	public void shouldReturnCorrectResultForLogicPresent() {
		List<Schedule> sch = schconfig.searchSchedules("child_enrollment", "PENTAVALENT 2", "penta2", ActionType.enroll);
		assertEquals(1, sch.size());
		
		Schedule s = sch.get(0);
		assertTrue(s.haspassLogic());
		
		sch = schconfig.searchSchedules("pnc_reg_form", "Boosters", "REMINDER", ActionType.enroll);
		assertEquals(1, sch.size());
		
		s = sch.get(0);
		assertFalse(s.haspassLogic());
	}

	@Test
    public void shouldAddSchedule() {
        List<Schedule> sch = schconfig.searchSchedules("child_enrollment", "PENTAVALENT 2", "penta2", ActionType.enroll);
        Schedule s = sch.get(0);

        List<Schedule> actual = schconfig.getSchedules();
        Schedule lastSchedule = actual.get(actual.size()-1);
        assertNotEquals(s, lastSchedule);

        schconfig.addSchedule(s);

        actual = schconfig.getSchedules();
        lastSchedule = actual.get(actual.size()-1);

        assertEquals(s, lastSchedule);
    }

	
	
}
