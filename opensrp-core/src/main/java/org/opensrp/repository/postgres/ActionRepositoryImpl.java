package org.opensrp.repository.postgres;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.opensrp.domain.postgres.ActionExample;
import org.opensrp.domain.postgres.ActionMetadata;
import org.opensrp.domain.postgres.ActionMetadataExample;
import org.opensrp.domain.postgres.ActionMetadataExample.Criteria;
import org.opensrp.repository.postgres.mapper.custom.CustomActionMapper;
import org.opensrp.repository.postgres.mapper.custom.CustomActionMetadataMapper;
import org.opensrp.scheduler.Action;
import org.opensrp.scheduler.repository.ActionsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository("actionsRepositoryPostgres")
public class ActionRepositoryImpl extends BaseRepositoryImpl<Action> implements ActionsRepository {
	
	@Autowired
	private CustomActionMapper actionMapper;
	
	@Autowired
	private CustomActionMetadataMapper actionMetadataMapper;
	
	@Override
	public Action get(String id) {
		if (StringUtils.isBlank(id)) {
			return null;
		}
		org.opensrp.domain.postgres.Action pgAction = actionMetadataMapper.selectByDocumentId(id);
		
		return convert(pgAction);
	}
	
	@Override
	public void add(Action entity) {
		if (entity == null || entity.baseEntityId() == null) {
			return;
		}
		
		if (retrievePrimaryKey(entity) != null) { //Event already added
			return;
		}
		
		if (entity.getId() == null)
			entity.setId(UUID.randomUUID().toString());
		
		setRevision(entity);
		
		org.opensrp.domain.postgres.Action pgAction = convert(entity, null);
		if (pgAction == null) {
			return;
		}
		
		int rowsAffected = actionMapper.insertSelectiveAndSetId(pgAction);
		if (rowsAffected < 1 || pgAction.getId() == null) {
			return;
		}
		
		ActionMetadata actionMetadata = createMetadata(entity, pgAction.getId());
		if (actionMetadata != null) {
			actionMetadataMapper.insertSelective(actionMetadata);
		}
		
	}
	
	@Override
	public void update(Action entity) {
		if (entity == null || entity.baseEntityId() == null) {
			return;
		}
		
		Long id = retrievePrimaryKey(entity);
		if (id == null) { // Action not added
			return;
		}
		
		setRevision(entity);
		org.opensrp.domain.postgres.Action pgAction = convert(entity, id);
		if (pgAction == null) {
			return;
		}
		
		ActionMetadata actionMetadata = createMetadata(entity, id);
		if (actionMetadata == null) {
			return;
		}
		
		int rowsAffected = actionMapper.updateByPrimaryKey(pgAction);
		if (rowsAffected < 1) {
			return;
		}
		
		ActionMetadataExample actionMetadataExample = new ActionMetadataExample();
		actionMetadataExample.createCriteria().andActionIdEqualTo(id);
		actionMetadata.setId(actionMetadataMapper.selectByExample(actionMetadataExample).get(0).getId());
		actionMetadataMapper.updateByPrimaryKey(actionMetadata);
		
	}
	
	@Override
	public List<Action> getAll() {
		List<org.opensrp.domain.postgres.Action> actions = actionMetadataMapper.selectMany(new ActionMetadataExample(), 0,
		    DEFAULT_FETCH_SIZE);
		return convert(actions);
	}
	
	@Override
	public void safeRemove(Action entity) {
		if (entity == null || entity.baseEntityId() == null) {
			return;
		}
		
		Long id = retrievePrimaryKey(entity);
		if (id == null) {
			return;
		}
		
		ActionMetadataExample actionMetadataExample = new ActionMetadataExample();
		actionMetadataExample.createCriteria().andActionIdEqualTo(id);
		int rowsAffected = actionMetadataMapper.deleteByExample(actionMetadataExample);
		if (rowsAffected < 1) {
			return;
		}
		
		actionMapper.deleteByPrimaryKey(id);
		
	}
	
	@Override
	public List<Action> findByProviderIdAndTimeStamp(String providerId, long timeStamp) {
		ActionMetadataExample example = new ActionMetadataExample();
		example.createCriteria().andProviderIdEqualTo(providerId).andServerVersionGreaterThanOrEqualTo(timeStamp + 1);
		return convert(actionMetadataMapper.selectMany(example, 0, DEFAULT_FETCH_SIZE));
	}
	
	@Override
	public List<Action> findAlertByANMIdEntityIdScheduleName(String providerId, String baseEntityId, String scheduleName) {
		ActionMetadataExample example = new ActionMetadataExample();
		example.createCriteria().andProviderIdEqualTo(providerId).andBaseEntityIdEqualTo(baseEntityId);
		return convert(actionMetadataMapper.selectManyBySchedule(example, scheduleName, 0, DEFAULT_FETCH_SIZE));
	}
	
	@Override
	public List<Action> findByCaseIdScheduleAndTimeStamp(String baseEntityId, String schedule, DateTime start,
	                                                     DateTime end) {
		if (start == null || end == null)
			throw new IllegalArgumentException("start and/or end date is null");
		ActionMetadataExample example = new ActionMetadataExample();
		example.createCriteria().andBaseEntityIdEqualTo(baseEntityId).andServerVersionBetween(start.getMillis(),
		    end.getMillis() + 1);
		return convert(actionMetadataMapper.selectManyBySchedule(example, schedule, 0, DEFAULT_FETCH_SIZE));
	}
	
	@Override
	public List<Action> findByCaseIdAndTimeStamp(String baseEntityId, long timeStamp) {
		ActionMetadataExample example = new ActionMetadataExample();
		example.createCriteria().andBaseEntityIdEqualTo(baseEntityId).andServerVersionGreaterThanOrEqualTo(timeStamp);
		return convert(actionMetadataMapper.selectMany(example, 0, DEFAULT_FETCH_SIZE));
	}
	
	@Override
	public void deleteAllByTarget(String target) {
		List<Long> idsToDelete = actionMapper.selectIdsByTarget(target);
		ActionMetadataExample metadataExample = new ActionMetadataExample();
		metadataExample.createCriteria().andActionIdIn(idsToDelete);
		actionMetadataMapper.deleteByExample(metadataExample);
		ActionExample example = new ActionExample();
		example.createCriteria().andIdIn(idsToDelete);
		actionMapper.deleteByExample(example);
		
	}
	
	@Override
	public void markAllAsInActiveFor(String baseEntityId) {
		ActionMetadataExample metadataExample = new ActionMetadataExample();
		metadataExample.createCriteria().andBaseEntityIdEqualTo(baseEntityId);
		List<Action> actions = convert(actionMetadataMapper.selectMany(metadataExample, 0, DEFAULT_FETCH_SIZE));
		for (Action action : actions) {
			action.markAsInActive();
			update(action);
		}
	}
	
	@Override
	public void addOrUpdateAlert(Action alertAction) {
		if (alertAction == null || alertAction.baseEntityId() == null) {
			return;
		}
		
		Long id = retrievePrimaryKey(alertAction);
		if (id == null) {
			add(alertAction);
		} else
			update(alertAction);
		
	}
	
	@Override
	public void markAlertAsInactiveFor(String providerId, String baseEntityId, String scheduleName) {
		ActionMetadataExample metadataExample = new ActionMetadataExample();
		metadataExample.createCriteria().andBaseEntityIdEqualTo(baseEntityId);
		Long actionsSize = actionMetadataMapper.countByExample(metadataExample);
		List<Action> actions = convert(actionMetadataMapper.selectMany(metadataExample, 0, actionsSize.intValue()));
		for (Action action : actions) {
			action.markAsInActive();
			update(action);
		}
		
	}
	
	@Override
	public List<Action> findByCriteria(String team, String providerId, long timeStamp, String sortBy, String sortOrder,
	                                   int limit) {
		ActionMetadataExample metadataExample = new ActionMetadataExample();
		Criteria criteria = metadataExample.createCriteria().andServerVersionGreaterThanOrEqualTo(timeStamp);
		
		if (team != null && !team.isEmpty()) {
			String[] idsArray = org.apache.commons.lang.StringUtils.split(team, ",");
			List<String> ids = new ArrayList<String>(Arrays.asList(idsArray));
			//include providerId records also
			if (providerId != null && !ids.contains(providerId)) {
				ids.add(providerId);
			}
			criteria.andProviderIdIn(ids);
		} else if (StringUtils.isNotEmpty(providerId)) {
			criteria.andProviderIdEqualTo(providerId);
		}
		metadataExample.setOrderByClause(getOrderByClause(sortBy, sortOrder));
		
		if (!criteria.isValid()) {
			throw new IllegalArgumentException("Atleast one search filter must be specified");
		} else
			return convert(actionMetadataMapper.selectMany(metadataExample, 0, limit));
	}
	
	//private methods
	private Action convert(org.opensrp.domain.postgres.Action action) {
		if (action == null || action.getJson() == null || !(action.getJson() instanceof Action)) {
			return null;
		}
		return (Action) action.getJson();
	}
	
	private org.opensrp.domain.postgres.Action convert(Action entity, Long primaryKey) {
		if (entity == null) {
			return null;
		}
		
		org.opensrp.domain.postgres.Action pgAction = new org.opensrp.domain.postgres.Action();
		pgAction.setId(primaryKey);
		pgAction.setJson(entity);
		
		return pgAction;
	}
	
	private List<Action> convert(List<org.opensrp.domain.postgres.Action> actions) {
		if (actions == null || actions.isEmpty()) {
			return new ArrayList<>();
		}
		
		List<Action> convertedActions = new ArrayList<>();
		for (org.opensrp.domain.postgres.Action action : actions) {
			Action convertedAction = convert(action);
			if (convertedAction != null) {
				convertedActions.add(convertedAction);
			}
		}
		
		return convertedActions;
	}
	
	protected Long retrievePrimaryKey(Action entity) {
		if (entity == null || entity.getId() == null) {
			return null;
		}
		String documentId = entity.getId();
		
		ActionMetadataExample actionMetadataExample = new ActionMetadataExample();
		actionMetadataExample.createCriteria().andDocumentIdEqualTo(documentId);
		
		org.opensrp.domain.postgres.Action pgAction = actionMetadataMapper.selectByDocumentId(documentId);
		if (pgAction == null) {
			return null;
		}
		return pgAction.getId();
	}
	
	private ActionMetadata createMetadata(Action entity, Long primaryKey) {
		ActionMetadata actionMetadata = new ActionMetadata();
		actionMetadata.setActionId(primaryKey);
		actionMetadata.setDocumentId(entity.getId());
		actionMetadata.setBaseEntityId(entity.baseEntityId());
		actionMetadata.setServerVersion(entity.getTimeStamp());
		actionMetadata.setProviderId(entity.providerId());
		
		//TODO implement review if in future to support Location, Team and TeamId
		return actionMetadata;
	}
	
	@Override
	protected Object getUniqueField(Action t) {
		if (t == null) {
			return null;
		}
		return t.getId();
	}
	
}
