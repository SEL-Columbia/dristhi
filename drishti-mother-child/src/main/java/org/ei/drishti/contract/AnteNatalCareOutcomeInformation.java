package org.ei.drishti.contract;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AnteNatalCareOutcomeInformation {
    private String caseId;
    private String anmIdentifier;
    private String childName;
    private String gender;
    private String immunizationsProvided;
    private String pregnancyOutcome;
    private String dateOfDelivery;

    public AnteNatalCareOutcomeInformation(String caseId, String anmIdentifier, String childName, String gender, String immunizationsProvided,
                                           String pregnancyOutcome, String dateOfDelivery) {
        this.caseId = caseId;
        this.anmIdentifier = anmIdentifier;
        this.childName = childName;
        this.gender = gender;
        this.immunizationsProvided = immunizationsProvided;
        this.pregnancyOutcome = pregnancyOutcome;
        this.dateOfDelivery = dateOfDelivery;
    }

    public String caseId() {
        return caseId;
    }

    public String anmIdentifier() {
        return anmIdentifier;
    }

    public String pregnancyOutcome() {
        return pregnancyOutcome;
    }

    public String childName() {
        return childName;
    }

    public List<String> immunizationsProvided() {
        return new ArrayList<>(Arrays.asList(immunizationsProvided.split(" ")));
    }

    public String gender() {
        return gender;
    }

    public LocalDate dateOfBirth() {
        return LocalDate.parse(dateOfDelivery);
    }

    public boolean isImmunizationProvided(String checkForThisImmunization) {
        return (" " + immunizationsProvided + " ").contains(" " + checkForThisImmunization + " ");
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
