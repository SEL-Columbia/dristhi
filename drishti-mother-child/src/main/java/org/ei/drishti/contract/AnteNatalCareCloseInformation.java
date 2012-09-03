package org.ei.drishti.contract;

import org.apache.commons.lang.builder.ToStringBuilder;

public class AnteNatalCareCloseInformation {
    private String caseId;
    private String closeReason;
    private String anmIdentifier;

    public AnteNatalCareCloseInformation(String caseId, String anmIdentifier, String closeReason) {
        this.caseId = caseId;
        this.closeReason = closeReason;
        this.anmIdentifier = anmIdentifier;
    }

    public String caseId() {
        return caseId;
    }

    public String reason() {
        return closeReason;
    }

    public String anmIdentifier() {
        return anmIdentifier;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
