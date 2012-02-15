package org.ei.drishti.repository;

import org.ei.drishti.domain.Mother;
import org.ektorp.CouchDbConnector;
import org.ektorp.support.GenerateView;
import org.motechproject.dao.MotechBaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.List;

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
}
