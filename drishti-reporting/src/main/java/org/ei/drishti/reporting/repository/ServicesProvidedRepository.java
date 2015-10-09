package org.ei.drishti.reporting.repository;

import org.ei.drishti.common.AllConstants;
import org.ei.drishti.common.domain.ReportDataDeleteRequest;
import org.ei.drishti.common.domain.ReportDataUpdateRequest;
import org.ei.drishti.common.domain.ReportingData;
import org.ei.drishti.common.monitor.Monitor;
import org.ei.drishti.common.monitor.Probe;
import org.ei.drishti.reporting.domain.Indicator;
import org.ei.drishti.reporting.domain.Location;
import org.ei.drishti.reporting.domain.ServiceProvidedReport;
import org.ei.drishti.reporting.domain.ServiceProvider;
import org.ei.drishti.reporting.repository.cache.IndicatorCacheableRepository;
import org.ei.drishti.reporting.repository.cache.ReadOnlyCachingRepository;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.ei.drishti.common.monitor.Metric.REPORTING_SERVICE_PROVIDED_CACHE_TIME;
import static org.ei.drishti.common.monitor.Metric.REPORTING_SERVICE_PROVIDED_INSERT_TIME;
import static org.ei.drishti.reporting.domain.ServiceProviderType.parse;

@Repository
public class ServicesProvidedRepository {
    private AllServiceProvidersRepository serviceProvidersRepository;
    private AllServicesProvidedRepository servicesProvidedRepository;
    private Monitor monitor;
    private static final Logger logger = LoggerFactory.getLogger(ServicesProvidedRepository.class);
    private ReadOnlyCachingRepository<Indicator> cachedIndicators;
    private AllLocationsRepository locationRepository;

    protected ServicesProvidedRepository() {
    }

    @Autowired
    public ServicesProvidedRepository(@Qualifier("serviceProvidedIndicatorRepository") IndicatorCacheableRepository indicatorRepository,
                                      AllLocationsRepository locationRepository,
                                      AllServiceProvidersRepository serviceProvidersRepository,
                                      AllServicesProvidedRepository servicesProvidedRepository, Monitor monitor) {
        this.serviceProvidersRepository = serviceProvidersRepository;
        this.servicesProvidedRepository = servicesProvidedRepository;
        this.monitor = monitor;
        cachedIndicators = new ReadOnlyCachingRepository<>(indicatorRepository);
        this.locationRepository = locationRepository;
    }

    @Transactional("service_provided")
    public void save(String serviceProviderIdentifier, String serviceProviderType, String externalId, String indicator,
                     String date, String village, String subCenter, String phcIdentifier, String quantity, String dristhiEntityId) {
        logger.info("Serviceprovided has been invoked");
        Probe probeForCache = monitor.start(REPORTING_SERVICE_PROVIDED_CACHE_TIME);
        Indicator fetchedIndicator = cachedIndicators.fetch(new Indicator(indicator));
        Date dates = LocalDate.parse(date).toDate();
        Location location = locationRepository.fetchBy(village, subCenter, phcIdentifier);
        ServiceProvider serviceProvider = serviceProvidersRepository.fetchBy(serviceProviderIdentifier, parse(serviceProviderType));
        monitor.end(probeForCache);

        int count = getCount(quantity);
        Probe probeForInsert = monitor.start(REPORTING_SERVICE_PROVIDED_INSERT_TIME);
        for (int i = 0; i < count; i++) {
            try {
                servicesProvidedRepository.save(serviceProvider, externalId, fetchedIndicator, dates, location, dristhiEntityId);
            } catch (Exception e) {
                cachedIndicators.clear(fetchedIndicator);
            }
        }
        monitor.end(probeForInsert);
    }

    @Transactional("service_provided")
    public void update(ReportDataUpdateRequest request) {
        servicesProvidedRepository.delete(request.indicator(), request.startDate(), request.endDate());
        saveReportingDataForIndicator(request.reportingData(), request.indicator());
    }

    private void saveReportingDataForIndicator(List<ReportingData> reportingData, String indicator) {
        for (ReportingData data : reportingData) {
            this.save(data.get(AllConstants.ReportDataParameters.ANM_IDENTIFIER),
                    data.get(AllConstants.ReportDataParameters.SERVICE_PROVIDER_TYPE),
                    data.get(AllConstants.ReportDataParameters.EXTERNAL_ID),
                    indicator,
                    data.get(AllConstants.ReportDataParameters.SERVICE_PROVIDED_DATE),
                    data.get(AllConstants.ReportDataParameters.VILLAGE),
                    data.get(AllConstants.ReportDataParameters.SUB_CENTER),
                    data.get(AllConstants.ReportDataParameters.PHC),
                    data.get(AllConstants.ReportDataParameters.QUANTITY),
                    data.get(AllConstants.ReportDataParameters.DRISTHI_ENTITY_ID)
            );
        }
    }

    private int getCount(String quantity) {
        return quantity == null ? 1 : Integer.parseInt(quantity);
    }

    @Transactional("service_provided")
    public List<ServiceProvidedReport> getNewReports(Integer token) {
       return servicesProvidedRepository.getNewReports(token);
    }

    @Transactional("service_provided")
    public List<ServiceProvidedReport> getNewReports(Integer token, int numberOfRowsToFetch) {
       return servicesProvidedRepository.getNewReports(token, numberOfRowsToFetch);
    }

    @Transactional("service_provided")
    public void delete(ReportDataDeleteRequest request) {
        servicesProvidedRepository.deleteReportsFor(request.dristhiEntityId());
    }
}
