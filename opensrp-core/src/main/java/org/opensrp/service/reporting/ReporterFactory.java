package org.opensrp.service.reporting;

import static org.opensrp.common.AllConstants.FormEntityTypes.CHILD_TYPE;
import static org.opensrp.common.AllConstants.FormEntityTypes.ELIGIBLE_COUPLE_TYPE;
import static org.opensrp.common.AllConstants.FormEntityTypes.MOTHER_TYPE;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ReporterFactory implements IReporterFactory {

    private ChildReporter childReporter;
    private EligibleCoupleReporter eligibleCoupleReporter;
    private MotherReporter motherReporter;

    @Autowired
    public ReporterFactory(EligibleCoupleReporter eligibleCoupleReporter, MotherReporter motherReporter, ChildReporter childReporter) {
        this.childReporter = childReporter;
        this.eligibleCoupleReporter = eligibleCoupleReporter;
        this.motherReporter = motherReporter;
    }

    public IReporter reporterFor(String entityType) {
        if (entityType.equalsIgnoreCase(CHILD_TYPE))
            return childReporter;
        if (entityType.equalsIgnoreCase(MOTHER_TYPE))
            return motherReporter;
        if (entityType.equalsIgnoreCase(ELIGIBLE_COUPLE_TYPE))
            return eligibleCoupleReporter;
        return null;
    }
}

