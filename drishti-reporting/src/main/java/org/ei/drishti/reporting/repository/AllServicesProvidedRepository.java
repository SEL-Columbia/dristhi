package org.ei.drishti.reporting.repository;

import org.ei.drishti.reporting.domain.*;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public class AllServicesProvidedRepository {
    private DataAccessTemplate dataAccessTemplate;

    protected AllServicesProvidedRepository() {
    }

    @Autowired
    public AllServicesProvidedRepository(@Qualifier("serviceProvidedDataAccessTemplate") DataAccessTemplate dataAccessTemplate) {
        this.dataAccessTemplate = dataAccessTemplate;
    }

    public void save(ServiceProvider serviceProvider, String externalId, Indicator indicator, Date date, Location location) {
        dataAccessTemplate.save(new ServiceProvided(serviceProvider, externalId, indicator, date, location));
    }

    public void delete(String indicator, String startDate, String endDate) {
        List result = dataAccessTemplate.findByNamedQuery(ServiceProvided.FIND_BY_ANM_IDENTIFIER_WITH_INDICATOR_FOR_MONTH,
                indicator, LocalDate.parse(startDate).toDate(), LocalDate.parse(endDate).toDate());
        dataAccessTemplate.deleteAll(result);
    }

    public List<ServiceProvidedReport> getNewReports(Integer token) {
        return dataAccessTemplate.findByNamedQuery(ServiceProvidedReport.FIND_NEW_SERVICE_PROVIDED, token);
    }
}
