package org.ei.drishti.reporting.domain;

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

    public Integer id() {
        return id;
    }

    public ANM(String anmIdentifier) {
        this.anmIdentifier = anmIdentifier;
    }

    public String anmIdentifier() {
        return anmIdentifier;
    }
}
