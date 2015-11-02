package org.ei.drishti.reporting.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

@Entity
@Table(name = "visit_configuration")

	
@NamedQuery(name = VisitConf.FIND_VISIT_CONF,
					query = "select v from VisitConf v")
public class VisitConf {
	
	public static final String FIND_VISIT_CONF = "find.visit.conf";
	
	@Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "anc_visit1_from_week")
    private String anc_visit1_from_week;
    
    @Column(name = "anc_visit2_from_week")
    private String anc_visit2_from_week;
    
    @Column(name = "anc_visit3_from_week")
    private String anc_visit3_from_week;
    
    @Column(name = "anc_visit4_from_week")
    private String anc_visit4_from_week;
   
    private VisitConf() {
    }

    
    public VisitConf(String anc_visit1_from_week,String anc_visit2_from_week,String anc_visit3_from_week,String anc_visit4_from_week) {
        
        this.anc_visit1_from_week=anc_visit1_from_week;
        this.anc_visit2_from_week=anc_visit2_from_week;
        this.anc_visit3_from_week=anc_visit3_from_week;
        this.anc_visit4_from_week=anc_visit4_from_week;
             
    }

    public Integer id() {
        return id;
    }
        
    public String anc_visit1_from_week() {
        return anc_visit1_from_week;
    }   
    public String anc_visit2_from_week() {
        return anc_visit2_from_week;
    }    
    public String anc_visit3_from_week() {
        return anc_visit3_from_week;
    }  
    public String anc_visit4_from_week(){
        return anc_visit4_from_week;
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