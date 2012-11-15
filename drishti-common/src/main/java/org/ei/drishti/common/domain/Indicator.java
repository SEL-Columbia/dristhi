package org.ei.drishti.common.domain;

import static org.apache.commons.lang.StringUtils.equalsIgnoreCase;

public enum Indicator {
    DPT("DPT"), HEP("HEP"), OPV("OPV"), MEASLES("MEASLES"), BCG("BCG"), ANC_AFTER_12_WEEKS("ANC>12"), ANC_BEFORE_12_WEEKS("ANC<12"), MOTHER_MORTALITY("MORT_M"),
    OCP("OCP"), IUD("IUD"), MALE_STERILIZATION("male_sterilization"), FEMALE_STERILIZATION("female_sterilization"), CONDOM("CONDOM");
    private String value;

    Indicator(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }

    public static Indicator from(String name) {
        Indicator[] indicators = values();
        for (Indicator indicator : indicators) {
            if (equalsIgnoreCase(indicator.value(), name)) {
                return indicator;
            }
        }
        return null;
    }
}
