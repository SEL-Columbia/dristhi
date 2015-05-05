package org.opensrp.reporting.domain;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "service_provided")
@NamedQueries({
        @NamedQuery(name = ServiceProvided.FIND_BY_ANM_IDENTIFIER_WITH_INDICATOR_FOR_MONTH,
                query = "select r from ServiceProvided r, Indicator i " +
                        "where r.indicator = i.id and i.indicator = ? and r.date >= ? and r.date < ?"),
        @NamedQuery(name = ServiceProvided.FIND_SERVICE_PROVIDED_FOR_REPORTING_MONTH,
                query = "select r from ServiceProvided r, Indicator i,ServiceProvider p, ServiceProviderType spt" +
                        " where r.indicator = i.id and r.serviceProvider = p.id and p.type = spt.id" +
                        " and r.date >= ? and r.date < ?"),
        @NamedQuery(name = ServiceProvided.FIND_SERVICE_PROVIDED_FOR_DRISTHI_ENTITY_ID,
                query = "select r from ServiceProvided r where r.dristhiEntityId = ?")
})

public class ServiceProvided {
    public static final String FIND_BY_ANM_IDENTIFIER_WITH_INDICATOR_FOR_MONTH = "find.service.provided.by.anm.identifier.with.indicator.for.month";
    public static final String FIND_SERVICE_PROVIDED_FOR_REPORTING_MONTH = "find.service.provided.for.reporting.month";
    public static final String FIND_SERVICE_PROVIDED_FOR_DRISTHI_ENTITY_ID = "find.service.provided.for.dristhi.entity.id";

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
        return date.toString();
    }
    public String dristhiEntityId() {
        return this.dristhiEntityId;
    }

    public org.opensrp.common.domain.Location location() {
        return new org.opensrp.common.domain.Location(location.village(), location.subCenter(), location.phc().phcIdentifier());
    }

    @Column(name = "date_")
    private Date date;

    @Column(name = "dristhi_entity_id")
    private String dristhiEntityId;

    @ManyToOne
    @JoinColumn(name = "location")
    private Location location;

    private ServiceProvided() {
    }

    public ServiceProvided(ServiceProvider serviceProvider, String externalId, Indicator indicator, Date date, Location location, String dristhiEntityId) {
        this.serviceProvider = serviceProvider;
        this.externalId = externalId;
        this.indicator = indicator;
        this.date = date;
        this.location = location;
        this.dristhiEntityId = dristhiEntityId;
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
