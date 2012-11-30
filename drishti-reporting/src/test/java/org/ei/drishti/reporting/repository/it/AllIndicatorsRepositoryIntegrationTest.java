package org.ei.drishti.reporting.repository.it;

import org.ei.drishti.reporting.domain.Indicator;
import org.ei.drishti.reporting.repository.cache.IndicatorCacheableRepository;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.List;

import static java.util.Arrays.asList;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

public class AllIndicatorsRepositoryIntegrationTest extends ServicesProvidedRepositoryIntegrationTestBase {
    @Autowired
    private @Qualifier("serviceProvidedIndicatorRepository") IndicatorCacheableRepository repository;

    @Test
    public void shouldSaveAndFetchIndicator() throws Exception {
        Indicator indicator = new Indicator("ANC");
        repository.save(indicator);

        Indicator fetchedIndicator = repository.fetch(indicator);
        assertEquals("ANC", fetchedIndicator.indicator());
        assertTrue("ID should be non-zero.", fetchedIndicator.id() != 0);
    }

    @Test
    public void shouldFetchAllIndicators() throws Exception {
        Indicator indicator1 = new Indicator("ANC");
        Indicator indicator2 = new Indicator("IUD");
        repository.save(indicator1);
        repository.save(indicator2);

        List<Indicator> indicators = repository.fetchAll();

        assertTrue(indicators.containsAll(asList(indicator1, indicator2)));
    }
}
