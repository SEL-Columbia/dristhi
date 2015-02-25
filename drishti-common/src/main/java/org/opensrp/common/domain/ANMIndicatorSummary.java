package org.ei.drishti.common.domain;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.codehaus.jackson.annotate.JsonProperty;

import java.util.List;

public class ANMIndicatorSummary {
    @JsonProperty
    private final String indicator;
    @JsonProperty
    private final String annualTarget;
    @JsonProperty
    private final List<MonthSummary> monthlySummaries;

    public ANMIndicatorSummary(String indicator, String annualTarget, List<MonthSummary> monthlySummaries) {
        this.indicator = indicator;
        this.annualTarget = annualTarget;
        this.monthlySummaries = monthlySummaries;
    }

    public List<MonthSummary> monthlySummaries() {
        return monthlySummaries;
    }

    public String annualTarget() {
        return annualTarget;
    }

    public String indicator() {
        return indicator;
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
