package org.opensrp.service.reporting;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.List;

public class ReportIndicator {
    private String indicator;
    private String reportEntityType;
    private String serviceProvidedDateField;
    private String serviceProvidedPlaceField;
    private String quantityField;
    private List<String> formFields;
    private ReferenceData referenceData;
    private List<String> reportWhen;
    private String reportEntityIdField;
    private String externalIdField;

    public ReportIndicator(String indicator) {
        this.indicator = indicator;
    }

    public ReportIndicator(String indicator, String reportEntityType, String quantityField, String serviceProvidedDateField,
                           List<String> formFields, ReferenceData referenceData, List<String> reportWhen, String serviceProvidedPlaceField, String externalIdField) {
        this.indicator = indicator;
        this.reportEntityType = reportEntityType;
        this.serviceProvidedDateField = serviceProvidedDateField;
        this.serviceProvidedPlaceField = serviceProvidedPlaceField;
        this.quantityField = quantityField;
        this.formFields = formFields;
        this.referenceData = referenceData;
        this.reportWhen = reportWhen;
        this.externalIdField = externalIdField;
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

    public String quantityField() {
        return quantityField;
    }

    public String serviceProvidedPlaceField() {
        return serviceProvidedPlaceField;
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

    public String externalIdField() {
        return externalIdField;
    }

    public ReportIndicator withReportEntityIdField(String reportEntityIdField) {
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
