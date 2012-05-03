package org.ei.drishti.contract;

import org.apache.commons.lang.builder.ToStringBuilder;

public class EligibleCoupleCloseRequest {
    private String caseId;

    public EligibleCoupleCloseRequest(String caseId) {
        this.caseId = caseId;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    public String caseId() {
        return caseId;
    }
}
