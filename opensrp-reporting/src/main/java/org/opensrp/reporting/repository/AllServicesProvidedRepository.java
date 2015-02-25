package org.opensrp.reporting.repository;

import org.opensrp.reporting.domain.*;
import org.joda.time.LocalDate;
import org.opensrp.reporting.domain.Indicator;
import org.opensrp.reporting.domain.Location;
import org.opensrp.reporting.domain.ServiceProvided;
import org.opensrp.reporting.domain.ServiceProvidedReport;
import org.opensrp.reporting.domain.ServiceProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public class  AllServicesProvidedRepository {
    private DataAccessTemplate dataAccessTemplate;

    protected AllServicesProvidedRepository() {
    }

    @Autowired
    public AllServicesProvidedRepository(@Qualifier("serviceProvidedDataAccessTemplate") DataAccessTemplate dataAccessTemplate) {
        this.dataAccessTemplate = dataAccessTemplate;
    }

    public void save(ServiceProvider serviceProvider, String externalId, Indicator indicator, Date date, Location location, String dristhiEntityId) {
        dataAccessTemplate.save(new ServiceProvided(serviceProvider, externalId, indicator, date, location, dristhiEntityId));
    }

    public void delete(String indicator, String startDate, String endDate) {
        List result = dataAccessTemplate.findByNamedQuery(ServiceProvided.FIND_BY_ANM_IDENTIFIER_WITH_INDICATOR_FOR_MONTH,
                indicator, LocalDate.parse(startDate).toDate(), LocalDate.parse(endDate).toDate());
        dataAccessTemplate.deleteAll(result);
    }

    public List<ServiceProvidedReport> getNewReports(Integer token) {
        return dataAccessTemplate.findByNamedQuery(ServiceProvidedReport.FIND_NEW_SERVICE_PROVIDED, token);
    }

    public List<ServiceProvided> getAllReportsForDristhiEntityID(String dristhiEntityID) {
        return dataAccessTemplate.findByNamedQuery(ServiceProvided.FIND_SERVICE_PROVIDED_FOR_DRISTHI_ENTITY_ID, dristhiEntityID);
    }

    public List<ServiceProvidedReport> getNewReports(Integer token, int numberOfRowsToFetch) {
        return dataAccessTemplate.getSessionFactory()
                .getCurrentSession()
                .getNamedQuery(ServiceProvidedReport.FIND_NEW_SERVICE_PROVIDED)
                .setParameter(0, token)
                .setMaxResults(numberOfRowsToFetch)
                .list();

    }

    public void deleteReportsFor(String dristhiEntityId) {
        dataAccessTemplate.deleteAll(getAllReportsForDristhiEntityID(dristhiEntityId));
    }
}
