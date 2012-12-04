package org.ei.drishti.reporting.repository.it;

import org.ei.drishti.reporting.domain.PHC;
import org.ei.drishti.reporting.domain.SP_ANM;
import org.ei.drishti.reporting.domain.ServiceProvider;
import org.ei.drishti.reporting.domain.ServiceProviderType;
import org.ei.drishti.reporting.repository.AllServiceProvidersRepository;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.ei.drishti.reporting.domain.ServiceProviderType.ANM;

public class AllServiceProvidersRepositoryIntegrationTest extends ServicesProvidedRepositoryIntegrationTestBase {
    @Autowired
    private AllServiceProvidersRepository repository;

    @Test
    @Transactional("service_provided")
    @Rollback
    public void shouldFetchServiceProviderThatIsPHC() throws Exception {
        PHC phc = new PHC("bhe", "Bherya");
        template.save(phc);
        ServiceProvider bheryaPHC = new ServiceProvider(phc.id(), ServiceProviderType.PHC);
        template.save(bheryaPHC);

        ServiceProvider serviceProvider = repository.fetchBy(phc.phcIdentifier(), ServiceProviderType.PHC);

        assertEquals(phc.id(), serviceProvider.serviceProviderId());
        assertTrue("ID should be non-zero.", serviceProvider.id() != 0);
    }

    @Test
    @Transactional("service_provided")
    @Rollback
    public void shouldFetchServiceProviderThatIsANM() throws Exception {
        PHC phc = new PHC("bhe", "Bherya");
        template.save(phc);
        SP_ANM anm = new SP_ANM("bhe1", phc.id());
        template.save(anm);
        ServiceProvider bheryaANM = new ServiceProvider(anm.id(), ANM);
        template.save(bheryaANM);

        ServiceProvider serviceProvider = repository.fetchBy(anm.anmIdentifier(), ANM);

        assertEquals(anm.id(), serviceProvider.serviceProviderId());
        assertTrue("ID should be non-zero.", serviceProvider.id() != 0);
    }
}
