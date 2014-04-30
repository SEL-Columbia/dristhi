package org.ei.drishti.service.reporting;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.List;

public class MCTSReportIndicator {
    private String indicator;
    private String reportEntityType;
    private String serviceProvidedDateField;
    private List<String> formFields;
    private ReferenceData referenceData;
    private List<String> reportWhen;
    private String reportEntityIdField;

    public MCTSReportIndicator(String indicator) {
        this.indicator = indicator;
    }

    public MCTSReportIndicator(String indicator, String reportEntityType, String serviceProvidedDateField,
                               List<String> formFields, ReferenceData referenceData, List<String> reportWhen) {
        this.indicator = indicator;
        this.reportEntityType = reportEntityType;
        this.serviceProvidedDateField = serviceProvidedDateField;
        this.formFields = formFields;
        this.referenceData = referenceData;
        this.reportWhen = reportWhen;
    }

    public String indicator() {
        return indicator;
    }

    public String reportEntityType() {
        return reportEntityType;
    }

    public String serviceProvidedDateField() {
        return serviceProvidedDateField;
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

    public String reportEntityIdField() {
        return reportEntityIdField;
    }

    public MCTSReportIndicator withReportEntityIdField(String reportEntityIdField) {
        this.reportEntityIdField = reportEntityIdField;
        return this;
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @Override
    public boolean equals(Object o) {
        return EqualsBuilder.reflectionEquals(this, o);
    }
}
