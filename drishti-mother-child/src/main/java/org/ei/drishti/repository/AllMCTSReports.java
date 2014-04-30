package org.ei.drishti.repository;

import org.ei.drishti.common.AllConstants;
import org.ei.drishti.domain.MCTSReport;
import org.ektorp.CouchDbConnector;
import org.motechproject.dao.MotechBaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

@Repository
public class AllMCTSReports extends MotechBaseRepository<MCTSReport> {

    @Autowired
    public AllMCTSReports(@Qualifier(AllConstants.DRISHTI_MCTS_DATABASE_CONNECTOR) CouchDbConnector db) {
        super(MCTSReport.class, db);
    }
}
