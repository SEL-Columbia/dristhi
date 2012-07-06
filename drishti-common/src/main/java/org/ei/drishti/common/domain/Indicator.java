package org.ei.drishti.common.domain;

public enum Indicator {
    DPT("DPT"), HEP("HEP"), OPV("OPV"), MEASLES("MEASLES"), BCG("BCG"), ANC_AFTER_12_WEEKS("ANC>12"), ANC_BEFORE_12_WEEKS("ANC<12"), MOTHER_MORTALITY("MORT_M");
    private String value;

    Indicator(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }
}