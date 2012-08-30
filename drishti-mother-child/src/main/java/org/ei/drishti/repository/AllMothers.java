package org.ei.drishti.repository;

import org.ei.drishti.domain.EligibleCouple;
import org.ei.drishti.domain.Mother;
import org.ektorp.CouchDbConnector;
import org.ektorp.support.GenerateView;
import org.motechproject.dao.MotechBaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public class AllMothers extends MotechBaseRepository<Mother> {
    @Autowired
    public AllMothers(@Qualifier("drishtiDatabaseConnector") CouchDbConnector db) {
        super(Mother.class, db);
    }

    public void register(Mother mother) {
        add(mother);
    }

    @GenerateView
    public Mother findByCaseId(String caseId) {
        List<Mother> mothers = queryView("by_caseId", caseId);
        if (mothers == null || mothers.isEmpty()) {
            return null;
        }
        return mothers.get(0);
    }

    @GenerateView
    public Mother findByThaayiCardNumber(String thaayiCardNumber) {
        List<Mother> mothers = queryView("by_thaayiCardNumber", thaayiCardNumber);
        if (mothers == null || mothers.isEmpty()) {
            return null;
        }
        return mothers.get(0);
    }

    public boolean motherExists(String caseId) {
        return findByCaseId(caseId) != null;
    }

    public Mother updateDetails(String caseId, Map<String, String> details) {
        Mother mother = findByCaseId(caseId);
        mother.details().putAll(details);
        update(mother);

        return mother;
    }
}
