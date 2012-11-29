package org.ei.drishti.reporting.repository.it;

import org.ei.drishti.reporting.domain.ANM;
import org.ei.drishti.reporting.repository.cache.ANMCacheableRepository;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static java.util.Arrays.asList;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

public class AllANMsRepositoryIntegrationTest extends ServicesProvidedRepositoryIntegrationTestBase {
    @Autowired
    private @Qualifier("serviceProvidedANMRepository") ANMCacheableRepository repository;

    @Test
    @Transactional("service_provided")
    @Rollback
    public void shouldSaveANM() throws Exception {
        repository.save(new ANM("ANM X"));

        ANM anm = repository.fetch(new ANM("ANM X"));
        assertEquals("ANM X", anm.anmIdentifier());
        assertTrue("ID should be non-zero.", anm.id() != 0);
    }

    @Test
    @Transactional("service_provided")
    @Rollback
    public void shouldFetchAllANMs() throws Exception {
        ANM anmX = new ANM("ANM X");
        ANM anmY = new ANM("ANM Y");
        repository.save(anmX);
        repository.save(anmY);

        List<ANM> anms = repository.fetchAll();

        assertTrue(anms.containsAll(asList(anmX, anmY)));
    }
}
