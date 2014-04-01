package org.ei.drishti.domain.register;

import java.util.List;

public class SterilizationFPDetails {
    private String typeOfSterilization;
    private String sterilizationDate;
    private List<String> followupVisitDates;
    private String remarks;

    public SterilizationFPDetails(String typeOfSterilization, String sterilizationDate, List<String> followupVisitDates,
                                  String remarks) {
        this.typeOfSterilization = typeOfSterilization;
        this.sterilizationDate = sterilizationDate;
        this.followupVisitDates = followupVisitDates;
        this.remarks = remarks;
    }

    public String typeOfSterilization() {
        return typeOfSterilization;
    }

    public String sterilizationDate() {
        return sterilizationDate;
    }

    public List<String> followupVisitDates() {
        return followupVisitDates;
    }

    public String remarks() {
        return remarks;
    }
}
