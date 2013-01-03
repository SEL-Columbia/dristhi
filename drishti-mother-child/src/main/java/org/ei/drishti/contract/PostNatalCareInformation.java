package org.ei.drishti.contract;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.joda.time.LocalDate;

public class PostNatalCareInformation {
    private String caseId;
    private String anmIdentifier;
    private String visitNumber;
    private String numberOfIFATabletsGiven;
    private String visitDate;

    public PostNatalCareInformation(String caseId, String anmIdentifier, String visitNumber, String numberOfIFATabletsGiven, String visitDate) {
        this.caseId = caseId;
        this.anmIdentifier = anmIdentifier;
        this.visitNumber = visitNumber;
        this.numberOfIFATabletsGiven = numberOfIFATabletsGiven;
        this.visitDate = visitDate;
    }

    public String caseId() {
        return caseId;
    }

    public String anmIdentifier() {
        return anmIdentifier;
    }

    public LocalDate visitDate() {
        return LocalDate.parse(visitDate);
    }

    public int visitNumber() {
        return Integer.parseInt(visitNumber);
    }

    public String numberOfIFATabletsProvided() {
        return numberOfIFATabletsGiven;
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
