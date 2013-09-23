package org.ei.drishti.repository;

import org.ei.drishti.domain.InfantBalanceOnHandReportToken;
import org.ektorp.CouchDbConnector;
import org.motechproject.dao.MotechBaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

@Repository
public class AllInfantBalanceOnHandReportTokens extends MotechBaseRepository<InfantBalanceOnHandReportToken> {
    @Autowired
    protected AllInfantBalanceOnHandReportTokens(@Qualifier("drishtiDatabaseConnector") CouchDbConnector db) {
        super(InfantBalanceOnHandReportToken.class, db);
    }
}
