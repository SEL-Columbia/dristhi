package org.ei.drishti.contract;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class ChildInformation {
    private String caseId;
    private String motherCaseId;
    private String anmIdentifier;
    private String name;
    private String gender;
    private String dateOfDelivery;
    private String immunizationsProvided;
    private Map<String, Map<String, String>> extraData;

    public ChildInformation(String caseId, String motherCaseId, String anmIdentifier, String name, String gender, String dateOfDelivery,
                            String immunizationsProvided, Map<String, Map<String, String>> extraData) {
        this.caseId = caseId;
        this.motherCaseId = motherCaseId;
        this.anmIdentifier = anmIdentifier;
        this.name = name;
        this.gender = gender;
        this.dateOfDelivery = dateOfDelivery;
        this.immunizationsProvided = immunizationsProvided;
        this.extraData = extraData;
    }

    public String caseId() {
        return caseId;
    }

    public String motherCaseId() {
        return motherCaseId;
    }

    public String name() {
        return name;
    }

    public List<String> immunizationsProvided() {
        return new ArrayList<>(Arrays.asList(immunizationsProvided.split(" ")));
    }

    public String gender() {
        return gender;
    }

    public String anmIdentifier() {
        return anmIdentifier;
    }

    public Map<String, String> details() {
        return extraData.get("details");
    }

    public boolean isImmunizationProvided(String checkForThisImmunization) {
        return (" " + immunizationsProvided + " ").contains(" " + checkForThisImmunization + " ");
    }

    public LocalDate dateOfBirth() {
        return LocalDate.parse(dateOfDelivery);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @Override
    public boolean equals(Object o) {
        return EqualsBuilder.reflectionEquals(o, this, false, getClass());
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(17, 37, this, false, getClass());
    }
}
