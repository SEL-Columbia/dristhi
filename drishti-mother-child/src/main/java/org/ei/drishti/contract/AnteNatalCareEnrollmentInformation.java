package org.ei.drishti.contract;

import org.joda.time.LocalDate;

import java.text.MessageFormat;
import java.util.Date;

public class AnteNatalCareEnrollmentInformation {
    private String caseId;
    private String thaayiCardNumber;
    private String name;
    private int age;
    private String anmPhoneNumber;
    private Date lmp;
    private String ecNumber;

    public AnteNatalCareEnrollmentInformation(String caseId, String thaayiCardNumber, String name, String anmPhoneNumber, Date lmp, String ecNumber) {
        this.caseId = caseId;
        this.thaayiCardNumber = thaayiCardNumber;
        this.name = name;
        this.anmPhoneNumber = anmPhoneNumber;
        this.lmp = lmp;
        this.ecNumber = ecNumber;
        this.age = 20;
    }

    public String caseId() {
        return caseId;
    }

    public String thaayiCardNumber() {
        return thaayiCardNumber;
    }

    public String name() {
        return name;
    }

    public String anmPhoneNumber() {
        return anmPhoneNumber;
    }

    public LocalDate lmpDate() {
        return lmp == null ? null : LocalDate.fromDateFields(lmp);
    }

    public String ecNumber() {
        return ecNumber;
    }

    @Override
    public String toString() {
        return MessageFormat.format("Mother: ThaayiCardNumber: {0}, Name: {1}, Case: {2}. LMP: {3}.", thaayiCardNumber, name, caseId, lmp);
    }
}
