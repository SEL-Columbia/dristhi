package org.ei.drishti.contract;

public class ChildRegistrationRequest {
    private String name;
    private String userName;

    public String toString() {
        return "Child: {Name=" + name + ", Registered by: " + userName + "}";
    }
}
