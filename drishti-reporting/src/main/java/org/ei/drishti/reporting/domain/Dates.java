package org.ei.drishti.reporting.domain;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "dim_date")
@NamedQuery(name = Dates.FIND_DATES_BY_DATE, query = "select r from Dates r where r.date=:date")
public class Dates {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "date_")
    private Date date;

    public static final String FIND_DATES_BY_DATE = "find.dates.by.date";

    private Dates() {
    }

    public Dates(Date date) {
        this(0, date);
    }

    public Dates(Integer id, Date date) {
        this.id = id;
        this.date = date;
    }

    public Integer id() {
        return id;
    }

    public Date date() {
        return date;
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
