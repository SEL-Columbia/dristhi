package org.ei.drishti.reporting.repository;

import org.ei.drishti.reporting.domain.ServiceProvided;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class AllServicesProvided {
    private DataAccessTemplate dataAccessTemplate;

    protected AllServicesProvided() {
    }

    @Autowired
    public AllServicesProvided(DataAccessTemplate dataAccessTemplate) {
        this.dataAccessTemplate = dataAccessTemplate;
    }

    public void save(Integer anmIdentifier, Integer externalId, Integer indicator, Integer date, Integer location) {
        dataAccessTemplate.save(new ServiceProvided(anmIdentifier, externalId, indicator, date, location));
    }
}
