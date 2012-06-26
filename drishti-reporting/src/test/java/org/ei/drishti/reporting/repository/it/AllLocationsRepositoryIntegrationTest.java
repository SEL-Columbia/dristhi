package org.ei.drishti.reporting.repository.it;

import org.ei.drishti.reporting.domain.Location;
import org.ei.drishti.reporting.repository.AllLocationsRepository;
import org.ei.drishti.reporting.repository.TestDataAccessTemplate;
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
    protected TestDataAccessTemplate template;

    @Autowired
    private AllLocationsRepository repository;

    @Before
    public void setUp() throws Exception {
        template.deleteAll(template.loadAll(Location.class));
    }

    @Test
    public void shouldSaveAndFetchLocation() throws Exception {
        repository.save("Bherya", "Sub Center", "PHC X");

        Location location = repository.fetch("Bherya", "Sub Center", "PHC X");

        assertEquals("Bherya", location.village());
        assertEquals("Sub Center", location.subCenter());
        assertEquals("PHC X", location.phc());
        assertTrue("ID should be non-zero.", location.id() != 0);
    }
}
