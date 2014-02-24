package org.ei.drishti.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.codehaus.jackson.annotate.JsonProperty;

import java.util.List;

public class VillagesDTO {
    @JsonProperty
    private String phcName;

    @JsonProperty
    private String phcIdentifier;

    @JsonProperty
    private String subCenter;

    @JsonProperty
    private List<String> villages;

    public VillagesDTO(String phcName, String phcIdentifier, String subCenter, List<String> villages) {
        this.phcName = phcName;
        this.phcIdentifier = phcIdentifier;
        this.subCenter = subCenter;
        this.villages = villages;
    }

    public String phcName() {
        return phcName;
    }

    public String subCenter() {
        return subCenter;
    }

    public List<String> villages() {
        return villages;
    }

    public String phcIdentifier() {
        return phcIdentifier;
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
