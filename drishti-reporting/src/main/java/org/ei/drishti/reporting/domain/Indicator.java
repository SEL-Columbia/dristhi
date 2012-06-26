package org.ei.drishti.reporting.domain;

import javax.persistence.*;

@Entity
@Table(name = "dim_indicator")
@NamedQuery(name = Indicator.FIND_BY_INDICATOR, query = "select r from Indicator r where r.indicator=:indicator")
public class Indicator {
    public static final String FIND_BY_INDICATOR = "find.by.indicator";

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "indicator")
    private String indicator;

    private Indicator() {
    }

    public Indicator(String indicator) {
        this.indicator = indicator;
    }

    public Integer id() {
        return id;
    }

    public String indicator() {
        return indicator;
    }
}
