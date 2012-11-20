package org.ei.drishti.dto;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import java.util.List;

public class MonthSummaryDatum {
    private final String month;
    private final String year;
    private final String currentProgress;
    private final String aggregatedProgress;
    private final List<String> externalIDs;

    public MonthSummaryDatum(String month, String year, String currentProgress, String aggregatedProgress, List<String> externalIDs) {
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
