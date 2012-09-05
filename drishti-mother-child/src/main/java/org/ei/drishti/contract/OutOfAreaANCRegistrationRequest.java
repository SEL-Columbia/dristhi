package org.ei.drishti.contract;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.joda.time.LocalDate;

public class OutOfAreaANCRegistrationRequest {
    private String caseId;
    private String wifeName;
    private String husbandName;
    private String anmIdentifier;
    private String village;
    private String subCenter;
    private String phc;
    private String thaayiCardNumber;
    private String lmp;
    private String anmPhoneNumber;

    public OutOfAreaANCRegistrationRequest(String caseId, String wifeName, String husbandName, String anmIdentifier, String village, String subCenter, String phc,
                                           String thaayiCardNumber, String lmp, String anmPhoneNumber) {
        this.wifeName = wifeName;
        this.husbandName = husbandName;
        this.caseId = caseId;
        this.anmIdentifier = anmIdentifier;
        this.village = village;
        this.subCenter = subCenter;
        this.phc = phc;
        this.thaayiCardNumber = thaayiCardNumber;
        this.lmp = lmp;
        this.anmPhoneNumber = anmPhoneNumber;
    }

    public String caseId() {
        return caseId;
    }

    public String wife() {
        return wifeName;
    }

    public String husband() {
        return husbandName;
    }

    public String anmIdentifier() {
        return anmIdentifier;
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

    public String thaayiCardNumber() {
        return thaayiCardNumber;
    }

    public String anmPhoneNumber() {
        return anmPhoneNumber;
    }

    public LocalDate lmpDate() {
        return LocalDate.parse(lmp);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
