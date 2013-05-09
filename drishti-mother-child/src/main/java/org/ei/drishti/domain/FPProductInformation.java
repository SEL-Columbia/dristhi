package org.ei.drishti.domain;

import static org.apache.commons.lang3.builder.EqualsBuilder.reflectionEquals;
import static org.apache.commons.lang3.builder.HashCodeBuilder.reflectionHashCode;
import static org.apache.commons.lang3.builder.ToStringBuilder.reflectionToString;

public class FPProductInformation {
    private final String anmId;
    private final String entityId;
    private final String currentMethod;
    private final String dmpaInjectionDate;
    private final String numberOfOCPStripsSupplied;
    private final String ocpRefillDate;
    private final String numberOfCondomsSupplied;
    private final String submissionDate;
    private final String previousFPMethod;
    private final String fpMethodChangeDate;

    public FPProductInformation(String entityId, String anmId, String currentMethod, String previousFPMethod, String dmpaInjectionDate,
                                String numberOfOCPStripsSupplied, String ocpRefillDate, String numberOfCondomsSupplied,
                                String submissionDate, String fpMethodChangeDate) {
        this.anmId = anmId;
        this.entityId = entityId;
        this.currentMethod = currentMethod;
        this.previousFPMethod = previousFPMethod;
        this.dmpaInjectionDate = dmpaInjectionDate;
        this.numberOfOCPStripsSupplied = numberOfOCPStripsSupplied;
        this.ocpRefillDate = ocpRefillDate;
        this.numberOfCondomsSupplied = numberOfCondomsSupplied;
        this.submissionDate = submissionDate;
        this.fpMethodChangeDate = fpMethodChangeDate;
    }

    public String anmId() {
        return anmId;
    }

    public String entityId() {
        return entityId;
    }

    public String currentFPMethod() {
        return currentMethod;
    }

    public String dmpaInjectionDate() {
        return dmpaInjectionDate;
    }

    public String numberOfOCPStripsSupplied() {
        return numberOfOCPStripsSupplied;
    }

    public String ocpRefillDate() {
        return ocpRefillDate;
    }

    public String numberOfCondomsSupplied() {
        return numberOfCondomsSupplied;
    }

    public String submissionDate() {
        return submissionDate;
    }

    public String previousFPMethod() {
        return previousFPMethod;
    }

    public String fpMethodChangeDate() {
        return fpMethodChangeDate;
    }

    @Override
    public boolean equals(Object o) {
        return reflectionEquals(o, this);
    }

    @Override
    public int hashCode() {
        return reflectionHashCode(this);
    }

    @Override
    public String toString() {
        return reflectionToString(this);
    }
}
