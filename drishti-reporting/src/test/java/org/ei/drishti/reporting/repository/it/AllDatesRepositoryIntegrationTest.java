package org.ei.drishti.reporting.repository.it;

import org.ei.drishti.reporting.domain.Dates;
import org.ei.drishti.reporting.repository.cache.DatesCacheableRepository;
import org.joda.time.LocalDate;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

import static java.util.Arrays.asList;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

public class AllDatesRepositoryIntegrationTest extends ServicesProvidedRepositoryIntegrationTestBase {
    @Autowired
    private @Qualifier("serviceProvidedDatesRepository") DatesCacheableRepository repository;

    @Test
    @Transactional("service_provided")
    @Rollback
    public void shouldSaveAndFetchDate() throws Exception {
        Date date = LocalDate.now().toDate();
        Dates dates = new Dates(date);
        repository.save(dates);

        Dates fetchedDate = repository.fetch(dates);
        assertEquals(date, fetchedDate.date());
        assertTrue("ID should be non-zero.", fetchedDate.id() != 0);
    }

    @Test
    @Transactional("service_provided")
    @Rollback
    public void shouldFetchAllDates() throws Exception {
        Date date1 = LocalDate.parse("2012-01-02").toDate();
        Date date2 = LocalDate.parse("2012-01-01").toDate();
        Dates firstDates = new Dates(date1);
        Dates secondDates = new Dates(date2);
        repository.save(firstDates);
        repository.save(secondDates);

        List<Dates> datesList = repository.fetchAll();

        assertTrue(datesList.containsAll(asList(firstDates, secondDates)));
    }
}
