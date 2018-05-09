package org.opensrp.scheduler.repository;

import java.util.List;

import org.joda.time.DateTime;
import org.opensrp.repository.BaseRepository;
import org.opensrp.scheduler.Action;

public interface ActionsRepository extends BaseRepository<Action>{
	
	List<Action> findByProviderIdAndTimeStamp(String providerId, long timeStamp);
	
	List<Action> findAlertByANMIdEntityIdScheduleName(String providerId, String baseEntityId, String scheduleName);
	
	List<Action> findByCaseIdScheduleAndTimeStamp(String baseEntityId, String schedule, DateTime start, DateTime end);
	
	List<Action> findByCaseIdAndTimeStamp(String baseEntityId, long timeStamp);
	
	void deleteAllByTarget(String target);
	
	void markAllAsInActiveFor(String baseEntityId);
	
	void addOrUpdateAlert(Action alertAction);
	
	void markAlertAsInactiveFor(String providerId, String baseEntityId, String scheduleName);
	
	List<Action> findByCriteria(String team, String providerId, long timeStamp, String sortBy, String sortOrder, int limit);
}
