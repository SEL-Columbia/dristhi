package org.ei.drishti.reporting.repository.it;

import org.ei.drishti.reporting.domain.Token;
import org.ei.drishti.reporting.repository.AllTokensRepository;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.assertEquals;

public class AllTokenRepositoryIntegrationTest extends ServicesProvidedIntegrationTestBase {
    @Autowired
    private AllTokensRepository repository;

    @Test
    @Transactional("service_provided")
    @Rollback
    public void shouldFetchToken() throws Exception {
        template.save(new Token("aggregate-reports-token", ((Integer) 1).toString()));
        template.save(new Token("another-token", ((Integer) 2).toString()));

        assertEquals((Integer) 1, repository.getAggregateReportsToken());
    }

    @Test
    @Transactional("service_provided")
    @Rollback
    public void shouldDefaultTokenToZeroWhenItDoesNotExist() throws Exception {
        assertEquals((Integer) 0, repository.getAggregateReportsToken());
    }
}
