package org.ei.drishti.repository;

import org.ei.drishti.common.AllConstants;
import org.ei.drishti.domain.Mother;
import org.ektorp.CouchDbConnector;
import org.ektorp.support.GenerateView;
import org.ektorp.support.View;
import org.motechproject.dao.MotechBaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AllMothers extends MotechBaseRepository<Mother> {
    @Autowired
    public AllMothers(@Qualifier(AllConstants.DRISHTI_DATABASE_CONNECTOR) CouchDbConnector db) {
        super(Mother.class, db);
    }

    @GenerateView
    public Mother findByCaseId(String caseId) {
        List<Mother> mothers = queryView("by_caseId", caseId);
        if (mothers == null || mothers.isEmpty()) {
            return null;
        }
        return mothers.get(0);
    }

    public boolean exists(String caseId) {
        return findByCaseId(caseId) != null;
    }

    @GenerateView
    public List<Mother> findByEcCaseId(String ecId) {
        List<Mother> mothers = queryView("by_ecCaseId", ecId);
        if (mothers == null || mothers.isEmpty()) {
            return null;
        }
        return mothers;
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

    @View(name = "all_open_mothers_by_ec_caseId",
            map = "function(doc) { if (doc.type === 'Mother' && !doc.isClosed && doc.ecCaseId) { emit(doc.ecCaseId); } }")
    public List<Mother> findAllOpenMothersByECCaseId(List<String> ecIds) {
        return db.queryView(createQuery("all_open_mothers_by_ec_caseId")
                .keys(ecIds)
                .includeDocs(true),
                Mother.class);
    }

    @View(name = "all_open_mothers_by_anmId",
            map = "function(doc) { if (doc.type === 'Mother' && !doc.isClosed && doc.anmIdentifier) { emit(doc.anmIdentifier); } }")
    public List<Mother> findAllOpenMothersForANM(String anmIdentifier) {
        return db.queryView(createQuery("all_open_mothers_by_anmId")
                .key(anmIdentifier)
                .includeDocs(true),
                Mother.class);
    }
}
