package org.ei.drishti.reporting.domain;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import javax.persistence.*;

@Entity
@Table(name = "dim_anm")
public class SP_ANM {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "anmIdentifier")
    private String anmIdentifier;

    @Column(name = "phc")
    private Integer phcId;

    @Column(name = "subcenter")
    private String subCenter;

    @Column(name = "name")
    private String name;

    private SP_ANM() {
    }

    public SP_ANM(String identifier, String name, String subCenter, Integer phcId) {
        this.anmIdentifier = identifier;
        this.subCenter = subCenter;
        this.phcId = phcId;
        this.name = name;
    }


    public Integer id() {
        return id;
    }

    public String identifier() {
        return anmIdentifier;
    }

    public String name() {
        return name;
    }

    public String subCenter() {
        return subCenter;
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
