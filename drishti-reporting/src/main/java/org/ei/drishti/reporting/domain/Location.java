package org.ei.drishti.reporting.domain;

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

    public Location(String village, String subCenter, String phc) {
        this.village = village;
        this.subCenter = subCenter;
        this.phc = phc;
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
}
