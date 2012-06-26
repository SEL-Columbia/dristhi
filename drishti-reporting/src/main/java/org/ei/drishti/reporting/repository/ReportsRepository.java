package org.ei.drishti.reporting.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ReportsRepository {
    private AllServicesProvidedRepository allServicesProvided;
//    private final CachingRepository<ANM> cachedANMs;

    @Autowired
    public ReportsRepository(AllServicesProvidedRepository allServicesProvided) {
        this.allServicesProvided = allServicesProvided;
//        cachedANMs = new CachingRepository<>(allANMsRepository);
    }

    public void save(String anmIdentifier, String externalId, String indicator, String date, String village, String subCenter, String phc) {
//        cachedANMs.fetch(new ANM(anmIdentifier));
        throw new RuntimeException("Not implemented");
    }
}
