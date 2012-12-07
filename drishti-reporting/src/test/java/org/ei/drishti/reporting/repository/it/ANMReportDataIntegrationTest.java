package org.ei.drishti.reporting.repository.it;

import org.ei.drishti.reporting.domain.ANM;
import org.ei.drishti.reporting.domain.ANMReportData;
import org.ei.drishti.reporting.domain.Dates;
import org.ei.drishti.reporting.domain.Indicator;
import org.ei.drishti.reporting.repository.AllANMReportDataRepository;
import org.ei.drishti.reporting.repository.cache.DatesCacheableRepository;
import org.joda.time.LocalDate;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

public class ANMReportDataIntegrationTest extends ANMReportsRepositoryIntegrationTestBase {
    @Autowired
    private AllANMReportDataRepository repository;

    @Autowired
    @Qualifier("anmReportsDatesRepository")
    private DatesCacheableRepository allDatesRepository;

    @Test
    @Transactional("anm_report")
    @Rollback
    public void shouldSaveANMReportData() throws Exception {
        ANM anm = new ANM("ANM X");
        Dates dates = new Dates(LocalDate.now().toDate());
        Indicator indicator = new Indicator("ANC Indicator");
        template.save(anm);
        allDatesRepository.save(dates);
        template.save(indicator);

        repository.save(anm, "EC CASE X", indicator, dates);

        assertEquals(1, template.loadAll(ANMReportData.class).size());
    }

    @Test
    @Transactional("anm_report")
    @Rollback
    public void shouldLoadDataWithDimensions() throws Exception {
        ANM anm = new ANM("ANM X");
        Dates dates = new Dates(LocalDate.now().toDate());
        Indicator indicator = new Indicator("ANC Indicator");
        template.save(anm);
        allDatesRepository.save(dates);
        template.save(indicator);

        repository.save(anm, "EC CASE X", indicator, dates);

        ANMReportData anmReportData = template.loadAll(ANMReportData.class).get(0);

        assertEquals(indicator, anmReportData.indicator());
        assertEquals(dates, anmReportData.date());
    }

    @Test
    @Transactional("anm_report")
    @Rollback
    public void shouldFetchReportDataByANMIdAndDate() throws Exception {
        ANM anm1 = new ANM("ANM X");
        ANM anm2 = new ANM("ANM Y");
        Dates dates = new Dates(LocalDate.parse("2012-03-31").toDate());
        Indicator indicator = new Indicator("ANC Indicator");
        template.save(anm1);
        template.save(anm2);
        allDatesRepository.save(dates);
        template.save(indicator);
        repository.save(anm1, "EC CASE X", indicator, dates);
        repository.save(anm2, "EC CASE Y", indicator, dates);

        List<ANMReportData> anmReportDataList = repository.fetchByANMIdAndDate(anm1.anmIdentifier(), LocalDate.parse("2012-01-01").toDate());

        assertTrue(anmReportDataList.contains(new ANMReportData(anm1, "EC CASE X", indicator, dates)));
        assertEquals(1, anmReportDataList.size());
    }

    @Test
    @Transactional("anm_report")
    @Rollback
    public void shouldFetchReportDataOnOrAfterGivenDate() throws Exception {
        ANM anm1 = new ANM("ANM X");
        ANM anm2 = new ANM("ANM Y");
        Dates dates1 = new Dates(LocalDate.parse("2012-03-30").toDate());
        Dates dates2 = new Dates(LocalDate.parse("2012-03-31").toDate());
        Dates dates3 = new Dates(LocalDate.parse("2012-04-01").toDate());
        Indicator indicator = new Indicator("ANC Indicator");
        template.save(anm1);
        template.save(anm2);
        allDatesRepository.save(dates1);
        allDatesRepository.save(dates2);
        allDatesRepository.save(dates3);
        template.save(indicator);
        repository.save(anm1, "EC CASE X", indicator, dates1);
        repository.save(anm1, "EC CASE X", indicator, dates2);
        repository.save(anm1, "EC CASE X", indicator, dates3);
        repository.save(anm2, "EC CASE Y", indicator, dates2);

        List<ANMReportData> anmReportDataList = repository.fetchByANMIdAndDate(anm1.anmIdentifier(), LocalDate.parse("2012-03-31").toDate());

        assertTrue(anmReportDataList.contains(new ANMReportData(anm1, "EC CASE X", indicator, dates2)));
        assertTrue(anmReportDataList.contains(new ANMReportData(anm1, "EC CASE X", indicator, dates3)));
        assertEquals(2, anmReportDataList.size());
    }
}
