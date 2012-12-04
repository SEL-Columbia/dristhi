package org.ei.drishti.reporting.repository;

import org.ei.drishti.reporting.domain.Location;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

@Repository
public class AllLocationsRepository {
    private DataAccessTemplate dataAccessTemplate;

    protected AllLocationsRepository() {
    }

    @Autowired
    public AllLocationsRepository(@Qualifier("serviceProvidedDataAccessTemplate") DataAccessTemplate dataAccessTemplate) {
        this.dataAccessTemplate = dataAccessTemplate;
    }

    public Location fetchBy(String village, String subCenter, String phcIdentifier) {
        return (Location) dataAccessTemplate.getUniqueResult(Location.FIND_BY_VILLAGE_SUBCENTER_AND_PHC_IDENTIFIER,
                new String[] {"village", "subCenter", "phcIdentifier"}, new Object[] {village,
                subCenter, phcIdentifier});

    }
}
