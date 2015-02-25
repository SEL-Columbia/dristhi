package org.opensrp.reporting.repository.it;

import org.junit.Test;
import org.opensrp.reporting.domain.PHC;
import org.opensrp.reporting.domain.SP_ANM;
import org.opensrp.reporting.domain.ServiceProvider;
import org.opensrp.reporting.domain.ServiceProviderType;
import org.opensrp.reporting.repository.AllServiceProvidersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static ch.lambdaj.Lambda.having;
import static ch.lambdaj.Lambda.on;
import static ch.lambdaj.Lambda.selectUnique;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.hamcrest.Matchers.equalTo;
import static org.opensrp.reporting.domain.ServiceProviderType.ANM;
import static org.opensrp.reporting.domain.ServiceProviderType.PHC;

public class AllServiceProvidersRepositoryIntegrationTest extends ServicesProvidedIntegrationTestBase {
    @Autowired
    private AllServiceProvidersRepository repository;

    @Test
    @Transactional("service_provided")
    @Rollback
    public void shouldFetchServiceProviderThatIsPHC() throws Exception {
        PHC phc = new PHC("bhe", "Bherya");
        template.save(phc);
        List<ServiceProviderType> serviceProviderTypes = template.loadAll(ServiceProviderType.class);
        ServiceProviderType phcServiceProvider = selectUnique(serviceProviderTypes, having(on(ServiceProviderType.class).type(), equalTo(PHC.type())));
        ServiceProvider bheryaPHC = new ServiceProvider(phc.id(), phcServiceProvider);
        template.save(bheryaPHC);

        ServiceProvider serviceProvider = repository.fetchBy(phc.phcIdentifier(), PHC);

        assertEquals(phc.id(), serviceProvider.serviceProviderId());
        assertTrue("ID should be non-zero.", serviceProvider.id() != 0);
    }

    @Test
    @Transactional("service_provided")
    @Rollback
    public void shouldFetchServiceProviderThatIsANM() throws Exception {
        PHC phc = new PHC("bhe", "Bherya");
        template.save(phc);
        SP_ANM anm = new SP_ANM("bhe1anm", "bhe1anm name", "Sub Center 1", phc.id());
        template.save(anm);
        List<ServiceProviderType> serviceProviderTypes = template.loadAll(ServiceProviderType.class);
        ServiceProviderType anmServiceProvider = selectUnique(serviceProviderTypes, having(on(ServiceProviderType.class).type(), equalTo(ANM.type())));
        ServiceProvider bheryaANM = new ServiceProvider(anm.id(), anmServiceProvider);
        template.save(bheryaANM);

        ServiceProvider serviceProvider = repository.fetchBy(anm.identifier(), ANM);

        assertEquals(anm.id(), serviceProvider.serviceProviderId());
        assertTrue("ID should be non-zero.", serviceProvider.id() != 0);
    }
}
