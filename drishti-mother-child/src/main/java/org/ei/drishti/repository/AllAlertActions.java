package org.ei.drishti.repository;

import org.ei.drishti.domain.AlertAction;
import org.ektorp.ComplexKey;
import org.ektorp.CouchDbConnector;
import org.ektorp.support.View;
import org.motechproject.dao.MotechBaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AllAlertActions extends MotechBaseRepository<AlertAction> {
    @Autowired
    protected AllAlertActions(@Qualifier("drishtiDatabaseConnector") CouchDbConnector db) {
        super(AlertAction.class, db);
    }

    @View(name = "by_caseID", map = "function(doc) { if(doc.type === 'AlertAction' && doc.caseID) {emit(doc.caseID, null)} }")
    public void add(AlertAction alertAction) {
        if (alertAction.alertType().equals("deleteAll")) {
            removeAll("caseID", alertAction.caseID());
        }
        super.add(alertAction);
    }

    @View(name = "alert_by_anm_and_time", map = "function(doc) { if (doc.type === 'AlertAction') { emit([doc.anmIdentifier, doc.timeStamp], null); } }")
    public List<AlertAction> findByANMIDAndTimeStamp(String anmIdentifier, long timeStamp) {
        ComplexKey startKey = ComplexKey.of(anmIdentifier, timeStamp + 1);
        ComplexKey endKey = ComplexKey.of(anmIdentifier, Long.MAX_VALUE);
        return db.queryView(createQuery("alert_by_anm_and_time").startKey(startKey).endKey(endKey).includeDocs(true), AlertAction.class);
    }
}
