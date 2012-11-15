package org.ei.drishti.reporting.repository.it;

import org.ei.drishti.reporting.domain.Dates;
import org.ei.drishti.reporting.repository.cache.DatesCacheableRepository;
import org.joda.time.LocalDate;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.Date;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

public class AllDatesRepositoryIntegrationTest extends ServicesProvidedRepositoryIntegrationTestBase {
    @Autowired
    private @Qualifier("serviceProvidedDatesRepository") DatesCacheableRepository repository;

    @Test
    public void shouldSaveAndFetchDate() throws Exception {
        Date date = LocalDate.now().toDate();
        Dates dates = new Dates(date);
        repository.save(dates);

        Dates fetchedDate = repository.fetch(dates);
        assertEquals(date, fetchedDate.date());
        assertTrue("ID should be non-zero.", fetchedDate.id() != 0);
    }
}
