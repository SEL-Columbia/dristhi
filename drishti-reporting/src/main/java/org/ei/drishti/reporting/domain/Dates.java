package org.ei.drishti.reporting.domain;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "date_")
public class Dates {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "date_")
    private Date date;

    public Dates(Date date) {
        this.date = date;
    }
}
