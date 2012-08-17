package org.ei.drishti.repository;

import org.ei.drishti.domain.EligibleCouple;
import org.ektorp.ComplexKey;
import org.ektorp.CouchDbConnector;
import org.ektorp.support.GenerateView;
import org.ektorp.support.View;
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
        remove(couple);
    }

    @View(name = "eligible_couple_by_ecnumber_and_village", map = "function(doc) { if (doc.type === 'EligibleCouple') { emit([doc.ecNumber, doc.village], null); } }")
    public EligibleCouple findByECNumberAndVillage(String ecNumber, String village) {
        ComplexKey key = ComplexKey.of(ecNumber, village);

        List<EligibleCouple> couples = db.queryView(createQuery("eligible_couple_by_ecnumber_and_village").key(key).includeDocs(true), EligibleCouple.class);
        if (couples.size() == 0) {
            return null;
        }
        return couples.get(0);
    }
}
