package org.ei.drishti.service.reporting;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.List;

public class FormIndicator {
    private String form;
    private List<ReportIndicator> indicators;

    public FormIndicator(String form, List<ReportIndicator> indicators) {
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

    public List<ReportIndicator> indicators() {
        return indicators;
    }
}