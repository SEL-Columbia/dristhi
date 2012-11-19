package org.ei.drishti.common.domain;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import java.util.List;

public class ANMReport {
    private List<ANMIndicatorSummary> summaries;
    private String anmIdentifier;

    public ANMReport(String anmIdentifier, List<ANMIndicatorSummary> summaries) {
        this.anmIdentifier = anmIdentifier;
        this.summaries = summaries;
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
