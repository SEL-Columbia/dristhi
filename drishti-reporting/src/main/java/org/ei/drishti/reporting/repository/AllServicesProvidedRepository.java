package org.ei.drishti.reporting.repository;

import org.ei.drishti.reporting.domain.ServiceProvided;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class AllServicesProvidedRepository {
    private DataAccessTemplate dataAccessTemplate;

    protected AllServicesProvidedRepository() {
    }

    @Autowired
    public AllServicesProvidedRepository(DataAccessTemplate dataAccessTemplate) {
        this.dataAccessTemplate = dataAccessTemplate;
    }

    public void save(Integer anmIdentifier, String externalId, Integer indicator, Integer date, Integer location) {
        dataAccessTemplate.save(new ServiceProvided(anmIdentifier, externalId, indicator, date, location));
    }
}
