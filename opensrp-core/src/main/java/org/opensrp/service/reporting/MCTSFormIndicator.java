package org.opensrp.service.reporting;

import java.util.List;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class MCTSFormIndicator {
    private String form;
    private List<MCTSReportIndicator> indicators;

    public MCTSFormIndicator(String form, List<MCTSReportIndicator> indicators) {
        this.form = form;
        this.indicators = indicators;
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

    public String form() {
        return form;
    }

    public List<MCTSReportIndicator> indicators() {
        return indicators;
    }
}