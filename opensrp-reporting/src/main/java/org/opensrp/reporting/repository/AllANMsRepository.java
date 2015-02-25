package org.opensrp.reporting.repository;

import org.opensrp.reporting.domain.ANM;
import org.opensrp.reporting.repository.cache.ANMCacheableRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.List;

import static org.opensrp.reporting.domain.ANM.FIND_BY_ANM_ID;

@Repository
public class AllANMsRepository implements ANMCacheableRepository {
    private DataAccessTemplate dataAccessTemplate;

    protected AllANMsRepository() {
    }

    @Autowired
    public AllANMsRepository(@Qualifier("anmReportsDataAccessTemplate") DataAccessTemplate dataAccessTemplate) {
        this.dataAccessTemplate = dataAccessTemplate;
    }

    @Override
    public ANM fetch(ANM object) {
        return (ANM) dataAccessTemplate.getUniqueResult(FIND_BY_ANM_ID,
                new String[]{"anmIdentifier"}, new Object[]{object.anmIdentifier()});
    }

    @Override
    public List<ANM> fetchAll() {
        return dataAccessTemplate.loadAll(ANM.class);
    }
}
