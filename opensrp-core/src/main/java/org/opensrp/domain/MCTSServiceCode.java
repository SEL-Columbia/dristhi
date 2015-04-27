package org.opensrp.domain;

import static java.text.MessageFormat.format;

import org.joda.time.LocalDate;

public enum MCTSServiceCode {
    ANC1("ANC1"),
    ANC2("ANC2"),
    ANC3("ANC3"),
    ANC4("ANC4"),
    IFA("IFA"),
    TT_1("TT1"),
    TT_2("TT2"),
    TT_BOOSTER("TTB"),
    ANAEMIA_NORMAL("ANM N"),
    ANAEMIA_MODERATE("ANM M"),
    ANAEMIA_SEVERE("ANM S"),
    COMPLICATION_HYPERTENSIVE("COM H"),
    COMPLICATION_DIABETES("COM D"),
    COMPLICATION_APH("COM A"),
    COMPLICATION_MALARIA("COM M"),
    Complication_None("COM N"),
    RTI_YES("RTI Y"),
    RTI_No("RTI N"),
    PNC_7_DAYS("PNC 7"),
    PNC_48_HOURS("PNC 48"),
    PNC_Complication_None("PNCC N"),
    PNC_COMPLICATION_PPH("PNCC P"),
    PNC_COMPLICATION_SEPSIS("PNCC S"),
    PNC_COMPLICATION_DEATH("PNCC D"),
    PNC_COMPLICATION_OTHERS("PNCC O"),
    PPC_None("PPC N"),
    PPC_STERILIZATION("PPC S"),
    PPC_IUD("PPC I"),
    PPC_INJECTIBLES("PPC J"),
    PNC_CHECKUP_Y("PNCH Y"),
    PNC_Checkup_N("PNCH N"),
    DPT_BOOSTER_1("DPTB"),
    DPT_BOOSTER_2("DT5"),
    JE("JE"),
    OPV1("OPV1"),
    OPV2("OPV2"),
    OPV3("OPV3"),
    OPVB("OPVB"),
    VITA1("VITA1"),
    VITA2("VITA2"),
    VITA3("VITA3"),
    VITA4("VITA4"),
    VITA5("VITA5"),
    VITA6("VITA6"),
    VITA7("VITA7"),
    VITA8("VITA8"),
    VITA9("VITA9"),
    BCG("BCG"),
    OPV0("OPV0"),
    CHILD_DEATH("CD"),
    PENT1("PENT1"),
    PENT2("PENT2"),
    PENT3("PENT3"),
    MEASLES("M1"),
    MEASLES_BOOSTER("M2"),
    MR("MR"),
    MATERNAL_DEATH("MD"),
    HEPB0("HEPB0");

    private final String codeForMCTS;

    MCTSServiceCode(String codeForMCTS) {
        this.codeForMCTS = codeForMCTS;
    }

    public String messageFor(String thayiCardNumber, LocalDate date) {
        return format("ANMPW {0} {1} {2}", thayiCardNumber, codeForMCTS, date.toString("ddMMyy"));
    }
}
