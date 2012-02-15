package org.ei.drishti.contract;

public class ChildRegistrationInformation {
    private String name;
    private String userName;

    public String toString() {
        return "Child: {Name=" + name + ", Registered by: " + userName + "}";
    }
}
