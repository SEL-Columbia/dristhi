package org.ei.drishti.reporting.repository;

import org.ei.drishti.reporting.domain.ANM;
import org.ei.drishti.reporting.domain.ANMReportData;
import org.ei.drishti.reporting.domain.Dates;
import org.ei.drishti.reporting.domain.Indicator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

import static org.ei.drishti.reporting.domain.ANMReportData.FIND_BY_ANM_IDENTIFIER_AND_DATE;

@Repository
public class AllANMReportDataRepository {
    private DataAccessTemplate dataAccessTemplate;

    protected AllANMReportDataRepository() {
    }

    @Autowired
    public AllANMReportDataRepository(@Qualifier("anmReportsDataAccessTemplate") DataAccessTemplate dataAccessTemplate) {
        this.dataAccessTemplate = dataAccessTemplate;
    }

    public void save(ANM anm, String externalId, Indicator indicator, Dates date) {
        dataAccessTemplate.save(new ANMReportData(anm, externalId, indicator, date));
    }

    public List<ANMReportData> fetchByANMIdAndDate(String anmIdentifier, Date date) {
        return (List<ANMReportData>) dataAccessTemplate.findByNamedQueryAndNamedParam(FIND_BY_ANM_IDENTIFIER_AND_DATE, new String[]{"anmIdentifier", "date"}, new Object[]{anmIdentifier, date});

    }
}
