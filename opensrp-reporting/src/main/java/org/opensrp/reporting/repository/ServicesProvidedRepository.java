package org.opensrp.reporting.repository;

import org.opensrp.common.AllConstants;
import org.opensrp.common.domain.ReportDataDeleteRequest;
import org.opensrp.common.domain.ReportDataUpdateRequest;
import org.opensrp.common.domain.ReportingData;
import org.opensrp.common.monitor.Monitor;
import org.opensrp.common.monitor.Probe;
import org.joda.time.LocalDate;
import org.opensrp.reporting.domain.Indicator;
import org.opensrp.reporting.domain.Location;
import org.opensrp.reporting.domain.ServiceProvidedReport;
import org.opensrp.reporting.domain.ServiceProvider;

import org.opensrp.reporting.repository.cache.IndicatorCacheableRepository;
import org.opensrp.reporting.repository.cache.ReadOnlyCachingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import static org.opensrp.reporting.domain.ServiceProviderType.parse;
import static org.opensrp.common.monitor.Metric.REPORTING_SERVICE_PROVIDED_CACHE_TIME;
import static org.opensrp.common.monitor.Metric.REPORTING_SERVICE_PROVIDED_INSERT_TIME;

@Repository
public class ServicesProvidedRepository {
    private AllServiceProvidersRepository serviceProvidersRepository;
    private AllServicesProvidedRepository servicesProvidedRepository;
    private Monitor monitor;

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
