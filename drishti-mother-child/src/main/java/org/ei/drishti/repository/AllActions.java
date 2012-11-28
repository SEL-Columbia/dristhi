package org.ei.drishti.repository;

import org.ei.drishti.domain.Action;
import org.ektorp.BulkDeleteDocument;
import org.ektorp.ComplexKey;
import org.ektorp.CouchDbConnector;
import org.ektorp.support.GenerateView;
import org.ektorp.support.View;
import org.motechproject.dao.MotechBaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class AllActions extends MotechBaseRepository<Action> {
    @Autowired
    protected AllActions(@Qualifier("drishtiDatabaseConnector") CouchDbConnector db) {
        super(Action.class, db);
    }

    public void add(Action action) {
        super.add(action);
    }

    @View(name = "by_caseID", map = "function(doc) { if(doc.type === 'Action' && doc.caseID) {emit(doc.caseID, null)} }")
    public void addWithDelete(Action action, String actionTarget) {
        deleteAll(findByActionTargetAndCaseId(actionTarget, action.caseID()));
        super.add(action);
    }

    public void deleteAllByTarget(String target) {
        deleteAll(findByActionTarget(target));
    }

    @GenerateView
    private List<Action> findByActionTarget(String target) {
        return queryView("by_actionTarget", target);
    }

    @View(name = "action_by_anm_and_time", map = "function(doc) { if (doc.type === 'Action') { emit([doc.anmIdentifier, doc.timeStamp], null); } }")
    public List<Action> findByANMIDAndTimeStamp(String anmIdentifier, long timeStamp) {
        ComplexKey startKey = ComplexKey.of(anmIdentifier, timeStamp + 1);
        ComplexKey endKey = ComplexKey.of(anmIdentifier, Long.MAX_VALUE);
        return db.queryView(createQuery("action_by_anm_and_time").startKey(startKey).endKey(endKey).includeDocs(true), Action.class);
    }

    @View(name = "action_by_target_and_caseId", map = "function(doc) { if (doc.type === 'Action') { emit([doc.actionTarget, doc.caseID], null); } }")
    private List<Action> findByActionTargetAndCaseId(String actionTarget, String caseId) {
        ComplexKey key = ComplexKey.of(actionTarget, caseId);
        return db.queryView(createQuery("action_by_target_and_caseId").key(key).includeDocs(true), Action.class);
    }

    private void deleteAll(List<Action> actions) {
        ArrayList<BulkDeleteDocument> deleteDocuments = new ArrayList<BulkDeleteDocument>();
        for (Action action : actions) {
            deleteDocuments.add(BulkDeleteDocument.of(action));
        }
        db.executeBulk(deleteDocuments);
    }
}
