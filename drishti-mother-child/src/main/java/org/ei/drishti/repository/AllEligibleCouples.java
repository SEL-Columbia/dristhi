package org.ei.drishti.repository;

import org.ei.drishti.domain.EligibleCouple;
import org.ektorp.CouchDbConnector;
import org.ektorp.support.GenerateView;
import org.motechproject.dao.MotechBaseRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public class AllEligibleCouples extends MotechBaseRepository<EligibleCouple> {
    private static Logger logger = LoggerFactory.getLogger(AllEligibleCouples.class);

    @Autowired
    public AllEligibleCouples(@Qualifier("drishtiDatabaseConnector") CouchDbConnector db) {
        super(EligibleCouple.class, db);
    }

    public void register(EligibleCouple couple) {
        add(couple);
    }

    @GenerateView
    public EligibleCouple findByCaseId(String caseId) {
        List<EligibleCouple> couples = queryView("by_caseId", caseId);
        if (couples == null || couples.isEmpty()) {
            return null;
        }
        return couples.get(0);
    }

    public void close(String caseId) {
        EligibleCouple couple = findByCaseId(caseId);
        if (couple == null) {
            logger.warn("Unable to close eligible couple with caseId: " + caseId + ". Case not found.");
            return;
        }
        update(couple.setIsClosed(true));
    }

    public EligibleCouple updateDetails(String caseId, Map<String, String> details) {
        EligibleCouple couple = findByCaseId(caseId);
        couple.details().putAll(details);
        update(couple);

        return couple;
    }
}
