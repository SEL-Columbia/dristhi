package org.ei.drishti.reporting.domain;

import javax.persistence.*;

@Entity
@Table(name = "service_provided")
public class ServiceProvided {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "service_provider")
    private ServiceProvider serviceProvider;

    @Column(name = "externalId")
    private String externalId;

    @ManyToOne
    @JoinColumn(name = "indicator")
    private Indicator indicator;

    @ManyToOne
    @JoinColumn(name = "date_")
    private Dates date;

    @ManyToOne
    @JoinColumn(name = "location")
    private Location location;

    private ServiceProvided() {
    }

    public ServiceProvided(ServiceProvider serviceProvider, String externalId, Indicator indicator, Dates date, Location location) {
        this.serviceProvider = serviceProvider;
        this.externalId = externalId;
        this.indicator = indicator;
        this.date = date;
        this.location = location;
    }
}
