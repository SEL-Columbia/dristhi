package org.opensrp.dto.register;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.codehaus.jackson.annotate.JsonProperty;

import java.util.List;
import java.util.Map;

public class OCPFPDetailsDTO {
    @JsonProperty
    private String fpAcceptanceDate;
    @JsonProperty
    private List<Map<String, String>> refills;
    @JsonProperty
    private String lmpDate;
    @JsonProperty
    private String uptResult;

    public OCPFPDetailsDTO(String fpAcceptanceDate, List<Map<String, String>> refills, String lmpDate, String uptResult) {
        this.fpAcceptanceDate = fpAcceptanceDate;
        this.refills = refills;
        this.lmpDate = lmpDate;
        this.uptResult = uptResult;
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
