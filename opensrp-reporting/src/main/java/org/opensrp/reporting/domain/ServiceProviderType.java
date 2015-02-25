package org.opensrp.reporting.domain;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import javax.persistence.*;

@Entity
@Table(name = "dim_service_provider_type")
public class ServiceProviderType {

    public static final ServiceProviderType PHC = new ServiceProviderType("PHC");
    public static final ServiceProviderType ANM = new ServiceProviderType("ANM");

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    public String getType() {
        return type;
    }

    @Column(name = "type")
    private String type;

    private ServiceProviderType() {
    }

    public ServiceProviderType(String type) {
        this.type = type;
    }

    public Integer id() {
        return id;
    }

    public static ServiceProviderType parse(String serviceProviderType){
        return ANM.type.equalsIgnoreCase(serviceProviderType) ? ANM : PHC;
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

    public String type() {
        return type;
    }
}
