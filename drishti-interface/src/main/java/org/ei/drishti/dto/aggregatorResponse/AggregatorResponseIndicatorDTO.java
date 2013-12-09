package org.ei.drishti.dto.aggregatorResponse;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.codehaus.jackson.annotate.JsonProperty;

import java.util.Map;

public class AggregatorResponseIndicatorDTO {
    @JsonProperty
    private Map<String, Integer> summary;

    public AggregatorResponseIndicatorDTO(Map<String, Integer> summary) {
        this.summary = summary;
    }

    public Map<String, Integer> summary() {
        return summary;
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
