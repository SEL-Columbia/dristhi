package org.opensrp.scheduler.repository;

import java.util.List;

import org.joda.time.DateTime;
import org.opensrp.dto.AlertStatus;
import org.opensrp.repository.BaseRepository;
import org.opensrp.scheduler.Alert;

public interface AlertsRepository extends BaseRepository<Alert>{
	
	List<Alert> findByProviderAndTimestamp(String provider, long timeStamp);
	
	List<Alert> findActiveByProviderAndTimestamp(String provider, long timeStamp);
	
	List<Alert> findAlertByProviderEntityIdTriggerName(String provider, String entityId, String triggerName);
	
	List<Alert> findActiveAlertByProviderEntityIdTriggerName(String provider, String entityId, String triggerName);
	
	List<Alert> findActiveAlertByEntityIdTriggerName(String entityId, String triggerName);
	
	List<Alert> findActiveAlertByEntityId(String entityId);
	
	List<Alert> findByEntityIdTriggerAndTimeStamp(String entityId, String trigger, DateTime start, DateTime end);
	
	void markAllAsClosedFor(String entityId, String reasonForClose);
	
	void markAlertAsClosedFor(String providerId, String entityId, String triggerName, String reasonForClose);
	
	void markAlertAsCompleteFor(String providerId, String entityId, String triggerName, String completionDate);
	
	void addOrUpdateScheduleNotificationAlert(String beneficiaryType, String entityId, String providerId, String triggerName,
	                                          String triggerCode, AlertStatus alertStatus, DateTime startDate,
	                                          DateTime expiryDate);
	
	void markAlertAsCompleteFor(String entityId, String triggerName, String completionDate);
	
}
