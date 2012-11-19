package org.ei.drishti.reporting.repository;

import org.ei.drishti.common.domain.ANMIndicatorSummary;
import org.ei.drishti.common.domain.ANMReport;
import org.ei.drishti.common.domain.MonthSummary;
import org.ei.drishti.common.monitor.Monitor;
import org.ei.drishti.common.util.DateUtil;
import org.ei.drishti.reporting.domain.*;
import org.ei.drishti.reporting.repository.cache.ANMCacheableRepository;
import org.ei.drishti.reporting.repository.cache.CacheableRepository;
import org.ei.drishti.reporting.repository.cache.DatesCacheableRepository;
import org.ei.drishti.reporting.repository.cache.IndicatorCacheableRepository;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.Date;
import java.util.List;

import static java.util.Arrays.asList;
import static org.joda.time.LocalDate.parse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
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

    @Mock
    private AllAnnualTargetsRepository annualTargetsRepository;

    private ANMReportsRepository repository;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        repository = new ANMReportsRepository(anmRepository, datesRepository, indicatorRepository, anmReportDataRepository, annualTargetsRepository, monitor);
    }

    @Test
    public void shouldSaveANMReportsAndUseCachedRepositories() throws Exception {
        String anmIdentifier = "ANM X";
        String externalId = "EC CASE 1";
        String indicator = "IUD";
        Date date = parse("2012-04-04").toDate();

        Dates dates = new Dates(2, date);
        Indicator indicator_ = new Indicator(2, indicator);
        ANM anm = new ANM(2, anmIdentifier);

        when(anmRepository.fetch(new ANM((anmIdentifier)))).thenReturn(anm);
        when(datesRepository.fetch(new Dates(date))).thenReturn(dates);
        when(indicatorRepository.fetch(new Indicator(indicator))).thenReturn(indicator_);

        repository.save(anmIdentifier, externalId, indicator, "2012-04-04");
        repository.save(anmIdentifier, externalId, indicator, "2012-04-04");

        verifyCallsToCachedRepository(anmRepository, new ANM(anmIdentifier));
        verifyCallsToCachedRepository(indicatorRepository, indicator_);
        verifyCallsToCachedRepository(datesRepository, dates);

        verify(anmReportDataRepository, times(2)).save(anm, externalId, indicator_, dates);
    }

    @Test
    public void shouldFetchANMIndicatorSummaries() throws Exception {
        DateUtil.fakeIt(LocalDate.parse("2012-03-31"));

        ANM anm = new ANM("ANM X");

        Dates january = new Dates(parse("2012-01-05").toDate());
        Dates february = new Dates(parse("2012-02-15").toDate());
        Dates march = new Dates(parse("2012-03-25").toDate());
        Indicator indicator1 = new Indicator("IUD");
        Indicator indicator2 = new Indicator("ANC");

        when(anmReportDataRepository.fetchByANMIdAndDate("ANM X", parse("2012-01-01").toDate())).thenReturn(asList(
                new ANMReportData(anm, "CASE 1", indicator1, january),
                new ANMReportData(anm, "CASE 2", indicator1, january),
                new ANMReportData(anm, "CASE 3", indicator1, february),
                new ANMReportData(anm, "CASE 4", indicator1, february),
                new ANMReportData(anm, "CASE 5", indicator2, march),
                new ANMReportData(anm, "CASE 6", indicator2, march)));
        when(annualTargetsRepository.fetchFor("ANM X", indicator1)).thenReturn(new AnnualTarget(1, 1, "40"));
        when(annualTargetsRepository.fetchFor("ANM X", indicator2)).thenReturn(new AnnualTarget(1, 1, "30"));

        List<ANMIndicatorSummary> anmIndicatorSummaries = repository.fetchANMSummary("ANM X");

        ANMIndicatorSummary expectedANMIUDSummary = new ANMIndicatorSummary("IUD", "40", asList(new MonthSummary("1", "2012", "2", "2", asList("CASE 1", "CASE 2")),
                new MonthSummary("2", "2012", "2", "4", asList("CASE 3", "CASE 4"))));
        ANMIndicatorSummary expectedANMANCSummary = new ANMIndicatorSummary("ANC", "30", asList(new MonthSummary("3", "2012", "2", "2", asList("CASE 5", "CASE 6"))));
        assertTrue(anmIndicatorSummaries.containsAll(asList(expectedANMANCSummary, expectedANMIUDSummary)));
        assertEquals(2, anmIndicatorSummaries.size());
    }

    @Test
    public void shouldReturnEmptyANMIndicatorSummariesWhenThereIsNoDataForANM() throws Exception {
        DateUtil.fakeIt(LocalDate.parse("2012-03-31"));
        when(anmReportDataRepository.fetchByANMIdAndDate("ANM X", parse("2012-01-01").toDate())).thenReturn(null);

        List<ANMIndicatorSummary> anmIndicatorSummaries = repository.fetchANMSummary("ANM X");

        assertEquals(0, anmIndicatorSummaries.size());
    }

    @Test
    public void shouldReturnANMIndicatorSummariesWithNullAnnualTargetWhenThereIsNoAnnualTargetForANM() throws Exception {
        DateUtil.fakeIt(LocalDate.parse("2012-03-31"));
        ANM anm = new ANM("ANM X");
        Dates dates = new Dates(parse("2012-01-05").toDate());
        Indicator indicator = new Indicator("IUD");

        when(anmReportDataRepository.fetchByANMIdAndDate("ANM X", parse("2012-01-01").toDate())).thenReturn(asList(new ANMReportData(anm, "CASE 6", indicator, dates)));
        when(annualTargetsRepository.fetchFor("ANM X", new Indicator("IUD"))).thenReturn(null);

        List<ANMIndicatorSummary> anmIndicatorSummaries = repository.fetchANMSummary("ANM X");

        ANMIndicatorSummary expectedANMIUDSummary = new ANMIndicatorSummary("IUD", null, asList(new MonthSummary("1", "2012", "1", "1", asList("CASE 6"))));
        assertEquals(asList(expectedANMIUDSummary), anmIndicatorSummaries);
    }

    @Test
    public void shouldReturnAllANMReports() throws Exception {
        DateUtil.fakeIt(LocalDate.parse("2012-03-31"));
        ANM anmX = new ANM("ANM X");
        ANM anmY = new ANM("ANM Y");
        Dates dates = new Dates(parse("2012-01-05").toDate());
        Indicator indicator = new Indicator("IUD");
        when(anmReportDataRepository.fetchByANMIdAndDate("ANM X", parse("2012-01-01").toDate())).thenReturn(asList(new ANMReportData(anmX, "CASE 1", indicator, dates)));
        when(anmReportDataRepository.fetchByANMIdAndDate("ANM Y", parse("2012-01-01").toDate())).thenReturn(asList(new ANMReportData(anmY, "CASE 2", indicator, dates)));
        when(annualTargetsRepository.fetchFor("ANM X", indicator)).thenReturn(new AnnualTarget(1, 1, "40"));
        when(annualTargetsRepository.fetchFor("ANM Y", indicator)).thenReturn(new AnnualTarget(1, 1, "30"));
        when(anmRepository.fetchAll()).thenReturn(asList(anmX, anmY));

        List<ANMReport> anmReports = repository.fetchAllANMsReport();

        ANMReport anmXReport = new ANMReport("ANM X", asList(new ANMIndicatorSummary("IUD", "40", asList(new MonthSummary("1", "2012", "1", "1", asList("CASE 1"))))));
        ANMReport anmYReport = new ANMReport("ANM Y", asList(new ANMIndicatorSummary("IUD", "30", asList(new MonthSummary("1", "2012", "1", "1", asList("CASE 2"))))));
        assertTrue(anmReports.containsAll(asList(anmXReport, anmYReport)));
        assertEquals(2, anmReports.size());
    }

    private <T> void verifyCallsToCachedRepository(CacheableRepository<T> blah, T obj) {
        verify(blah, times(1)).fetch(obj);
        verify(blah, times(0)).save(obj);
        verifyNoMoreInteractions(blah);
    }
}
