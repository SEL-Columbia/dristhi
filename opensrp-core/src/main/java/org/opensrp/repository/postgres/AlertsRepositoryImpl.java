package org.opensrp.repository.postgres;

import java.util.List;

import org.joda.time.DateTime;
import org.opensrp.dto.AlertStatus;
import org.opensrp.scheduler.Alert;
import org.opensrp.scheduler.repository.AlertsRepository;
import org.springframework.stereotype.Repository;

@Repository
public class AlertsRepositoryImpl implements AlertsRepository{

	@Override
	public Alert get(String id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void add(Alert entity) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void update(Alert entity) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<Alert> getAll() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void safeRemove(Alert entity) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<Alert> findByProviderAndTimestamp(String provider, long timeStamp) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Alert> findActiveByProviderAndTimestamp(String provider, long timeStamp) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Alert> findAlertByProviderEntityIdTriggerName(String provider, String entityId, String triggerName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Alert> findActiveAlertByProviderEntityIdTriggerName(String provider, String entityId, String triggerName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Alert> findActiveAlertByEntityIdTriggerName(String entityId, String triggerName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Alert> findActiveAlertByEntityId(String entityId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Alert> findByEntityIdTriggerAndTimeStamp(String entityId, String trigger, DateTime start, DateTime end) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void markAllAsClosedFor(String entityId, String reasonForClose) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void markAlertAsClosedFor(String providerId, String entityId, String triggerName, String reasonForClose) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void markAlertAsCompleteFor(String providerId, String entityId, String triggerName, String completionDate) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addOrUpdateScheduleNotificationAlert(String beneficiaryType, String entityId, String providerId,
	                                                 String triggerName, String triggerCode, AlertStatus alertStatus,
	                                                 DateTime startDate, DateTime expiryDate) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void markAlertAsCompleteFor(String entityId, String triggerName, String completionDate) {
		// TODO Auto-generated method stub
		
	}
	
}
