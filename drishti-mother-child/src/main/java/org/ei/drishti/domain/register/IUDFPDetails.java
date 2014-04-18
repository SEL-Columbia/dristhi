package org.ei.drishti.domain.register;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class IUDFPDetails extends FPDetails {
    private String fpAcceptanceDate;
    private String iudPlace;
    private String lmpDate;
    private String uptResult;

    public IUDFPDetails(String fpAcceptanceDate, String iudPlace, String lmpDate, String uptResult) {
        this.fpAcceptanceDate = fpAcceptanceDate;
        this.iudPlace = iudPlace;
        this.lmpDate = lmpDate;
        this.uptResult = uptResult;
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
