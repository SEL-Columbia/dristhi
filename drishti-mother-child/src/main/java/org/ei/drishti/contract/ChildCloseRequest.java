package org.ei.drishti.contract;

import org.apache.commons.lang.builder.ToStringBuilder;

public class ChildCloseRequest {
    String caseId;
    String anmIdentifier;

    public ChildCloseRequest(String caseId, String anmIdentifier) {
        this.caseId = caseId;
        this.anmIdentifier = anmIdentifier;
    }

    public String anmIdentifier() {
        return anmIdentifier;
    }

    public String caseId() {
        return caseId;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
