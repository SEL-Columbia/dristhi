package org.ei.drishti.domain.register;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.List;

public class SterilizationFPDetails extends FPDetails {
    private String typeOfSterilization;
    private String sterilizationDate;
    private List<String> followupVisitDates;
    private String remarks;

    public SterilizationFPDetails(String typeOfSterilization, String sterilizationDate, List<String> followupVisitDates,
                                  String remarks) {
        this.typeOfSterilization = typeOfSterilization;
        this.sterilizationDate = sterilizationDate;
        this.followupVisitDates = followupVisitDates;
        this.remarks = remarks;
    }

    public String typeOfSterilization() {
        return typeOfSterilization;
    }

    public String sterilizationDate() {
        return sterilizationDate;
    }

    public List<String> followupVisitDates() {
        return followupVisitDates;
    }

    public String remarks() {
        return remarks;
    }

    @Override
    public boolean equals(Object o) {
        return EqualsBuilder.reflectionEquals(this, o);
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
