package org.opensrp.reporting.domain;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "annual_target")
@NamedQuery(name = AnnualTarget.FIND_BY_ANM_AND_INDICATOR_AND_DATE, query = "select r from AnnualTarget r, ANM a, Indicator i where r.anmIdentifier=a.id and r.indicator=i.id and a.anmIdentifier=:anmIdentifier and i.indicator=:indicator and r.startDate<=:reportDate and r.endDate>=:reportDate")
public class AnnualTarget {
    public static final String FIND_BY_ANM_AND_INDICATOR_AND_DATE = "find.by.anm.and.indicator.and.date";

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

    @Column(name = "start_date")
    public Date startDate;

    @Column(name = "end_date")
    public Date endDate;

    private AnnualTarget() {
    }

    public AnnualTarget(Integer anmIdentifier, Integer indicator, String target, Date startDate, Date endDate) {
        this.anmIdentifier = anmIdentifier;
        this.indicator = indicator;
        this.target = target;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public String target() {
        return target;
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
