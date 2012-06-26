package org.ei.drishti.reporting.repository;

import org.ei.drishti.reporting.domain.Location;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class AllLocationsRepository {
    private DataAccessTemplate dataAccessTemplate;

    public AllLocationsRepository() {
    }

    @Autowired
    public AllLocationsRepository(DataAccessTemplate dataAccessTemplate) {
        this.dataAccessTemplate = dataAccessTemplate;
    }

    public void save(String village, String subCenter, String phc) {
        dataAccessTemplate.save(new Location(village, subCenter, phc));
    }

    public Location fetch(String village, String subCenter, String phc) {
        return (Location) dataAccessTemplate.getUniqueResult(Location.FIND_BY_VILLAGE_SUBCENTER_AND_PHC,
                new String[] {"village", "subCenter", "phc"}, new Object[] {village, subCenter, phc});
    }
}
