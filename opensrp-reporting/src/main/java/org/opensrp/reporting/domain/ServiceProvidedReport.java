package org.opensrp.reporting.domain;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import javax.persistence.*;
import java.text.SimpleDateFormat;
import java.util.Date;

@Entity
@Table(name = "service_provided_report_view")
@NamedQuery(name = ServiceProvidedReport.FIND_NEW_SERVICE_PROVIDED,
        query = "select r from ServiceProvidedReport r where r.id > ? order by r.id")
public class ServiceProvidedReport {

    public static final String FIND_NEW_SERVICE_PROVIDED = "find.new.service.provided";

    @Id
    @Column(name = "id", insertable = false, updatable = false)
    private Integer id;

    @Column(name = "anmidentifier", insertable = false, updatable = false)
    private String anmIdentifier;

    @Column(name = "service_provided_type", insertable = false, updatable = false)
    private String type;

    @Column(name = "indicator", insertable = false, updatable = false)
    private String indicator;

    @Column(name = "service_date", insertable = false, updatable = false)
    private Date date;

    @Column(name = "village", insertable = false, updatable = false)
    private String village;

    @Column(name = "subcenter", insertable = false, updatable = false)
    private String subCenter;

    @Column(name = "phc", insertable = false, updatable = false)
    private String phc;

    @Column(name = "taluka", insertable = false, updatable = false)
    private String taluka;

    @Column(name = "district", insertable = false, updatable = false)
    private String district;

    @Column(name = "state", insertable = false, updatable = false)
    private String state;

    public ServiceProvidedReport(Integer id, String anmIdentifier, String type, String indicator, Date date,
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

    public ServiceProvidedReport() {
    }

    public Integer id() {
        return id;
    }

    public String anmIdentifier() {
        return anmIdentifier;
    }

    public String type() {
        return type;
    }

    public String indicator() {
        return indicator;
    }

    public String date() {
        return new SimpleDateFormat("yyyy-MM-dd").format(date);
    }

    public String village() {
        return village;
    }

    public String subCenter() {
        return subCenter;
    }

    public String phc() {
        return phc;
    }

    public String taluka() {
        return taluka;
    }

    public String district() {
        return district;
    }

    public String state() {
        return state;
    }

    public ServiceProvidedReport withId(Integer id) {
        this.id = id;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        return EqualsBuilder.reflectionEquals(this, o, new String[]{"id"});
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this, new String[]{"id"});
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    public ServiceProvidedReport withDate(Date date) {
        this.date = date;
        return this;
    }
}