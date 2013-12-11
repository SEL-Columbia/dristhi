package org.ei.drishti.reporting.repository.it;

import org.ei.drishti.reporting.domain.PHC;
import org.ei.drishti.reporting.domain.SP_ANM;
import org.ei.drishti.reporting.repository.AllSP_ANMsRepository;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static java.util.Arrays.asList;
import static junit.framework.Assert.assertTrue;

public class AllSP_ANMsRepositoryIntegrationTest extends ServicesProvidedIntegrationTestBase {
    @Autowired
    private AllSP_ANMsRepository repository;

    @Test
    @Transactional("service_provided")
    @Rollback
    public void shouldFetchAllANMs() throws Exception {
        PHC phc1 = new PHC("phc1", "PHC 1");
        PHC phc2 = new PHC("phc2", "PHC 2");
        template.save(phc1);
        template.save(phc2);
        SP_ANM anm1 = new SP_ANM("ANM 1", phc1.id());
        SP_ANM anm2 = new SP_ANM("ANM 2", phc2.id());
        template.save(anm1);
        template.save(anm2);

        List<SP_ANM> anmList = repository.fetchAll();

        assertTrue(anmList.containsAll(asList(anm1, anm2)));
    }
}
