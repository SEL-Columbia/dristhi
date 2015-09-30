package org.ei.drishti.reporting.domain;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.*;

@Entity
@Table(name = "user_masters_new")
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
    private Integer country;
    
    @Column(name = "county")
    private Integer county;
    
    @Column(name = "district")
    private Integer district;
    
    @Column(name = "subdistrict")
    private Integer subdistrict;
    
    @Column(name = "subcenter")
    private Integer subcenter;
    
    @Column(name = "hospital")
    private Integer hospital;
    
  
    private ANMVillages() {
    }

    public ANMVillages(Integer id,String villages, String user_role, String user_id,String name,String phone_number,
    		Integer country,Integer county,Integer district,Integer subdistrict,Integer subcenter,Integer hospital) {
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
    		Integer country,Integer county,Integer district,Integer subdistrict,Integer subcenter,Integer hospital) {
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
    
    public Integer country() {  
        return country;
    }
    
    public Integer county() {
        return county;
    }
    
    public Integer district() {
        return district;
    }
    
    public Integer subdistrict() {
        return subdistrict;
    }
    
    public Integer subcenter() {
        return subcenter;
    }
    
    public Integer hospital() {
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
