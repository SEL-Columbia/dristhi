package org.ei.drishti.contract;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.joda.time.LocalDate;
import org.motechproject.util.DateUtil;

public class AnteNatalCareInformation {
    private String caseId;
    private String anmIdentifier;
    private int ancVisitNumber;
    private int numberOfIFATabletsGiven;

    public AnteNatalCareInformation(String caseId, String anmIdentifier, int visitNumber) {
        this.caseId = caseId;
        this.anmIdentifier = anmIdentifier;
        this.ancVisitNumber = visitNumber;
    }

    public String caseId() {
        return caseId;
    }

    public AnteNatalCareInformation withNumberOfIFATabletsProvided(int numberOfTablets) {
        this.numberOfIFATabletsGiven = numberOfTablets;
        return this;
    }

    public boolean areIFATabletsProvided() {
        return numberOfIFATabletsGiven > 0;
    }

    public int visitNumber() {
        return ancVisitNumber;
    }

    public LocalDate visitDate() {
        return DateUtil.today();
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

