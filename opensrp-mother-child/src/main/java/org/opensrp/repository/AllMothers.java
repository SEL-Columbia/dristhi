package org.opensrp.repository;

import org.opensrp.common.AllConstants;
import org.opensrp.domain.Mother;
import org.ektorp.CouchDbConnector;
import org.ektorp.ViewResult;
import org.ektorp.support.GenerateView;
import org.ektorp.support.View;
import org.motechproject.dao.MotechBaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
            map = "function(doc) { if (doc.type === 'Mother' && doc.isClosed === 'false' && doc.ecCaseId) { emit(doc.ecCaseId); } }")
    public List<Mother> findAllOpenMothersByECCaseId(List<String> ecIds) {
        return db.queryView(createQuery("all_open_mothers_by_ec_caseId")
                        .keys(ecIds)
                        .includeDocs(true),
                Mother.class
        );
    }

    @View(name = "all_open_anc_by_ec_caseId",
            map = "function(doc) { if (doc.type === 'Mother' && doc.isClosed === 'false' && doc.details.type === 'ANC' && doc.ecCaseId) { emit(doc.ecCaseId); } }")
    public List<Mother> findAllOpenANCByECCaseId(String ecId) {
        return db.queryView(createQuery("all_open_anc_by_ec_caseId")
                        .key(ecId)
                        .includeDocs(true),
                Mother.class
        );
    }

    @View(name = "all_open_mothers_by_anmId",
            map = "function(doc) { if (doc.type === 'Mother' && doc.isClosed === 'false' && doc.anmIdentifier && doc.details.type === 'ANC') { emit(doc.anmIdentifier); } }")
    public List<Mother> findAllOpenMothersForANM(String anmIdentifier) {
        return db.queryView(createQuery("all_open_mothers_by_anmId")
                .key(anmIdentifier)
                .includeDocs(true), Mother.class);
    }

    @View(name = "all_open_mothers_count",
            map = "function(doc) { if (doc.type === 'Mother' && doc.isClosed === 'false' && doc.anmIdentifier && doc.details.type === 'ANC') { emit(doc.anmIdentifier); } }",
            reduce = "_count")
    public Map<String, Integer> allOpenMotherCount(List<String> anmIdentifiers) {
        List<ViewResult.Row> rows = db.queryView(createQuery("all_open_mothers_count")
                .keys(anmIdentifiers)
                .group(true)
                .reduce(true)
                .cacheOk(true)).getRows();
        Map<String, Integer> openMotherCount = new HashMap<>();
        for (ViewResult.Row row : rows) {
            openMotherCount.put(row.getKey(), row.getValueAsInt());
        }
        return openMotherCount;
    }

    @View(name = "all_open_pncs",
            map = "function(doc) { if (doc.type === 'Mother' && doc.isClosed === 'false' && doc.anmIdentifier && doc.details.type === 'PNC') { emit(doc.anmIdentifier); } }",
            reduce = "_count")
    public Map<String, Integer> allOpenPNCCount(List<String> anmIdentifiers) {
        List<ViewResult.Row> rows = db.queryView(createQuery("all_open_pncs")
                .keys(anmIdentifiers)
                .group(true)
                .reduce(true)
                .cacheOk(true)).getRows();
        Map<String, Integer> openPNCCount = new HashMap<>();
        for (ViewResult.Row row : rows) {
            openPNCCount.put(row.getKey(), row.getValueAsInt());
        }
        return openPNCCount;
    }

    @View(name = "all_mothers_by_CaseIDs",
            map = "function(doc) { if (doc.type === 'Mother' && doc.caseId) { emit(doc.caseId); } }")
    public List<Mother> findAll(List<String> motherIds) {
        return db.queryView(createQuery("all_mothers_by_CaseIDs")
                        .keys(motherIds)
                        .includeDocs(true),
                Mother.class
        );
    }

    @View(name = "all_open_pncs_by_anmId",
            map = "function(doc) { if (doc.type === 'Mother' && doc.isClosed === 'false' && doc.anmIdentifier && doc.details.type === 'PNC') { emit(doc.anmIdentifier); } }")
    public List<Mother> findAllOpenPNCsForANM(String anmIdentifier) {
        return db.queryView(createQuery("all_open_pncs_by_anmId")
                .key(anmIdentifier)
                .includeDocs(true), Mother.class);

    }

    @View(name = "all_mothers",
            map = "function(doc) { if (doc.type === 'Mother') { emit(doc.anmIdentifier); } }")
    public List<Mother> all(String anmIdentifier) {
        return db.queryView(createQuery("all_mothers")
                .key(anmIdentifier)
                .includeDocs(true), Mother.class);
    }

}
