package org.ei.drishti.repository;

import org.ei.drishti.common.AllConstants;
import org.ei.drishti.domain.Child;
import org.ektorp.CouchDbConnector;
import org.ektorp.ViewResult;
import org.ektorp.support.GenerateView;
import org.ektorp.support.View;
import org.joda.time.LocalDate;
import org.motechproject.dao.MotechBaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @View(name = "by_date_of_birth",
            map = "function(doc) { if (doc.type === 'Child' && doc.isClosed === 'false' && doc.dateOfBirth) { emit(doc.dateOfBirth); } }")
    public List<Child> findAllChildrenLessThanOneYearOldAsOfDate(LocalDate date) {
        return db.queryView(createQuery("by_date_of_birth")
                .startKey(date.minusYears(1))
                .endKey(date.minusDays(1))
                .includeDocs(true),
                Child.class);
    }

    public List<Child> findAllChildrenWhoTurnedOneYearOld(LocalDate date) {
        LocalDate startKey = date.minusYears(1);
        LocalDate endKey = startKey.plusMonths(1);
        List<Child> children_turned_one_year_old_as_of_date = db.queryView(createQuery("by_date_of_birth")
                .startKey(startKey)
                .endKey(endKey)
                .includeDocs(true),
                Child.class);
        return children_turned_one_year_old_as_of_date;
    }

    @View(name = "all_open_children_by_mother_id",
            map = "function(doc) { if (doc.type === 'Child' && doc.isClosed === 'false' && doc.motherCaseId) { emit(doc.motherCaseId); } }")
    public List<Child> findAllOpenChildrenByMotherId(List<String> motherIds) {
        return db.queryView(createQuery("all_open_children_by_mother_id")
                .keys(motherIds)
                .includeDocs(true),
                Child.class);
    }

    public List<Child> findAllChildrenLessThanFiveYearOldAsOfDate(LocalDate date) {
        return db.queryView(createQuery("by_date_of_birth")
                .startKey(date.minusYears(5))
                .endKey(date)
                .includeDocs(true),
                Child.class);
    }

    @View(name = "all_open_children",
            map = "function(doc) { if (doc.type === 'Child' && doc.isClosed === 'false' && doc.anmIdentifier) { emit(doc.anmIdentifier); } }",
            reduce = "_count")
    public Map<String, Integer> openChildCount(List<String> anmIdentifiers) {
        List<ViewResult.Row> rows = db.queryView(createQuery("all_open_children")
                .keys(anmIdentifiers)
                .group(true)
                .reduce(true)
                .cacheOk(true)).getRows();
        Map<String, Integer> openChildCount = new HashMap<>();
        for (ViewResult.Row row : rows) {
            openChildCount.put(row.getKey(), row.getValueAsInt());
        }
        return openChildCount;
    }

    @View(name = "all_open_children_for_anm",
            map = "function(doc) { if (doc.type === 'Child' && doc.isClosed === 'false' && doc.anmIdentifier) { emit(doc.anmIdentifier); } }")
    public List<Child> findAllOpenChildrenForANM(String anmIdentifier) {
        return db
                .queryView(createQuery("all_open_children_for_anm")
                        .key(anmIdentifier)
                        .includeDocs(true), Child.class);
    }

    @View(name = "all_children",
            map = "function(doc) { if (doc.type === 'Child' && doc.isClosed === 'false') { emit(doc.caseId); }}")
    public List<Child> all() {
        return db.queryView(createQuery("all_children").includeDocs(true), Child.class);
    }
}
