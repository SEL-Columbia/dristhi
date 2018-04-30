package org.opensrp.repository.postgres;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.opensrp.domain.postgres.AlertMetadata;
import org.opensrp.domain.postgres.AlertMetadataExample;
import org.opensrp.dto.AlertStatus;
import org.opensrp.repository.postgres.mapper.custom.CustomAlertMapper;
import org.opensrp.repository.postgres.mapper.custom.CustomAlertMetadataMapper;
import org.opensrp.scheduler.Alert;
import org.opensrp.scheduler.Alert.AlertType;
import org.opensrp.scheduler.Alert.TriggerType;
import org.opensrp.scheduler.repository.AlertsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository("alertsRepositoryPostgres")
public class AlertsRepositoryImpl extends BaseRepositoryImpl<Alert> implements AlertsRepository {
	
	@Autowired
	private CustomAlertMapper alertMapper;
	
	@Autowired
	private CustomAlertMetadataMapper alertMetadataMapper;
	
	@Override
	public Alert get(String id) {
		if (StringUtils.isBlank(id)) {
			return null;
		}
		org.opensrp.domain.postgres.Alert pgAlert = alertMetadataMapper.selectByDocumentId(id);
		
		return convert(pgAlert);
	}
	
	@Override
	public void add(Alert entity) {
		
		if (entity == null || entity.entityId() == null) {
			return;
		}
		
		if (retrievePrimaryKey(entity) != null) { //Event already added
			return;
		}
		
		if (entity.getId() == null)
			entity.setId(UUID.randomUUID().toString());
		
		setRevision(entity);
		org.opensrp.domain.postgres.Alert pgAlert = convert(entity, null);
		if (pgAlert == null) {
			return;
		}
		
		int rowsAffected = alertMapper.insertSelectiveAndSetId(pgAlert);
		if (rowsAffected < 1 || pgAlert.getId() == null) {
			return;
		}
		
		AlertMetadata eventMetadata = createMetadata(entity, pgAlert.getId());
		if (eventMetadata != null) {
			alertMetadataMapper.insertSelective(eventMetadata);
		}
		
	}
	
	@Override
	public void update(Alert entity) {
		if (entity == null || entity.entityId() == null) {
			return;
		}
		
		Long id = retrievePrimaryKey(entity);
		if (id == null) { // Alert not added
			return;
		}
		
		setRevision(entity);
		org.opensrp.domain.postgres.Alert pgAlert = convert(entity, id);
		if (pgAlert == null) {
			return;
		}
		
		AlertMetadata alertMetadata = createMetadata(entity, id);
		if (alertMetadata == null) {
			return;
		}
		
		int rowsAffected = alertMapper.updateByPrimaryKey(pgAlert);
		if (rowsAffected < 1) {
			return;
		}
		
		AlertMetadataExample alertMetadataExample = new AlertMetadataExample();
		alertMetadataExample.createCriteria().andAlertIdEqualTo(id);
		alertMetadata.setId(alertMetadataMapper.selectByExample(alertMetadataExample).get(0).getId());
		alertMetadataMapper.updateByPrimaryKey(alertMetadata);
		
	}
	
	@Override
	public List<Alert> getAll() {
		List<org.opensrp.domain.postgres.Alert> actions = alertMetadataMapper.selectMany(new AlertMetadataExample(), 0,
		    DEFAULT_FETCH_SIZE);
		return convert(actions);
	}
	
	@Override
	public void safeRemove(Alert entity) {
		if (entity == null || entity.entityId() == null) {
			return;
		}
		
		Long id = retrievePrimaryKey(entity);
		if (id == null) {
			return;
		}
		
		AlertMetadataExample alertMetadataExample = new AlertMetadataExample();
		alertMetadataExample.createCriteria().andAlertIdEqualTo(id);
		int rowsAffected = alertMetadataMapper.deleteByExample(alertMetadataExample);
		if (rowsAffected < 1) {
			return;
		}
		
		alertMapper.deleteByPrimaryKey(id);
		
	}
	
	@Override
	public List<Alert> findByProviderAndTimestamp(String provider, long timeStamp) {
		AlertMetadataExample alertMetadataExample = new AlertMetadataExample();
		alertMetadataExample.createCriteria().andProviderIdEqualTo(provider)
		        .andServerVersionGreaterThanOrEqualTo(timeStamp + 1);
		return convert(alertMetadataMapper.selectMany(alertMetadataExample, 0, DEFAULT_FETCH_SIZE));
	}
	
	@Override
	public List<Alert> findActiveByProviderAndTimestamp(String provider, long timeStamp) {
		AlertMetadataExample alertMetadataExample = new AlertMetadataExample();
		alertMetadataExample.createCriteria().andProviderIdEqualTo(provider)
		        .andServerVersionGreaterThanOrEqualTo(timeStamp + 1).andIsActiveEqualTo(true);
		return convert(alertMetadataMapper.selectMany(alertMetadataExample, 0, DEFAULT_FETCH_SIZE));
	}
	
	@Override
	public List<Alert> findAlertByProviderEntityIdTriggerName(String provider, String entityId, String triggerName) {
		AlertMetadataExample alertMetadataExample = new AlertMetadataExample();
		alertMetadataExample.createCriteria().andProviderIdEqualTo(provider).andBaseEntityIdEqualTo(entityId)
		        .andTriggerNameEqualTo(triggerName);
		return convert(alertMetadataMapper.selectMany(alertMetadataExample, 0, DEFAULT_FETCH_SIZE));
	}
	
	@Override
	public List<Alert> findActiveAlertByProviderEntityIdTriggerName(String provider, String entityId, String triggerName) {
		AlertMetadataExample alertMetadataExample = new AlertMetadataExample();
		alertMetadataExample.createCriteria().andProviderIdEqualTo(provider).andBaseEntityIdEqualTo(entityId)
		        .andTriggerNameEqualTo(triggerName).andIsActiveEqualTo(true);
		return convert(alertMetadataMapper.selectMany(alertMetadataExample, 0, DEFAULT_FETCH_SIZE));
	}
	
	@Override
	public List<Alert> findActiveAlertByEntityIdTriggerName(String entityId, String triggerName) {
		AlertMetadataExample alertMetadataExample = new AlertMetadataExample();
		alertMetadataExample.createCriteria().andBaseEntityIdEqualTo(entityId).andTriggerNameEqualTo(triggerName)
		        .andIsActiveEqualTo(true);
		return convert(alertMetadataMapper.selectMany(alertMetadataExample, 0, DEFAULT_FETCH_SIZE));
	}
	
	@Override
	public List<Alert> findActiveAlertByEntityId(String entityId) {
		AlertMetadataExample alertMetadataExample = new AlertMetadataExample();
		alertMetadataExample.createCriteria().andBaseEntityIdEqualTo(entityId).andIsActiveEqualTo(true);
		return convert(alertMetadataMapper.selectMany(alertMetadataExample, 0, DEFAULT_FETCH_SIZE));
	}
	
	@Override
	public List<Alert> findByEntityIdTriggerAndTimeStamp(String entityId, String trigger, DateTime start, DateTime end) {
		AlertMetadataExample alertMetadataExample = new AlertMetadataExample();
		alertMetadataExample.createCriteria().andBaseEntityIdEqualTo(entityId).andTriggerNameEqualTo(trigger)
		        .andServerVersionBetween(start.getMillis() + 1, end.getMillis());
		return convert(alertMetadataMapper.selectMany(alertMetadataExample, 0, DEFAULT_FETCH_SIZE));
	}
	
	@Override
	public void markAllAsClosedFor(String entityId, String reasonForClose) {
		List<Alert> alerts = findActiveAlertByEntityId(entityId);
		for (Alert alert : alerts) {
			alert.markAlertAsClosed(reasonForClose);
			update(alert);
		}
	}
	
	@Override
	public void markAlertAsClosedFor(String providerId, String entityId, String triggerName, String reasonForClose) {
		List<Alert> alerts = findActiveAlertByProviderEntityIdTriggerName(providerId, entityId, triggerName);
		for (Alert alert : alerts) {
			alert.markAlertAsClosed(reasonForClose);
			update(alert);
		}
	}
	
	@Override
	public void markAlertAsCompleteFor(String providerId, String entityId, String triggerName, String completionDate) {
		List<Alert> alerts = findActiveAlertByProviderEntityIdTriggerName(providerId, entityId, triggerName);
		for (Alert alert : alerts) {
			alert.markAlertAsComplete(completionDate);
			update(alert);
		}
		
	}
	
	@Override
	public void markAlertAsCompleteFor(String entityId, String triggerName, String completionDate) {
		List<Alert> alerts = findActiveAlertByEntityIdTriggerName(entityId, triggerName);
		for (Alert alert : alerts) {
			alert.markAlertAsComplete(completionDate);
			update(alert);
		}
	}
	
	@Override
	public void addOrUpdateScheduleNotificationAlert(String beneficiaryType, String entityId, String providerId,
	                                                 String triggerName, String triggerCode, AlertStatus alertStatus,
	                                                 DateTime startDate, DateTime expiryDate) {
		List<Alert> existingAlerts = findActiveAlertByProviderEntityIdTriggerName(providerId, entityId, triggerName);
		if (existingAlerts.isEmpty()) {
			add(new Alert(providerId, entityId, beneficiaryType, AlertType.notification, TriggerType.schedule, triggerName,
			        triggerCode, startDate, expiryDate, alertStatus, null));
		} else {
			Alert al = existingAlerts.get(0);
			// if visit code is same then update otherwise add another record
			if (StringUtils.isNotBlank(al.triggerCode()) && StringUtils.isNotBlank(triggerCode)
			        && al.triggerCode().equalsIgnoreCase(triggerCode)) {
				al.setAlertStatus(alertStatus.name());
				al.setStartDate(startDate.toString());
				al.setExpiryDate(expiryDate.toString());
				al.details().put(alertStatus.name() + ":start", startDate.toString());
				al.details().put(alertStatus.name() + ":end", expiryDate.toString());
				
				update(al);
			} else {
				add(new Alert(providerId, entityId, beneficiaryType, AlertType.notification, TriggerType.schedule,
				        triggerName, triggerCode, startDate, expiryDate, alertStatus, null));
			}
		}
		
	}
	
	@Override
	protected Long retrievePrimaryKey(Alert alert) {
		if (alert == null || alert.getId() == null) {
			return null;
		}
		String documentId = alert.getId();
		
		AlertMetadataExample alertMetadataExample = new AlertMetadataExample();
		alertMetadataExample.createCriteria().andDocumentIdEqualTo(documentId);
		
		org.opensrp.domain.postgres.Alert pgAlert = alertMetadataMapper.selectByDocumentId(documentId);
		if (pgAlert == null) {
			return null;
		}
		return pgAlert.getId();
	}
	
	@Override
	protected Object getUniqueField(Alert alert) {
		if (alert == null) {
			return null;
		}
		return alert.getId();
	}
	
	//private methods
	private Alert convert(org.opensrp.domain.postgres.Alert pgAlert) {
		if (pgAlert == null || pgAlert.getJson() == null || !(pgAlert.getJson() instanceof Alert)) {
			return null;
		}
		return (Alert) pgAlert.getJson();
	}
	
	private org.opensrp.domain.postgres.Alert convert(Alert entity, Long primaryKey) {
		if (entity == null) {
			return null;
		}
		
		org.opensrp.domain.postgres.Alert pgAlert = new org.opensrp.domain.postgres.Alert();
		pgAlert.setId(primaryKey);
		pgAlert.setJson(entity);
		
		return pgAlert;
	}
	
	private List<Alert> convert(List<org.opensrp.domain.postgres.Alert> alerts) {
		if (alerts == null || alerts.isEmpty()) {
			return new ArrayList<>();
		}
		
		List<Alert> convertedAlerts = new ArrayList<>();
		for (org.opensrp.domain.postgres.Alert alert : alerts) {
			Alert convertedAlert = convert(alert);
			if (convertedAlert != null) {
				convertedAlerts.add(convertedAlert);
			}
		}
		
		return convertedAlerts;
	}
	
	private AlertMetadata createMetadata(Alert entity, Long id) {
		AlertMetadata metadata = new AlertMetadata();
		metadata.setAlertId(id);
		metadata.setDocumentId(entity.getId());
		metadata.setBaseEntityId(entity.entityId());
		metadata.setProviderId(entity.providerId());
		metadata.setServerVersion(entity.timestamp());
		metadata.setIsActive(entity.getIsActive());
		metadata.setTriggerName(entity.triggerName());
		return metadata;
	}
	
}
