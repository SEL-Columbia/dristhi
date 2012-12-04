package org.ei.drishti.reporting.domain;

import javax.persistence.*;

@Entity
@Table(name = "service_provided")
public class ServiceProvided {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "service_provider")
    private Integer serviceProvider;

    @Column(name = "externalId")
    private String externalId;

    @Column(name = "indicator")
    private Integer indicator;

    @Column(name = "date_")
    private Integer date;

    @Column(name = "location")
    private Integer location;

    private ServiceProvided() {
    }

    public ServiceProvided(Integer serviceProvider, String externalId, Integer indicator, Integer date, Integer location) {
        this.serviceProvider = serviceProvider;
        this.externalId = externalId;
        this.indicator = indicator;
        this.date = date;
        this.location = location;
    }
}
