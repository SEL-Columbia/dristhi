package org.ei.drishti.reporting.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import javax.persistence.*;

@Entity
@Table(name = "anc_due")

	
@NamedQuery(name = ANCVisitDue.FIND_BY_ENTITY_ID,
					query = "select w from ANCVisitDue w where w.entityid=:entityid")
public class ANCVisitDue {
	//Date date=new Date();
	public static final String FIND_BY_ENTITY_ID = "find.by.entity.id";
	
	
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "entityid")
    private String entityid;
    
    @Column(name = "patientnum")
    private String patientnum;
    
    @Column(name = "anmnum")
    private String anmnum;
    
    @Column(name = "visittype")
    private String visittype;
    
    @Column(name = "visitno")
    private Integer visitno;
    
    @Column(name = "lmpdate")
    private String lmpdate;

    @Column(name = "womenname") 
    private String womenname;
    
    @Column(name = "visitdate")
    private String visitdate;
    
    @Column(name = "anmid")
    private String anmid;
    
    private ANCVisitDue() {
    }

    
    public ANCVisitDue(String entityid,String patientnum,String anmnum,String visittype,Integer visitno,String lmpdate, String womenname,String visitdate,String anmid) {
        
        this.entityid=entityid;
        this.patientnum=patientnum;
        this.anmnum=anmnum;
        this.visittype=visittype;
        this.visitno=visitno;
        this.visitdate=visitdate;
        this.womenname=womenname;
        this.lmpdate=lmpdate;
        this.anmid=anmid;
             
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
    public String patientnum() {
        return patientnum;
    } 
    public void setpatientnum(String patientnum){
    	this.patientnum=patientnum;
    }
    public String anmnum() {
        return anmnum;
    }  
    public String visittype(){
        return visittype;
    }  
     public Integer visitno() {
        return visitno;
     }
     public void setvisitno(Integer visitno) {
         this.visitno=visitno;
      }
     
     public String lmpdate() {
            return lmpdate;
    }
     public void setvisitdate(String visitdate){
    	 this.visitdate=visitdate;
     }
     public String womenName() {
         return womenname;
    }
     public String visitdate() {
         return visitdate;
    }
      public String anmid() {
         return anmid;
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
