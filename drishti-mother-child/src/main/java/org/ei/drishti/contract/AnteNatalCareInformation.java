package org.ei.drishti.contract;

import java.util.Date;

public class AnteNatalCareInformation {
    private String caseId;
    private Date tetanus1Date;
    private Date tetanus2Date;
    private Date ironFolicAcidTablet1Date;
    private Date ironFolicAcidTablet2Date;
    private Date ironFolicAcidTablet3Date;

    public AnteNatalCareInformation(String caseId) {
        this.caseId = caseId;
    }

    public String caseId() {
        return caseId;
    }

    @Override
    public String toString() {
        return "AnteNatalCareInformation{" +
                "caseId='" + caseId + '\'' +
                ", tetanus1Date='" + tetanus1Date + '\'' +
                ", tetanus2Date='" + tetanus2Date + '\'' +
                ", ironFolicAcidTablet1Date='" + ironFolicAcidTablet1Date + '\'' +
                ", ironFolicAcidTablet2Date='" + ironFolicAcidTablet2Date + '\'' +
                ", ironFolicAcidTablet3Date='" + ironFolicAcidTablet3Date + '\'' +
                '}';
    }
}

