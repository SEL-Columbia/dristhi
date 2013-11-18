package org.ei.drishti.dto.report;

import org.codehaus.jackson.annotate.JsonProperty;

import java.util.Date;

public class ServiceProvidedReportDTO {


    @JsonProperty
    private Integer id;

    @JsonProperty
    private String anmIdentifier;

    @JsonProperty
    private String type;

    @JsonProperty
    private String indicator;

    @JsonProperty
    private java.util.Date date;

    @JsonProperty
    private Integer day;

    @JsonProperty
    private Integer month;

    @JsonProperty
    private Integer year;

    @JsonProperty
    private String village;

    @JsonProperty
    private String subCenter;

    @JsonProperty
    private String phc;

    @JsonProperty
    private String taluka;

    @JsonProperty
    private String district;

    @JsonProperty
    private String state;

    public ServiceProvidedReportDTO(Integer id, String anmIdentifier, String type, String indicator, Date date,
                                    String village, String subCenter, String phc, String taluka, String district, String state) {
        this.id = id;
        this.anmIdentifier = anmIdentifier;
        this.type = type;
        this.indicator = indicator;
        this.date = date;
        this.village = village;
        this.subCenter = subCenter;
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


    public ServiceProvidedReportDTO withDay(Integer day) {
        this.day = day;
        return this;
    }

    public ServiceProvidedReportDTO withMonth(Integer month) {
        this.month = month;
        return this;
    }

    public ServiceProvidedReportDTO withYear(Integer year) {
        this.year= year;
        return this;
    }

    public ServiceProvidedReportDTO withDate(Date date) {
        this.date = date;
        return this;
    }
}