package org.ei.drishti.common.domain;

import static org.apache.commons.lang.StringUtils.equalsIgnoreCase;

public enum Indicator {
    DPT("DPT"), DPT3_OR_OPV3("DPT3_OPV3"), DPT_BOOSTER_OR_OPV_BOOSTER("DPTB_OPVB"), DPT_BOOSTER2("DPT_BOOSTER_2"), HEP("HEP"), OPV("OPV"), MEASLES("MEASLES"), VIT_A_1("VIT_A_1"), VIT_A_2("VIT_A_2"), BCG("BCG"),
    PENT1("PENT1"), PENT2("PENT2"), PENT3("PENT3"), MMR("MMR"), JE("JE"),
    NM("NM"), LNM("LNM"), ENM("ENM"), INFANT_MORTALITY("IM"), CHILD_MORTALITY("UFM"), LBW("LBW"), BF_POST_BIRTH("BF_POST_BIRTH"), WEIGHED_AT_BIRTH("WEIGHED_AT_BIRTH"),
    ANC("ANC"), ANC_BEFORE_12_WEEKS("ANC<12"), ANC4("ANC4"), MOTHER_MORTALITY("MM"), MTP_LESS_THAN_12_WEEKS("MTP<12"), MTP_GREATER_THAN_12_WEEKS("MTP>12"), SPONTANEOUS_ABORTION("SPONTANEOUS_ABORTION"), MMA("MMA"), MMD("MMD"), MMP("MMP"),
    LIVE_BIRTH("LIVE_BIRTH"), STILL_BIRTH("STILL_BIRTH"), DELIVERY("DELIVERY"), INSTITUTIONAL_DELIVERY("INSTITUTIONAL_DELIVERY"), D_HOM("D_HOM"), D_SC("D_SC"), D_PHC("D_PHC"), D_CHC("D_CHC"), D_SDH("D_SDH"), D_DH("D_DH"), D_PRI("D_PRI"),
    CESAREAN("CESAREAN"),
    FP_OCP("OCP"), FP_IUD("IUD"), FP_MALE_STERILIZATION("MALE_STERILIZATION"), FP_FEMALE_STERILIZATION("FEMALE_STERILIZATION"), FP_CONDOM("CONDOM"), FP_DMPA("DMPA"),
    FP_OCP_ST("OCP_ST"), FP_OCP_SC("OCP_SC"), FP_OCP_CASTE_OTHERS("OCP_C_OTHERS"),
    FP_TRADITIONAL_METHOD("FP_TRADITIONAL"), FP_LAM("LAM"), FP_FEMALE_STERILIZATION_APL("FS_APL"), FP_FEMALE_STERILIZATION_BPL("FS_BPL"),
    PNC3("PNC3"),
    TT1("TT1"), TT2("TT2"), TTB("TTB"), SUB_TT("SUB_TT");

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
