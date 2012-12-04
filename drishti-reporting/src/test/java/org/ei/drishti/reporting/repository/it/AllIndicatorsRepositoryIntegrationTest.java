package org.ei.drishti.reporting.repository.it;

import org.ei.drishti.reporting.domain.Indicator;
import org.ei.drishti.reporting.repository.AllIndicatorsRepository;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static java.util.Arrays.asList;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

public class AllIndicatorsRepositoryIntegrationTest extends ServicesProvidedRepositoryIntegrationTestBase {
    @Autowired
    @Qualifier("serviceProvidedIndicatorRepository")
    private AllIndicatorsRepository repository;

    @Test
    @Transactional("service_provided")
    @Rollback
    public void shouldSaveAndFetchIndicator() throws Exception {
        Indicator indicator = new Indicator("ANC");
        template.save(indicator);

        Indicator fetchedIndicator = repository.fetch(indicator);
        assertEquals("ANC", fetchedIndicator.indicator());
        assertTrue("ID should be non-zero.", fetchedIndicator.id() != 0);
    }

    @Test
    @Transactional("service_provided")
    @Rollback
    public void shouldFetchAllIndicators() throws Exception {
        Indicator indicator1 = new Indicator("ANC");
        Indicator indicator2 = new Indicator("IUD");
        template.save(indicator1);
        template.save(indicator2);

        List<Indicator> indicators = repository.fetchAll();

        assertTrue(indicators.containsAll(asList(indicator1, indicator2)));
        assertTrue("ID should be non-zero.", indicators.get(0).id() != 0);
    }
}
