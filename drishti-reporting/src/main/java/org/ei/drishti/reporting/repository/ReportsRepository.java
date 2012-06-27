package org.ei.drishti.reporting.repository;

import org.ei.drishti.reporting.domain.ANM;
import org.ei.drishti.reporting.domain.Dates;
import org.ei.drishti.reporting.domain.Indicator;
import org.ei.drishti.reporting.domain.Location;
import org.ei.drishti.reporting.repository.cache.*;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ReportsRepository {
    private AllServicesProvidedRepository servicesProvidedRepository;

    private CachingRepository<ANM> cachedANMs;
    private CachingRepository<Dates> cachedDates;
    private CachingRepository<Indicator> cachedIndicators;
    private CachingRepository<Location> cachedLocations;

    @Autowired
    public ReportsRepository(ANMCacheableRepository anmRepository, DatesCacheableRepository datesRepository, IndicatorCacheableRepository indicatorRepository, LocationCacheableRepository locationRepository, AllServicesProvidedRepository servicesProvidedRepository) {
        this.servicesProvidedRepository = servicesProvidedRepository;
        cachedANMs = new CachingRepository<>(anmRepository);
        cachedDates = new CachingRepository<>(datesRepository);
        cachedIndicators = new CachingRepository<>(indicatorRepository);
        cachedLocations = new CachingRepository<>(locationRepository);
    }

    public void save(String anmIdentifier, String externalId, String indicator, String date, String village, String subCenter, String phc) {
        ANM anm = cachedANMs.fetch(new ANM(anmIdentifier));
        Indicator fetchedIndicator = cachedIndicators.fetch(new Indicator(indicator));
        Dates dates = cachedDates.fetch(new Dates(LocalDate.parse(date).toDate()));
        Location location = cachedLocations.fetch(new Location(village, subCenter, phc));

        servicesProvidedRepository.save(anm.id(), externalId, fetchedIndicator.id(), dates.id(), location.id());
    }
}
