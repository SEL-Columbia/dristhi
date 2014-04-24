package org.ei.drishti.domain.register;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.codehaus.jackson.annotate.JsonProperty;

import java.util.List;
import java.util.Map;

public class RefillableFPDetails extends FPDetails {
    @JsonProperty
    private String fpAcceptanceDate;
    @JsonProperty
    private List<Map<String, String>> refills;

    public RefillableFPDetails(String fpAcceptanceDate, List<Map<String, String>> refills) {
        this.fpAcceptanceDate = fpAcceptanceDate;
        this.refills = refills;
    }

    protected RefillableFPDetails() {
    }

    public String fpAcceptanceDate() {
        return fpAcceptanceDate;
    }

    public List<Map<String, String>> refills() {
        return refills;
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
