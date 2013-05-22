package org.ei.drishti.repository;

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

    @GenerateView
    public List<Mother> findByEcCaseId(String ecId) {
        List<Mother> mothers = queryView("by_ecCaseId", ecId);
        if (mothers == null || mothers.isEmpty()) {
            return null;
        }
        return mothers;
    }

    public boolean exists(String caseId) {
        return findByCaseId(caseId) != null;
    }

    public Mother updateDetails(String caseId, Map<String, String> details) {
        Mother mother = findByCaseId(caseId);
        mother.details().putAll(details);
        update(mother);

        return mother;
    }

    public Mother updateDeliveryOutcomeFor(String caseId, String dateOfDelivery) {
        Mother mother = findByCaseId(caseId);
        mother.withDeliveryOutCome(dateOfDelivery);
        update(mother);

        return mother;
    }

    public void close(String caseId) {
        Mother mother = findByCaseId(caseId);
        update(mother.setIsClosed(true));
    }

    public void closeAllMothersForEC(String ecId) {
        List<Mother> mothers = findByEcCaseId(ecId);
        if (mothers == null || mothers.isEmpty()) {
            return;
        }

        for (Mother mother : mothers) {
            mother.setIsClosed(true);
            update(mother);
        }
    }
}
