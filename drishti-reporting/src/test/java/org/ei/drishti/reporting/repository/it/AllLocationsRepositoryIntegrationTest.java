package org.ei.drishti.reporting.repository.it;

import org.ei.drishti.reporting.domain.Location;
import org.ei.drishti.reporting.repository.TestDataAccessTemplate;
import org.ei.drishti.reporting.repository.cache.LocationCacheableRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:test-applicationContext-drishti-reporting.xml")
public class AllLocationsRepositoryIntegrationTest {
    @Autowired
    @Qualifier("testDataAccessTemplate")
    private TestDataAccessTemplate template;

    @Autowired
    private LocationCacheableRepository repository;

    @Before
    public void setUp() throws Exception {
        template.deleteAll(template.loadAll(Location.class));
    }

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
