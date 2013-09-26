package org.ei.drishti.reporting.domain;

import javax.persistence.*;

@Entity
@Table(name = "service_provided")
@NamedQueries({
        @NamedQuery(name = ServiceProvided.FIND_BY_ANM_IDENTIFIER_WITH_INDICATOR_FOR_MONTH, query = "select r from ServiceProvided r, Dates d, Indicator i where  r.date = d.id and r.indicator = i.id and i.indicator = ? and d.date >= ? and d.date < ?")
})

public class ServiceProvided {
    public static final String FIND_BY_ANM_IDENTIFIER_WITH_INDICATOR_FOR_MONTH = "find.service.provided.by.anm.identifier.with.indicator.for.month";

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
