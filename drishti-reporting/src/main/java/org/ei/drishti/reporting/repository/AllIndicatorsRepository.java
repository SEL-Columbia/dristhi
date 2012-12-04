package org.ei.drishti.reporting.repository;

import org.ei.drishti.reporting.domain.Indicator;
import org.ei.drishti.reporting.repository.cache.IndicatorCacheableRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.List;

import static org.ei.drishti.reporting.domain.Indicator.FIND_BY_INDICATOR;

@Repository
public class AllIndicatorsRepository implements IndicatorCacheableRepository {
    private DataAccessTemplate dataAccessTemplate;

    protected AllIndicatorsRepository() {
    }

    @Autowired
    public AllIndicatorsRepository(@Qualifier("anmReportsDataAccessTemplate") DataAccessTemplate dataAccessTemplate) {
        this.dataAccessTemplate = dataAccessTemplate;
    }

    @Override
    public Indicator fetch(Indicator objectWhichShouldBeFilledWithMoreInformation) {
        return (Indicator) dataAccessTemplate.getUniqueResult(FIND_BY_INDICATOR, new String[] {"indicator"}, new Object[] {objectWhichShouldBeFilledWithMoreInformation.indicator()});
    }

    @Override
    public List<Indicator> fetchAll() {
        return dataAccessTemplate.loadAll(Indicator.class);
    }
}
