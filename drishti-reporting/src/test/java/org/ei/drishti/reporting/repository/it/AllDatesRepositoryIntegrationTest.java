package org.ei.drishti.reporting.repository.it;

import org.ei.drishti.reporting.domain.Dates;
import org.ei.drishti.reporting.repository.AllDatesRepository;
import org.ei.drishti.reporting.repository.TestDataAccessTemplate;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:test-applicationContext-drishti-reporting.xml")
public class AllDatesRepositoryIntegrationTest {
    @Autowired
    @Qualifier("testDataAccessTemplate")
    private TestDataAccessTemplate template;

    @Autowired
    private AllDatesRepository repository;

    @Before
    public void setUp() throws Exception {
        template.deleteAll(template.loadAll(Dates.class));
    }

    @Test
    public void shouldSaveAndFetchDate() throws Exception {
        Date date = LocalDate.now().toDate();
        repository.save(date);

        Dates fetchedDate = repository.fetch(date);
        assertEquals(date, fetchedDate.date());
        assertTrue("ID should be non-zero.", fetchedDate.id() != 0);
    }
}
