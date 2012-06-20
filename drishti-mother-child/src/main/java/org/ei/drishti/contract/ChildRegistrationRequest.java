package org.ei.drishti.contract;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.joda.time.LocalDate;
import org.motechproject.util.DateUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class ChildRegistrationRequest {
    private String childName;
    private String caseId;
    private String village;
    private String anmIdentifier;
    private String thaayiCardNumber = "1234567";
    private String immunizationsProvided;
    private Date dateOfBirth = DateUtil.today().toDate();

    public ChildRegistrationRequest(String caseId, String childName, String village, String thaayiCardNumber, Date dateOfBirth, String anmIdentifier, String immunizationsProvided) {
        this.childName = childName;
        this.thaayiCardNumber = thaayiCardNumber;
        this.dateOfBirth = dateOfBirth;
        this.caseId = caseId;
        this.village = village;
        this.anmIdentifier = anmIdentifier;
        this.immunizationsProvided = immunizationsProvided;
    }

    public String name() {
        return childName;
    }

    public String thaayiCardNumber() {
        return thaayiCardNumber;
    }

    public LocalDate dateOfBirth() {
        return dateOfBirth == null ? new LocalDate(dateOfBirth) : LocalDate.fromDateFields(dateOfBirth);
    }

    public String anmIdentifier() {
        return anmIdentifier;
    }

    public String caseId() {
        return caseId;
    }

    public List<String> immunizationsProvided() {
        return new ArrayList<>(Arrays.asList(immunizationsProvided.split(" ")));
    }

    public boolean isImmunizationProvided(String checkForThisImmunization) {
        return (" " + immunizationsProvided + " ").contains(" " + checkForThisImmunization + " ");
    }

    public String village() {
        return village;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
