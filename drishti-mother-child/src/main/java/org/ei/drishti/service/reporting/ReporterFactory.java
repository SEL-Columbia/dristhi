package org.ei.drishti.service.reporting;

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

    public IReporter reporterFor(String bindType) {
        if(bindType.equalsIgnoreCase("child"))
            return childReporter;
        if(bindType.equalsIgnoreCase("mother"))
            return motherReporter;
        if(bindType.equalsIgnoreCase("eligible_couple"))
            return eligibleCoupleReporter;
        return null;
    }
}

