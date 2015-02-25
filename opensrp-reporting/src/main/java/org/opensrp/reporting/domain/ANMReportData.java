package org.opensrp.reporting.domain;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "anm_report_data")
@NamedQueries({
        @NamedQuery(name = ANMReportData.FIND_BY_ANM_IDENTIFIER_AND_DATE,
                query = "select r from ANMReportData r, ANM a " +
                        "where r.anm=a.id and a.anmIdentifier=:anmIdentifier and r.date >= :date"),
        @NamedQuery(name = ANMReportData.FIND_BY_ANM_IDENTIFIER_WITH_INDICATOR_FOR_MONTH,
                query = "select r from ANMReportData r, ANM a, Indicator i " +
                        "where r.anm=a.id and r.indicator = i.id  and i.indicator = ? and r.date >= ? and r.date < ?"),
        @NamedQuery(name = ANMReportData.FIND_BY_ANM_IDENTIFIER_FOR_REPORTING_MONTH,
                query = "select r from ANMReportData r, ANM a, Indicator i " +
                        "where r.anm=a.id and r.indicator = i.id and a.anmIdentifier = ? and r.date >= ? and r.date < ?"),
        @NamedQuery(name = ANMReportData.FIND_BY_EXTERNAL_IDENTIFIER,
                query = "select r from ANMReportData r where r.externalId = ?")
})
public class ANMReportData {
    public static final String FIND_BY_ANM_IDENTIFIER_AND_DATE = "find.by.anm.identifier.and.date";
    public static final String FIND_BY_ANM_IDENTIFIER_WITH_INDICATOR_FOR_MONTH = "find.by.anm.identifier.with.indicator.for.month";
    public static final String FIND_BY_ANM_IDENTIFIER_FOR_REPORTING_MONTH = "find.by.anm.identifier.for.reporting.month";
    public static final String FIND_BY_EXTERNAL_IDENTIFIER = "find.by.external.identifier";

    public String id() {
        return id.toString();
    }

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "anmIdentifier", insertable = true, updatable = true)
    private ANM anm;

    @Column(name = "externalId")
    private String externalId;

    @Column(name = "date_")
    private Date date;

    @ManyToOne
    @JoinColumn(name = "indicator", insertable = true, updatable = true)
    private Indicator indicator;

    private ANMReportData() {
    }

    public ANMReportData(ANM anm, String externalId, Indicator indicator, Date date) {
        this.anm = anm;
        this.externalId = externalId;
        this.indicator = indicator;
        this.date = date;
    }

    public Indicator indicator() {
        return indicator;
    }

    public Date date() {
        return date;
    }

    public String externalId() {
        return externalId;
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

    public String anmIdentifier() {
        return anm.anmIdentifier();
    }

}
