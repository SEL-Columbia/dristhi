package org.ei.drishti.reporting.repository;

import org.ei.drishti.reporting.domain.ANM;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class AllANMsRepository {
    private DataAccessTemplate template;

    public AllANMsRepository() {
    }

    @Autowired
    public AllANMsRepository(DataAccessTemplate template) {
        this.template = template;
    }

    public void save(String anmIdentifier){
        template.save(new ANM(anmIdentifier));
    }

    public ANM fetch(String anmIdentifier) {
        return (ANM) template.getUniqueResult(ANM.FIND_BY_ANM_ID, new String[] {"anmIdentifier"}, new Object[] {anmIdentifier});
    }
}
