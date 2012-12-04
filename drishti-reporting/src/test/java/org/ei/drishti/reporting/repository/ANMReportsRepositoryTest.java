package org.ei.drishti.reporting.repository;

import org.ei.drishti.common.domain.ANMIndicatorSummary;
import org.ei.drishti.common.domain.ANMReport;
import org.ei.drishti.common.monitor.Monitor;
import org.ei.drishti.common.util.DateUtil;
import org.ei.drishti.reporting.domain.*;
import org.ei.drishti.reporting.repository.cache.*;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.Date;
import java.util.List;

import static java.util.Arrays.asList;
import static junit.framework.Assert.assertEquals;
import static org.ei.drishti.common.util.ANMIndicatorSummaryFactory.*;
import static org.joda.time.LocalDate.parse;
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

        verifyCallsToReadOnlyCachedRepository(anmRepository, new ANM(anmIdentifier));
        verifyCallsToReadOnlyCachedRepository(indicatorRepository, indicator_);
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

        assertTrue(anmIndicatorSummaries.containsAll(asList(createSummaryForANC(), createSummaryForIUD())));
        assertEquals(2, anmIndicatorSummaries.size());
    }

    @Test
    public void shouldFetchANMIndicatorSummariesWithDistinctExternalIds() throws Exception {
        DateUtil.fakeIt(LocalDate.parse("2012-03-31"));
        ANM anm = new ANM("ANM X");
        Dates dates = new Dates(parse("2012-01-05").toDate());
        Indicator indicator = new Indicator("CONDOM");
        when(anmReportDataRepository.fetchByANMIdAndDate("ANM X", parse("2012-01-01").toDate())).thenReturn(asList(
                new ANMReportData(anm, "CASE 7", indicator, dates),
                new ANMReportData(anm, "CASE 7", indicator, dates)));
        when(annualTargetsRepository.fetchFor("ANM X", indicator)).thenReturn(new AnnualTarget(1, 1, "20"));

        List<ANMIndicatorSummary> anmIndicatorSummaries = repository.fetchANMSummary("ANM X");

        assertTrue(anmIndicatorSummaries.containsAll(asList(createSummaryForCondom())));
        assertEquals(1, anmIndicatorSummaries.size());
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
        Dates january = new Dates(parse("2012-01-05").toDate());
        Dates february = new Dates(parse("2012-02-15").toDate());
        Indicator indicator = new Indicator("IUD");
        when(anmReportDataRepository.fetchByANMIdAndDate("ANM X", parse("2012-01-01").toDate())).thenReturn(asList(
                new ANMReportData(anm, "CASE 1", indicator, january),
                new ANMReportData(anm, "CASE 2", indicator, january),
                new ANMReportData(anm, "CASE 3", indicator, february),
                new ANMReportData(anm, "CASE 4", indicator, february)));
        when(annualTargetsRepository.fetchFor("ANM X", new Indicator("IUD"))).thenReturn(null);

        List<ANMIndicatorSummary> anmIndicatorSummaries = repository.fetchANMSummary("ANM X");

        ANMIndicatorSummary expectedANMIUDSummary = createSummaryForIUD(null);
        assertEquals(asList(expectedANMIUDSummary), anmIndicatorSummaries);
    }

    @Test
    public void shouldReturnAllANMReports() throws Exception {
        DateUtil.fakeIt(LocalDate.parse("2012-03-31"));
        ANM anmX = new ANM("ANM X");
        ANM anmY = new ANM("ANM Y");
        Dates january = new Dates(parse("2012-01-05").toDate());
        Dates february = new Dates(parse("2012-02-15").toDate());
        Dates march = new Dates(parse("2012-03-25").toDate());
        Indicator indicator1 = new Indicator("IUD");
        Indicator indicator2 = new Indicator("ANC");
        when(anmReportDataRepository.fetchByANMIdAndDate("ANM X", parse("2012-01-01").toDate())).thenReturn(asList(
                new ANMReportData(anmX, "CASE 1", indicator1, january),
                new ANMReportData(anmX, "CASE 2", indicator1, january),
                new ANMReportData(anmX, "CASE 3", indicator1, february),
                new ANMReportData(anmX, "CASE 4", indicator1, february)));
        when(anmReportDataRepository.fetchByANMIdAndDate("ANM Y", parse("2012-01-01").toDate())).thenReturn(asList(
                new ANMReportData(anmY, "CASE 5", indicator2, march),
                new ANMReportData(anmY, "CASE 6", indicator2, march)));
        when(annualTargetsRepository.fetchFor("ANM X", indicator1)).thenReturn(new AnnualTarget(1, 1, "40"));
        when(annualTargetsRepository.fetchFor("ANM Y", indicator2)).thenReturn(new AnnualTarget(1, 1, "30"));
        when(anmRepository.fetchAll()).thenReturn(asList(anmX, anmY));

        List<ANMReport> anmReports = repository.fetchAllANMsReport();

        ANMReport anmXReport = new ANMReport("ANM X", asList(createSummaryForIUD()));
        ANMReport anmYReport = new ANMReport("ANM Y", asList(createSummaryForANC()));
        assertTrue(anmReports.containsAll(asList(anmXReport, anmYReport)));
        assertEquals(2, anmReports.size());
    }

    private <T> void verifyCallsToReadOnlyCachedRepository(ReadOnlyCacheableRepository<T> repo, T object) {
        verify(repo, times(1)).fetch(object);
        verifyNoMoreInteractions(repo);
    }


    private <T> void verifyCallsToCachedRepository(CacheableRepository<T> blah, T obj) {
        verify(blah, times(1)).fetch(obj);
        verify(blah, times(0)).save(obj);
        verifyNoMoreInteractions(blah);
    }
}
