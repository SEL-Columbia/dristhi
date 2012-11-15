package org.ei.drishti.reporting.repository;

import org.ei.drishti.common.monitor.Monitor;
import org.ei.drishti.common.monitor.Probe;
import org.ei.drishti.reporting.domain.ANM;
import org.ei.drishti.reporting.domain.Dates;
import org.ei.drishti.reporting.domain.Indicator;
import org.ei.drishti.reporting.repository.cache.*;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import static org.ei.drishti.common.monitor.Metric.REPORTING_ANM_REPORTS_CACHE_TIME;
import static org.ei.drishti.common.monitor.Metric.REPORTING_ANM_REPORTS_INSERT_TIME;

@Component
@Repository
public class ANMReportsRepository {

    private AllANMReportDataRepository anmReportDataRepository;
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
                                AllANMReportDataRepository anmReportDataRepository, Monitor monitor) {
        this.anmReportDataRepository = anmReportDataRepository;
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
        anmReportDataRepository.save(anm.id(), externalId, fetchedIndicator.id(), dates.id());
        monitor.end(probeForInsert);
    }
}
