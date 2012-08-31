package org.ei.drishti.contract;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.joda.time.LocalDate;

import java.util.Date;

public class AnteNatalCareEnrollmentInformation {
    private String caseId;
    private String ecCaseId;
    private String thaayiCardNumber;
    private String anmPhoneNumber;
    private String anmIdentifier;
    private Date lmp;

    public AnteNatalCareEnrollmentInformation(String caseId, String ecCaseId, String thaayiCardNumber, String anmPhoneNumber, String anmIdentifier, Date lmp) {
        this.caseId = caseId;
        this.ecCaseId = ecCaseId;
        this.thaayiCardNumber = thaayiCardNumber;
        this.lmp = lmp;
        this.anmPhoneNumber = anmPhoneNumber;
        this.anmIdentifier = anmIdentifier;
    }

    public String caseId() {
        return caseId;
    }

    public String ecCaseId() {
        return ecCaseId;
    }

    public String thaayiCardNumber() {
        return thaayiCardNumber;
    }

    public String anmIdentifier() {
        return anmIdentifier;
    }

    public String anmPhoneNumber() {
        return anmPhoneNumber;
    }

    public LocalDate lmpDate() {
        return lmp == null ? null : LocalDate.fromDateFields(lmp);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
