package org.ei.drishti.reporting.repository;

import org.ei.drishti.reporting.domain.ANMReportData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class AllANMReportDataRepository {
    private DataAccessTemplate dataAccessTemplate;

    protected AllANMReportDataRepository() {
    }

    @Autowired
    public AllANMReportDataRepository(@Qualifier("anmReportsDataAccessTemplate") DataAccessTemplate dataAccessTemplate) {
        this.dataAccessTemplate = dataAccessTemplate;
    }

    public void save(int anmIdentifier, String externalId, int indicator, int date) {
        dataAccessTemplate.save(new ANMReportData(anmIdentifier, externalId, indicator, date));
    }
}
