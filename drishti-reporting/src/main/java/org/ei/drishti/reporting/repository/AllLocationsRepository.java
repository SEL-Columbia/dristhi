package org.ei.drishti.reporting.repository;

import org.ei.drishti.reporting.domain.Location;
import org.ei.drishti.reporting.repository.cache.LocationCacheableRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class AllLocationsRepository implements LocationCacheableRepository {
    private DataAccessTemplate dataAccessTemplate;

    public AllLocationsRepository() {
    }

    @Autowired
    public AllLocationsRepository(DataAccessTemplate dataAccessTemplate) {
        this.dataAccessTemplate = dataAccessTemplate;
    }

    @Override
    public void save(Location objectToBeSaved) {
        dataAccessTemplate.save(objectToBeSaved);
    }

    @Override
    public Location fetch(Location objectWhichShouldBeFilledWithMoreInformation) {
        return (Location) dataAccessTemplate.getUniqueResult(Location.FIND_BY_VILLAGE_SUBCENTER_AND_PHC,
                new String[] {"village", "subCenter", "phc"}, new Object[] {objectWhichShouldBeFilledWithMoreInformation.village(),
                objectWhichShouldBeFilledWithMoreInformation.subCenter(), objectWhichShouldBeFilledWithMoreInformation.phc()});
    }
}
