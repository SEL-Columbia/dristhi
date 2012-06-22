package org.ei.drishti.reporting.domain;

import javax.persistence.*;

@Entity
@Table(name = "location")
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

    public Location(String village, String subCenter, String phc) {
        this.village = village;
        this.subCenter = subCenter;
        this.phc = phc;
    }
}
