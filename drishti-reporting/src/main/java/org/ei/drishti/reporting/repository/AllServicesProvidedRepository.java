package org.ei.drishti.reporting.repository;

import org.ei.drishti.reporting.domain.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

@Repository
public class AllServicesProvidedRepository {
    private DataAccessTemplate dataAccessTemplate;

    protected AllServicesProvidedRepository() {
    }

    @Autowired
    public AllServicesProvidedRepository(@Qualifier("serviceProvidedDataAccessTemplate") DataAccessTemplate dataAccessTemplate) {
        this.dataAccessTemplate = dataAccessTemplate;
    }

    public void save(ServiceProvider serviceProvider, String externalId, Indicator indicator, Dates date, Location location) {
        dataAccessTemplate.save(new ServiceProvided(serviceProvider, externalId, indicator, date, location));
    }
}
