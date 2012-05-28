package org.ei.drishti.repository;

import org.ei.drishti.domain.Child;
import org.ektorp.CouchDbConnector;
import org.ektorp.support.GenerateView;
import org.motechproject.dao.MotechBaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AllChildren extends MotechBaseRepository<Child> {
    @Autowired
    protected AllChildren(@Qualifier("drishtiDatabaseConnector") CouchDbConnector db) {
        super(Child.class, db);
    }

    public void register(Child child) {
        add(child);
    }

    @GenerateView
    public Child findByCaseId(String caseId) {
        List<Child> children = queryView("by_caseId", caseId);
        if (children == null || children.isEmpty()) {
            return null;
        }
        return children.get(0);
    }
}
