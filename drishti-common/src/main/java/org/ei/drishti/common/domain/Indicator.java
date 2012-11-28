package org.ei.drishti.common.domain;

import static org.apache.commons.lang.StringUtils.equalsIgnoreCase;

public enum Indicator {
    DPT("DPT"), HEP("HEP"), OPV("OPV"), MEASLES("MEASLES"), BCG("BCG"), ANC_AFTER_12_WEEKS("ANC>12"), ANC_BEFORE_12_WEEKS("ANC<12"), MOTHER_MORTALITY("MORT_M"),
    FP_OCP("OCP"), FP_IUD("IUD"), FP_MALE_STERILIZATION("MALE_STERILIZATION"), FP_FEMALE_STERILIZATION("FEMALE_STERILIZATION"), FP_CONDOM("CONDOM"), FP_DMPA("DMPA"), FP_TRADITIONAL_METHOD("FP_TRADITIONAL"), FP_LAM("LAM"),
    TT("TT");
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
