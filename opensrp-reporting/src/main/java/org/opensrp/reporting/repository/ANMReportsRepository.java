package org.opensrp.reporting.repository;

import org.opensrp.common.domain.*;
import org.opensrp.common.monitor.Monitor;
import org.opensrp.common.monitor.Probe;
import org.joda.time.LocalDate;
import org.opensrp.reporting.domain.ANM;
import org.opensrp.reporting.domain.ANMReportData;
import org.opensrp.reporting.domain.AnnualTarget;
import org.opensrp.reporting.domain.Indicator;
import org.opensrp.reporting.repository.cache.ANMCacheableRepository;
import org.opensrp.reporting.repository.cache.IndicatorCacheableRepository;
import org.opensrp.reporting.repository.cache.ReadOnlyCachingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static ch.lambdaj.Lambda.*;
import static java.lang.String.valueOf;
import static org.opensrp.common.AllConstants.ReportDataParameters.*;
import static org.opensrp.common.monitor.Metric.REPORTING_ANM_REPORTS_CACHE_TIME;
import static org.opensrp.common.monitor.Metric.REPORTING_ANM_REPORTS_INSERT_TIME;
import static org.opensrp.common.util.DateUtil.today;
import static org.hamcrest.Matchers.*;

@Repository
public class ANMReportsRepository {
    private ReportMonth reportMonth;
    private AllANMReportDataRepository anmReportDataRepository;
    private AllAnnualTargetsRepository annualTargetsRepository;
    private Monitor monitor;

    private ReadOnlyCachingRepository<ANM> cachedANMs;
    private ReadOnlyCachingRepository<Indicator> cachedIndicators;

    protected ANMReportsRepository() {
    }

    @Autowired
    public ANMReportsRepository(ANMCacheableRepository anmRepository,
                                @Qualifier("anmReportsIndicatorRepository") IndicatorCacheableRepository indicatorRepository,
                                AllANMReportDataRepository anmReportDataRepository,
                                AllAnnualTargetsRepository annualTargetsRepository, Monitor monitor, ReportMonth reportMonth) {
        this.anmReportDataRepository = anmReportDataRepository;
        this.annualTargetsRepository = annualTargetsRepository;
        this.monitor = monitor;
        this.reportMonth = reportMonth;
        cachedANMs = new ReadOnlyCachingRepository<>(anmRepository);
        cachedIndicators = new ReadOnlyCachingRepository<>(indicatorRepository);
    }

    @Transactional("anm_report")
    public void save(String anmIdentifier, String externalId, String indicator, String date, String quantity) {
        Probe probeForCache = monitor.start(REPORTING_ANM_REPORTS_CACHE_TIME);
        ANM anm = cachedANMs.fetch(new ANM(anmIdentifier));
        Indicator fetchedIndicator = cachedIndicators.fetch(new Indicator(indicator));
        monitor.end(probeForCache);

        int count = getCount(quantity);
        Probe probeForInsert = monitor.start(REPORTING_ANM_REPORTS_INSERT_TIME);
        for (int i = 0; i < count; i++) {
            try {
                anmReportDataRepository.save(anm, externalId, fetchedIndicator, LocalDate.parse(date).toDate());
            } catch (Exception e) {
                cachedANMs.clear(anm);
                cachedIndicators.clear(fetchedIndicator);
            }
        }
        monitor.end(probeForInsert);
    }

    //Todo: Need to refactor
    @Transactional("anm_report")
    public List<ANMIndicatorSummary> fetchANMSummary(String anmIdentifier) {
        List<ANMIndicatorSummary> anmIndicatorSummaries = new ArrayList<>();

        List<ANMReportData> allReportData = anmReportDataRepository.fetchByANMIdAndDate(anmIdentifier, reportMonth.startDateOfReportingYear());

        Collection<Indicator> indicators = getDistinctIndicators(allReportData);
        for (Indicator indicator : indicators) {
            List<ANMReportData> allReportDataForIndicator = filterReportsByIndicator(allReportData, indicator);
            int aggregatedProgress = 0;
            List<MonthSummary> monthSummaries = new ArrayList<>();

            for (LocalDate indexDate = new LocalDate(reportMonth.startDateOfReportingYear()); indexDate.isBefore(reportMonth.startDateOfNextReportingMonth(today())); indexDate = indexDate.plusMonths(1)) {
                LocalDate reportingMonthEndDate = new LocalDate(indexDate).plusMonths(1);
                List<ANMReportData> allReportDataForAMonth = filterReportsByMonth(allReportDataForIndicator, indexDate, reportMonth.endDateOfReportingMonthGivenStartDate(indexDate));
                if (allReportDataForAMonth.size() == 0) {
                    continue;
                }

                int currentProgress = allReportDataForAMonth.size();
                aggregatedProgress += currentProgress;
                List<String> externalIds = getAllExternalIds(allReportDataForAMonth);

                monthSummaries.add(new MonthSummary(valueOf(reportingMonthEndDate.getMonthOfYear()), valueOf(reportingMonthEndDate.getYear()),
                        valueOf(currentProgress), valueOf(aggregatedProgress), externalIds));
            }
            AnnualTarget annualTarget = annualTargetsRepository.fetchFor(anmIdentifier, indicator, today().toDate());
            String target = annualTarget == null ? null : annualTarget.target();
            anmIndicatorSummaries.add(new ANMIndicatorSummary(indicator.indicator(), target, monthSummaries));
        }
        return anmIndicatorSummaries;
    }

    @Transactional("anm_report")
    public List<ANMReport> fetchAllANMsReport() {
        List<ANM> allANMs = cachedANMs.fetchAll();
        ArrayList<ANMReport> anmReports = new ArrayList<>();
        for (ANM anm : allANMs) {
            anmReports.add(new ANMReport(anm.anmIdentifier(), fetchANMSummary(anm.anmIdentifier())));
        }
        return anmReports;
    }

    private List<String> getAllExternalIds(List<ANMReportData> reportDataList) {
        Collection<String> externalIds = selectDistinct(collect(reportDataList, on(ANMReportData.class).externalId()));
        return new ArrayList<>(externalIds);
    }

    private Collection<Indicator> getDistinctIndicators(List<ANMReportData> reportDataList) {
        return selectDistinct(collect(reportDataList, on(ANMReportData.class).indicator()));
    }

    private List<ANMReportData> filterReportsByMonth(List<ANMReportData> reportDataList, LocalDate startDate, LocalDate endDate) {
        return filter(having(on(ANMReportData.class).date(), allOf(greaterThanOrEqualTo(startDate.toDate()), lessThanOrEqualTo(endDate.toDate()))), reportDataList);
    }

    private List<ANMReportData> filterReportsByIndicator(List<ANMReportData> reportDataList, Indicator indicator) {
        return filter(having(on(ANMReportData.class).indicator(), equalTo(indicator)), reportDataList);
    }

    private int getCount(String quantity) {
        return quantity == null ? 1 : Integer.parseInt(quantity);
    }

    @Transactional("anm_report")
    public void update(ReportDataUpdateRequest request) {
        anmReportDataRepository.delete(request.indicator(), request.startDate(), request.endDate());
        saveReportDataForIndicator(request.reportingData(), request.indicator());
    }

    private void saveReportDataForIndicator(List<ReportingData> reportingData, String indicator) {
        for (ReportingData data : reportingData) {
            save(data.get(ANM_IDENTIFIER), data.get(EXTERNAL_ID), indicator, data.get(SERVICE_PROVIDED_DATE), data.get(QUANTITY));
        }
    }

    @Transactional("anm_report")
    public List getReportsFor(String anmId, String startDate, String endDate) {
        return anmReportDataRepository.getReportsFor(anmId, startDate, endDate);
    }

    @Transactional("anm_report")
    public void delete(ReportDataDeleteRequest request) {
        anmReportDataRepository.deleteReportsForExternalId(request.dristhiEntityId());
    }
}
