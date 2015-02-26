package org.opensrp.domain.register;

import org.codehaus.jackson.annotate.JsonProperty;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.util.List;
import java.util.Map;

@JsonSerialize
public class OCPFPDetails {
    @JsonProperty
    private String fpAcceptanceDate;
    @JsonProperty
    private List<Map<String, String>> refills;
    @JsonProperty
    private String lmpDate;
    @JsonProperty
    private String uptResult;

    private OCPFPDetails() {
    }

    public OCPFPDetails(String fpAcceptanceDate, List<Map<String, String>> refills, String lmpDate, String uptResult) {
        this.fpAcceptanceDate = fpAcceptanceDate;
        this.refills = refills;
        this.lmpDate = lmpDate;
        this.uptResult = uptResult;
    }

    public String fpAcceptanceDate() {
        return fpAcceptanceDate;
    }

    public List<Map<String, String>> refills() {
        return refills;
    }

    public String lmpDate() {
        return lmpDate;
    }

    public String uptResult() {
        return uptResult;
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
