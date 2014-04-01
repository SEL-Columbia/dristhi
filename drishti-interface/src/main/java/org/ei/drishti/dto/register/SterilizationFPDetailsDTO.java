package org.ei.drishti.dto.register;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.codehaus.jackson.annotate.JsonProperty;

import java.util.List;

public class SterilizationFPDetailsDTO {
    @JsonProperty
    private String typeOfSterilization;
    @JsonProperty
    private String sterilizationDate;
    @JsonProperty
    private List<String> followupVisitDates;
    @JsonProperty
    private String remarks;

    public SterilizationFPDetailsDTO(String typeOfSterilization, String sterilizationDate, List<String> followupVisitDates,
                                     String remarks) {
        this.typeOfSterilization = typeOfSterilization;
        this.sterilizationDate = sterilizationDate;
        this.followupVisitDates = followupVisitDates;
        this.remarks = remarks;
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
