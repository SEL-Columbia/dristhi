package org.ei.drishti.reporting.repository;

import org.ei.drishti.common.domain.ReportDataDeleteRequest;
import org.ei.drishti.common.domain.ReportDataUpdateRequest;
import org.ei.drishti.common.domain.ReportingData;
import org.ei.drishti.common.monitor.Monitor;
import org.ei.drishti.reporting.domain.*;
import org.ei.drishti.reporting.repository.cache.CacheableRepository;
import org.ei.drishti.reporting.repository.cache.IndicatorCacheableRepository;
import org.ei.drishti.reporting.repository.cache.ReadOnlyCacheableRepository;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.Date;
import java.util.HashMap;

import static java.util.Arrays.asList;
import static org.ei.drishti.common.AllConstants.ReportDataParameters.*;
import static org.ei.drishti.common.AllConstants.ReportDataParameters.ANM_IDENTIFIER;
import static org.ei.drishti.common.AllConstants.ReportDataParameters.INDICATOR;
import static org.ei.drishti.reporting.domain.ServiceProviderType.ANM;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class ServicesProvidedRepositoryTest {
    @Mock
    private IndicatorCacheableRepository indicatorRepository;

    @Mock
    private AllLocationsRepository locationRepository;

    @Mock
    private AllServiceProvidersRepository serviceProvidersRepository;

    @Mock
    private AllServicesProvidedRepository servicesProvidedRepository;

    @Mock
    private Monitor monitor;

    private ServicesProvidedRepository repository;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        repository = new ServicesProvidedRepository(indicatorRepository, locationRepository, serviceProvidersRepository, servicesProvidedRepository, monitor);
    }

    @Test
    public void shouldSaveReportDataAndUseCachedRepositories() throws Exception {
        String anmIdentifier = "ANM X";
        String externalId = "12345";
        String indicator = "ANC";
        String village = "Bherya";
        String subCenter = "Sub Center";
        Date date = LocalDate.parse("2012-04-04").toDate();
        PHC phc = new PHC(34, "PHC X", "PHC");
        String dristhiEntityId = "entity id 1";
        ServiceProvider serviceProvider = new ServiceProvider(2, 2, ANM);
        Indicator fetchedIndicator = new Indicator(2, indicator);
        Location location = new Location(2, village, subCenter, phc, "taluka", "mysore", "karnataka");
        when(serviceProvidersRepository.fetchBy("ANM X", ANM)).thenReturn(serviceProvider);
        when(indicatorRepository.fetch(new Indicator(indicator))).thenReturn(fetchedIndicator);
        when(locationRepository.fetchBy(village, subCenter, phc.phcIdentifier())).thenReturn(location);

        repository.save(anmIdentifier, "ANM", externalId, indicator, "2012-04-04", village, subCenter, phc.phcIdentifier(), null, dristhiEntityId);
        repository.save(anmIdentifier, "ANM", externalId, indicator, "2012-04-04", village, subCenter, phc.phcIdentifier(), null, dristhiEntityId);

        verifyCallsToReadOnlyCachedRepository(indicatorRepository, new Indicator(indicator));
        verify(locationRepository, times(2)).fetchBy(village, subCenter, phc.phcIdentifier());
        verify(serviceProvidersRepository, times(2)).fetchBy(anmIdentifier, ANM);
        verify(servicesProvidedRepository, times(2)).save(serviceProvider, "12345", fetchedIndicator, date, location, dristhiEntityId);
    }

    @Test
    public void shouldSaveAsPerQuantityIfQuantityIsNotNull() throws Exception {
        String anmIdentifier = "ANM X";
        String externalId = "12345";
        String indicator = "ANC";
        String village = "Bherya";
        String subCenter = "Sub Center";
        String dristhiEntityId = "entity id 1";
        Date date = LocalDate.parse("2012-04-04").toDate();
        PHC phc = new PHC(34, "PHC X", "PHC");

        ServiceProvider serviceProvider = new ServiceProvider(2, 2, ANM);
        Indicator fetchedIndicator = new Indicator(2, indicator);
        Location location = new Location(2, village, subCenter, phc, "taluka", "mysore", "karnataka");
        when(serviceProvidersRepository.fetchBy("ANM X", ANM)).thenReturn(serviceProvider);
        when(indicatorRepository.fetch(new Indicator(indicator))).thenReturn(fetchedIndicator);
        when(locationRepository.fetchBy(village, subCenter, phc.phcIdentifier())).thenReturn(location);

        repository.save(anmIdentifier, "ANM", externalId, indicator, "2012-04-04", village, subCenter, phc.phcIdentifier(), "40", dristhiEntityId);

        verify(servicesProvidedRepository, times(40)).save(serviceProvider, "12345", fetchedIndicator, date, location, dristhiEntityId);
    }

    @Test
    public void shouldUpdateListOfServiceProvidedIndicatorRowsForReportingMonthForAllANMs() throws Exception {
        String anmIdentifier = "ANM X";
        String externalId = "12345";
        String indicator = "INDICATOR 1";
        String village = "Bherya";
        String subCenter = "Sub Center";
        String startDate = "2013-01-26";
        String endDate = "2013-02-25";

        HashMap<String, String> data = new HashMap<String, String>();
        data.put(SERVICE_PROVIDER_TYPE, "ANM");
        data.put(EXTERNAL_ID, externalId);
        data.put(SERVICE_PROVIDED_DATE, startDate);
        data.put(VILLAGE, village);
        data.put(SUB_CENTER, subCenter);
        data.put(PHC,"phc");
        data.put(QUANTITY, "1");
        data.put(INDICATOR, indicator);
        data.put(ANM_IDENTIFIER, anmIdentifier);
        data.put(DRISTHI_ENTITY_ID, "entity id 1");
        ReportingData reportingData = new ReportingData("type", data);

        ReportDataUpdateRequest request = new ReportDataUpdateRequest()
                .withType("type")
                .withIndicator(indicator)
                .withStartDate(startDate)
                .withEndDate(endDate)
                .withReportingData(asList(reportingData));

        Date date = LocalDate.parse(startDate).toDate();
        PHC phc = new PHC(34, "phc", "PHC");

        ServiceProvider serviceProvider = new ServiceProvider(2, 2, ANM);
        Indicator fetchedIndicator = new Indicator(2, indicator);
        Location location = new Location(2, village, subCenter, phc, "taluka", "mysore", "karnataka");
        when(serviceProvidersRepository.fetchBy(anmIdentifier, ANM)).thenReturn(serviceProvider);
        when(indicatorRepository.fetch(new Indicator(indicator))).thenReturn(fetchedIndicator);
        when(locationRepository.fetchBy(village, subCenter, phc.phcIdentifier())).thenReturn(location);

        repository.update(request);

        verify(servicesProvidedRepository).delete(indicator, startDate, endDate);
        verify(servicesProvidedRepository).save(serviceProvider, "12345", fetchedIndicator, date, location, "entity id 1");
    }

    @Test
    public void shouldDeleteAllReportsForGivenDristhiEntityID() throws Exception {
        ReportDataDeleteRequest request = new ReportDataDeleteRequest()
                .withType("type")
                .withDristhiEntityId("entity id 1");

        repository.delete(request);

        verify(servicesProvidedRepository).deleteReportsFor("entity id 1");
    }

    @Test
    public void shouldCallServiceProvidedRepositoryForReports() {
        repository.getNewReports(1232);

        verify(servicesProvidedRepository).getNewReports(1232);
    }


    private <T> void verifyCallsToReadOnlyCachedRepository(ReadOnlyCacheableRepository<T> repo, T object) {
        verify(repo, times(1)).fetch(object);
        verifyNoMoreInteractions(repo);
    }

    private <T> void verifyCallsToCachedRepository(CacheableRepository<T> repo, T object) {
        verify(repo, times(1)).fetch(object);
        verify(repo, times(0)).save(object);
        verifyNoMoreInteractions(repo);
    }
}
