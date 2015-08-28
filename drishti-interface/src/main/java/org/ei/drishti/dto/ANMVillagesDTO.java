package org.ei.drishti.dto;


import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.codehaus.jackson.annotate.JsonProperty;

import java.util.List;


public class ANMVillagesDTO {
	
	@JsonProperty
    private String user_id;
	
	@JsonProperty
    private String userrole;
	
	 @JsonProperty
	    private List<String> villages;
	 
	 public ANMVillagesDTO(String user_id, String userrole,List<String> villages){
		 this.villages = villages; 
		 this.user_id=user_id;
		 this.userrole=userrole;
	 }
	 

	 public String userrole() {
	        return userrole;
	    }

	 public String user_id() {
	        return user_id;
	    }
	 public List<String> villages() {
	        return villages;
	    }
	 
	 @Override
	    public boolean equals(Object o) {
	        return EqualsBuilder.reflectionEquals(this, o);
	    }

	    @Override
	    public int hashCode() {
	        return HashCodeBuilder.reflectionHashCode(this);
	    }

	    @Override
	    public String toString() {
	        return ToStringBuilder.reflectionToString(this);
	    }

}
