package org.ei.drishti.dto.report;

import org.codehaus.jackson.annotate.JsonProperty;
import org.joda.time.LocalDate;

public class ServiceProvidedReportDTO {


    @JsonProperty
    private Integer id;

    @JsonProperty
    private String anm_identifier;

    @JsonProperty
    private String type;

    @JsonProperty
    private String indicator;

    @JsonProperty
    private LocalDate reported_date;

    @JsonProperty
    private Integer nrhm_report_month;

    @JsonProperty
    private Integer nrhm_report_year;

    @JsonProperty
    private String village;

    @JsonProperty
    private String sub_center;

    @JsonProperty
    private String phc;

    @JsonProperty
    private String taluka;

    @JsonProperty
    private String district;

    @JsonProperty
    private String state;

    public ServiceProvidedReportDTO(Integer id, String anm_identifier, String type, String indicator, LocalDate reported_date,
                                    String village, String sub_center, String phc, String taluka, String district, String state) {
        this.id = id;
        this.anm_identifier = anm_identifier;
        this.type = type;
        this.indicator = indicator;
        this.reported_date = reported_date;
        this.village = village;
        this.sub_center = sub_center;
        this.phc = phc;
        this.taluka = taluka;
        this.district = district;
        this.state = state;
    }

    public ServiceProvidedReportDTO() {
    }

    public ServiceProvidedReportDTO withId(Integer id) {
        this.id = id;
        return this;
    }

    public ServiceProvidedReportDTO withNRHMReportingMonth(Integer month) {
        this.nrhm_report_month = month;
        return this;
    }

    public ServiceProvidedReportDTO withNRHMReportingYear(Integer year) {
        this.nrhm_report_year = year;
        return this;
    }

    public ServiceProvidedReportDTO withDate(LocalDate date) {
        this.reported_date = date;
        return this;
    }
}