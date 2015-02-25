package org.opensrp.reporting.repository.it;

import org.junit.Test;
import org.opensrp.reporting.domain.ANM;
import org.opensrp.reporting.domain.AnnualTarget;
import org.opensrp.reporting.domain.Indicator;
import org.opensrp.reporting.repository.AllAnnualTargetsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;
import static org.joda.time.LocalDate.parse;

public class AllAnnualTargetsRepositoryIntegrationTest extends ANMReportsIntegrationTestBase {
    @Autowired
    private AllAnnualTargetsRepository repository;

    @Test
    @Transactional("anm_report")
    @Rollback
    public void shouldFetchAnnualTargetForGivenANMAndIndicator() throws Exception {
        Indicator indicator = new Indicator("INDICATOR");
        ANM anm = new ANM("ANM X");
        template.save(indicator);
        template.save(anm);
        template.save(new AnnualTarget(anm.id(), indicator.id(), "40", parse("2012-03-26").toDate(), parse("2013-03-25").toDate()));

        AnnualTarget annualTarget = repository.fetchFor(anm.anmIdentifier(), indicator, parse("2012-03-31").toDate());

        assertEquals("40", annualTarget.target());
    }

    @Test
    @Transactional("anm_report")
    @Rollback
    public void shouldFetchNoAnnualTargetIfReportDateIsNotWithinRange() throws Exception {
        Indicator indicator = new Indicator("INDICATOR");
        ANM anm = new ANM("ANM X");
        template.save(indicator);
        template.save(anm);
        template.save(new AnnualTarget(anm.id(), indicator.id(), "40", parse("2012-03-26").toDate(), parse("2013-03-25").toDate()));

        AnnualTarget annualTarget = repository.fetchFor(anm.anmIdentifier(), indicator, parse("2012-02-28").toDate());

        assertNull(annualTarget);
    }

    @Test
    @Transactional("anm_report")
    @Rollback
    public void shouldFetchAnnualTargetIfReportDateIsStartOrEndOfRange() throws Exception {
        Indicator firstIndicator = new Indicator("INDICATOR1");
        Indicator secondIndicator = new Indicator("INDICATOR2");
        ANM anm = new ANM("ANM X");
        template.save(firstIndicator);
        template.save(secondIndicator);
        template.save(anm);
        template.save(new AnnualTarget(anm.id(), firstIndicator.id(), "40", parse("2012-03-26").toDate(), parse("2013-03-25").toDate()));
        template.save(new AnnualTarget(anm.id(), secondIndicator.id(), "80", parse("2012-03-26").toDate(), parse("2013-03-25").toDate()));

        AnnualTarget annualTargetForFirstIndicator = repository.fetchFor(anm.anmIdentifier(), firstIndicator, parse("2012-03-26").toDate());
        AnnualTarget annualTargetForSecondIndicator = repository.fetchFor(anm.anmIdentifier(), secondIndicator, parse("2013-03-25").toDate());

        assertEquals("40", annualTargetForFirstIndicator.target());
        assertEquals("80", annualTargetForSecondIndicator.target());
    }
}
