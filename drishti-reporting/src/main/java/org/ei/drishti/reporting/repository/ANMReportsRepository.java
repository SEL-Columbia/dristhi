package org.ei.drishti.reporting.repository;

import org.ei.drishti.common.domain.ANMReport;
import org.ei.drishti.common.domain.ANMIndicatorSummary;
import org.ei.drishti.common.domain.MonthSummary;
import org.ei.drishti.common.monitor.Monitor;
import org.ei.drishti.common.monitor.Probe;
import org.ei.drishti.common.util.DateUtil;
import org.ei.drishti.reporting.domain.*;
import org.ei.drishti.reporting.repository.cache.ANMCacheableRepository;
import org.ei.drishti.reporting.repository.cache.CachingRepository;
import org.ei.drishti.reporting.repository.cache.DatesCacheableRepository;
import org.ei.drishti.reporting.repository.cache.IndicatorCacheableRepository;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static ch.lambdaj.Lambda.*;
import static org.ei.drishti.common.monitor.Metric.REPORTING_ANM_REPORTS_CACHE_TIME;
import static org.ei.drishti.common.monitor.Metric.REPORTING_ANM_REPORTS_INSERT_TIME;
import static org.hamcrest.Matchers.equalTo;

@Component
@Repository
public class ANMReportsRepository {
    private AllANMReportDataRepository anmReportDataRepository;
    private AllAnnualTargetsRepository annualTargetsRepository;
    private Monitor monitor;

    private CachingRepository<ANM> cachedANMs;
    private CachingRepository<Dates> cachedDates;
    private CachingRepository<Indicator> cachedIndicators;

    protected ANMReportsRepository() {
    }

    @Autowired
    public ANMReportsRepository(@Qualifier("anmReportsANMRepository") ANMCacheableRepository anmRepository,
                                @Qualifier("anmReportsDatesRepository") DatesCacheableRepository datesRepository,
                                @Qualifier("anmReportsIndicatorRepository") IndicatorCacheableRepository indicatorRepository,
                                AllANMReportDataRepository anmReportDataRepository, AllAnnualTargetsRepository annualTargetsRepository, Monitor monitor) {
        this.anmReportDataRepository = anmReportDataRepository;
        this.annualTargetsRepository = annualTargetsRepository;
        this.monitor = monitor;
        cachedANMs = new CachingRepository<>(anmRepository);
        cachedDates = new CachingRepository<>(datesRepository);
        cachedIndicators = new CachingRepository<>(indicatorRepository);
    }

    public void save(String anmIdentifier, String externalId, String indicator, String date) {
        Probe probeForCache = monitor.start(REPORTING_ANM_REPORTS_CACHE_TIME);
        ANM anm = cachedANMs.fetch(new ANM(anmIdentifier));
        Indicator fetchedIndicator = cachedIndicators.fetch(new Indicator(indicator));
        Dates dates = cachedDates.fetch(new Dates(LocalDate.parse(date).toDate()));
        monitor.end(probeForCache);

        Probe probeForInsert = monitor.start(REPORTING_ANM_REPORTS_INSERT_TIME);
        anmReportDataRepository.save(anm, externalId, fetchedIndicator, dates);
        monitor.end(probeForInsert);
    }

    //Need to refactor
    public List<ANMIndicatorSummary> fetchANMSummary(String anmIdentifier) {
        List<ANMIndicatorSummary> anmIndicatorSummaries = new ArrayList<>();

        List<ANMReportData> allReportData = anmReportDataRepository.fetchByANMIdAndDate(anmIdentifier, LocalDate.parse("2012-01-01").toDate());

        Collection<Indicator> indicators = getDistinctIndicators(allReportData);
        for (Indicator indicator : indicators) {
            List<ANMReportData> allReportDataForIndicator = filterReportsByIndicator(allReportData, indicator);
            int aggregatedProgress = 0;
            List<MonthSummary> monthSummaries = new ArrayList<>();
            for (int month = 0; month < DateUtil.today().getMonthOfYear(); month++) {
                List<ANMReportData> allReportDataForAMonth = filterReportsByMonth(allReportDataForIndicator, month);
                if (allReportDataForAMonth.size() == 0) {
                    continue;
                }

                int year = allReportDataForAMonth.get(0).date().year();
                int currentProgress = allReportDataForAMonth.size();
                aggregatedProgress += currentProgress;
                List<String> externalIds = getAllExternalIds(allReportDataForAMonth);

                monthSummaries.add(new MonthSummary(String.valueOf(month + 1), String.valueOf(year), String.valueOf(currentProgress), String.valueOf(aggregatedProgress), externalIds));
            }
            AnnualTarget annualTarget = annualTargetsRepository.fetchFor(anmIdentifier, indicator);
            String target = annualTarget == null ? null : annualTarget.target();
            anmIndicatorSummaries.add(new ANMIndicatorSummary(indicator.indicator(), target, monthSummaries));
        }
        return anmIndicatorSummaries;
    }

    public List<ANMReport> fetchAllANMsReport() {
        List<ANM> allANMs = cachedANMs.fetchAll();
        ArrayList<ANMReport> anmReports = new ArrayList<>();
        for (ANM anm : allANMs) {
            anmReports.add(new ANMReport(anm.anmIdentifier(), fetchANMSummary(anm.anmIdentifier())));
        }
        return anmReports;
    }

    private List<String> getAllExternalIds(List<ANMReportData> reportDataList) {
        return extract(reportDataList, on(ANMReportData.class).externalId());
    }

    private Collection<Indicator> getDistinctIndicators(List<ANMReportData> reportDataList) {
        return selectDistinct(collect(reportDataList, on(ANMReportData.class).indicator()));
    }

    private List<ANMReportData> filterReportsByMonth(List<ANMReportData> reportDataList, int month) {
        return filter(having(on(ANMReportData.class).date().month(), equalTo(month)), reportDataList);
    }

    private List<ANMReportData> filterReportsByIndicator(List<ANMReportData> reportDataList, Indicator indicator) {
        return filter(having(on(ANMReportData.class).indicator(), equalTo(indicator)), reportDataList);
    }
}
