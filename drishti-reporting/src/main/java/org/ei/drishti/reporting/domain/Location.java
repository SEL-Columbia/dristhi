package org.ei.drishti.reporting.domain;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import javax.persistence.*;

@Entity
@Table(name = "dim_location")
@NamedQuery(name = Location.FIND_BY_VILLAGE_SUBCENTER_AND_PHC, query = "select r from Location r where r.village=:village and r.subCenter=:subCenter and r.phc=:phc")
public class Location {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "village")
    private String village;

    @Column(name = "subCenter")
    private String subCenter;

    @Column(name = "phc")
    private String phc;

    public static final String FIND_BY_VILLAGE_SUBCENTER_AND_PHC = "find.by.village.subcenter.and.phc";

    private Location() {
    }

    public Location(Integer id, String village, String subCenter, String phc) {
        this.id = id;
        this.village = village;
        this.subCenter = subCenter;
        this.phc = phc;
    }

    public Location(String village, String subCenter, String phc) {
        this(0, village, subCenter, phc);
    }

    public Integer id() {
        return id;
    }

    public String village() {
        return village;
    }

    public String subCenter() {
        return subCenter;
    }

    public String phc() {
        return phc;
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
