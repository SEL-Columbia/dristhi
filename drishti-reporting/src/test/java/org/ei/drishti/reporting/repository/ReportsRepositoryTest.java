package org.ei.drishti.reporting.repository;

import org.ei.drishti.common.monitor.Monitor;
import org.ei.drishti.reporting.domain.ANM;
import org.ei.drishti.reporting.domain.Dates;
import org.ei.drishti.reporting.domain.Indicator;
import org.ei.drishti.reporting.domain.Location;
import org.ei.drishti.reporting.repository.cache.*;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.Date;

import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class ReportsRepositoryTest {
    @Mock
    private ANMCacheableRepository anmRepository;

    @Mock
    private IndicatorCacheableRepository indicatorRepository;

    @Mock
    private LocationCacheableRepository locationRepository;

    @Mock
    private DatesCacheableRepository datesRepository;

    @Mock
    private AllServicesProvidedRepository servicesProvidedRepository;

    @Mock
    private Monitor monitor;
    private ReportsRepository repository;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        repository = new ReportsRepository(anmRepository, datesRepository, indicatorRepository, locationRepository, servicesProvidedRepository, monitor);
    }

    @Test
    public void shouldSaveReportDataAndUseCachedRepositories() throws Exception {
        String anmIdentifier = "ANM X";
        String externalId = "12345";
        String indicator = "ANC";
        String village = "Bherya";
        String subCenter = "Sub Center";
        String phc = "PHC X";
        Date date = LocalDate.parse("2012-04-04").toDate();

        when(anmRepository.fetch(new ANM((anmIdentifier)))).thenReturn(new ANM(2, anmIdentifier));
        when(datesRepository.fetch(new Dates(date))).thenReturn(new Dates(2, date));
        when(indicatorRepository.fetch(new Indicator(indicator))).thenReturn(new Indicator(2, indicator));
        when(locationRepository.fetch(new Location(village, subCenter, phc))).thenReturn(new Location(2, village, subCenter, phc));

        repository.save(anmIdentifier, externalId, indicator, "2012-04-04", village, subCenter, phc);
        repository.save(anmIdentifier, externalId, indicator, "2012-04-04", village, subCenter, phc);

        verifyCallsToCachedRepository(anmRepository, new ANM(anmIdentifier));
        verifyCallsToCachedRepository(indicatorRepository, new Indicator(indicator));
        verifyCallsToCachedRepository(datesRepository, new Dates(date));
        verifyCallsToCachedRepository(locationRepository, new Location(village, subCenter, phc));

        verify(servicesProvidedRepository, times(2)).save(2, "12345", 2, 2, 2);
    }

    private <T> void verifyCallsToCachedRepository(CacheableRepository<T> blah, T obj) {
        verify(blah, times(1)).fetch(obj);
        verify(blah, times(0)).save(obj);
        verifyNoMoreInteractions(blah);
    }
}
