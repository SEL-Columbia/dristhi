package org.ei.drishti.dto.aggregatorResponse;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.codehaus.jackson.annotate.JsonProperty;

import java.util.Map;

public class AggregatorResponseDTO {
    @JsonProperty
    private AggregatorResponseIndicatorDTO indicator;

    public AggregatorResponseDTO(Map<String, Integer> indicatorSummaries) {
        indicator = new AggregatorResponseIndicatorDTO(indicatorSummaries);
    }

    public Map<String, Integer> indicatorSummary() {
        return indicator.summary();
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
