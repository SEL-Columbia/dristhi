package org.ei.drishti.reporting.repository;

import org.ei.drishti.reporting.domain.AnnualTarget;
import org.ei.drishti.reporting.domain.Indicator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import static org.ei.drishti.reporting.domain.AnnualTarget.FIND_BY_ANM_AND_INDICATOR;

@Repository
public class AllAnnualTargetsRepository {
    private DataAccessTemplate dataAccessTemplate;

    protected AllAnnualTargetsRepository() {
    }

    @Autowired
    public AllAnnualTargetsRepository(@Qualifier("anmReportsDataAccessTemplate") DataAccessTemplate dataAccessTemplate) {
        this.dataAccessTemplate = dataAccessTemplate;
    }

    public void save(AnnualTarget annualTarget) {
        dataAccessTemplate.save(annualTarget);
    }

    public AnnualTarget fetchFor(String anmIdentifier, Indicator indicator) {
        return (AnnualTarget) dataAccessTemplate.getUniqueResult(FIND_BY_ANM_AND_INDICATOR, new String[]{"anmIdentifier", "indicator"}, new Object[]{anmIdentifier, indicator.indicator()});
    }
}
