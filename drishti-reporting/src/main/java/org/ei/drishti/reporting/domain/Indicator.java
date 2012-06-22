package org.ei.drishti.reporting.domain;

import javax.persistence.*;

@Entity
@Table(name = "indicator_")
public class Indicator {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "indicator_")
    private String indicator;

    public Indicator(String indicator) {
        this.indicator = indicator;
    }
}
