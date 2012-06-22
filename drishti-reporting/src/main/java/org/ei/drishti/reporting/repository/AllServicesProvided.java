package org.ei.drishti.reporting.repository;

import org.ei.drishti.reporting.domain.ServiceProvided;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

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

    public void save(String thaayiCardNumber, Date serviceDate, String serviceProvided, String anmIdentifier) {
        dataAccessTemplate.save(new ServiceProvided(thaayiCardNumber, serviceDate, serviceProvided, anmIdentifier));
    }
}
