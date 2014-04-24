package org.ei.drishti.domain.register;

import java.util.List;

public class MaleSterilizationFPDetails extends SterilizationFPDetails {

    private MaleSterilizationFPDetails() {
    }

    public MaleSterilizationFPDetails(String typeOfSterilization, String sterilizationDate, List<String> followupVisitDates) {
        super(typeOfSterilization, sterilizationDate, followupVisitDates);
    }

    public MaleSterilizationFPDetails(String typeOfSterilization, String sterilizationDate) {
        super(typeOfSterilization, sterilizationDate);
    }
}
