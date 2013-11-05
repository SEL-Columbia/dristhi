package org.ei.drishti.reporting.controller;

public class ANMReportDTO {
    private String anmIdentifier;
    private String indicator;
    private String id;
    private String date;

    public ANMReportDTO(String id, String indicator, String anmIdentifier, String date) {
        this.id = id;
        this.indicator = indicator;
        this.anmIdentifier = anmIdentifier;
        this.date = date;
    }
}
