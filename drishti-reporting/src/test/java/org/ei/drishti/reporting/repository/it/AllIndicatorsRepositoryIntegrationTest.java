package org.ei.drishti.reporting.repository.it;

import org.ei.drishti.reporting.domain.Indicator;
import org.ei.drishti.reporting.repository.AllIndicatorsRepository;
import org.ei.drishti.reporting.repository.TestDataAccessTemplate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static junit.framework.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:test-applicationContext-drishti-reporting.xml")
public class AllIndicatorsRepositoryIntegrationTest {
    @Autowired
    @Qualifier("testDataAccessTemplate")
    protected TestDataAccessTemplate template;

    @Autowired
    private AllIndicatorsRepository repository;

    @Before
    public void setUp() throws Exception {
        template.deleteAll(template.loadAll(Indicator.class));
    }

    @Test
    public void shouldSaveAndFetchIndicator() throws Exception {
        repository.save("ANC");

        assertEquals("ANC", repository.fetch("ANC").indicator());
    }
}
