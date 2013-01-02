package org.ei.drishti.common.domain;

import static org.apache.commons.lang.StringUtils.equalsIgnoreCase;

public enum Indicator {
    DPT("DPT"), DPT1("DPT_1"), DPT2("DPT_2"), DPT3("DPT_3"), DPT_BOOSTER2("DPT_BOOSTER_2"), HEP("HEP"), OPV("OPV"), MEASLES("MEASLES"), VIT_A_1("VIT_A_1"), VIT_A_2("VIT_A_2"), BCG("BCG"), NM("NM"), LNM("LNM"), ENM("ENM"), INFANT_MORTALITY("IM"), CHILD_MORTALITY("UFM"), LBW("LBW"), BF_POST_BIRTH("BF_POST_BIRTH"), WEIGHED_AT_BIRTH("WEIGHED_AT_BIRTH"),
    ANC("ANC"), ANC_BEFORE_12_WEEKS("ANC<12"), ANC4("ANC4"), MOTHER_MORTALITY("MM"), MTP_LESS_THAN_12_WEEKS("MTP<12"), MTP_GREATER_THAN_12_WEEKS("MTP>12"), SPONTANEOUS_ABORTION("SPONTANEOUS_ABORTION"), MMA("MMA"), MMD("MMD"), MMP("MMP"),
    LIVE_BIRTH("LIVE_BIRTH"), STILL_BIRTH("STILL_BIRTH"), DELIVERY("DELIVERY"), INSTITUTIONAL_DELIVERY("INSTITUTIONAL_DELIVERY"),
    FP_OCP("OCP"), FP_IUD("IUD"), FP_MALE_STERILIZATION("MALE_STERILIZATION"), FP_FEMALE_STERILIZATION("FEMALE_STERILIZATION"), FP_CONDOM("CONDOM"), FP_DMPA("DMPA"),
    FP_TRADITIONAL_METHOD("FP_TRADITIONAL"), FP_LAM("LAM"),
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
