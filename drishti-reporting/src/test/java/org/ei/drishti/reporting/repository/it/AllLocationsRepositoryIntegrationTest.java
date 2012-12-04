package org.ei.drishti.reporting.repository.it;

import org.ei.drishti.reporting.domain.Location;
import org.ei.drishti.reporting.domain.PHC;
import org.ei.drishti.reporting.repository.AllLocationsRepository;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

public class AllLocationsRepositoryIntegrationTest extends ServicesProvidedRepositoryIntegrationTestBase {
    @Autowired
    private AllLocationsRepository repository;

    @Test
    @Transactional("service_provided")
    @Rollback
    public void shouldFetchByVillageSCAndPHC() throws Exception {
        PHC phc = new PHC("PHC X", "Bherya");
        PHC anotherPHC = new PHC("PHC Y", "Bherya");
        template.save(phc);
        template.save(anotherPHC);
        Location location = new Location("Bherya", "Sub Center", phc, "taluka", "mysore", "karnataka");
        Location anotherLocation = new Location("Keelanapura", "Sub Center 2", anotherPHC, "taluka", "mysore", "karnataka");
        template.save(location);
        template.save(anotherLocation);

        Location fetchedLocation = repository.fetchBy("Bherya", "Sub Center", "PHC X");

        assertEquals("Bherya", fetchedLocation.village());
        assertEquals("Sub Center", fetchedLocation.subCenter());
        assertEquals(phc, fetchedLocation.phc());
        assertEquals("taluka", fetchedLocation.taluka());
        assertEquals("mysore", fetchedLocation.district());
        assertEquals("karnataka", fetchedLocation.state());
        assertTrue("ID should be non-zero.", fetchedLocation.id() != 0);
    }
}
