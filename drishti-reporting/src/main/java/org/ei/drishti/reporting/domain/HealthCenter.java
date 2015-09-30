/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

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

/**
 *
 * @author administrator
 */
@Entity
@Table(name = "health_centers_new")
@NamedQuery(name = HealthCenter.FIND_BY_ID,
                query = "select p from HealthCenter p where p.id=:id")

public class HealthCenter {
    public static final String FIND_BY_ID = "find.by.id";
    
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;  
    
    @Column(name = "hospital_name")
    private String hospital_name;
    
    @Column(name = "hospital_type")
    private String hospital_type;
       
    @Column(name = "hospital_address")
    private String hospital_address;

    @Column(name = "parent_hospital")   
    private String parent_hospital;
    
    @Column(name = "villages")
    private String villages;
    
    @Column(name = "country_name")
    private Integer country_name;
    
    @Column(name = "county_name")
    private Integer county_name;
    
    @Column(name = "district_name")
    private Integer district_name;
    
    @Column(name = "subdistrict_name")
    private Integer subdistrict_name;
    
       
  
    private HealthCenter() {
    }

    public HealthCenter(Integer id,String hospital_name, String hospital_type, String hospital_address,String parent_hospital,String villages,
    		Integer country_name,Integer county_name,Integer district_name,Integer subdistrict_name) {
        this.id=id;
        this.hospital_name = hospital_name;
        this.hospital_type = hospital_type;
        this.hospital_address = hospital_address;
        this.parent_hospital = parent_hospital;
        this.villages = villages;
        this.country_name = country_name;
        this.county_name = county_name;
        this.district_name = district_name;
        this.subdistrict_name = subdistrict_name;
        
        
    // logger.info("******anm****** villages****");
    }
    
    public HealthCenter(String hospital_name, String hospital_type, String hospital_address,String parent_hospital,String villages,
    		Integer country_name,Integer county_name,Integer district_name,Integer subdistrict_name) {
        this(0, hospital_name, hospital_type, hospital_address, parent_hospital,villages,country_name,county_name,district_name,subdistrict_name);
    }

    
    public Integer id() {
        return id;
    }
    
    public String hospital_name() {
        return hospital_name;
    }
    
    public String hospital_type() {
        return hospital_type;
    }
    
    public String hospital_address() {
        return hospital_address;
    }
    
    public String parent_hospital() {
        return parent_hospital;
    }
    
    public String villages() {
        return villages;
    }
    
    public Integer country_name() {  
        return country_name;
    }
    
    public Integer county_name() {
        return county_name;
    }
    
    public Integer district_name() {
        return district_name;
    }
    
    public Integer subdistrict_name() {
        return subdistrict_name;
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
