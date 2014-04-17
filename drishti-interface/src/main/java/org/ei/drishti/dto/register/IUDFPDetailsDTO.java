package org.ei.drishti.dto.register;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.codehaus.jackson.annotate.JsonProperty;

public class IUDFPDetailsDTO {
    @JsonProperty
    private String fpAcceptanceDate;
    @JsonProperty
    private String iudPlace;

    public IUDFPDetailsDTO(String fpAcceptanceDate, String iudPlace) {
        this.fpAcceptanceDate = fpAcceptanceDate;
        this.iudPlace = iudPlace;
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
