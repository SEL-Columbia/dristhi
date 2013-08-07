package org.ei.drishti.repository;

import org.ei.drishti.domain.Child;
import org.ektorp.CouchDbConnector;
import org.ektorp.support.GenerateView;
import org.ektorp.support.View;
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

    @GenerateView
    public Child findByCaseId(String caseId) {
        List<Child> children = queryView("by_caseId", caseId);
        if (children == null || children.isEmpty()) {
            return null;
        }
        return children.get(0);
    }

    @View(name = "children_by_mother_id", map = "function(doc) { if (doc.type === 'Child') { emit(doc.motherCaseId); } }")
    public List<Child> findByMotherId(String entityId) {
        return db.queryView(createQuery("children_by_mother_id").key(entityId).includeDocs(true), Child.class);
    }

    public boolean childExists(String caseId) {
        return findByCaseId(caseId) != null;
    }

    public void close(String caseId) {
        Child child = findByCaseId(caseId);
        update(child.setIsClosed(true));
    }

    public void remove(String id) {
        Child child = findByCaseId(id);
        remove(child);
    }
}
