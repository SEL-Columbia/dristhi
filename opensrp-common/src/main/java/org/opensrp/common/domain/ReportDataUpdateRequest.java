package org.opensrp.common.domain;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.http.annotation.Immutable;
import org.codehaus.jackson.annotate.JsonProperty;

import java.io.Serializable;
import java.util.List;

@Immutable
public class ReportDataUpdateRequest implements Serializable {

    @JsonProperty
    private String startDate;
    @JsonProperty
    private String endDate;
    @JsonProperty
    private String indicator;
    @JsonProperty
    private String type;

    @JsonProperty
    private List<ReportingData> reportingData;

    public ReportDataUpdateRequest() {
    }

    public ReportDataUpdateRequest(String type) {
        this.type = type;
    }

    public static ReportDataUpdateRequest buildReportDataRequest(String type, Indicator indicator, String reportingMonthStartDate,
                                                                 String reportingMonthEndDate, List<ReportingData> serviceProvidedData) {
        return new ReportDataUpdateRequest()
                .withType(type)
                .withReportingData(serviceProvidedData)
                .withStartDate(reportingMonthStartDate)
                .withEndDate(reportingMonthEndDate)
                .withIndicator(indicator.value());
    }

    public String indicator() {
        return this.indicator;
    }

    public String startDate() {
        return this.startDate;
    }

    public String endDate() {
        return this.endDate;
    }

    public String type() {
        return this.type;
    }

    public List<ReportingData> reportingData() {
        return this.reportingData;
    }

    public ReportDataUpdateRequest withIndicator(String indicator) {
        this.indicator = indicator;
        return this;
    }

    public ReportDataUpdateRequest withStartDate(String startDate) {
        this.startDate = startDate;
        return this;
    }

    public ReportDataUpdateRequest withType(String type) {
        this.type = type;
        return this;
    }

    public ReportDataUpdateRequest withEndDate(String endDate) {
        this.endDate = endDate;
        return this;
    }

    public ReportDataUpdateRequest withReportingData(List<ReportingData> reportingData) {
        this.reportingData = reportingData;
        return this;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @Override
    public final boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public final int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}
