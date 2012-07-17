package org.ei.drishti.contract;

import org.apache.commons.lang.builder.ToStringBuilder;

public class EligibleCoupleRegistrationRequest {
    private String ecNumber;
    private String wifeName;
    private String husbandName;
    private String caseId;
    private String anmIdentifier;
    private String village;
    private String subCenter;
    private String phc;
    private String currentMethod;

    public EligibleCoupleRegistrationRequest(String caseId, String ecNumber, String wifeName, String husbandName, String anmIdentifier, String village, String subCenter, String phc, String currentMethod) {
        this.caseId = caseId;
        this.ecNumber = ecNumber;
        this.wifeName = wifeName;
        this.husbandName = husbandName;
        this.anmIdentifier = anmIdentifier;
        this.village = village;
        this.subCenter = subCenter;
        this.phc = phc;
        this.currentMethod = currentMethod;
    }

    public String caseId() {
        return caseId;
    }

    public String ecNumber() {
        return ecNumber;
    }

    public String wife() {
        return wifeName;
    }

    public String husband() {
        return husbandName;
    }

    public String anmIdentifier() {
        return anmIdentifier;
    }

    public String village() {
        return village;
    }

    public String subCenter() {
        return subCenter;
    }

    public String phc() {
        return phc;
    }

    public String currentMethod() {
        return currentMethod;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
