package org.opensrp.reporting.repository.it;

import org.junit.Test;
import org.opensrp.reporting.domain.PHC;
import org.opensrp.reporting.domain.SP_ANM;
import org.opensrp.reporting.repository.AllSP_ANMsRepository;
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
        SP_ANM anm1 = new SP_ANM("ANM 1", "anm1 name", "Sub Center 1", phc1.id());
        SP_ANM anm2 = new SP_ANM("ANM 2", "anm2 name", "Sub Center 1", phc2.id());
        template.save(anm1);
        template.save(anm2);

        List<SP_ANM> anmList = repository.fetchAll();

        assertTrue(anmList.containsAll(asList(anm1, anm2)));
    }

    @Test
    @Transactional("service_provided")
    @Rollback
    public void shouldFetchAllANMsInTheSameSC() throws Exception {
        PHC phc1 = new PHC("phc1", "PHC 1");
        PHC phc2 = new PHC("phc2", "PHC 2");
        template.save(phc1);
        template.save(phc2);
        SP_ANM anm1 = new SP_ANM("ANM 1", "anm1 name", "Sub Center 1", phc1.id());
        SP_ANM anm2 = new SP_ANM("ANM 2", "anm2 name", "Sub Center 1", phc1.id());
        SP_ANM anm3 = new SP_ANM("ANM 3", "anm3 name", "Sub Center 2", phc1.id());
        template.save(anm1);
        template.save(anm2);
        template.save(anm3);

        List<SP_ANM> anmList = repository.fetchAllANMSInSameSC("ANM 1");

        assertTrue(anmList.containsAll(asList(anm1, anm2)));
    }

    @Test
    @Transactional("service_provided")
    @Rollback
    public void shouldFetchAllANMsInTheSamePHC() throws Exception {
        PHC phc1 = new PHC("phc1", "PHC 1");
        PHC phc2 = new PHC("phc2", "PHC 2");
        template.save(phc1);
        template.save(phc2);
        SP_ANM anm1 = new SP_ANM("ANM 1", "anm1 name", "Sub Center 1", phc1.id());
        SP_ANM anm2 = new SP_ANM("ANM 2", "anm2 name", "Sub Center 1", phc1.id());
        SP_ANM anm3 = new SP_ANM("ANM 3", "anm3 name", "Sub Center 2", phc2.id());
        template.save(anm1);
        template.save(anm2);
        template.save(anm3);

        List<SP_ANM> anmList = repository.fetchAllANMSInSamePHC("ANM 1");

        assertTrue(anmList.containsAll(asList(anm1, anm2)));
    }
}
