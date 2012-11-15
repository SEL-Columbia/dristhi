package org.ei.drishti.reporting.repository;

import org.ei.drishti.common.monitor.Monitor;
import org.ei.drishti.common.monitor.Probe;
import org.ei.drishti.reporting.domain.ANM;
import org.ei.drishti.reporting.domain.Dates;
import org.ei.drishti.reporting.domain.Indicator;
import org.ei.drishti.reporting.domain.Location;
import org.ei.drishti.reporting.repository.cache.*;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import static org.ei.drishti.common.monitor.Metric.REPORTING_SERVICE_PROVIDED_CACHE_TIME;
import static org.ei.drishti.common.monitor.Metric.REPORTING_SERVICE_PROVIDED_INSERT_TIME;

@Component
@Repository
public class ServicesProvidedRepository {
    private AllServicesProvidedRepository servicesProvidedRepository;
    private Monitor monitor;

    private CachingRepository<ANM> cachedANMs;
    private CachingRepository<Dates> cachedDates;
    private CachingRepository<Indicator> cachedIndicators;
    private CachingRepository<Location> cachedLocations;

    protected ServicesProvidedRepository() {
    }

    @Autowired
    public ServicesProvidedRepository(@Qualifier("serviceProvidedANMRepository") ANMCacheableRepository anmRepository,
                                      @Qualifier("serviceProvidedDatesRepository") DatesCacheableRepository datesRepository,
                                      @Qualifier("serviceProvidedIndicatorRepository") IndicatorCacheableRepository indicatorRepository,
                                      LocationCacheableRepository locationRepository,
                                      AllServicesProvidedRepository servicesProvidedRepository, Monitor monitor) {
        this.servicesProvidedRepository = servicesProvidedRepository;
        this.monitor = monitor;
        cachedANMs = new CachingRepository<>(anmRepository);
        cachedDates = new CachingRepository<>(datesRepository);
        cachedIndicators = new CachingRepository<>(indicatorRepository);
        cachedLocations = new CachingRepository<>(locationRepository);
    }

    public void save(String anmIdentifier, String externalId, String indicator, String date, String village, String subCenter, String phc) {
        Probe probeForCache = monitor.start(REPORTING_SERVICE_PROVIDED_CACHE_TIME);
        ANM anm = cachedANMs.fetch(new ANM(anmIdentifier));
        Indicator fetchedIndicator = cachedIndicators.fetch(new Indicator(indicator));
        Dates dates = cachedDates.fetch(new Dates(LocalDate.parse(date).toDate()));
        Location location = cachedLocations.fetch(new Location(village, subCenter, phc));
        monitor.end(probeForCache);

        Probe probeForInsert = monitor.start(REPORTING_SERVICE_PROVIDED_INSERT_TIME);
        servicesProvidedRepository.save(anm.id(), externalId, fetchedIndicator.id(), dates.id(), location.id());
        monitor.end(probeForInsert);
    }
}
