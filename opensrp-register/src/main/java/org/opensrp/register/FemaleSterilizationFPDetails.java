package org.opensrp.register;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.util.ArrayList;
import java.util.List;

@JsonSerialize
public class FemaleSterilizationFPDetails {
    @JsonProperty
    private String typeOfSterilization;
    @JsonProperty
    private String sterilizationDate;
    @JsonProperty
    private List<String> followupVisitDates;

    private FemaleSterilizationFPDetails() {
    }

    public FemaleSterilizationFPDetails(String typeOfSterilization, String sterilizationDate, List<String> followupVisitDates) {
        this.typeOfSterilization = typeOfSterilization;
        this.sterilizationDate = sterilizationDate;
        this.followupVisitDates = followupVisitDates;
    }

    public FemaleSterilizationFPDetails(String typeOfSterilization, String sterilizationDate) {
        this.typeOfSterilization = typeOfSterilization;
        this.sterilizationDate = sterilizationDate;
    }

    public String typeOfSterilization() {
        return typeOfSterilization;
    }

    public String sterilizationDate() {
        return sterilizationDate;
    }

    public List<String> followupVisitDates() {
        if (followupVisitDates == null) {
            followupVisitDates = new ArrayList<>();
        }
        return followupVisitDates;
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