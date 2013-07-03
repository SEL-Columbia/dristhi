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
import java.util.Map;

import static java.util.Arrays.asList;
import static org.ei.drishti.common.AllConstants.ChildImmunizationFields.IMMUNIZATIONS_GIVEN_FIELD_NAME;
import static org.springframework.util.StringUtils.collectionToDelimitedString;

@Repository
public class AllChildren extends MotechBaseRepository<Child> {
    @Autowired
    protected AllChildren(@Qualifier("drishtiDatabaseConnector") CouchDbConnector db) {
        super(Child.class, db);
    }

    @GenerateView
    public Child findByCaseId(String caseId) {
        return findChild(caseId, "by_caseId");
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

    private Child findChild(String caseId, String criteria) {
        List<Child> children = queryView(criteria, caseId);
        if (children == null || children.isEmpty()) {
            return null;
        }
        return children.get(0);
    }
}
