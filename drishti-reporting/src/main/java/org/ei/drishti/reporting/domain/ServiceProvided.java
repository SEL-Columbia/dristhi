package org.ei.drishti.reporting.domain;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import javax.persistence.*;

@Entity
@Table(name = "service_provided")
@NamedQueries({
        @NamedQuery(name = ServiceProvided.FIND_BY_ANM_IDENTIFIER_WITH_INDICATOR_FOR_MONTH,
                query = "select r from ServiceProvided r, Dates d, Indicator i " +
                        "where  r.date = d.id and r.indicator = i.id and i.indicator = ? and d.date >= ? and d.date < ?"),
        @NamedQuery(name = ServiceProvided.FIND_SERVICE_PROVIDED_FOR_REPORTING_MONTH,
                query = "select r from ServiceProvided r, Dates d, Indicator i,ServiceProvider p, ServiceProviderType spt" +
                        " where  r.date = d.id and r.indicator = i.id and r.serviceProvider = p.id and p.type = spt.id" +
                        " and d.date >= ? and d.date < ?")
})

public class ServiceProvided {
    public static final String FIND_BY_ANM_IDENTIFIER_WITH_INDICATOR_FOR_MONTH = "find.service.provided.by.anm.identifier.with.indicator.for.month";
    public static final String FIND_SERVICE_PROVIDED_FOR_REPORTING_MONTH = "find.service.provided.for.reporting.month";

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

    public String id() {
        return id.toString();
    }

    public String serviceProviderType() {
        return serviceProvider.getType().getType();
    }

    public String indicator() {
        return indicator.indicator();
    }

    public String date() {
        return date.date().toString();
    }

    public org.ei.drishti.domain.Location location() {
        return new org.ei.drishti.domain.Location(location.village(), location.subCenter(), location.phc().phcIdentifier());
    }

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

    @Override
    public boolean equals(Object o) {
        return EqualsBuilder.reflectionEquals(this, o, new String[]{"id", "revision"});
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this, new String[]{"id", "revision"});
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
