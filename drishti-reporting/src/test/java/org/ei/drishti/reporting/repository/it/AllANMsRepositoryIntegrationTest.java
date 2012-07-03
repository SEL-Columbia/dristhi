package org.ei.drishti.reporting.repository.it;

import org.ei.drishti.reporting.domain.ANM;
import org.ei.drishti.reporting.repository.cache.ANMCacheableRepository;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

public class AllANMsRepositoryIntegrationTest extends RepositoryIntegrationTestBase {
    @Autowired
    private ANMCacheableRepository repository;

    @Test
    public void shouldSaveANM() throws Exception {
        repository.save(new ANM("ANM X"));

        ANM anm = repository.fetch(new ANM("ANM X"));
        assertEquals("ANM X", anm.anmIdentifier());
        assertTrue("ID should be non-zero.", anm.id() != 0);
    }
}
