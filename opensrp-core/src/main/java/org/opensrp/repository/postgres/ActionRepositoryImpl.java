package org.opensrp.repository.postgres;

import java.util.List;

import org.joda.time.DateTime;
import org.opensrp.scheduler.Action;
import org.opensrp.scheduler.repository.ActionsRepository;
import org.springframework.stereotype.Repository;

@Repository
public class ActionRepositoryImpl implements ActionsRepository {

	@Override
	public Action get(String id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void add(Action entity) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void update(Action entity) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<Action> getAll() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void safeRemove(Action entity) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<Action> findByProviderIdAndTimeStamp(String providerId, long timeStamp) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Action> findAlertByANMIdEntityIdScheduleName(String providerId, String baseEntityId, String scheduleName) {
		// TODO Auto-generated method stub
		return null;
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
	
}
