package org.opensrp.reporting.domain;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import javax.persistence.*;

@Entity
@Table(name = "dim_anm")
@NamedQuery(name = ANM.FIND_BY_ANM_ID, query = "select r from ANM r where r.anmIdentifier=:anmIdentifier")
public class ANM {
    public static final String FIND_BY_ANM_ID = "find.by.anm.id";

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "anmIdentifier")
    private String anmIdentifier;

    private ANM() {
    }

    public ANM(String anmIdentifier) {
        this(0, anmIdentifier);
    }

    public ANM(Integer id, String anmIdentifier) {
        this.id = id;
        this.anmIdentifier = anmIdentifier;
    }

    public Integer id() {
        return id;
    }

    public String anmIdentifier() {
        return anmIdentifier;
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
