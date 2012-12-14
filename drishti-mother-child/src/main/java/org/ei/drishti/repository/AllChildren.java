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

import static java.util.Arrays.asList;
import static org.ei.drishti.common.AllConstants.ChildImmunizationCommCareFields.IMMUNIZATIONS_PROVIDED_COMMCARE_FIELD_NAME;
import static org.springframework.util.StringUtils.collectionToDelimitedString;

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

    public Child update(String caseId, Map<String, String> details) {
        Child child = findByCaseId(caseId);
        child = updateChildImmunizationIfProvided(child, details);
        child.details().putAll(details);
        update(child);
        return child;
    }

    private Child updateChildImmunizationIfProvided(Child child, Map<String, String> details) {
        if(details.containsKey(IMMUNIZATIONS_PROVIDED_COMMCARE_FIELD_NAME)){
            List<String> immunizationsProvided = child.immunizationsProvided();
            List<String> recentImmunizations = asList(details.get(IMMUNIZATIONS_PROVIDED_COMMCARE_FIELD_NAME).split(" "));
            for (String recentImmunization : recentImmunizations) {
                if(!immunizationsProvided.contains(recentImmunization)){
                    immunizationsProvided.add(recentImmunization);
                }
            }
            child.setImmunizationsProvided(immunizationsProvided);
            details.put(IMMUNIZATIONS_PROVIDED_COMMCARE_FIELD_NAME, collectionToDelimitedString(immunizationsProvided, " "));
        }
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
