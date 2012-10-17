package org.ei.drishti.repository;

import org.ei.drishti.domain.Child;
import org.ektorp.CouchDbConnector;
import org.ektorp.support.GenerateView;
import org.motechproject.dao.MotechBaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

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
        return findChild(caseId, "by_caseId");
    }

    @GenerateView
    public Child findByMotherCaseId(String motherCaseId) {
        return findChild(motherCaseId, "by_motherCaseId");
    }

    public Child updateDetails(String caseId, Map<String, String> details) {
        Child child = findByCaseId(caseId);
        child.details().putAll(details);
        update(child);
        return child;
    }

    public boolean childExists(String caseId) {
        return findByCaseId(caseId) != null;
    }

    private Child findChild(String caseId, String criteria) {
        List<Child> children = queryView(criteria, caseId);
        if (children == null || children.isEmpty()) {
            return null;
        }
        return children.get(0);
    }
}
