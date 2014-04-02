package org.ei.drishti.domain.register;

import java.util.List;

public class FemaleSterilizationFPDetails extends SterilizationFPDetails {

    public FemaleSterilizationFPDetails(String typeOfSterilization, String sterilizationDate, List<String> followupVisitDates, String remarks) {
        super(typeOfSterilization, sterilizationDate, followupVisitDates, remarks);
    }
}