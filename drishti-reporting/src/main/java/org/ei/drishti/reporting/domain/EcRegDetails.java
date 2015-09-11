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
@Table(name = "ec_reg")
@NamedQuery(name = EcRegDetails.FIND_BY_ENTITYID, query = "select e from EcRegDetails e where e.entityid=:entityid")
public class EcRegDetails {

	public static final String FIND_BY_ENTITYID = "find.by.entityid";
	
	@Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "entityid")
    private String entityid;
    
    @Column(name = "phonenumber")
    private String phonenumber;
    
    
    private EcRegDetails() {
    }

    public EcRegDetails(String entityid,String phonenumber) {
        
        this.entityid=entityid;
        this.phonenumber=phonenumber;
       }

    public Integer id() {
        return id;
    }
    public void setentityid(String entityid){
   	 this.entityid=entityid;
    }
    
    public String entityid() {
        return entityid;
    }   
    public String phonenumber() {
        return phonenumber;
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
