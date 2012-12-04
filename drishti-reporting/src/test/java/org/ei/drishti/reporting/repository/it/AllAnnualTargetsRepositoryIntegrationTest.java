package org.ei.drishti.reporting.repository.it;

import org.ei.drishti.reporting.domain.ANM;
import org.ei.drishti.reporting.domain.AnnualTarget;
import org.ei.drishti.reporting.domain.Indicator;
import org.ei.drishti.reporting.repository.AllAnnualTargetsRepository;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import static junit.framework.Assert.assertEquals;

public class AllAnnualTargetsRepositoryIntegrationTest extends ANMReportsRepositoryIntegrationTestBase {
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
        template.save(new AnnualTarget(anm.id(), indicator.id(), "40"));

        AnnualTarget annualTarget = repository.fetchFor(anm.anmIdentifier(), indicator);

        assertEquals("40", annualTarget.target());
    }
}
