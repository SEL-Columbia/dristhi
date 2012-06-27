package org.ei.drishti.reporting.repository.it;

import org.ei.drishti.reporting.domain.Indicator;
import org.ei.drishti.reporting.repository.cache.IndicatorCacheableRepository;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

public class AllIndicatorsRepositoryIntegrationTest extends RepositoryIntegrationTestBase{
    @Autowired
    private IndicatorCacheableRepository repository;

    @Test
    public void shouldSaveAndFetchIndicator() throws Exception {
        Indicator indicator = new Indicator("ANC");
        repository.save(indicator);

        Indicator fetchedIndicator = repository.fetch(indicator);
        assertEquals("ANC", fetchedIndicator.indicator());
        assertTrue("ID should be non-zero.", fetchedIndicator.id() != 0);
    }
}
