package org.ei.drishti.domain;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.codehaus.jackson.annotate.JsonProperty;
import org.ei.drishti.dto.LocationDTO;
import org.ei.drishti.dto.VillagesDTO;

public class ANMDetail {
    @JsonProperty
    private String identifier;

    @JsonProperty
    private String name;

    @JsonProperty
    private LocationDTO location;
    //new change
    /*@JsonProperty
    private VillagesDTO villages;*/

    @JsonProperty
    private int ecCount;

    @JsonProperty
    private int fpCount;

    @JsonProperty
    private int ancCount;

    @JsonProperty
    private int pncCount;

    @JsonProperty
    private int childCount;

    public ANMDetail(String identifier, String name, LocationDTO location, int ecCount, int fpCount, int ancCount, int pncCount, int childCount) {
        this.identifier = identifier;
        this.name = name;
        this.location = location;
        this.ecCount = ecCount;
        this.fpCount = fpCount;
        this.ancCount = ancCount;
        this.pncCount = pncCount;
        this.childCount = childCount;
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
    
    //new change
   /* public  VillagesDTO villages(){
    	return villages;
    }*/
    
    

    public int ecCount() {
        return ecCount;
    }

    public int fpCount() {
        return fpCount;
    }

    public int ancCount() {
        return ancCount;
    }

    public int pncCount() {
        return pncCount;
    }

    public int childCount() {
        return childCount;
    }

    @Override
    public boolean equals(Object o) {
        return EqualsBuilder.reflectionEquals(this, o, "id", "revision");
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this, "id", "revision");
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
