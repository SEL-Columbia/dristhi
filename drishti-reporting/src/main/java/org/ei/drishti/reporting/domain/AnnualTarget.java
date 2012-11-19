package org.ei.drishti.reporting.domain;

import javax.persistence.*;

@Entity
@Table(name = "annual_target")
@NamedQuery(name = AnnualTarget.FIND_BY_ANM_AND_INDICATOR, query = "select r from AnnualTarget r, ANM a, Indicator i where r.anmIdentifier=a.id and r.indicator=i.id and a.anmIdentifier=:anmIdentifier and i.indicator=:indicator")
public class AnnualTarget {
    public static final String FIND_BY_ANM_AND_INDICATOR = "find.by.anm.and.indicator";

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "anmIdentifier")
    private Integer anmIdentifier;

    @Column(name = "indicator")
    private Integer indicator;

    @Column(name = "target")
    private String target;

    private AnnualTarget() {
    }

    public AnnualTarget(Integer anmIdentifier, Integer indicator, String target) {
        this.anmIdentifier = anmIdentifier;
        this.indicator = indicator;
        this.target = target;
    }

    public String target() {
        return target;
    }
}
