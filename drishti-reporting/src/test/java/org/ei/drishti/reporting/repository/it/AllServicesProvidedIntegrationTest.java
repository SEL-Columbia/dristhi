package org.ei.drishti.reporting.repository.it;

import org.ei.drishti.reporting.domain.*;
import org.ei.drishti.reporting.repository.*;
import org.ei.drishti.reporting.repository.cache.ANMCacheableRepository;
import org.ei.drishti.reporting.repository.cache.DatesCacheableRepository;
import org.ei.drishti.reporting.repository.cache.IndicatorCacheableRepository;
import org.ei.drishti.reporting.repository.cache.LocationCacheableRepository;
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
    private ANMCacheableRepository allANMsRepository;

    @Autowired
    private DatesCacheableRepository allDatesRepository;

    @Autowired
    private IndicatorCacheableRepository allIndicatorsRepository;

    @Autowired
    private LocationCacheableRepository allLocationsRepository;

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
        ANM anm = new ANM("ANM X");
        Dates dates = new Dates(LocalDate.now().toDate());
        Indicator indicator = new Indicator("ANC");
        Location location = new Location("Bherya", "Sub Center", "PHC X");
        allANMsRepository.save(anm);
        allDatesRepository.save(dates);
        allIndicatorsRepository.save(indicator);
        allLocationsRepository.save(location);

        Integer anmId = allANMsRepository.fetch(anm).id();
        Integer dateId = allDatesRepository.fetch(dates).id();
        Integer indicatorId = allIndicatorsRepository.fetch(indicator).id();
        Integer locationId = allLocationsRepository.fetch(location).id();

        repository.save(anmId, 123, indicatorId, dateId, locationId);

        assertEquals(1, template.loadAll(ServiceProvided.class).size());
    }
}
