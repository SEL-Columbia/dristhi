package org.ei.drishti.repository;

import org.ei.drishti.domain.Action;
import org.ektorp.ComplexKey;
import org.ektorp.CouchDbConnector;
import org.ektorp.support.View;
import org.motechproject.dao.MotechBaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

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
    public void addWithDelete(Action action) {
        removeAll("caseID", action.caseID());
        super.add(action);
    }

    @View(name = "action_by_anm_and_time", map = "function(doc) { if (doc.type === 'Action') { emit([doc.anmIdentifier, doc.timeStamp], null); } }")
    public List<Action> findByANMIDAndTimeStamp(String anmIdentifier, long timeStamp) {
        ComplexKey startKey = ComplexKey.of(anmIdentifier, timeStamp + 1);
        ComplexKey endKey = ComplexKey.of(anmIdentifier, Long.MAX_VALUE);
        return db.queryView(createQuery("action_by_anm_and_time").startKey(startKey).endKey(endKey).includeDocs(true), Action.class);
    }
}
