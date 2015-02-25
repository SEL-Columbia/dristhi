package org.opensrp.reporting.repository;

import org.opensrp.reporting.domain.SP_ANM;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public class AllSP_ANMsRepository {
    private DataAccessTemplate dataAccessTemplate;

    protected AllSP_ANMsRepository() {
    }

    @Autowired
    public AllSP_ANMsRepository(@Qualifier("serviceProvidedDataAccessTemplate") DataAccessTemplate dataAccessTemplate) {
        this.dataAccessTemplate = dataAccessTemplate;
    }

    @Transactional("service_provided")
    public List<SP_ANM> fetchAll() {
        return dataAccessTemplate.loadAll(SP_ANM.class);
    }

    @Transactional("service_provided")
    public List<SP_ANM> fetchAllANMSInSameSC(String anmIdentifier) {
        return (List<SP_ANM>) dataAccessTemplate.findByNamedQueryAndNamedParam(SP_ANM.FIND_ANMS_IN_SAME_SC,
                new String[]{"anmIdentifier"}, new Object[]{anmIdentifier});
    }

    @Transactional("service_provided")
    public List<SP_ANM> fetchAllANMSInSamePHC(String anmIdentifier) {
        return (List<SP_ANM>) dataAccessTemplate.findByNamedQueryAndNamedParam(SP_ANM.FIND_ANMS_IN_SAME_PHC,
                new String[]{"anmIdentifier"}, new Object[]{anmIdentifier});
    }
}
