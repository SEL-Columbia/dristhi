package org.ei.drishti.reporting.repository.it;

import org.ei.drishti.reporting.domain.AnnualTarget;
import org.ei.drishti.reporting.domain.ANM;
import org.ei.drishti.reporting.domain.Indicator;
import org.ei.drishti.reporting.repository.AllAnnualTargetsRepository;
import org.ei.drishti.reporting.repository.cache.ANMCacheableRepository;
import org.ei.drishti.reporting.repository.cache.IndicatorCacheableRepository;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import static junit.framework.Assert.assertEquals;

public class AllAnnualTargetsRepositoryIntegrationTest extends ANMReportsRepositoryIntegrationTestBase {
    @Autowired
    @Qualifier("anmReportsIndicatorRepository")
    private IndicatorCacheableRepository indicatorsRepository;

    @Autowired
    @Qualifier("anmReportsANMRepository")
    private ANMCacheableRepository anmsRepository;

    @Autowired
    private AllAnnualTargetsRepository repository;

    @Test
    public void shouldFetchAnnualTargetForGivenANMAndIndicator() throws Exception {
        Indicator indicator = new Indicator("IUD");
        ANM anm = new ANM("ANM X");
        indicatorsRepository.save(indicator);
        anmsRepository.save(anm);
        repository.save(new AnnualTarget(anm.id(), indicator.id(), "40"));

        AnnualTarget annualTarget = repository.fetchFor(anm.anmIdentifier(), indicator);

        assertEquals("40", annualTarget.target());
    }
}
