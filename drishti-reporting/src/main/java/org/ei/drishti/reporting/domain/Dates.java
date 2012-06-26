package org.ei.drishti.reporting.domain;

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
        this.date = date;
    }

    public Integer id() {
        return id;
    }

    public Date date() {
        return date;
    }
}
