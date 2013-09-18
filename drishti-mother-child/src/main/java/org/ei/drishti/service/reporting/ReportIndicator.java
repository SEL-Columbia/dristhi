package org.ei.drishti.service.reporting;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.List;

public class ReportIndicator {
    private String indicator;
    private String bindType;
    private String quantityField;
    private List<String> formFields;
    private ReferenceData referenceData;
    private List<String> reportWhen;

    public ReportIndicator(String indicator) {
        this.indicator = indicator;
    }

    public ReportIndicator(String indicator, String bindType, String quantityField, List<String> formFields,
                           ReferenceData referenceData, List<String> reportWhen) {
        this.indicator = indicator;
        this.formFields = formFields;
        this.referenceData = referenceData;
        this.reportWhen = reportWhen;
        this.bindType = bindType;
        this.quantityField = quantityField;
    }

    public List<String> formFields() {
        return formFields;
    }

    public ReferenceData referenceData() {
        return referenceData;
    }

    public List<String> reportingRules() {
        return reportWhen;
    }

    public String bindType() {
        return bindType;
    }

    public String indicator() {
        return indicator;
    }

    public String quantityField() {
        return quantityField;
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
