package org.ei.drishti.repository;

import org.ei.drishti.common.AllConstants;
import org.ei.drishti.domain.Child;
import org.ektorp.CouchDbConnector;
import org.ektorp.support.GenerateView;
import org.ektorp.support.View;
import org.joda.time.LocalDate;
import org.motechproject.dao.MotechBaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AllChildren extends MotechBaseRepository<Child> {
    @Autowired
    protected AllChildren(@Qualifier(AllConstants.DRISHTI_DATABASE_CONNECTOR) CouchDbConnector db) {
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

    @View(name = "children_less_than_one_year_old_as_of_date",
            map = "function(doc) { if (doc.type === 'Child' && !doc.isClosed && doc.dateOfBirth) { emit(new Date(Date.parse(doc.dateOfBirth))); } }")
    public List<Child> findAllChildrenLessThanOneYearOldAsOfDate(LocalDate date) {
        return db.queryView(createQuery("children_less_than_one_year_old_as_of_date")
                .startKey(date.minusYears(1))
                .endKey(date)
                .includeDocs(true),
                Child.class);
    }
}
