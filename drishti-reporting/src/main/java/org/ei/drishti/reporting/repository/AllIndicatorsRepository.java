package org.ei.drishti.reporting.repository;

import org.ei.drishti.reporting.domain.Indicator;
import org.ei.drishti.reporting.repository.cache.IndicatorCacheableRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import static org.ei.drishti.reporting.domain.Indicator.FIND_BY_INDICATOR;

@Repository
@Transactional
public class AllIndicatorsRepository implements IndicatorCacheableRepository {
    private DataAccessTemplate dataAccessTemplate;

    public AllIndicatorsRepository() {
    }

    @Autowired
    public AllIndicatorsRepository(DataAccessTemplate dataAccessTemplate) {
        this.dataAccessTemplate = dataAccessTemplate;
    }

    @Override
    public void save(Indicator objectToBeSaved) {
        dataAccessTemplate.save(objectToBeSaved);
    }

    @Override
    public Indicator fetch(Indicator objectWhichShouldBeFilledWithMoreInformation) {
        return (Indicator) dataAccessTemplate.getUniqueResult(FIND_BY_INDICATOR, new String[] {"indicator"}, new Object[] {objectWhichShouldBeFilledWithMoreInformation.indicator()});
    }
}
