package org.opensrp.reporting.domain;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

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
        this(0, indicator);
    }

    public Indicator(Integer id, String indicator) {
        this.id = id;
        this.indicator = indicator;
    }

    public Integer id() {
        return id;
    }

    public String indicator() {
        return indicator;
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
