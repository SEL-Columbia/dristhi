package org.opensrp.reporting.domain;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import javax.persistence.*;

@Entity
@Table(name = "dim_service_provider")
@NamedQueries({
        @NamedQuery(name = ServiceProvider.FIND_BY_PHC_IDENTIFIER, query = "select r from ServiceProvider r, PHC p, ServiceProviderType s where r.serviceProvider = p.id and r.type = s.id and p.phcIdentifier=:phcIdentifier and s.type='PHC'"),
        @NamedQuery(name = ServiceProvider.FIND_BY_ANM_IDENTIFIER, query = "select r from ServiceProvider r, SP_ANM a, ServiceProviderType s where r.serviceProvider = a.id and r.type = s.id and a.anmIdentifier=:anmIdentifier and s.type='ANM'")
})
public class ServiceProvider {
    public static final String FIND_BY_PHC_IDENTIFIER = "find.service.provider.by.phc.identifier";
    public static final String FIND_BY_ANM_IDENTIFIER = "find.service.provider.by.anm.identifier";

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "service_provider")
    private Integer serviceProvider;

    @ManyToOne
    @JoinColumn(name = "type", insertable = true, updatable = true)
    private ServiceProviderType type;


    private ServiceProvider() {
    }

    public ServiceProvider(Integer id, Integer serviceProvider, ServiceProviderType type) {
        this.id = id;
        this.serviceProvider = serviceProvider;
        this.type = type;
    }

    public ServiceProvider(Integer serviceProvider, ServiceProviderType type) {
        this.serviceProvider = serviceProvider;
        this.type = type;
    }

    public Integer id() {
        return id;
    }

    public Integer serviceProviderId() {
        return serviceProvider;
    }

    @Override
    public boolean equals(Object o) {
        return EqualsBuilder.reflectionEquals(this, o, new String[]{"id"});
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this, new String[]{"id"});
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    public ServiceProviderType getType() {
        return type;
    }
}
