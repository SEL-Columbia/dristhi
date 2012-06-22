package org.ei.drishti.reporting.repository;

import org.springframework.stereotype.Component;

@Component
public class ReportsRepository {
    public void save(String anmIdentifier, String externalId, String indicator, String date, String village, String subCenter, String phc) {
        throw new RuntimeException("Not implemented");
    }
}
