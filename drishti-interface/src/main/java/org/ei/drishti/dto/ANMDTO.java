package org.ei.drishti.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.codehaus.jackson.annotate.JsonProperty;

public class ANMDTO {
    @JsonProperty
    private String identifier;

    @JsonProperty
    private String name;

    @JsonProperty
    private LocationDTO location;
    
   //adding new variable
   /* @JsonProperty
    private VillagesDTO villages;
*/
    public ANMDTO(String identifier, String name, LocationDTO location) {
        this.identifier = identifier;
        this.name = name;
        this.location = location;
        //this.villages=villages;
    }

    public String identifier() {
        return identifier;
    }

    public String name() {
        return name;
    }

    public LocationDTO location() {
        return location;
    }
    //adding new method
    /*public  VillagesDTO villages(){
    	return villages;
    }*/
    

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
