package org.ei.drishti.domain.register;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class IUDFPDetails extends FPDetails {
    private String fpAcceptanceDate;
    private String iudPlace;

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
