package org.ei.drishti.reporting.repository.it;

import org.ei.drishti.reporting.domain.Location;
import org.ei.drishti.reporting.repository.cache.LocationCacheableRepository;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static java.util.Arrays.asList;
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

    @Test
    public void shouldLoadAllLocations() throws Exception {
        Location location1 = new Location("Bherya", "Sub Center", "PHC X");
        Location location2 = new Location("Keelanapura", "Sub Center 2", "PHC Y");
        repository.save(location1);
        repository.save(location2);

        List<Location> locations = repository.fetchAll();

        assertTrue(locations.containsAll(asList(location1, location2)));
    }
}
