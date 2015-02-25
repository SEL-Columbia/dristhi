package org.opensrp.reporting.domain;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import javax.persistence.*;

@Entity
@Table(name = "dim_location")
@NamedQueries({
        @NamedQuery(name = Location.FIND_BY_VILLAGE_SUBCENTER_AND_PHC_IDENTIFIER,
                query = "select r from Location r, PHC p where r.phc=p.id and r.village=:village and r.subCenter=:subCenter and p.phcIdentifier=:phcIdentifier"),
        @NamedQuery(name = Location.FIND_BY_ANM_IDENTIFIER,
                query = "select r from Location r, PHC p, SP_ANM a where r.phc=p.id and r.subCenter=a.subCenter and a.anmIdentifier=:anmIdentifier"),
        @NamedQuery(name = Location.FIND_VILLAGES_BY_PHC_AND_SUBCENTER,
                query = "select r from Location r, PHC p where r.phc = p.id and p.phcIdentifier = :phcIdentifier and r.subCenter =:subCenter")
})
public class Location {
    public static final String FIND_BY_VILLAGE_SUBCENTER_AND_PHC_IDENTIFIER = "find.by.village.subcenter.and.phcIdentifier";
    public static final String FIND_BY_ANM_IDENTIFIER = "find.by.anmIdentifier";
    public static final String FIND_VILLAGES_BY_PHC_AND_SUBCENTER = "find.villages.by.phc.and.subcenter";

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "village")
    private String village;

    @Column(name = "subCenter")
    private String subCenter;

    @JoinColumn(name = "phc", insertable = true, updatable = true)
    @ManyToOne
    private PHC phc;

    @Column(name = "taluka")
    private String taluka;

    @Column(name = "district")
    private String district;

    @Column(name = "state")
    private String state;

    private Location() {
    }

    public Location(Integer id, String village, String subCenter, PHC phc, String taluka, String district, String state) {
        this.id = id;
        this.village = village;
        this.subCenter = subCenter;
        this.phc = phc;
        this.taluka = taluka;
        this.district = district;
        this.state = state;
    }

    public Location(String village, String subCenter, PHC phc, String taluka, String district, String state) {
        this(0, village, subCenter, phc, taluka, district, state);
    }

    public Integer id() {
        return id;
    }

    public String village() {
        return village;
    }

    public String subCenter() {
        return subCenter;
    }

    public PHC phc() {
        return phc;
    }

    public String phcName() {
        return phc.name();
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

    public String taluka() {
        return taluka;
    }

    public String district() {
        return district;
    }

    public String state() {
        return state;
    }
}
