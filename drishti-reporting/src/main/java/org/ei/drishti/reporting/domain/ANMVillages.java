package org.ei.drishti.reporting.domain;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.*;

@Entity
@Table(name = "user_masters")
@NamedQueries({
        
        @NamedQuery(name = ANMVillages.FIND_BY_USER_ID,
                query = "select r from ANMVillages r where r.user_id=:user_id"),
        @NamedQuery(name = ANMVillages.FIND_PHONENUMBER_BY_USER_ID,
                query = "select r.phone_number from ANMVillages r where r.user_id=:user_id"),

        })

public class ANMVillages {
	private static Logger logger = LoggerFactory
			.getLogger(ANMVillages.class.toString());
	
    public static final String FIND_BY_USER_ID = "find.by.user_id";
    public static final String FIND_PHONENUMBER_BY_USER_ID = "find.phonenumber.by.user_id";
    
    
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;  
    
    @Column(name = "user_role")
    private String user_role;
    
    @Column(name = "user_id")
    private String user_id;
       
    @Column(name = "villages")
    private String villages;

    @Column(name = "name")   
    private String name;
    
    @Column(name = "phone_number")
    private String phone_number;
    
    @Column(name = "country")
    private String country;
    
    @Column(name = "county")
    private String county;
    
    @Column(name = "district")
    private String district;
    
    @Column(name = "subdistrict")
    private String subdistrict;
    
    @Column(name = "subcenter")
    private String subcenter;
    
    @Column(name = "hospital")
    private String hospital;
    
  
    private ANMVillages() {
    }

    public ANMVillages(Integer id,String villages, String user_role, String user_id,String name,String phone_number,
    		String country,String county,String district,String subdistrict,String subcenter,String hospital) {
        this.id=id;
        this.villages = villages;
        this.user_role = user_role;
        this.user_id = user_id;
        this.name = name;
        this.phone_number = phone_number;
        this.country = country;
        this.county = county;
        this.district = district;
        this.subdistrict = subdistrict;
        this.subcenter = subcenter;
        this.hospital = hospital;
        
     logger.info("******anm****** villages****");
    }
    
    public ANMVillages(String villages, String userrole, String user_id, String name,String phone_number,
    		String country,String county,String district,String subdistrict,String subcenter,String hospital) {
        this(0, villages, userrole, user_id, name,phone_number,country,county,district,subdistrict,subcenter,hospital);
    }

    
    public Integer id() {
        return id;
    }
    
    public String user_role() {
        return user_role;
    }
    
    public String user_id() {
        return user_id;
    }
    
    public String villages() {
        return villages;
    }
    
    public String name() {
        return name;
    }
    
    public String phone_number() {
        return phone_number;
    }
    
    public String country() {  
        return country;
    }
    
    public String county() {
        return county;
    }
    
    public String district() {
        return district;
    }
    
    public String subdistrict() {
        return subdistrict;
    }
    
    public String subcenter() {
        return subcenter;
    }
    
    public String hospital() {
        return hospital;
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
