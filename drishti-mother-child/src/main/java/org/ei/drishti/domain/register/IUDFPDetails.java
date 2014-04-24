package org.ei.drishti.domain.register;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.codehaus.jackson.annotate.JsonProperty;

public class IUDFPDetails extends FPDetails {
    @JsonProperty
    private String fpAcceptanceDate;
    @JsonProperty
    private String iudPlace;
    @JsonProperty
    private String lmpDate;
    @JsonProperty
    private String uptResult;

    private IUDFPDetails() {
    }

    public IUDFPDetails(String fpAcceptanceDate, String iudPlace, String lmpDate, String uptResult) {
        this.fpAcceptanceDate = fpAcceptanceDate;
        this.iudPlace = iudPlace;
        this.lmpDate = lmpDate;
        this.uptResult = uptResult;
    }

    public IUDFPDetails(String fpAcceptanceDate, String iudPlace) {
        this.fpAcceptanceDate = fpAcceptanceDate;
        this.iudPlace = iudPlace;
    }

    public String fpAcceptanceDate() {
        return fpAcceptanceDate;
    }

    public String iudPlace() {
        return iudPlace;
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
