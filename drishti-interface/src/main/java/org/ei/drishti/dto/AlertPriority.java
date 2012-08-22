package org.ei.drishti.dto;

public enum AlertPriority {
    urgent("urgent"),
    normal("normal");
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
