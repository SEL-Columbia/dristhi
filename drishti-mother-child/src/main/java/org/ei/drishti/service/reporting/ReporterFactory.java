package org.ei.drishti.service.reporting;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.ei.drishti.common.AllConstants.FormEntityTypes.*;

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

    public IReporter reporterFor(String bindType) {
        if(bindType.equalsIgnoreCase(CHILD_TYPE))
            return childReporter;
        if(bindType.equalsIgnoreCase(MOTHER_TYPE))
            return motherReporter;
        if(bindType.equalsIgnoreCase(ELIGIBLE_COUPLE_TYPE))
            return eligibleCoupleReporter;
        return null;
    }
}

