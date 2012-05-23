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

    public EligibleCoupleRegistrationRequest(String caseId, String ecNumber, String wifeName, String husbandName, String anmIdentifier, String village, String subCenter) {
        this.caseId = caseId;
        this.ecNumber = ecNumber;
        this.wifeName = wifeName;
        this.husbandName = husbandName;
        this.anmIdentifier = anmIdentifier;
        this.village = village;
        this.subCenter = subCenter;
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

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
