package org.ei.drishti.reporting.domain;

import javax.persistence.*;

@Entity
@Table(name = "anm_report_data")
public class ANMReportData {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "anmIdentifier")
    private Integer anmIdentifier;

    @Column(name = "externalId")
    private String externalId;

    @Column(name = "indicator")
    private Integer indicator;

    @Column(name = "date_")
    private Integer date;

    private ANMReportData() {
    }

    public ANMReportData(Integer anmIdentifier, String externalId, Integer indicator, Integer date) {
        this.anmIdentifier = anmIdentifier;
        this.externalId = externalId;
        this.indicator = indicator;
        this.date = date;
    }
}
