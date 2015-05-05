package org.opensrp.register.service.reporting;

import org.opensrp.service.reporting.IReporter;
import org.opensrp.service.reporting.IReporterFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.opensrp.common.AllConstants.FormEntityTypes.*;

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

