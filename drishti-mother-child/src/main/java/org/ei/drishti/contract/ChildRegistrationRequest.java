package org.ei.drishti.contract;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.joda.time.LocalDate;
import org.motechproject.util.DateUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class ChildRegistrationRequest {
    private String caseId;
    private String childName;
    private String village;
    private String subCenter;
    private String phc;
    private String anmIdentifier;
    private String thaayiCardNumber = "1234567";
    private String immunizationsProvided;
    private Date dateOfBirth = DateUtil.today().toDate();

    public ChildRegistrationRequest(String caseId, String childName, String village, String subCenter, String phc, String thaayiCardNumber, Date dateOfBirth, String anmIdentifier, String immunizationsProvided) {
        this.caseId = caseId;
        this.childName = childName;
        this.village = village;
        this.subCenter = subCenter;
        this.phc = phc;
        this.thaayiCardNumber = thaayiCardNumber;
        this.dateOfBirth = dateOfBirth;
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

    public String subCenter() {
        return subCenter;
    }

    public String phc() {
        return phc;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
