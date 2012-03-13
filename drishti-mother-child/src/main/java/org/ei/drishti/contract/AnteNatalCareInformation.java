package org.ei.drishti.contract;

import org.joda.time.LocalDate;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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
        return "AnteNatalCareInformation { " +
                "caseId= '" + caseId + "'" +
                ", TT1 = " + tetanus1Date + ", TT2 = " + tetanus2Date + ", IFA 1 = " + ironFolicAcidTablet1Date +
                ", IFA 2 = " + ironFolicAcidTablet2Date + ", ANC 1 = " + anc1Date + ", ANC 2 = " + anc2Date +
                ", ANC 3 = " + anc3Date + ", ANC 4 = " + anc4Date + " }";
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

    public Map<Integer, LocalDate> ancVisits() {
        Map<Integer, LocalDate> visitNumberToDate = new HashMap<Integer, LocalDate>();
        addVisit(visitNumberToDate, 1, this.anc1Date);
        addVisit(visitNumberToDate, 2, this.anc2Date);
        addVisit(visitNumberToDate, 3, this.anc3Date);
        addVisit(visitNumberToDate, 4, this.anc4Date);
        return visitNumberToDate;
    }

    public Map<Integer, LocalDate> ttVisits() {
        Map<Integer, LocalDate> visitNumberToDate = new HashMap<Integer, LocalDate>();
        addVisit(visitNumberToDate, 1, this.tetanus1Date);
        addVisit(visitNumberToDate, 2, this.tetanus2Date);
        return visitNumberToDate;
    }

    public Map<Integer, LocalDate> ifaVisits() {
        Map<Integer, LocalDate> visitNumberToDate = new HashMap<Integer, LocalDate>();
        addVisit(visitNumberToDate, 1, this.ironFolicAcidTablet1Date);
        addVisit(visitNumberToDate, 2, this.ironFolicAcidTablet2Date);
        return visitNumberToDate;
    }

    private void addVisit(Map<Integer, LocalDate> visitNumberToDate, int visitNumber, Date date) {
        if (date != null) {
            visitNumberToDate.put(visitNumber, new LocalDate(date));
        }
    }
}

