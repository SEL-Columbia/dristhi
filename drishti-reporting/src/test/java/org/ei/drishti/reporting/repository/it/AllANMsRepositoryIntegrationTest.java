package org.ei.drishti.reporting.repository.it;

import org.ei.drishti.reporting.domain.ANM;
import org.ei.drishti.reporting.repository.AllANMsRepository;
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
public class AllANMsRepositoryIntegrationTest {
    @Autowired
    @Qualifier("testDataAccessTemplate")
    protected TestDataAccessTemplate template;

    @Autowired
    private AllANMsRepository repository;

    @Before
    public void setUp() throws Exception {
        template.deleteAll(template.loadAll(ANM.class));
    }

    @Test
    public void shouldSaveANM() throws Exception {
        repository.save("ANM X");

        assertEquals("ANM X", repository.fetch("ANM X").anmIdentifier());
    }
}
