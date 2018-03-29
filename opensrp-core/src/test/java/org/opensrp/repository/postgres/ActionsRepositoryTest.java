package org.opensrp.repository.postgres;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.Test;
import org.opensrp.dto.ActionData;
import org.opensrp.scheduler.Action;
import org.opensrp.scheduler.repository.ActionsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class ActionsRepositoryTest extends BaseRepositoryTest {
	
	@Autowired
	@Qualifier("actionsRepositoryPostgres")
	private ActionsRepository actionsRepository;
	
	@Override
	protected Set<String> getDatabaseScripts() {
		Set<String> scripts = new HashSet<String>();
		scripts.add("action.sql");
		return scripts;
	}
	
	@Test
	public void testGet() {
		Action action = actionsRepository.get("05934ae338431f28bf6793b2417c98f9");
		assertEquals(6, action.data().size());
		assertEquals("PENTA 1", action.data().get("scheduleName"));
		assertEquals("2018-02-22", action.data().get("startDate"));
		assertEquals("aabcd2cc-c111-41c6-85e6-cb5d9e350d08", action.baseEntityId());
		
		//missing action
		assertNull(actionsRepository.get("05934ae338431f28bf6793b241"));
	}
	
	@Test
	public void testAdd() {
		Action action = new Action("bshjfsdf-sdfsd9-4243-ssf", "tester", ActionData.markAlertAsClosed("BCG", "2018-02-23"));
		actionsRepository.add(action);
		assertEquals(35, actionsRepository.getAll().size());
		
		action = actionsRepository.findByCaseIdAndTimeStamp("bshjfsdf-sdfsd9-4243-ssf", 0l).get(0);
		assertEquals("BCG", action.data().get("visitCode"));
		assertEquals("2018-02-23", action.data().get("completionDate"));
		assertEquals("closeAlert", action.getActionType());
		
	}
	
	@Test
	public void testUpdate() {
		Action action = actionsRepository.get("05934ae338431f28bf6793b2417c98f9");
		action.getDetails().put("Key11", "Baby123");
		String today = new LocalDate().toString();
		action.data().put("expiryDate", today);
		
		actionsRepository.update(action);
		
		Action updatedAction = actionsRepository.get("05934ae338431f28bf6793b2417c98f9");
		assertEquals("Baby123", updatedAction.getDetails().get("Key11"));
		assertEquals(today, updatedAction.data().get("expiryDate"));
		
	}
	
	@Test
	public void testGetAll() {
		assertEquals(34, actionsRepository.getAll().size());
	}
	
	@Test
	public void testSafeRemove() {
		
		Action action = actionsRepository.get("05934ae338431f28bf6793b2417c7d94");
		actionsRepository.safeRemove(action);
		
		assertEquals(33, actionsRepository.getAll().size());
		
		assertNull(actionsRepository.get("05934ae338431f28bf6793b2417c7d94"));
	}
	
	@Test
	public void testFindByProviderIdAndTimeStamp() {
		assertEquals(34, actionsRepository.findByProviderIdAndTimeStamp("biddemo", 0l).size());
		
		assertEquals(24, actionsRepository.findByProviderIdAndTimeStamp("biddemo", 1520932703825l).size());
		
		assertTrue(actionsRepository.findByProviderIdAndTimeStamp("biddeo", 0l).isEmpty());
		
		assertTrue(actionsRepository.findByProviderIdAndTimeStamp("biddemo", System.currentTimeMillis()).isEmpty());
	}
	
	@Test
	public void findAlertByANMIdEntityIdScheduleName() {
		List<Action> actions = actionsRepository.findAlertByANMIdEntityIdScheduleName("biddemo",
		    "b06e0847-0d68-4f5c-9288-58bc394fe052", "BCG");
		assertEquals(1, actions.size());
		assertEquals("05934ae338431f28bf6793b2417db6ca", actions.get(0).getId());
		assertEquals("2018-01-11", actions.get(0).data().get("startDate"));
		assertEquals("05934ae338431f28bf6793b2417db6ca", actions.get(0).getId());
		
		assertTrue(actionsRepository
		        .findAlertByANMIdEntityIdScheduleName("biddemo", "b06e0847-0d68-4f5c-9288-58bc394fe052", "OPV 2").isEmpty());
	}
	
	@Test
	public void testFindByCaseIdScheduleAndTimeStamp() {
		List<Action> actions = actionsRepository.findByCaseIdScheduleAndTimeStamp("b06e0847-0d68-4f5c-9288-58bc394fe052",
		    "BCG", new DateTime("2018-03-13"), new DateTime());
		assertEquals(1, actions.size());
		assertEquals("05934ae338431f28bf6793b2417db6ca", actions.get(0).getId());
		assertEquals("2018-01-11", actions.get(0).data().get("startDate"));
		assertEquals("05934ae338431f28bf6793b2417db6ca", actions.get(0).getId());
		
		assertTrue(actionsRepository.findByCaseIdScheduleAndTimeStamp("b06e0847-0d68-4f5c-9288-58bc394fe052", "BCG",
		    new DateTime("2018-03-20"), new DateTime()).isEmpty());
	}
	
	@Test
	public void testFindByCaseIdAndTimeStamp() {
		List<Action> actions = actionsRepository.findByCaseIdAndTimeStamp("b06e0847-0d68-4f5c-9288-58bc394fe052",
		    new DateTime("2018-03-13").getMillis());
		assertEquals(5, actions.size());
		
		actions = actionsRepository.findByCaseIdAndTimeStamp("b06e0847-0d68-4f5c-9288-58bc394fe052", 1520978414514l);
		assertEquals("05934ae338431f28bf6793b2417da475", actions.get(0).getId());
		assertEquals("2018-02-22", actions.get(0).data().get("startDate"));
		assertEquals("pcv1", actions.get(0).data().get("visitCode"));
		
		assertTrue(actionsRepository.findByCaseIdScheduleAndTimeStamp("b06e0847-0d68-4f5c-9288-58bc394fe052", "BCG",
		    new DateTime("2018-03-20"), new DateTime()).isEmpty());
	}
	
	@Test
	public void testDeleteAllByTarget() {
		actionsRepository.deleteAllByTarget("alert");
		assertTrue(actionsRepository.getAll().isEmpty());
	}
	
	@Test
	public void testMarkAllAsInActiveFor() {
		actionsRepository.markAllAsInActiveFor("b06e0847-0d68-4f5c-9288-58bc394fe052");
		List<Action> actions = actionsRepository.findByCaseIdAndTimeStamp("b06e0847-0d68-4f5c-9288-58bc394fe052", 0l);
		for (Action action : actions)
			assertFalse(action.getIsActionActive());
	}
	
	@Test
	public void testAddOrUpdateAlert() {
		Action action = new Action("bshjfsdf-sdfsd9-42642-ssf", "tester1",
		        ActionData.markAlertAsClosed("OPV 1", "2018-03-23"));
		actionsRepository.addOrUpdateAlert(action);
		assertEquals(35, actionsRepository.getAll().size());
		
		action = actionsRepository.findByCaseIdAndTimeStamp("bshjfsdf-sdfsd9-42642-ssf", 0l).get(0);
		assertEquals("OPV 1", action.data().get("visitCode"));
		assertEquals("2018-03-23", action.data().get("completionDate"));
		assertEquals("closeAlert", action.getActionType());
		
		action.getDetails().put("tag", "1234");
		String today = new LocalDate().toString();
		action.data().put("expiryDate", today);
		
		actionsRepository.addOrUpdateAlert(action);
		
		assertEquals(35, actionsRepository.getAll().size());
		Action updatedAction = actionsRepository.findByCaseIdAndTimeStamp("bshjfsdf-sdfsd9-42642-ssf", 0l).get(0);
		assertEquals("1234", updatedAction.getDetails().get("tag"));
		assertEquals(today, updatedAction.data().get("expiryDate"));
		
	}
	
	@Test
	public void testMarkAlertAsInactiveFor() {
		String provider = "biddemo";
		String entityId = "b06e0847-0d68-4f5c-9288-58bc394fe052";
		String schedule = "BCG";
		actionsRepository.markAlertAsInactiveFor(provider, entityId, schedule);
		List<Action> actions = actionsRepository.findAlertByANMIdEntityIdScheduleName(provider, entityId, schedule);
		for (Action action : actions)
			assertFalse(action.getIsActionActive());
	}
	
	@Test
	public void testFindByCriteria() {
		
		long now = System.currentTimeMillis();
		Action action = new Action("bshdsf989-32hjh-d9-42642-ssf", "tester1",
		        ActionData.markAlertAsClosed("OPV 2", "2018-04-23"));
		actionsRepository.addOrUpdateAlert(action);
		
		assertEquals(34, actionsRepository.findByCriteria(null, "biddemo", 0, null, null, 50).size());
		
		assertEquals(35, actionsRepository.findByCriteria("biddemo,tester1", "biddemo", 0, null, null, 50).size());
		
		assertEquals(35, actionsRepository.findByCriteria("biddemo,tester1", null, 0, null, null, 50).size());
		
		assertEquals(5, actionsRepository.findByCriteria("biddemo,tester1", null, 0, "server_version", "desc", 5).size());
		
		assertEquals(1, actionsRepository.findByCriteria("biddemo,tester1", null, now, null, null, 50).size());
	}
	
}
