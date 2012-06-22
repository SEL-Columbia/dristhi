package org.ei.drishti.reporting.domain;

import javax.persistence.*;

@Entity
@Table(name = "anm")
public class ANM {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "anmIdentifier")
    private String anmIdentifier;

    public ANM(String anmIdentifier) {
        this.anmIdentifier = anmIdentifier;
    }
}
