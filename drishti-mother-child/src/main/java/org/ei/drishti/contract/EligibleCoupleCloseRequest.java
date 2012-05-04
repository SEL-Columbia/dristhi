package org.ei.drishti.contract;

import org.apache.commons.lang.builder.ToStringBuilder;

public class EligibleCoupleCloseRequest {
    private String caseId;
    private String anmIdentifier;

    public EligibleCoupleCloseRequest(String caseId, String anmIdentifier) {
        this.caseId = caseId;
        this.anmIdentifier = anmIdentifier;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    public String caseId() {
        return caseId;
    }

    public String anmIdentifier() {
        return anmIdentifier;
    }
}
