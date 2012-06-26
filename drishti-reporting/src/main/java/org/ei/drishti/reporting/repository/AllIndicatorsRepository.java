package org.ei.drishti.reporting.repository;

import org.ei.drishti.reporting.domain.Indicator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import static org.ei.drishti.reporting.domain.Indicator.FIND_BY_INDICATOR;

@Repository
@Transactional
public class AllIndicatorsRepository {
    private DataAccessTemplate dataAccessTemplate;

    public AllIndicatorsRepository() {
    }

    @Autowired
    public AllIndicatorsRepository(DataAccessTemplate dataAccessTemplate) {
        this.dataAccessTemplate = dataAccessTemplate;
    }

    public void save(String indicator){
        dataAccessTemplate.save(new Indicator(indicator));
    }

    public Indicator fetch(String indicator) {
        return (Indicator) dataAccessTemplate.getUniqueResult(FIND_BY_INDICATOR, new String[] {"indicator"}, new Object[] {indicator});
    }
}
