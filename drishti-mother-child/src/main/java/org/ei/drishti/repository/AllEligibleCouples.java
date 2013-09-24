package org.ei.drishti.repository;

import org.ei.drishti.common.AllConstants;
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

@Repository
public class AllEligibleCouples extends MotechBaseRepository<EligibleCouple> {
    private static Logger logger = LoggerFactory.getLogger(AllEligibleCouples.class);
    private final AllMothers allMothers;

    @Autowired
    public AllEligibleCouples(@Qualifier(AllConstants.DRISHTI_DATABASE_CONNECTOR) CouchDbConnector db, AllMothers allMothers) {
        super(EligibleCouple.class, db);
        this.allMothers = allMothers;
    }

    @GenerateView
    public EligibleCouple findByCaseId(String caseId) {
        List<EligibleCouple> couples = queryView("by_caseId", caseId);
        if (couples == null || couples.isEmpty()) {
            return null;
        }
        return couples.get(0);
    }

    public boolean exists(String caseId) {
        return findByCaseId(caseId) != null;
    }

    public void close(String caseId) {
        EligibleCouple couple = findByCaseId(caseId);
        if (couple == null) {
            logger.warn("Unable to close eligible couple with caseId: " + caseId + ". Case not found.");
            return;
        }
        update(couple.setIsClosed(true));
        allMothers.closeAllMothersForEC(caseId);
    }
}
