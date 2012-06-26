package org.ei.drishti.reporting.repository.it;

import org.ei.drishti.reporting.domain.*;
import org.ei.drishti.reporting.repository.*;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static junit.framework.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:test-applicationContext-drishti-reporting.xml")
public class AllServicesProvidedIntegrationTest {
    @Autowired
    @Qualifier("testDataAccessTemplate")
    private TestDataAccessTemplate template;

    @Autowired
    private AllServicesProvidedRepository repository;

    @Autowired
    private AllANMsRepository allANMsRepository;

    @Autowired
    private AllDatesRepository allDatesRepository;

    @Autowired
    private AllIndicatorsRepository allIndicatorsRepository;

    @Autowired
    private AllLocationsRepository allLocationsRepository;

    @Before
    public void setUp() throws Exception {
        template.deleteAll(template.loadAll(ServiceProvided.class));
        template.deleteAll(template.loadAll(ANM.class));
        template.deleteAll(template.loadAll(Dates.class));
        template.deleteAll(template.loadAll(Indicator.class));
        template.deleteAll(template.loadAll(Location.class));
    }

    @Test
    public void shouldSaveAService() throws Exception {
        allANMsRepository.save("ANM X");
        allDatesRepository.save(LocalDate.now().toDate());
        allIndicatorsRepository.save("ANC");
        allLocationsRepository.save("Bherya", "Sub Center", "PHC X");

        Integer anmId = allANMsRepository.fetch("ANM X").id();
        Integer dateId = allDatesRepository.fetch(LocalDate.now().toDate()).id();
        Integer indicatorId = allIndicatorsRepository.fetch("ANC").id();
        Integer locationId = allLocationsRepository.fetch("Bherya", "Sub Center", "PHC X").id();

        repository.save(anmId, 123, indicatorId, dateId, locationId);

        assertEquals(1, template.loadAll(ServiceProvided.class).size());
    }
}
