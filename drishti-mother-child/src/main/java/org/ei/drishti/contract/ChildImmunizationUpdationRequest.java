package org.ei.drishti.contract;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ChildImmunizationUpdationRequest {
    private String caseId;
    private String anmIdentifier;
    private String immunizationsProvided;
    private String vitaminADose;
    private String immunizationDate;

    public ChildImmunizationUpdationRequest(String caseId, String anmIdentifier, String immunizationsProvided, String immunizationDate) {
        this.caseId = caseId;
        this.anmIdentifier = anmIdentifier;
        this.immunizationsProvided = immunizationsProvided;
        this.immunizationDate = immunizationDate;
    }

    public ChildImmunizationUpdationRequest withVitaminADose(String vitaminADose) {
        this.vitaminADose = vitaminADose;
        return this;
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

    public List<String> immunizationsProvidedList() {
        return new ArrayList<>(Arrays.asList(immunizationsProvided.split(" ")));
    }

    public String immunizationsProvided() {
        return immunizationsProvided;
    }

    public LocalDate immunizationsProvidedDate() {
        return DateTime.parse(immunizationDate).toLocalDate();
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

    public String vitaminADose() {
        return vitaminADose;
    }
}
