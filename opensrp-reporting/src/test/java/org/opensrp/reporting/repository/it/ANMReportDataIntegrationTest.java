package org.opensrp.reporting.repository.it;

import org.joda.time.LocalDate;
import org.junit.Test;
import org.opensrp.reporting.domain.ANM;
import org.opensrp.reporting.domain.ANMReportData;
import org.opensrp.reporting.domain.Indicator;
import org.opensrp.reporting.repository.AllANMReportDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

public class ANMReportDataIntegrationTest extends ANMReportsIntegrationTestBase {
    @Autowired
    private AllANMReportDataRepository repository;

    @Test
    @Transactional("anm_report")
    @Rollback
    public void shouldSaveANMReportData() throws Exception {
        ANM anm = new ANM("ANM X");
        Date dates = LocalDate.now().toDate();
        Indicator indicator = new Indicator("ANC Indicator");
        template.save(anm);
        template.save(indicator);

        repository.save(anm, "EC CASE X", indicator, dates);

        assertEquals(1, template.loadAll(ANMReportData.class).size());
    }

    @Test
    @Transactional("anm_report")
    @Rollback
    public void shouldLoadDataWithDimensions() throws Exception {
        ANM anm = new ANM("ANM X");
        Date dates = LocalDate.now().toDate();
        Indicator indicator = new Indicator("ANC Indicator");
        template.save(anm);
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
        Date date = LocalDate.parse("2012-03-31").toDate();
        Indicator indicator = new Indicator("ANC Indicator");
        template.save(anm1);
        template.save(anm2);
        template.save(indicator);
        repository.save(anm1, "EC CASE X", indicator, date);
        repository.save(anm2, "EC CASE Y", indicator, date);

        List<ANMReportData> anmReportDataList = repository.fetchByANMIdAndDate(anm1.anmIdentifier(), LocalDate.parse("2012-01-01").toDate());

        assertTrue(anmReportDataList.contains(new ANMReportData(anm1, "EC CASE X", indicator, date)));
        assertEquals(1, anmReportDataList.size());
    }

    @Test
    @Transactional("anm_report")
    @Rollback
    public void shouldFetchReportDataOnOrAfterGivenDate() throws Exception {
        ANM anm1 = new ANM("ANM X");
        ANM anm2 = new ANM("ANM Y");
        Date dates1 = LocalDate.parse("2012-03-30").toDate();
        Date dates2 = LocalDate.parse("2012-03-31").toDate();
        Date dates3 = LocalDate.parse("2012-04-01").toDate();
        Indicator indicator = new Indicator("ANC Indicator");
        template.save(anm1);
        template.save(anm2);
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

    @Test
    @Transactional("anm_report")
    @Rollback
    public void shouldDeleteAllIndicatorsForReportingMonthForAllANMs() throws Exception {
        ANM anm = new ANM("ANM X");
        Date startDate = LocalDate.parse("2013-01-26").toDate();
        Date endDate = LocalDate.parse("2013-02-25").toDate();
        Indicator indicator = new Indicator("ANC Indicator");
        template.save(anm);
        template.save(indicator);

        repository.delete("ANC Indicator", "2013-01-26", "2013-02-25");

        assertEquals(0, template.loadAll(ANMReportData.class).size());
    }

    @Test
    @Transactional("anm_report")
    @Rollback
    public void shouldFetchANMReportDataForReportingMonth() throws Exception {
        ANM anm = new ANM("ANM X");
        Date startDate = LocalDate.parse("2012-10-26").toDate();
        Indicator indicator = new Indicator("INDICATOR 1");
        template.save(anm);
        template.save(indicator);
        repository.save(anm, "123", indicator, startDate);

        List result = repository.getReportsFor("ANM X", "2012-10-26", "2012-11-25");

        assertEquals(1, result.size());
    }

    @Test
    @Transactional("anm_report")
    @Rollback
    public void shouldFetchAllReportDataForGivenExternalId() throws Exception {
        ANM anm = new ANM("ANM X");
        Date startDate = LocalDate.parse("2012-10-26").toDate();
        Indicator indicator = new Indicator("INDICATOR 1");
        String externalId = "entity id 1";
        template.save(anm);
        template.save(indicator);
        repository.save(anm, externalId, indicator, startDate);

        List result = repository.getReportsForExternalId("entity id 1");

        assertEquals(1, result.size());
    }

    @Test
    @Transactional("anm_report")
    @Rollback
    public void shouldDeleteAllReportDataForGivenExternalId() throws Exception {
        ANM anm = new ANM("ANM X");
        Date startDate = LocalDate.parse("2012-10-26").toDate();
        Indicator indicator = new Indicator("INDICATOR 1");
        String externalId = "entity id 1";
        String anotherExternalId = "entity id 2";
        template.save(anm);
        template.save(indicator);
        repository.save(anm, externalId, indicator, startDate);
        repository.save(anm, anotherExternalId, indicator, startDate);

        repository.deleteReportsForExternalId("entity id 1");

        assertEquals(0, repository.getReportsForExternalId("entity id 1").size());
    }
}
