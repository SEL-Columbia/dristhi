package org.ei.drishti.reporting.domain;

import javax.persistence.*;
import java.text.SimpleDateFormat;
import java.util.Date;

@Entity
@Table(name = "service_provided_report_view")
@NamedQuery(name = ServiceProvidedReport.FIND_ALL_SERVICE_PROVIDED_BY_DATE_FOR_ANM,
        query = "select r from ServiceProvidedReport r where r.anmIdentifier = ? and r.date >= ? and r.date < ?")

public class ServiceProvidedReport {

    public static final String FIND_ALL_SERVICE_PROVIDED_BY_DATE_FOR_ANM = "find.all.service.provided.by.date.for.anm";

    @Id
    @Column(name = "id", insertable = false, updatable = false)
    private String id;

    @Column(name = "anmidentifier", insertable = false, updatable = false)
    private String anmIdentifier;

    @Column(name = "service_provided_type", insertable = false, updatable = false)
    private String type;

    @Column(name = "indicator", insertable = false, updatable = false)
    private String indicator;

    public String id() {
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

    public ServiceProvidedReport(String id, String anmIdentifier, String type, String indicator, Date date, String village, String subCenter, String phc, String taluka, String district, String state) {
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
}