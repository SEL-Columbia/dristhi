package org.opensrp.reporting.repository.it;

import org.junit.Test;
import org.opensrp.reporting.domain.Location;
import org.opensrp.reporting.domain.PHC;
import org.opensrp.reporting.domain.SP_ANM;
import org.opensrp.reporting.repository.AllLocationsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

public class AllLocationsRepositoryIntegrationTest extends ServicesProvidedIntegrationTestBase {
    @Autowired
    private AllLocationsRepository repository;

    @Test
    @Transactional("service_provided")
    @Rollback
    public void shouldFetchLocationByVillageSCAndPHC() throws Exception {
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

    @Test
    @Transactional("service_provided")
    @Rollback
    public void shouldFetchLocationByANMIdentifier() throws Exception {
        PHC phc = new PHC("PHC X", "Bherya");
        PHC anotherPHC = new PHC("PHC Y", "Bherya");
        template.save(phc);
        template.save(anotherPHC);
        Location location = new Location("Bherya", "Sub Center", phc, "taluka", "mysore", "karnataka");
        Location anotherLocation = new Location("Keelanapura", "Sub Center 2", anotherPHC, "taluka", "mysore", "karnataka");
        template.save(location);
        template.save(anotherLocation);
        SP_ANM anm = new SP_ANM("anm1", "anm1 name", "Sub Center", phc.id());
        SP_ANM anotherANMForPHCX = new SP_ANM("anm2", "anm2 name", "Sub Center 1", phc.id());
        SP_ANM anmForPHCY = new SP_ANM("anm3", "anm3 name", "Sub Center 2", anotherPHC.id());
        template.save(anm);
        template.save(anotherANMForPHCX);
        template.save(anmForPHCY);

        Location fetchedLocation = repository.fetchByANMIdentifier("anm1");

        assertEquals("Bherya", fetchedLocation.village());
        assertEquals("Sub Center", fetchedLocation.subCenter());
        assertEquals(phc, fetchedLocation.phc());
        assertEquals("taluka", fetchedLocation.taluka());
        assertEquals("mysore", fetchedLocation.district());
        assertEquals("karnataka", fetchedLocation.state());
        assertTrue("ID should be non-zero.", fetchedLocation.id() != 0);
    }

    @Test
    @Transactional("service_provided")
    @Rollback
    public void shouldFetchAllVillagesFromPHCAndSubCenterOfANM() throws Exception {
        PHC phc = new PHC("PHC X", "Bherya");
        PHC anotherPHC = new PHC("PHC Y", "Bherya");
        template.save(phc);
        template.save(anotherPHC);
        Location village1InBheryaPHC = new Location("Village 1", "Sub Center 1", phc, "taluka", "mysore", "karnataka");
        Location village2InBheryaPHC = new Location("Village 2", "Sub Center 1", phc, "taluka", "mysore", "karnataka");
        Location anotherVillageInBheryaPHC = new Location("Village", "Sub Center 2", phc, "taluka", "mysore", "karnataka");
        Location villageInKeelanpuraPHC = new Location("Keelanapura", "Sub Center 2", anotherPHC, "taluka", "mysore", "karnataka");
        SP_ANM anm = new SP_ANM("anm1", "anm1 name", "Sub Center 1", phc.id());

        template.save(village1InBheryaPHC);
        template.save(village2InBheryaPHC);
        template.save(anotherVillageInBheryaPHC);
        template.save(villageInKeelanpuraPHC);
        template.save(anm);

        List villages = repository.fetchVillagesForANM(anm.identifier());

        assertEquals(2, villages.size());
        assertTrue(villages.contains(new Location("Village 1", "Sub Center 1", phc, "taluka", "mysore", "karnataka")));
        assertTrue(villages.contains(new Location("Village 2", "Sub Center 1", phc, "taluka", "mysore", "karnataka")));
    }
}
