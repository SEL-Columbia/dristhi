package org.ei.drishti.dto;

public enum AlertPriority {
    upcoming("upcoming"), normal("normal"), urgent("urgent");

    private String value;

    AlertPriority(String value) {
        this.value = value;
    }

    public static AlertPriority from(String alertPriority) {
        return valueOf(alertPriority);
    }

    public String value() {
        return value;
    }
}
