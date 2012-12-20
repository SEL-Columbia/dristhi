package org.ei.drishti.reporting.repository;

import org.ei.drishti.common.monitor.Monitor;
import org.ei.drishti.common.monitor.Probe;
import org.ei.drishti.reporting.domain.Dates;
import org.ei.drishti.reporting.domain.Indicator;
import org.ei.drishti.reporting.domain.Location;
import org.ei.drishti.reporting.domain.ServiceProvider;
import org.ei.drishti.reporting.repository.cache.CachingRepository;
import org.ei.drishti.reporting.repository.cache.DatesCacheableRepository;
import org.ei.drishti.reporting.repository.cache.IndicatorCacheableRepository;
import org.ei.drishti.reporting.repository.cache.ReadOnlyCachingRepository;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import static org.ei.drishti.common.monitor.Metric.REPORTING_SERVICE_PROVIDED_CACHE_TIME;
import static org.ei.drishti.common.monitor.Metric.REPORTING_SERVICE_PROVIDED_INSERT_TIME;
import static org.ei.drishti.reporting.domain.ServiceProviderType.parse;

@Component
@Repository
public class ServicesProvidedRepository {
    private AllServiceProvidersRepository serviceProvidersRepository;
    private AllServicesProvidedRepository servicesProvidedRepository;
    private Monitor monitor;

    private CachingRepository<Dates> cachedDates;
    private ReadOnlyCachingRepository<Indicator> cachedIndicators;
    private AllLocationsRepository locationRepository;

    protected ServicesProvidedRepository() {
    }

    @Autowired
    public ServicesProvidedRepository(@Qualifier("serviceProvidedDatesRepository") DatesCacheableRepository datesRepository,
                                      @Qualifier("serviceProvidedIndicatorRepository") IndicatorCacheableRepository indicatorRepository,
                                      AllLocationsRepository locationRepository,
                                      AllServiceProvidersRepository serviceProvidersRepository,
                                      AllServicesProvidedRepository servicesProvidedRepository, Monitor monitor) {
        this.serviceProvidersRepository = serviceProvidersRepository;
        this.servicesProvidedRepository = servicesProvidedRepository;
        this.monitor = monitor;
        cachedIndicators = new ReadOnlyCachingRepository<>(indicatorRepository);
        this.locationRepository = locationRepository;
        cachedDates = new CachingRepository<>(datesRepository);
    }

    @Transactional("service_provided")
    public void save(String serviceProviderIdentifier, String serviceProviderType, String externalId, String indicator, String date, String village, String subCenter, String phcIdentifier, String quantity) {
        Probe probeForCache = monitor.start(REPORTING_SERVICE_PROVIDED_CACHE_TIME);
        Indicator fetchedIndicator = cachedIndicators.fetch(new Indicator(indicator));
        Dates dates = cachedDates.fetch(new Dates(LocalDate.parse(date).toDate()));
        Location location = locationRepository.fetchBy(village, subCenter, phcIdentifier);
        ServiceProvider serviceProvider = serviceProvidersRepository.fetchBy(serviceProviderIdentifier, parse(serviceProviderType));
        monitor.end(probeForCache);

        int count = getCount(quantity);
        Probe probeForInsert = monitor.start(REPORTING_SERVICE_PROVIDED_INSERT_TIME);
        for (int i = 0; i < count; i++) {
            try {
                servicesProvidedRepository.save(serviceProvider, externalId, fetchedIndicator, dates, location);
            } catch (Exception e) {
                cachedIndicators.clear(fetchedIndicator);
                cachedDates.clear(dates);

            }
        }
        monitor.end(probeForInsert);
    }

    private int getCount(String quantity) {
        return quantity == null ? 1 : Integer.parseInt(quantity);
    }
}
