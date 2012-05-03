package org.ei.drishti.repository;

import org.ei.drishti.domain.EligibleCouple;
import org.ektorp.CouchDbConnector;
import org.ektorp.support.GenerateView;
import org.motechproject.dao.MotechBaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AllEligibleCouples extends MotechBaseRepository<EligibleCouple> {
    @Autowired
    public AllEligibleCouples(@Qualifier("drishtiDatabaseConnector") CouchDbConnector db) {
        super(EligibleCouple.class, db);
    }

    public void register(EligibleCouple couple) {
        add(couple);
    }

    @GenerateView
    private EligibleCouple findByCaseId(String caseId) {
        List<EligibleCouple> couples = queryView("by_caseId", caseId);
        if (couples == null || couples.isEmpty()) {
            return null;
        }
        return couples.get(0);
    }

    public void close(String caseId) {
        remove(findByCaseId(caseId));
    }
}
