package org.ei.drishti.reporting.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ReportsRepository {
    private AllServicesProvidedRepository allServicesProvided;

    @Autowired
    public ReportsRepository(AllServicesProvidedRepository allServicesProvided) {
        this.allServicesProvided = allServicesProvided;
    }

    public void save(String anmIdentifier, String externalId, String indicator, String date, String village, String subCenter, String phc) {
        throw new RuntimeException("Not implemented");
    }
}
