package org.ei.drishti.reporting.domain;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "mother_registration")
public class ServiceProvided {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "thaayiCardNumber")
    private String thaayiCardNumber;

    @Column(name = "serviceDate")
    private Date serviceDate;

    @Column(name = "service")
    private String service;

    @Column(name = "anmIdentifier")
    private String anmIdentifier;

    public ServiceProvided(String thaayiCardNumber, Date serviceDate, String service, String anmIdentifier) {
        this.thaayiCardNumber = thaayiCardNumber;
        this.serviceDate = serviceDate;
        this.service = service;
        this.anmIdentifier = anmIdentifier;
    }
}
