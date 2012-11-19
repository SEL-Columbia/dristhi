package org.ei.drishti.common.domain;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.codehaus.jackson.annotate.JsonProperty;

import java.util.List;

public class MonthSummary {
    @JsonProperty
    private final String month;
    @JsonProperty
    private final String year;
    @JsonProperty
    private final String currentProgress;
    @JsonProperty
    private final String aggregatedProgress;
    @JsonProperty
    private final List<String> externalIDs;

    public MonthSummary(String month, String year, String currentProgress, String aggregatedProgress, List<String> externalIDs) {
        this.month = month;
        this.year = year;
        this.currentProgress = currentProgress;
        this.aggregatedProgress = aggregatedProgress;
        this.externalIDs = externalIDs;
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
