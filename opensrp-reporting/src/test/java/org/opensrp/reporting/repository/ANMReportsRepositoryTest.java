package org.opensrp.reporting.repository;

import org.opensrp.common.domain.*;
import org.opensrp.common.monitor.Monitor;
import org.opensrp.common.util.DateUtil;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static java.util.Arrays.asList;
import static junit.framework.Assert.assertEquals;
import static org.opensrp.common.AllConstants.ReportDataParameters.*;
import static org.opensrp.common.util.ANMIndicatorSummaryFactory.*;
import static org.joda.time.LocalDate.parse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;
import org.opensrp.reporting.domain.ANM;
import org.opensrp.reporting.domain.ANMReportData;
import org.opensrp.reporting.domain.AnnualTarget;
import org.opensrp.reporting.domain.Indicator;
import org.opensrp.reporting.repository.ANMReportsRepository;
import org.opensrp.reporting.repository.AllANMReportDataRepository;
import org.opensrp.reporting.repository.AllAnnualTargetsRepository;
import org.opensrp.reporting.repository.cache.ANMCacheableRepository;
import org.opensrp.reporting.repository.cache.CacheableRepository;
import org.opensrp.reporting.repository.cache.IndicatorCacheableRepository;
import org.opensrp.reporting.repository.cache.ReadOnlyCacheableRepository;

public class ANMReportsRepositoryTest {
    @Mock
    private ANMCacheableRepository anmRepository;

    @Mock
    private IndicatorCacheableRepository indicatorRepository;

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
        repository = new ANMReportsRepository(anmRepository, indicatorRepository,
                anmReportDataRepository, annualTargetsRepository, monitor, new ReportMonth());
    }

    @Test
    public void shouldSaveANMReportsAndUseCachedRepositories() throws Exception {
        String anmIdentifier = "ANM X";
        String externalId = "EC CASE 1";
        String indicator = "IUD";
        Date date = parse("2012-04-04").toDate();

        Indicator fetchedIndicator = new Indicator(2, indicator);
        ANM anm = new ANM(2, anmIdentifier);

        when(anmRepository.fetch(new ANM((anmIdentifier)))).thenReturn(anm);
        when(indicatorRepository.fetch(new Indicator(indicator))).thenReturn(fetchedIndicator);

        repository.save(anmIdentifier, externalId, indicator, "2012-04-04", null);
        repository.save(anmIdentifier, externalId, indicator, "2012-04-04", null);

        verifyCallsToReadOnlyCachedRepository(anmRepository, new ANM(anmIdentifier));
        verifyCallsToReadOnlyCachedRepository(indicatorRepository, fetchedIndicator);

        verify(anmReportDataRepository, times(2)).save(anm, externalId, fetchedIndicator, date);
    }


    @Test
    public void shouldSaveAsPerQuantityIfQuantityIsNotNull() throws Exception {
        String anmIdentifier = "ANM X";
        String externalId = "EC CASE 1";
        String indicator = "IUD";
        Date date = parse("2012-04-04").toDate();

        Indicator fetchedIndicator = new Indicator(2, indicator);
        ANM anm = new ANM(2, anmIdentifier);

        when(anmRepository.fetch(new ANM((anmIdentifier)))).thenReturn(anm);
        when(indicatorRepository.fetch(new Indicator(indicator))).thenReturn(fetchedIndicator);

        repository.save(anmIdentifier, externalId, indicator, "2012-04-04", "50");

        verify(anmReportDataRepository, times(50)).save(anm, externalId, fetchedIndicator, date);
    }

    @Test
    public void shouldFetchANMIndicatorSummaries() throws Exception {
        DateUtil.fakeIt(parse("2012-06-30"));
        ANM anm = new ANM("ANM X");
        Date date1 = parse("2012-03-29").toDate();
        Date date2 = parse("2012-05-15").toDate();
        Date date3 = parse("2012-05-30").toDate();
        Indicator indicator1 = new Indicator("IUD");
        Indicator indicator2 = new Indicator("ANC");
        when(anmReportDataRepository.fetchByANMIdAndDate("ANM X", parse("2012-03-26").toDate())).thenReturn(asList(
                new ANMReportData(anm, "CASE 1", indicator1, date1),
                new ANMReportData(anm, "CASE 2", indicator1, date1),
                new ANMReportData(anm, "CASE 3", indicator1, date2),
                new ANMReportData(anm, "CASE 4", indicator1, date2),
                new ANMReportData(anm, "CASE 5", indicator2, date3),
                new ANMReportData(anm, "CASE 6", indicator2, date3)));
        when(annualTargetsRepository.fetchFor("ANM X", indicator1, parse("2012-06-30").toDate())).thenReturn(new AnnualTarget(1, 1, "40", parse("2012-03-26").toDate(), parse("2013-03-25").toDate()));
        when(annualTargetsRepository.fetchFor("ANM X", indicator2, parse("2012-06-30").toDate())).thenReturn(new AnnualTarget(1, 1, "30", parse("2012-03-26").toDate(), parse("2013-03-25").toDate()));

        List<ANMIndicatorSummary> anmIndicatorSummaries = repository.fetchANMSummary("ANM X");

        assertTrue(anmIndicatorSummaries.containsAll(asList(createSummaryForANC(), createSummaryForIUD())));
        assertEquals(2, anmIndicatorSummaries.size());
    }

    @Test
    public void shouldSetMonthOfSummaryToBeNextYearsInCaseOfLastMonthOfTheYear() throws Exception {
        DateUtil.fakeIt(parse("2013-01-30"));
        ANM anm = new ANM("ANM X");
        Date date = parse("2012-12-29").toDate();
        Indicator indicator = new Indicator("IUD");
        when(anmReportDataRepository.fetchByANMIdAndDate("ANM X", parse("2012-03-26").toDate())).thenReturn(asList(new ANMReportData(anm, "CASE 1", indicator, date)));
        when(annualTargetsRepository.fetchFor("ANM X", indicator, parse("2013-01-30").toDate())).thenReturn(new AnnualTarget(1, 1, "40", parse("2012-03-26").toDate(), parse("2013-03-25").toDate()));

        List<ANMIndicatorSummary> anmIndicatorSummaries = repository.fetchANMSummary("ANM X");

        assertEquals(asList(new ANMIndicatorSummary("IUD", "40", asList(new MonthSummary("1", "2013", "1", "1", asList("CASE 1"))))), anmIndicatorSummaries);
    }

    @Test
    public void shouldFetchANMIndicatorSummariesIncludingAllServicesProvidedTillTheCurrentMonth() throws Exception {
        DateUtil.fakeIt(parse("2013-01-15"));
        ANM anm = new ANM("ANM X");
        Date january = parse("2013-01-11").toDate();
        Indicator indicator1 = new Indicator("IUD");
        when(anmReportDataRepository.fetchByANMIdAndDate("ANM X", parse("2012-03-26").toDate())).thenReturn(asList(
                new ANMReportData(anm, "CASE 1", indicator1, january)));
        when(annualTargetsRepository.fetchFor("ANM X", indicator1, parse("2013-01-15").toDate())).thenReturn(new AnnualTarget(1, 1, "40", parse("2012-03-26").toDate(), parse("2013-03-25").toDate()));

        List<ANMIndicatorSummary> anmIndicatorSummaries = repository.fetchANMSummary("ANM X");

        assertEquals(1, anmIndicatorSummaries.size());
        assertEquals(1, anmIndicatorSummaries.get(0).monthlySummaries().size());
    }

    @Test
    public void shouldFetchANMIndicatorSummariesWithDistinctExternalIds() throws Exception {
        DateUtil.fakeIt(parse("2013-03-22"));
        ANM anm = new ANM("ANM X");
        Date dates = parse("2013-03-21").toDate();
        Indicator indicator = new Indicator("CONDOM");
        when(anmReportDataRepository.fetchByANMIdAndDate("ANM X", parse("2012-03-26").toDate())).thenReturn(asList(
                new ANMReportData(anm, "CASE 7", indicator, dates),
                new ANMReportData(anm, "CASE 7", indicator, dates)));
        when(annualTargetsRepository.fetchFor("ANM X", indicator, parse("2013-03-22").toDate())).thenReturn(new AnnualTarget(1, 1, "20", parse("2012-03-26").toDate(), parse("2013-03-25").toDate()));

        List<ANMIndicatorSummary> anmIndicatorSummaries = repository.fetchANMSummary("ANM X");

        System.out.println(anmIndicatorSummaries);
        assertTrue(anmIndicatorSummaries.containsAll(asList(createSummaryForCondom())));
        assertEquals(1, anmIndicatorSummaries.size());
    }

    @Test
    public void shouldFetchANMIndicatorSummariesForCurrentReportingYear() throws Exception {
        DateUtil.fakeIt(parse("2013-01-02"));

        repository.fetchANMSummary("ANM X");

        verify(anmReportDataRepository).fetchByANMIdAndDate("ANM X", parse("2012-03-26").toDate());
    }

    @Test
    public void shouldFetchReportsForCurrentYearIfCurrentDayIsFirstDayOfTheReportingYear() throws Exception {
        DateUtil.fakeIt(parse("2012-03-26"));

        repository.fetchANMSummary("ANM X");

        verify(anmReportDataRepository).fetchByANMIdAndDate("ANM X", parse("2012-03-26").toDate());
    }

    @Test
    public void shouldFetchReportsForPreviousYearIfCurrentDayIsLastDayOfTheReportingYear() throws Exception {
        DateUtil.fakeIt(parse("2013-03-25"));

        repository.fetchANMSummary("ANM X");

        verify(anmReportDataRepository).fetchByANMIdAndDate("ANM X", parse("2012-03-26").toDate());
    }

    @Test
    public void shouldReturnEmptyANMIndicatorSummariesWhenThereIsNoDataForANM() throws Exception {
        DateUtil.fakeIt(parse("2012-03-31"));
        when(anmReportDataRepository.fetchByANMIdAndDate("ANM X", parse("2012-01-01").toDate())).thenReturn(null);

        List<ANMIndicatorSummary> anmIndicatorSummaries = repository.fetchANMSummary("ANM X");

        assertEquals(0, anmIndicatorSummaries.size());
    }

    @Test
    public void shouldReturnANMIndicatorSummariesWithNullAnnualTargetWhenThereIsNoAnnualTargetForANM() throws Exception {
        DateUtil.fakeIt(parse("2012-06-30"));
        ANM anm = new ANM("ANM X");
        Date date1 = parse("2012-03-29").toDate();
        Date date2 = parse("2012-05-15").toDate();
        Indicator indicator = new Indicator("IUD");
        when(anmReportDataRepository.fetchByANMIdAndDate("ANM X", parse("2012-03-26").toDate())).thenReturn(asList(
                new ANMReportData(anm, "CASE 1", indicator, date1),
                new ANMReportData(anm, "CASE 2", indicator, date1),
                new ANMReportData(anm, "CASE 3", indicator, date2),
                new ANMReportData(anm, "CASE 4", indicator, date2)));
        when(annualTargetsRepository.fetchFor("ANM X", new Indicator("IUD"), parse("2012-06-30").toDate())).thenReturn(null);

        List<ANMIndicatorSummary> anmIndicatorSummaries = repository.fetchANMSummary("ANM X");

        ANMIndicatorSummary expectedANMIUDSummary = createSummaryForIUD(null);
        assertEquals(asList(expectedANMIUDSummary), anmIndicatorSummaries);
    }

    @Test
    public void shouldReturnAllANMReports() throws Exception {
        DateUtil.fakeIt(parse("2012-06-30"));
        ANM anmX = new ANM("ANM X");
        ANM anmY = new ANM("ANM Y");
        Date date1 = parse("2012-03-29").toDate();
        Date date2 = parse("2012-04-30").toDate();
        Date date3 = parse("2012-05-27").toDate();
        Indicator indicator1 = new Indicator("IUD");
        Indicator indicator2 = new Indicator("ANC");
        when(anmReportDataRepository.fetchByANMIdAndDate("ANM X", parse("2012-03-26").toDate())).thenReturn(asList(
                new ANMReportData(anmX, "CASE 1", indicator1, date1),
                new ANMReportData(anmX, "CASE 2", indicator1, date1),
                new ANMReportData(anmX, "CASE 3", indicator1, date2),
                new ANMReportData(anmX, "CASE 4", indicator1, date2)));
        when(anmReportDataRepository.fetchByANMIdAndDate("ANM Y", parse("2012-03-26").toDate())).thenReturn(asList(
                new ANMReportData(anmY, "CASE 5", indicator2, date3),
                new ANMReportData(anmY, "CASE 6", indicator2, date3)));
        when(annualTargetsRepository.fetchFor("ANM X", indicator1, parse("2012-06-30").toDate())).thenReturn(new AnnualTarget(1, 1, "40", parse("2012-03-26").toDate(), parse("2013-03-25").toDate()));
        when(annualTargetsRepository.fetchFor("ANM Y", indicator2, parse("2012-06-30").toDate())).thenReturn(new AnnualTarget(1, 1, "30", parse("2012-03-26").toDate(), parse("2013-03-25").toDate()));
        when(anmRepository.fetchAll()).thenReturn(asList(anmX, anmY));

        List<ANMReport> anmReports = repository.fetchAllANMsReport();

        ANMReport anmXReport = new ANMReport("ANM X", asList(createSummaryForIUD()));
        ANMReport anmYReport = new ANMReport("ANM Y", asList(createSummaryForANC()));

        assertTrue(anmReports.containsAll(asList(anmXReport, anmYReport)));
        assertEquals(2, anmReports.size());
    }

    @Test
    public void shouldUpdateAllIndicatorsForReportingMonthForAllANMS() {
        String anmIdentifier = "ANM X";
        String externalId = "EC CASE 1";
        String indicator = "INDICATOR 1";
        String reportDate = "2013-01-26";
        Date date = parse(reportDate).toDate();

        Indicator fetchedIndicator = new Indicator(2, indicator);
        ANM anm = new ANM(2, anmIdentifier);
        HashMap<String, String> data = new HashMap<String, String>();
        data.put(SERVICE_PROVIDER_TYPE, "ANM");
        data.put(SERVICE_PROVIDED_DATE, reportDate);
        data.put(EXTERNAL_ID, externalId);
        data.put(PHC, "phc");
        data.put(QUANTITY, "1");
        data.put(INDICATOR, indicator);
        data.put(ANM_IDENTIFIER, anmIdentifier);

        ReportingData reportingData = new ReportingData("type", data)
                .withQuantity("1");

        ReportDataUpdateRequest request = new ReportDataUpdateRequest().withType("type")
                .withIndicator(indicator)
                .withStartDate("2013-01-26")
                .withEndDate("2013-02-25")
                .withReportingData(asList(reportingData));

        when(anmRepository.fetch(new ANM((anmIdentifier)))).thenReturn(anm);
        when(indicatorRepository.fetch(new Indicator(indicator))).thenReturn(fetchedIndicator);

        repository.update(request);

        verify(anmReportDataRepository).delete("INDICATOR 1", "2013-01-26", "2013-02-25");
        verify(anmReportDataRepository).save(anm, externalId, fetchedIndicator, date);
    }

    @Test
    public void shouldCallANMReportRepositoryForReports() {
        repository.getReportsFor("ANM X", "2013-10-26", "2013-11-25");

        verify(anmReportDataRepository).getReportsFor("ANM X", "2013-10-26", "2013-11-25");
    }

    @Test
    public void shouldDeleteANMReportsForGivenExternalId() {
        ReportDataDeleteRequest request = new ReportDataDeleteRequest()
                .withType("type")
                .withDristhiEntityId("entity id 1");
        repository.delete(request);
        verify(anmReportDataRepository).deleteReportsForExternalId("entity id 1");
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
