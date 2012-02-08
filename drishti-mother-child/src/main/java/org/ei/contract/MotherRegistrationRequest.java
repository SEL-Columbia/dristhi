package org.ei.contract;

public class MotherRegistrationRequest {
    private int age;
    private String name;
    private String thaayiCardNumber;

    @Override
    public String toString() {
        return "Mother: {age=" + age + ", name='" + name + '\'' + ", thaayiCardNumber='" + thaayiCardNumber + '\'' + '}';
    }

    public String thaayiCardNumber() {
        return thaayiCardNumber;
    }
}
