package org.opensrp.scheduler.repository;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.ektorp.BulkDeleteDocument;
import org.ektorp.ComplexKey;
import org.ektorp.CouchDbConnector;
import org.ektorp.support.GenerateView;
import org.ektorp.support.View;
import org.joda.time.DateTime;
import org.motechproject.dao.MotechBaseRepository;
import org.opensrp.common.AllConstants;
import org.opensrp.scheduler.Action;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

@Repository
public class AllActions extends MotechBaseRepository<Action> {
    private static Logger logger = LoggerFactory.getLogger(AllActions.class.toString());

    @Autowired
    protected AllActions(@Qualifier(AllConstants.OPENSRP_DATABASE_CONNECTOR) CouchDbConnector db) {
        super(Action.class, db);
    }

    @View(name = "action_by_provider_and_time", map = "function(doc) { if (doc.type === 'Action') { emit([doc.providerId, doc.timeStamp], null); } }")
    public List<Action> findByProviderIdAndTimeStamp(String providerId, long timeStamp) {
        ComplexKey startKey = ComplexKey.of(providerId, timeStamp + 1);
        ComplexKey endKey = ComplexKey.of(providerId, Long.MAX_VALUE);
        return db.queryView(createQuery("action_by_provider_and_time").startKey(startKey).endKey(endKey).includeDocs(true), Action.class);
    }

    @View(name = "action_by_provider_entityId_scheduleName",
            map = "function(doc) { " +
                    "if(doc.type === 'Action' && doc.actionTarget === 'alert' && doc.providerId && doc.baseEntityId && doc.data && doc.data.scheduleName) {" +
                    "emit([doc.providerId, doc.baseEntityId, doc.data.scheduleName], null)} " +
                    "}")
    public List<Action> findAlertByANMIdEntityIdScheduleName(String providerId, String baseEntityId, String scheduleName) {
        ComplexKey key = ComplexKey.of(providerId, baseEntityId, scheduleName);
        return db.queryView(createQuery("action_by_provider_entityId_scheduleName").key(key).includeDocs(true), Action.class);
    }

    @View(name = "action_by_baseEntityId_and_schedule_and_time", map = "function(doc) { if (doc.type === 'Action') { emit([doc.baseEntityId, doc.data.scheduleName, doc.timeStamp], null); } }")
    public List<Action> findByCaseIdScheduleAndTimeStamp(String baseEntityId, String schedule, DateTime start, DateTime end) {
        ComplexKey startKey = ComplexKey.of(baseEntityId, schedule, start.getMillis());
        ComplexKey endKey = ComplexKey.of(baseEntityId, schedule, end.getMillis() + 1);
        return db.queryView(createQuery("action_by_baseEntityId_and_schedule_and_time").startKey(startKey).endKey(endKey).includeDocs(true), Action.class);
    }
    
    public void deleteAllByTarget(String target) {
        deleteAll(findByActionTarget(target));
    }

    public void markAllAsInActiveFor(String baseEntityId) {
        List<Action> actions = findByBaseEntityId(baseEntityId);
        for (Action action : actions) {
            action.markAsInActive();
        }
        db.executeBulk(actions);
    }

    @GenerateView
    private List<Action> findByActionTarget(String target) {
        return queryView("by_actionTarget", target);
    }

    @GenerateView
    private List<Action> findByBaseEntityId(String baseEntityId) {
        return queryView("by_baseEntityId", baseEntityId);
    }

    private void deleteAll(List<Action> actions) {
        ArrayList<BulkDeleteDocument> deleteDocuments = new ArrayList<>();
        for (Action action : actions) {
            deleteDocuments.add(BulkDeleteDocument.of(action));
        }
        db.executeBulk(deleteDocuments);
    }

    public void addOrUpdateAlert(Action alertAction) {
        List<Action> existingAlerts = findAlertByANMIdEntityIdScheduleName(alertAction.providerId(), alertAction.baseEntityId(), alertAction.data().get("scheduleName"));
        if (existingAlerts.size() > 1) {
            logger.warn(MessageFormat.format("Found more than one alert for the combination of providerId: {0}, entityId: {1} and scheduleName : {2}. Alerts : {3}",
                    alertAction.providerId(), alertAction.baseEntityId(), alertAction.data().get("scheduleName"), existingAlerts));
        }
        for (Action existingAlert : existingAlerts) {
            safeRemove(existingAlert);
        }
        add(alertAction);
    }

    public void markAlertAsInactiveFor(String providerId, String baseEntityId, String scheduleName) {
        List<Action> existingAlerts = findAlertByANMIdEntityIdScheduleName(providerId, baseEntityId, scheduleName);
        if (existingAlerts.size() > 1) {
            logger.warn(MessageFormat.format("Found more than one alert for the combination of providerId: {0}, entityId: {1} and scheduleName : {2}. Alerts : {3}",
                    providerId, baseEntityId, scheduleName, existingAlerts));
        }
        for (Action existingAlert : existingAlerts) {
            existingAlert.markAsInActive();
        }
        db.executeBulk(existingAlerts);
    }
}
