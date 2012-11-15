package org.ei.drishti.reporting.repository.it;

import org.ei.drishti.reporting.domain.Location;
import org.ei.drishti.reporting.repository.cache.LocationCacheableRepository;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

public class AllLocationsRepositoryIntegrationTest extends ServicesProvidedRepositoryIntegrationTestBase {
    @Autowired
    private LocationCacheableRepository repository;

    @Test
    public void shouldSaveAndFetchLocation() throws Exception {
        Location location = new Location("Bherya", "Sub Center", "PHC X");
        repository.save(location);

        Location fetchedLocation = repository.fetch(location);

        assertEquals("Bherya", fetchedLocation.village());
        assertEquals("Sub Center", fetchedLocation.subCenter());
        assertEquals("PHC X", fetchedLocation.phc());
        assertTrue("ID should be non-zero.", fetchedLocation.id() != 0);
    }
}
