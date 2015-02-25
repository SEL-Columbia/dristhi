package org.opensrp.reporting.domain;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import javax.persistence.*;

@Entity
@Table(name = "dim_phc")
@NamedQuery(name = PHC.FIND_BY_PHC_IDENTIFIER, query = "select r from PHC r where r.phcIdentifier=:phcIdentifier")
public class PHC {
    public static final String FIND_BY_PHC_IDENTIFIER = "find.by.phc.identifier";

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "phcIdentifier")
    private String phcIdentifier;

    @Column(name = "name")
    private String name;

    private PHC() {
    }

    public PHC(String phcIdentifier, String name) {
        this(0, phcIdentifier, name);
    }

    public PHC(Integer id, String phcIdentifier, String name) {
        this.id = id;
        this.phcIdentifier = phcIdentifier;
        this.name = name;
    }

    public Integer id() {
        return id;
    }

    public String phcIdentifier() {
        return phcIdentifier;
    }

    public String name() {
        return name;
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
}
