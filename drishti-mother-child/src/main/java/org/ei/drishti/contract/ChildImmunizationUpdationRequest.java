package org.ei.drishti.contract;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

public class ChildImmunizationUpdationRequest {
    String caseId;
    String anmIdentifier;
    String immunizationsProvided;

    public ChildImmunizationUpdationRequest(String caseId, String anmIdentifier, String immunizationsProvided) {
        this.caseId = caseId;
        this.anmIdentifier = anmIdentifier;
        this.immunizationsProvided = immunizationsProvided;
    }

    public boolean isImmunizationProvided(String checkForThisImmunization) {
        return (" " + immunizationsProvided + " ").contains(" " + checkForThisImmunization + " ");
    }

    public String anmIdentifier() {
        return anmIdentifier;
    }

    public String caseId() {
        return caseId;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @Override
    public boolean equals(Object o) {
        return EqualsBuilder.reflectionEquals(this, o);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}
