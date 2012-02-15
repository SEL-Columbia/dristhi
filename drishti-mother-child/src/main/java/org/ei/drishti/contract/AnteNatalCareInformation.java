package org.ei.drishti.contract;

public class AnteNatalCareInformation {
    private String caseId;
    private String tetanus1Date;
    private String tetanus2Date;
    private String ironFolicAcidTablet1Date;
    private String ironFolicAcidTablet2Date;
    private String ironFolicAcidTablet3Date;

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

