package org.ei.drishti.contract;

import java.text.MessageFormat;

public class MotherRegistrationInformation {
    private String caseId;
    private int age;
    private String name;
    private String thaayiCardNumber;

    public MotherRegistrationInformation(String caseId, String thaayiCardNumber, String name) {
        this.caseId = caseId;
        this.thaayiCardNumber = thaayiCardNumber;
        this.name = name;
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

    @Override
    public String toString() {
        return MessageFormat.format("Mother: ThaayiCardNumber: {0}, Name: {1}, Age: {2}", thaayiCardNumber, name, age);
    }
}
