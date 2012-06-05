package org.ei.drishti.reporting.domain;

import javax.persistence.*;

@Entity
@Table(name = "mother_registration")
public class MotherRegistration {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "day")
    private String name;

    public MotherRegistration(String name) {
        this.name = name;
    }
}
