package org.ei.drishti.repository;

import org.ei.drishti.domain.AlertAction;
import org.ektorp.CouchDbConnector;
import org.motechproject.dao.MotechBaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

@Repository
public class AllAlertActions extends MotechBaseRepository<AlertAction> {
    @Autowired
    protected AllAlertActions(@Qualifier("drishtiDatabaseConnector") CouchDbConnector db) {
        super(AlertAction.class, db);
    }

    public void add(AlertAction alertAction) {
        super.add(alertAction);
    }
}
