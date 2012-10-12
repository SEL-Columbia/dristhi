package org.ei.drishti.contract;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

public class PostNatalCareInformation {
    private String caseId;
    private String anmIdentifier;
    private String pncVisitNumber;
    private String numberOfIFATabletsGiven;
    private String submissionDate;

    public PostNatalCareInformation(String caseId, String anmIdentifier, String pncVisitNumber, String numberOfIFATabletsGiven, String submissionDate) {
        this.caseId = caseId;
        this.anmIdentifier = anmIdentifier;
        this.pncVisitNumber = pncVisitNumber;
        this.numberOfIFATabletsGiven = numberOfIFATabletsGiven;
        this.submissionDate = submissionDate;
    }

    public String caseId() {
        return caseId;
    }

    public String anmIdentifier() {
        return anmIdentifier;
    }

    @Override
    public boolean equals(Object o) {
        return EqualsBuilder.reflectionEquals(o, this);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
