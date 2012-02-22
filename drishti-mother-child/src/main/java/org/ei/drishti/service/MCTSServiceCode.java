package org.ei.drishti.service;

import org.joda.time.LocalDate;

import static java.text.MessageFormat.format;

public enum MCTSServiceCode {
    ANC_1("ANC1"),
    ANC_2("ANC2"),
    ANC_3("ANC3"),
    ANC_4("ANC4"),
    IFA("IFA"),
    TT_1("TT1"),
    TT_2("TT2"),
    TT_Booster("TTB"),
    Anaemia_Normal("ANM N"),
    Anaemia_Moderate("ANM M"),
    Anaemia_Severe("ANM S"),
    Complication_Hypertensive("COM H"),
    Complication_Diabetes("COM D"),
    Complication_APH("COM A"),
    Complication_Malaria("COM M"),
    Complication_None("COM N"),
    RTI_Yes("RTI Y"),
    RTI_No("RTI N"),
    PNC_7_Hours("PNC 7"),
    PNC_48_Hours("PNC 48"),
    PNC_Complication_None("PNCC N"),
    PNC_Complication_PPH("PNCC P"),
    PNC_Complication_Sepsis("PNCC S"),
    PNC_Complication_Death("PNCC D"),
    PNC_Complication_Others("PNCC O"),
    PPC_None("PPC N"),
    PPC_Sterilization("PPC S"),
    PPC_IUD("PPC I"),
    PPC_Injectibles("PPC J"),
    PNC_Checkup_Y("PNCH Y"),
    PNC_Checkup_N("PNCH N");

    private final String codeForMCTS;

    MCTSServiceCode(String codeForMCTS) {
        this.codeForMCTS = codeForMCTS;
    }

    public String messageFor(String thaayiCardNumber, LocalDate date) {
        return format("ANMPW {0} {1} {2}", thaayiCardNumber, codeForMCTS, date.toString("ddMMyy"));
    }
}
