package org.ei.drishti.reporting.domain;

import javax.persistence.*;

@Entity
@Table(name = "service_provided")
public class ServiceProvided {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "anmIdentifier")
    private Integer anmIdentifier;

    @Column(name = "externalId")
    private Integer externalId;

    @Column(name = "indicator")
    private Integer indicator;

    @Column(name = "date_")
    private Integer date;

    @Column(name = "location")
    private Integer location;

    private ServiceProvided() {
    }

    public ServiceProvided(Integer anmIdentifier, Integer externalId, Integer indicator, Integer date, Integer location) {
        this.anmIdentifier = anmIdentifier;
        this.externalId = externalId;
        this.indicator = indicator;
        this.date = date;
        this.location = location;
    }
}
