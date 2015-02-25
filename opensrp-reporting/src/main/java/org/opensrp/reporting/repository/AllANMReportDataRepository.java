package org.opensrp.reporting.repository;

import org.joda.time.LocalDate;
import org.opensrp.reporting.domain.ANM;
import org.opensrp.reporting.domain.ANMReportData;
import org.opensrp.reporting.domain.Indicator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

import static org.opensrp.reporting.domain.ANMReportData.*;

@Repository
public class AllANMReportDataRepository {
    private DataAccessTemplate dataAccessTemplate;

    protected AllANMReportDataRepository() {
    }

    @Autowired
    public AllANMReportDataRepository(@Qualifier("anmReportsDataAccessTemplate") DataAccessTemplate dataAccessTemplate) {
        this.dataAccessTemplate = dataAccessTemplate;
    }

    public void save(ANM anm, String externalId, Indicator indicator, Date date) {
        dataAccessTemplate.save(new ANMReportData(anm, externalId, indicator, date));
    }

    public List<ANMReportData> fetchByANMIdAndDate(String anmIdentifier, Date date) {
        return (List<ANMReportData>) dataAccessTemplate.findByNamedQueryAndNamedParam(FIND_BY_ANM_IDENTIFIER_AND_DATE,
                new String[]{"anmIdentifier", "date"}, new Object[]{anmIdentifier, date});

    }

    public void delete(String indicator, String startDate, String endDate) {
        List result = dataAccessTemplate.findByNamedQuery(FIND_BY_ANM_IDENTIFIER_WITH_INDICATOR_FOR_MONTH,
                new Object[]{indicator, LocalDate.parse(startDate).toDate(), LocalDate.parse(endDate).toDate()});
        dataAccessTemplate.deleteAll(result);
    }

    public List getReportsFor(String anmId, String startDate, String endDate) {
        return dataAccessTemplate.findByNamedQuery(FIND_BY_ANM_IDENTIFIER_FOR_REPORTING_MONTH,
                new Object[]{anmId, LocalDate.parse(startDate).toDate(), LocalDate.parse(endDate).toDate()});
    }

    public List getReportsForExternalId(String externalId) {
        return dataAccessTemplate.findByNamedQuery(FIND_BY_EXTERNAL_IDENTIFIER, externalId);
    }

    public void deleteReportsForExternalId(String externalId) {
        dataAccessTemplate.deleteAll(getReportsForExternalId(externalId));
    }
}
