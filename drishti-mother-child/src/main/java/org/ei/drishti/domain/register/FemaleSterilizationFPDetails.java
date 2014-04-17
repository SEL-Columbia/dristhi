package org.ei.drishti.domain.register;

import java.util.List;

public class FemaleSterilizationFPDetails extends SterilizationFPDetails {

    public FemaleSterilizationFPDetails(String typeOfSterilization, String sterilizationDate, List<String> followupVisitDates) {
        super(typeOfSterilization, sterilizationDate, followupVisitDates);
    }
}