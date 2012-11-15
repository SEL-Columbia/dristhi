package org.ei.drishti.reporting.repository.it;

import org.ei.drishti.reporting.domain.*;
import org.ei.drishti.reporting.repository.AllANMReportDataRepository;
import org.ei.drishti.reporting.repository.cache.ANMCacheableRepository;
import org.ei.drishti.reporting.repository.cache.DatesCacheableRepository;
import org.ei.drishti.reporting.repository.cache.IndicatorCacheableRepository;
import org.joda.time.LocalDate;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import static junit.framework.Assert.assertEquals;

public class AllANMReportDataIntegrationTest extends ANMReportsRepositoryIntegrationTestBase {

    @Autowired
    private AllANMReportDataRepository repository;

    @Autowired
    private @Qualifier("anmReportsANMRepository") ANMCacheableRepository allANMsRepository;

    @Autowired
    private @Qualifier("anmReportsDatesRepository") DatesCacheableRepository allDatesRepository;

    @Autowired
    private @Qualifier("anmReportsIndicatorRepository") IndicatorCacheableRepository allIndicatorsRepository;

    @Test
    public void shouldSaveAService() throws Exception {
        ANM anm = new ANM("ANM X");
        Dates dates = new Dates(LocalDate.now().toDate());
        Indicator indicator = new Indicator("ANC");
        allANMsRepository.save(anm);
        allDatesRepository.save(dates);
        allIndicatorsRepository.save(indicator);

        Integer anmId = allANMsRepository.fetch(anm).id();
        Integer dateId = allDatesRepository.fetch(dates).id();
        Integer indicatorId = allIndicatorsRepository.fetch(indicator).id();

        repository.save(anmId, "EC CASE X", indicatorId, dateId);

        assertEquals(1, template.loadAll(ANMReportData.class).size());
    }
}
