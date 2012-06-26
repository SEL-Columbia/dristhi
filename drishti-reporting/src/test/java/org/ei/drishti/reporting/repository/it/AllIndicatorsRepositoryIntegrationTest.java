package org.ei.drishti.reporting.repository.it;

import org.ei.drishti.reporting.domain.Indicator;
import org.ei.drishti.reporting.repository.TestDataAccessTemplate;
import org.ei.drishti.reporting.repository.cache.IndicatorCacheableRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:test-applicationContext-drishti-reporting.xml")
public class AllIndicatorsRepositoryIntegrationTest {
    @Autowired
    @Qualifier("testDataAccessTemplate")
    private TestDataAccessTemplate template;

    @Autowired
    private IndicatorCacheableRepository repository;

    @Before
    public void setUp() throws Exception {
        template.deleteAll(template.loadAll(Indicator.class));
    }

    @Test
    public void shouldSaveAndFetchIndicator() throws Exception {
        Indicator indicator = new Indicator("ANC");
        repository.save(indicator);

        Indicator fetchedIndicator = repository.fetch(indicator);
        assertEquals("ANC", fetchedIndicator.indicator());
        assertTrue("ID should be non-zero.", fetchedIndicator.id() != 0);
    }
}
