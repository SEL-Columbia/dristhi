package org.ei.drishti.contract;

import org.apache.commons.lang.builder.ToStringBuilder;

public class EligibleCoupleRegistrationRequest {
    private String ecNumber;
    private String wifeName;
    private String husbandName;
    private String caseId;
    private String anmIdentifier;

    public EligibleCoupleRegistrationRequest(String caseId, String ecNumber, String wifeName, String husbandName, String anmIdentifier) {
        this.caseId = caseId;
        this.ecNumber = ecNumber;
        this.wifeName = wifeName;
        this.husbandName = husbandName;
        this.anmIdentifier = anmIdentifier;
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

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
