package org.ei.drishti.reporting.repository;

import org.ei.drishti.reporting.domain.MotherRegistration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class AllMotherRegistrations {
    private DataAccessTemplate dataAccessTemplate;

    protected AllMotherRegistrations() {
    }

    @Autowired
    public AllMotherRegistrations(DataAccessTemplate dataAccessTemplate) {
        this.dataAccessTemplate = dataAccessTemplate;
    }

    public void save(String motherName) {
        dataAccessTemplate.save(new MotherRegistration(motherName));
    }
}
