package org.opensrp.repository.postgres;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.opensrp.domain.postgres.ActionExample;
import org.opensrp.domain.postgres.ActionMetadata;
import org.opensrp.domain.postgres.ActionMetadataExample;
import org.opensrp.domain.postgres.EventMetadataExample;
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
		org.opensrp.domain.postgres.Action pgAction = actionMapper.selectByDocumentId(id);
		
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
		
		org.opensrp.domain.postgres.Action pgAction = convert(entity, null);
		if (pgAction == null) {
			return;
		}
		
		int rowsAffected = actionMapper.insertSelectiveAndSetId(pgAction);
		if (rowsAffected < 1 || pgAction.getId() == null) {
			return;
		}
		
		ActionMetadata eventMetadata = createMetadata(entity, pgAction.getId());
		if (eventMetadata != null) {
			actionMetadataMapper.insertSelective(eventMetadata);
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
		
		org.opensrp.domain.postgres.Action pgAction = convert(entity, id);
		if (pgAction == null) {
			return;
		}
		
		ActionMetadata actionMetadata = createMetadata(entity, id);
		if (actionMetadata == null) {
			return;
		}
		
		int rowsAffected = actionMapper.updateByPrimaryKeySelective(pgAction);
		if (rowsAffected < 1) {
			return;
		}
		
		ActionMetadataExample actionMetadataExample = new ActionMetadataExample();
		actionMetadataExample.createCriteria().andActionIdEqualTo(id);
		actionMetadataMapper.updateByExampleSelective(actionMetadata, actionMetadataExample);
		
	}
	
	@Override
	public List<Action> getAll() {
		List<org.opensrp.domain.postgres.Action> actions = actionMapper.selectByExample(new ActionExample());
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
		return convert(actionMetadataMapper.selectMany(example, 0, DEFAULT_FETCH_SIZE));
	}
	
	@Override
	public List<Action> findByCaseIdScheduleAndTimeStamp(String baseEntityId, String schedule, DateTime start,
	                                                     DateTime end) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public List<Action> findByCaseIdAndTimeStamp(String baseEntityId, long timeStamp) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void deleteAllByTarget(String target) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void markAllAsInActiveFor(String baseEntityId) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void addOrUpdateAlert(Action alertAction) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void markAlertAsInactiveFor(String providerId, String baseEntityId, String scheduleName) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public List<Action> findByCriteria(String team, String providerId, long timeStamp, String sortBy, String sortOrder,
	                                   int limit) {
		// TODO Auto-generated method stub
		return null;
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
		if (entity == null) {
			return null;
		}
		String documentId = entity.getId();
		
		EventMetadataExample eventMetadataExample = new EventMetadataExample();
		eventMetadataExample.createCriteria().andDocumentIdEqualTo(documentId);
		
		org.opensrp.domain.postgres.Action pgClient = actionMetadataMapper.selectByDocumentId(documentId);
		if (pgClient == null) {
			return null;
		}
		return pgClient.getId();
	}
	
	private ActionMetadata createMetadata(Action entity, Long primaryKey) {
		ActionMetadata actionMetadata = new ActionMetadata();
		actionMetadata.setActionId(primaryKey);
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
