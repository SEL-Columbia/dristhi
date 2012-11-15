package org.ei.drishti.reporting.repository;

import org.ei.drishti.common.monitor.Monitor;
import org.ei.drishti.reporting.domain.ANM;
import org.ei.drishti.reporting.domain.Dates;
import org.ei.drishti.reporting.domain.Indicator;
import org.ei.drishti.reporting.repository.cache.ANMCacheableRepository;
import org.ei.drishti.reporting.repository.cache.CacheableRepository;
import org.ei.drishti.reporting.repository.cache.DatesCacheableRepository;
import org.ei.drishti.reporting.repository.cache.IndicatorCacheableRepository;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.Date;

import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class ANMReportsRepositoryTest {
    @Mock
    private ANMCacheableRepository anmRepository;

    @Mock
    private IndicatorCacheableRepository indicatorRepository;

    @Mock
    private DatesCacheableRepository datesRepository;

    @Mock
    private AllANMReportDataRepository anmReportDataRepository;

    @Mock
    private Monitor monitor;

    private ANMReportsRepository repository;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        repository = new ANMReportsRepository(anmRepository, datesRepository, indicatorRepository, anmReportDataRepository, monitor);
    }

    @Test
    public void shouldSaveANMReportsAndUseCachedRepositories() throws Exception {
        String anmIdentifier = "ANM X";
        String externalId = "EC CASE 1";
        String indicator = "IUD";
        Date date = LocalDate.parse("2012-04-04").toDate();

        when(anmRepository.fetch(new ANM((anmIdentifier)))).thenReturn(new ANM(2, anmIdentifier));
        when(datesRepository.fetch(new Dates(date))).thenReturn(new Dates(2, date));
        when(indicatorRepository.fetch(new Indicator(indicator))).thenReturn(new Indicator(2, indicator));

        repository.save(anmIdentifier, externalId, indicator, "2012-04-04");
        repository.save(anmIdentifier, externalId, indicator, "2012-04-04");

        verifyCallsToCachedRepository(anmRepository, new ANM(anmIdentifier));
        verifyCallsToCachedRepository(indicatorRepository, new Indicator(indicator));
        verifyCallsToCachedRepository(datesRepository, new Dates(date));

        verify(anmReportDataRepository, times(2)).save(2, externalId, 2, 2);
    }

    private <T> void verifyCallsToCachedRepository(CacheableRepository<T> blah, T obj) {
        verify(blah, times(1)).fetch(obj);
        verify(blah, times(0)).save(obj);
        verifyNoMoreInteractions(blah);
    }
}
