package org.ei.drishti.reporting.repository;

import org.ei.drishti.reporting.domain.ANM;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import static org.ei.drishti.reporting.domain.ANM.FIND_BY_ANM_ID;

@Repository
@Transactional
public class AllANMsRepository {
    private DataAccessTemplate dataAccessTemplate;

    public AllANMsRepository() {
    }

    @Autowired
    public AllANMsRepository(DataAccessTemplate dataAccessTemplate) {
        this.dataAccessTemplate = dataAccessTemplate;
    }

    public void save(String anmIdentifier) {
        dataAccessTemplate.save(new ANM(anmIdentifier));
    }

    public ANM fetch(String anmIdentifier) {
        return (ANM) dataAccessTemplate.getUniqueResult(FIND_BY_ANM_ID, new String[]{"anmIdentifier"}, new Object[]{anmIdentifier});
    }
}
