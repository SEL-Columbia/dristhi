package org.opensrp.repository.postgres;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.joda.time.DateTime;
import org.junit.Test;
import org.opensrp.dto.AlertStatus;
import org.opensrp.scheduler.Alert;
import org.opensrp.scheduler.Alert.AlertType;
import org.opensrp.scheduler.Alert.TriggerType;
import org.opensrp.scheduler.repository.AlertsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class AlertsRepositoryTest extends BaseRepositoryTest {
	
	@Autowired
	@Qualifier("alertsRepositoryPostgres")
	private AlertsRepository alertsRepository;
	
	@Override
	protected Set<String> getDatabaseScripts() {
		Set<String> scripts = new HashSet<String>();
		scripts.add("alert.sql");
		return scripts;
	}
	
	@Test
	public void testGet() {
		Alert alert = alertsRepository.get("170c64f5-a187-4c90-b76d-09ff14d5e280");
		assertEquals("06e4d8c0-f3ff-458c-8141-53d199355c7a", alert.entityId());
		assertEquals("PCV 3", alert.getTriggerName());
		assertEquals(1521756016156l, alert.timestamp());
		
		assertNull(alertsRepository.get("170c64f5-nbnsd8-sdfds-234-09ff14d5e280"));
	}
	
	@Test
	public void testAdd() {
		Alert alert = new Alert("tester1", "ssdf-mn878-jsdjsd-kn", "child", AlertType.notification, TriggerType.schedule,
		        "BCG 2", "b2g2", new DateTime("2017-08-22"), new DateTime("2018-02-22"), AlertStatus.normal, null);
		alertsRepository.add(alert);
		assertEquals(16, alertsRepository.getAll().size());
		Alert savedAlert = alertsRepository.findActiveAlertByEntityId("ssdf-mn878-jsdjsd-kn").get(0);
		assertEquals("2017-08-22", savedAlert.startDate());
		assertEquals("2018-02-22", savedAlert.expiryDate());
	}
	
	@Test
	public void testUpdate() {
		Alert alert = alertsRepository.findActiveAlertByEntityId("4a2a4ad9-cd29-47cb-bdb9-5b617a73b898").get(0);
		alert.setClosingPeriod("closingPresiod");
		alert.setDateClosed("2017-09-18");
		alert.setReasonClosed("Test closing");
		long timeStamp = System.currentTimeMillis();
		alert.setTimeStamp(timeStamp);
		
		alertsRepository.update(alert);
		
		Alert updatedAlert = alertsRepository.findActiveAlertByEntityId("4a2a4ad9-cd29-47cb-bdb9-5b617a73b898").get(0);
		assertEquals("closingPresiod", updatedAlert.closingPeriod());
		assertEquals("2017-09-18", updatedAlert.getDateClosed());
		assertEquals("Test closing", updatedAlert.getReasonClosed());
		assertEquals(timeStamp, updatedAlert.timestamp());
	}
	
	@Test
	public void testGetAll() {
		assertEquals(15, alertsRepository.getAll().size());
		
		alertsRepository.safeRemove(alertsRepository.get("f210392d-2905-458a-8301-5a7fb844c448"));
		
		List<Alert> alerts = alertsRepository.getAll();
		assertEquals(14, alerts.size());
		
		for (Alert alert : alerts)
			assertNotEquals("f210392d-2905-458a-8301-5a7fb844c448", alert.getId());
	}
	
	@Test
	public void testSafeRemove() {
		Alert alert = alertsRepository.findActiveAlertByEntityId("4a2a4ad9-cd29-47cb-bdb9-5b617a73b898").get(0);
		alertsRepository.safeRemove(alert);
		assertEquals(14, alertsRepository.getAll().size());
		
		assertTrue(alertsRepository.findActiveAlertByEntityId("4a2a4ad9-cd29-47cb-bdb9-5b617a73b898").isEmpty());
		
		for (Alert alert_ : alertsRepository.getAll())
			assertNotEquals("4a2a4ad9-cd29-47cb-bdb9-5b617a73b898", alert_.entityId());
	}
	
	@Test
	public void testFindByProviderAndTimestamp() {
		assertEquals(8, alertsRepository.findActiveByProviderAndTimestamp("biddemo", 0l).size());
		
		assertEquals(4, alertsRepository.findActiveByProviderAndTimestamp("biddemo", 1521842403899l).size());
		
		List<Alert> alerts = alertsRepository.findActiveByProviderAndTimestamp("biddemo", 1522188003908l);
		
		assertEquals(1, alerts.size());
		assertEquals("01741058-588c-4105-b2e4-6e5ae47f4880", alerts.get(0).getId());
		
		assertTrue(alertsRepository.findActiveByProviderAndTimestamp("biddemo1", 0l).isEmpty());
		
		assertTrue(alertsRepository.findActiveByProviderAndTimestamp("biddemo", System.currentTimeMillis()).isEmpty());
	}
	
	@Test
	public void testFindActiveByProviderAndTimestamp() {
		assertEquals(8, alertsRepository.findActiveByProviderAndTimestamp("biddemo", 0l).size());
		
		alertsRepository.markAllAsClosedFor("01a12dba-d25e-4518-8da3-cfa8cf5ebf40", "testing");
		
		assertEquals(3, alertsRepository.findActiveByProviderAndTimestamp("biddemo", 0l).size());
		
		List<Alert> alerts = alertsRepository.findActiveByProviderAndTimestamp("biddemo", 1520978414377l);
		assertEquals(2, alerts.size());
		for (Alert alert : alerts) {
			assertTrue(alert.getTimeStamp() > 1520978414377l);
			assertEquals("biddemo", alert.providerId());
		}
		
		assertTrue(alertsRepository.findActiveByProviderAndTimestamp("biddemo1", 0l).isEmpty());
		
		assertTrue(alertsRepository.findActiveByProviderAndTimestamp("biddemo", System.currentTimeMillis()).isEmpty());
		
	}
	
	@Test
	public void testFindAlertByProviderEntityIdTriggerName() {
		
		List<Alert> alerts = alertsRepository.findAlertByProviderEntityIdTriggerName("biddemo",
		    "01a12dba-d25e-4518-8da3-cfa8cf5ebf40", "PCV 1");
		assertEquals(1, alerts.size());
		assertEquals("f210392d-2905-458a-8301-5a7fb844c448", alerts.get(0).getId());
		
		alerts = alertsRepository.findAlertByProviderEntityIdTriggerName("biddemo", "01a12dba-d25e-4518-8da3-cfa8cf5ebf40",
		    "PENTA 2");
		assertEquals(1, alerts.size());
		assertEquals("52e33167-4b64-4189-bf0c-e7a916815bc9", alerts.get(0).getId());
		
		assertTrue(alertsRepository
		        .findAlertByProviderEntityIdTriggerName("biddemo1", "01a12dba-d25e-4518-8da3-cfa8cf5ebf40", "PENTA 2")
		        .isEmpty());
		
		assertTrue(alertsRepository
		        .findAlertByProviderEntityIdTriggerName("biddemo", "01a12dba-d25e-451-cfa8cf5ebf40", "PENTA 2").isEmpty());
		assertTrue(alertsRepository
		        .findAlertByProviderEntityIdTriggerName("biddemo", "01a12dba-d25e-4518-8da3-cfa8cf5ebf40", "BCG").isEmpty());
	}
	
	@Test
	public void testFindActiveAlertByProviderEntityIdTriggerName() {
		List<Alert> alerts = alertsRepository.findActiveAlertByProviderEntityIdTriggerName("biddemo",
		    "01a12dba-d25e-4518-8da3-cfa8cf5ebf40", "PCV 1");
		assertEquals(1, alerts.size());
		assertEquals("f210392d-2905-458a-8301-5a7fb844c448", alerts.get(0).getId());
		
		alerts = alertsRepository.findActiveAlertByProviderEntityIdTriggerName("biddemo",
		    "01a12dba-d25e-4518-8da3-cfa8cf5ebf40", "PENTA 2");
		assertEquals(1, alerts.size());
		assertEquals("52e33167-4b64-4189-bf0c-e7a916815bc9", alerts.get(0).getId());
		
		alertsRepository.markAlertAsCompleteFor("01a12dba-d25e-4518-8da3-cfa8cf5ebf40", "PENTA 2", "2018-03-31");
		
		assertTrue(alertsRepository
		        .findActiveAlertByProviderEntityIdTriggerName("biddemo", "01a12dba-d25e-4518-8da3-cfa8cf5ebf40", "PENTA 2")
		        .isEmpty());
		
		assertTrue(alertsRepository
		        .findActiveAlertByProviderEntityIdTriggerName("biddemo1", "01a12dba-d25e-4518-8da3-cfa8cf5ebf40", "PENTA 2")
		        .isEmpty());
		
		assertTrue(alertsRepository
		        .findActiveAlertByProviderEntityIdTriggerName("biddemo", "01a12dba-d25e-451-cfa8cf5ebf40", "PENTA 2")
		        .isEmpty());
		assertTrue(alertsRepository
		        .findActiveAlertByProviderEntityIdTriggerName("biddemo", "01a12dba-d25e-4518-8da3-cfa8cf5ebf40", "BCG")
		        .isEmpty());
	}
	
	@Test
	public void testFindActiveAlertByEntityIdTriggerName() {
		List<Alert> alerts = alertsRepository.findActiveAlertByEntityIdTriggerName("01a12dba-d25e-4518-8da3-cfa8cf5ebf40",
		    "PCV 1");
		assertEquals(1, alerts.size());
		assertEquals("f210392d-2905-458a-8301-5a7fb844c448", alerts.get(0).getId());
		
		alerts = alertsRepository.findActiveAlertByEntityIdTriggerName("01a12dba-d25e-4518-8da3-cfa8cf5ebf40", "PENTA 2");
		assertEquals(1, alerts.size());
		assertEquals("52e33167-4b64-4189-bf0c-e7a916815bc9", alerts.get(0).getId());
		
		alertsRepository.markAlertAsCompleteFor("01a12dba-d25e-4518-8da3-cfa8cf5ebf40", "PENTA 2", "2018-03-31");
		
		assertTrue(alertsRepository.findActiveAlertByEntityIdTriggerName("01a12dba-d25e-4518-8da3-cfa8cf5ebf40", "PENTA 2")
		        .isEmpty());
		
		assertTrue(
		    alertsRepository.findActiveAlertByEntityIdTriggerName("01a12dba-d25e-451-cfa8cf5ebf40", "PENTA 2").isEmpty());
		assertTrue(
		    alertsRepository.findActiveAlertByEntityIdTriggerName("01a12dba-d25e-4518-8da3-cfa8cf5ebf40", "BCG").isEmpty());
	}
	
	@Test
	public void testFindActiveAlertByEntityId() {
		List<Alert> alerts = alertsRepository.findActiveAlertByEntityId("06e4d8c0-f3ff-458c-8141-53d199355c7a");
		assertEquals(3, alerts.size());
		
		for (Alert alert : alerts) {
			assertEquals("06e4d8c0-f3ff-458c-8141-53d199355c7a", alert.entityId());
			assertTrue(alert.isActive());
		}
		
		alertsRepository.markAllAsClosedFor("06e4d8c0-f3ff-458c-8141-53d199355c7a", "testing");
		
		assertTrue(alertsRepository.findActiveAlertByEntityId("06e4d8c0-f3ff-458c-8141-53d199355c7a").isEmpty());
		
		assertTrue(alertsRepository.findActiveAlertByEntityId("06e4d8c0-f3ff-453kk-kjj1-53d199355c7a").isEmpty());
	}
	
	@Test
	public void testFindByEntityIdTriggerAndTimeStamp() {
		List<Alert> alerts = alertsRepository.findByEntityIdTriggerAndTimeStamp("01a12dba-d25e-4518-8da3-cfa8cf5ebf40",
		    "PCV 1", new DateTime(0), new DateTime());
		assertEquals(1, alerts.size());
		assertEquals("f210392d-2905-458a-8301-5a7fb844c448", alerts.get(0).getId());
		
		alertsRepository.markAlertAsCompleteFor("01a12dba-d25e-4518-8da3-cfa8cf5ebf40", "PCV 1", "2018-03-31");
		
		assertTrue(alertsRepository.findActiveAlertByEntityIdTriggerName("01a12dba-d25e-4518-8da3-cfa8cf5ebf40", "PCV 1")
		        .isEmpty());
		
		assertTrue(alertsRepository.findByEntityIdTriggerAndTimeStamp("01a12dba-d25e-4518-8da3-cfa8cf5ebf40", "PCV 1",
		    new DateTime(1521842403913l), new DateTime()).isEmpty());
		assertTrue(alertsRepository.findByEntityIdTriggerAndTimeStamp("01a12dba-d25e-4518-8da3-cfa8cf5ebf40", "BCG",
		    new DateTime(1521842403913l), new DateTime()).isEmpty());
		
	}
	
	@Test
	public void testMarkAllAsClosedFor() {
		alertsRepository.markAllAsClosedFor("06e4d8c0-f3ff-458c-8141-53d199355c7a", "DYuyi");
		assertFalse(alertsRepository.get("89bcc696-9491-4b03-8064-d7752b0cb12a").isActive());
		assertTrue(alertsRepository.findActiveAlertByEntityId("06e4d8c0-f3ff-458c-8141-53d199355c7a").isEmpty());
	}
	
	@Test
	public void testMarkAlertAsClosedFor() {
		alertsRepository.markAlertAsClosedFor("biddemo", "01a12dba-d25e-4518-8da3-cfa8cf5ebf40", "PENTA 2",
		    "Testing Close!!");
		
		assertTrue(alertsRepository.findActiveAlertByEntityIdTriggerName("01a12dba-d25e-4518-8da3-cfa8cf5ebf40", "PENTA 2")
		        .isEmpty());
		
		Alert alert = alertsRepository.get("52e33167-4b64-4189-bf0c-e7a916815bc9");
		assertEquals(false, alert.isActive());
		assertEquals("Testing Close!!", alert.getReasonClosed());
		assertEquals(AlertStatus.closed.name(), alert.getAlertStatus());
	}
	
	@Test
	public void testMarkAlertAsCompleteFor() {
		alertsRepository.markAlertAsCompleteFor("biddemo", "01a12dba-d25e-4518-8da3-cfa8cf5ebf40", "PENTA 2", "2018-03-27");
		
		assertTrue(alertsRepository.findActiveAlertByEntityIdTriggerName("01a12dba-d25e-4518-8da3-cfa8cf5ebf40", "PENTA 2")
		        .isEmpty());
		
		Alert alert = alertsRepository.get("52e33167-4b64-4189-bf0c-e7a916815bc9");
		assertEquals(false, alert.isActive());
		assertEquals("2018-03-27", alert.getDateComplete());
		assertEquals(AlertStatus.complete.name(), alert.getAlertStatus());
	}
	
	@Test
	public void testMarkAlertAsCompleteForWithoutProvider() {
		alertsRepository.markAlertAsCompleteFor("01a12dba-d25e-4518-8da3-cfa8cf5ebf40", "PENTA 2", "2018-03-27");
		
		assertTrue(alertsRepository.findActiveAlertByEntityIdTriggerName("01a12dba-d25e-4518-8da3-cfa8cf5ebf40", "PENTA 2")
		        .isEmpty());
		
		Alert alert = alertsRepository.get("52e33167-4b64-4189-bf0c-e7a916815bc9");
		assertEquals(false, alert.isActive());
		assertEquals("2018-03-27", alert.getDateComplete());
		assertEquals(AlertStatus.complete.name(), alert.getAlertStatus());
	}
	
	@Test
	public void testAddOrUpdateScheduleNotificationAlert() {
		//test update
		alertsRepository.addOrUpdateScheduleNotificationAlert("vaccination", "06e4d8c0-f3ff-458c-8141-53d199355c7a", "DYuyi",
		    "OPV 3", "opv3", AlertStatus.upcoming, new DateTime("2018-04-28"), new DateTime("2021-02-19"));
		
		Alert alert = alertsRepository.findActiveAlertByEntityIdTriggerName("06e4d8c0-f3ff-458c-8141-53d199355c7a", "OPV 3")
		        .get(0);
		assertEquals(new DateTime("2018-04-28").toString(), alert.getStartDate());
		assertEquals(new DateTime("2021-02-19").toString(), alert.getExpiryDate());
		assertEquals(AlertStatus.upcoming.name(), alert.getAlertStatus());
		
		assertEquals(15, alertsRepository.getAll().size());
		
		//test add
		alertsRepository.addOrUpdateScheduleNotificationAlert("vaccination", "06e4d8c0-f3ff-458c-8141-53d199355c7a", "DYuyi",
		    "OPV 4", "opv4", AlertStatus.urgent, new DateTime("2017-01-21"), new DateTime("2020-05-09"));
		
		alert = alertsRepository.findActiveAlertByEntityIdTriggerName("06e4d8c0-f3ff-458c-8141-53d199355c7a", "OPV 4")
		        .get(0);
		assertEquals("2017-01-21", alert.getStartDate());
		assertEquals("2020-05-09", alert.getExpiryDate());
		assertEquals(AlertStatus.urgent.name(), alert.getAlertStatus());
		
		assertEquals(16, alertsRepository.getAll().size());
	}
}
