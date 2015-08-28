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
        })

public class ANMVillages {
	private static Logger logger = LoggerFactory
			.getLogger(ANMVillages.class.toString());
	
    public static final String FIND_BY_USER_ID = "find.by.user_id";
    
    
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;  
    
    @Column(name = "userrole")
    private String userrole;
    
    @Column(name = "user_id")
    private String user_id;
       
    @Column(name = "villages")
    private String villages;

    private ANMVillages() {
    }

    public ANMVillages(Integer id,String villages, String userrole, String user_id) {
        this.id=id;
        this.villages = villages;
        this.userrole = userrole;
        this.user_id = user_id;
     logger.info("******anm****** villages****");
    }
    
    public ANMVillages(String villages, String userrole, String user_id) {
        this(0, villages, userrole, user_id);
    }

    
    public Integer id() {
        return id;
    }
    
    public String userrole() {
        return userrole;
    }
    
    public String user_id() {
        return user_id;
    }
    
    public String villages() {
        return villages;
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
