package org.ei.drishti.contract;

import org.joda.time.LocalDate;

import java.util.Date;

public class AnteNatalCareInformation {
    private String caseId;
    private Date tetanus1Date;
    private Date tetanus2Date;
    private Date ironFolicAcidTablet1Date;
    private Date ironFolicAcidTablet2Date;
    private Date anc1Date;
    private Date anc2Date;
    private Date anc3Date;
    private Date anc4Date;

    public AnteNatalCareInformation(String caseId) {
        this.caseId = caseId;
    }

    public String caseId() {
        return caseId;
    }

    @Override
    public String toString() {
        return "AnteNatalCareInformation{" +
                "caseId='" + caseId + '\'' +
                ", tetanus1Date='" + tetanus1Date + '\'' +
                ", tetanus2Date='" + tetanus2Date + '\'' +
                ", ironFolicAcidTablet1Date='" + ironFolicAcidTablet1Date + '\'' +
                ", ironFolicAcidTablet2Date='" + ironFolicAcidTablet2Date + '\'' +
                '}';
    }

    public AnteNatalCareInformation withTT1Date(LocalDate date) {
        this.tetanus1Date = date.toDate();
        return this;
    }

    public AnteNatalCareInformation withTT2Date(LocalDate date) {
        this.tetanus2Date = date.toDate();
        return this;
    }

    public LocalDate tt1Date() {
        return new LocalDate(this.tetanus1Date);
    }

    public LocalDate tt2Date() {
        return new LocalDate(this.tetanus2Date);
    }

    public AnteNatalCareInformation withIFA1Date(LocalDate date) {
        this.ironFolicAcidTablet1Date = date.toDate();
        return this;
    }

    public AnteNatalCareInformation withIFA2Date(LocalDate date) {
        this.ironFolicAcidTablet2Date = date.toDate();
        return this;
    }

    public LocalDate ifa1Date() {
        return new LocalDate(this.ironFolicAcidTablet1Date);
    }

    public LocalDate ifa2Date() {
        return new LocalDate(this.ironFolicAcidTablet2Date);
    }

    public LocalDate anc1Date() {
        return new LocalDate(anc1Date);
    }

    public LocalDate anc2Date() {
        return new LocalDate(anc2Date);
    }

    public LocalDate anc3Date() {
        return new LocalDate(anc3Date);
    }

    public LocalDate anc4Date() {
        return new LocalDate(anc4Date);
    }

    public AnteNatalCareInformation withAnc1Date(LocalDate anc1Date) {
        this.anc1Date = anc1Date.toDate();
        return this;
    }

    public AnteNatalCareInformation withAnc2Date(LocalDate anc2Date) {
        this.anc2Date = anc2Date.toDate();
        return this;
    }

    public AnteNatalCareInformation withAnc3Date(LocalDate anc3Date) {
        this.anc3Date = anc3Date.toDate();
        return this;
    }

    public AnteNatalCareInformation withAnc4Date(LocalDate anc4Date) {
        this.anc4Date = anc4Date.toDate();
        return this;
    }
}

