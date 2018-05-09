package org.opensrp.repository.it;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.opensrp.dto.AlertStatus.normal;

import java.util.List;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opensrp.dto.AlertStatus;
import org.opensrp.scheduler.Alert;
import org.opensrp.scheduler.Alert.AlertType;
import org.opensrp.scheduler.Alert.TriggerType;
import org.opensrp.scheduler.repository.couch.AllAlerts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:test-applicationContext-opensrp.xml")
public class AllAlertsIntegrationTest {
    @Autowired
    AllAlerts allAlerts;

    @Before
    public void setUp() throws Exception {
       allAlerts.removeAll();
    }
    
    @Test
    public void shouldSaveANotification() throws Exception {
    	DateTime dueDate = DateTime.now().minusDays(1);
        DateTime expiryDate = dueDate.plusWeeks(2);
        
        Alert alert = new Alert("ANM 1", "Entity 1", "mother", AlertType.notification, TriggerType.schedule, 
        		"Penta 1", "penta1", dueDate, expiryDate, AlertStatus.upcoming, null);

        allAlerts.add(alert);

        List<Alert> allTheAlertsInDB = allAlerts.getAll();
        assertEquals(1, allTheAlertsInDB.size());
        assertEquals(alert, allTheAlertsInDB.get(0));
    }

    @Test
    public void shouldNotFindAnyAlertsIfNoneExistForGivenProvider() throws Exception {
        assertEquals(0, allAlerts.findActiveByProviderAndTimestamp("ANM 1", 0).size());
    }

    @Test
    public void shouldReturnAlertActionsBasedOnProviderIDAndTimeStamp() throws Exception {
    	DateTime dueDate = DateTime.now().minusDays(1);
        DateTime expiryDate = dueDate.plusWeeks(2);
        
    	Alert alert1 = new Alert("ANM 1", "Entity 1", "mother", AlertType.notification, TriggerType.schedule, 
        		"Penta 1", "penta1", dueDate, expiryDate, AlertStatus.upcoming, null);
    	allAlerts.add(alert1);
    	
    	Alert alert2 = new Alert("ANM 1", "Entity 2", "mother", AlertType.notification, TriggerType.schedule, 
        		"Penta 1", "penta1", dueDate, expiryDate, AlertStatus.upcoming, null);
    	allAlerts.add(alert2);

    	Alert alert3 = new Alert("ANM 1", "Entity 3", "mother", AlertType.notification, TriggerType.schedule, 
        		"Penta 1", "penta1", dueDate, expiryDate, AlertStatus.upcoming, null);
    	allAlerts.add(alert3);

        assertEquals(asList(alert1, alert2, alert3), allAlerts.findActiveByProviderAndTimestamp("ANM 1", 0));
        assertEquals(asList(alert2, alert3), allAlerts.findActiveByProviderAndTimestamp("ANM 1", alert1.timestamp()));
        assertEquals(asList(alert3), allAlerts.findActiveByProviderAndTimestamp("ANM 1", alert2.timestamp()));

        assertEquals(0, allAlerts.findActiveByProviderAndTimestamp("ANM 1", alert3.timestamp()).size());
    }

    @Test
    public void shouldFindAlertsOnlyForTheANMSpecified() throws Exception {
    	DateTime dueDate = DateTime.now().minusDays(1);
        DateTime expiryDate = dueDate.plusWeeks(2);
        
        Alert alert1 = new Alert("ANM 1", "Entity 1", "mother", AlertType.notification, TriggerType.schedule, 
        		"Penta 1", "penta1", dueDate, expiryDate, AlertStatus.upcoming, null);
    	allAlerts.add(alert1);

    	Alert alert2 = new Alert("ANM 2", "Entity 2", "mother", AlertType.notification, TriggerType.schedule, 
        		"Penta 1", "penta1", dueDate, expiryDate, AlertStatus.upcoming, null);
    	allAlerts.add(alert2);
    	
    	Alert alert3 = new Alert("ANM 1", "Entity 3", "mother", AlertType.notification, TriggerType.schedule, 
        		"Penta 1", "penta1", dueDate, expiryDate, AlertStatus.upcoming, null);
    	allAlerts.add(alert3);

        assertEquals(asList(alert1, alert3), allAlerts.findActiveByProviderAndTimestamp("ANM 1", 0));
        assertEquals(asList(alert2), allAlerts.findActiveByProviderAndTimestamp("ANM 2", 0));
    }

    @Test
    public void shouldMarkAllAlertsAsInActiveForACase() throws Exception {
    	DateTime dueDate = DateTime.now().minusDays(1);
        DateTime expiryDate = dueDate.plusWeeks(2);
        
    	Alert alert1 = new Alert("ANM 1", "Entity 1", "mother", AlertType.notification, TriggerType.schedule, 
        		"Penta 1", "penta1", dueDate, expiryDate, AlertStatus.upcoming, null);

    	Alert alert2 = new Alert("ANM 1", "Entity 2", "mother", AlertType.notification, TriggerType.schedule, 
        		"Penta 1", "penta1", dueDate, expiryDate, AlertStatus.upcoming, null);
    	
    	Alert alert3 = new Alert("ANM 2", "Entity 3", "mother", AlertType.notification, TriggerType.schedule, 
        		"Penta 1", "penta1", dueDate, expiryDate, AlertStatus.upcoming, null);
    	
    	Alert alert4 = new Alert("ANM 1", "Entity 4", "mother", AlertType.notification, TriggerType.schedule, 
        		"Penta 1", "penta1", dueDate, expiryDate, AlertStatus.upcoming, null);

    	allAlerts.add(alert1);
    	allAlerts.add(alert2);
    	allAlerts.add(alert3);
    	allAlerts.add(alert4);

        allAlerts.markAllAsClosedFor("Entity 1", "none");

        assertEquals(asList(alert2, alert4), allAlerts.findActiveByProviderAndTimestamp("ANM 1", 0));
        assertEquals(asList(alert3), allAlerts.findActiveByProviderAndTimestamp("ANM 2", 0));
    }

    @Test
    public void shouldNotDoAnythingIfNoActionsAreFoundForATarget() {
    	//TODO
    }

    @Test
    public void shouldReturnActionBasedOnProviderIdEntityIdScheduleName() throws Exception {
    	DateTime dueDate = DateTime.now().minusDays(1);
        DateTime expiryDate = dueDate.plusWeeks(2);
        
    	Alert alert1 = new Alert("ANM 1", "Entity 1", "mother", AlertType.notification, TriggerType.schedule, 
        		"Penta 1", "penta1", dueDate, expiryDate, AlertStatus.upcoming, null);

    	Alert alert2 = new Alert("ANM 1", "Entity 1", "mother", AlertType.notification, TriggerType.schedule, 
        		"Penta 2", "penta2", dueDate, expiryDate, AlertStatus.upcoming, null);
    	
    	Alert alert3 = new Alert("ANM 2", "Entity 2", "mother", AlertType.notification, TriggerType.schedule, 
        		"Penta 1", "penta1", dueDate, expiryDate, AlertStatus.upcoming, null);
    	
    	Alert alert4 = new Alert("ANM 1", "Entity 3", "mother", AlertType.notification, TriggerType.schedule, 
        		"Penta 2", "penta2", dueDate, expiryDate, AlertStatus.upcoming, null);

    	allAlerts.add(alert1);
    	allAlerts.add(alert2);
    	allAlerts.add(alert3);
    	allAlerts.add(alert4);


        assertEquals(asList(alert1), allAlerts.findActiveAlertByProviderEntityIdTriggerName("ANM 1", "Entity 1", "Penta 1"));
        assertEquals(asList(alert2), allAlerts.findActiveAlertByProviderEntityIdTriggerName("ANM 1", "Entity 1", "Penta 2"));
        assertEquals(asList(alert3), allAlerts.findActiveAlertByProviderEntityIdTriggerName("ANM 2", "Entity 2", "Penta 1"));
        assertEquals(asList(alert4), allAlerts.findActiveAlertByProviderEntityIdTriggerName("ANM 1", "Entity 3", "Penta 2"));
    }

    @Test
    public void shouldRemoveExistingAlertBeforeAddingNewOne() throws Exception {
    	DateTime dueDate = DateTime.now().minusDays(1);
        DateTime expiryDate = dueDate.plusWeeks(2);
        
    	allAlerts.addOrUpdateScheduleNotificationAlert("mother", "Entity 1", "ANM 1", "Penta 1", "penta1", AlertStatus.upcoming, dueDate, expiryDate);
        assertTrue(allAlerts.findActiveAlertByEntityId("Entity 1").size() == 1);

        allAlerts.addOrUpdateScheduleNotificationAlert("mother", "Entity 1", "ANM 1", "Penta 1", "penta1", AlertStatus.normal, dueDate, expiryDate);

        List<Alert> al = allAlerts.findActiveAlertByEntityId("Entity 1");
        assertTrue(al.size() == 1);
        assertTrue(al.get(0).alertStatus().equalsIgnoreCase(normal.name()));
    }

    @Test
    public void shouldAddNewAlertWhenThereIsNoExistingAlert() throws Exception {
    	DateTime dueDate = DateTime.now().minusDays(1);
        DateTime expiryDate = dueDate.plusWeeks(2);
        
        allAlerts.addOrUpdateScheduleNotificationAlert("mother", "Entity 1", "ANM 1", "Penta 1", "penta1", AlertStatus.upcoming, dueDate, expiryDate);
        
        assertTrue(allAlerts.findActiveAlertByEntityId("Entity 1").size() == 1);
    }

    @Test
    public void shouldUpdateAlertAsAnInactive() {
    	DateTime dueDate = DateTime.now().minusDays(1);
        DateTime expiryDate = dueDate.plusWeeks(2);
        
    	Alert alert1 = new Alert("ANM 1", "Entity 1", "mother", AlertType.notification, TriggerType.schedule, 
        		"Penta 1", "penta1", dueDate, expiryDate, AlertStatus.upcoming, null);

    	Alert alert2 = new Alert("ANM 1", "Entity 1", "mother", AlertType.notification, TriggerType.schedule, 
        		"Penta 2", "penta2", dueDate, expiryDate, AlertStatus.upcoming, null);
    	
    	allAlerts.add(alert1);
    	allAlerts.add(alert2);

        assertEquals(asList(alert1, alert2), allAlerts.findActiveAlertByEntityId("Entity 1"));
        assertEquals(asList(alert1), allAlerts.findActiveAlertByProviderEntityIdTriggerName("ANM 1", "Entity 1", "Penta 1"));
        assertEquals(asList(alert2), allAlerts.findActiveAlertByProviderEntityIdTriggerName("ANM 1", "Entity 1", "Penta 2"));
    	
        allAlerts.markAlertAsClosedFor("ANM 1", "Entity 1", "Penta 1", "none");

        assertEquals(asList(alert2), allAlerts.findActiveAlertByEntityId("Entity 1"));
        assertEquals(asList(), allAlerts.findActiveAlertByProviderEntityIdTriggerName("ANM 1", "Entity 1", "Penta 1"));
        assertEquals(asList(alert2), allAlerts.findActiveAlertByProviderEntityIdTriggerName("ANM 1", "Entity 1", "Penta 2"));
    }
}
