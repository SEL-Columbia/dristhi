package org.ei.drishti.common.domain;

import static org.apache.commons.lang.StringUtils.equalsIgnoreCase;

public enum EconomicStatus {
    BPL("bpl", Indicator.FP_FEMALE_STERILIZATION_BPL),
    APL("apl", Indicator.FP_FEMALE_STERILIZATION_APL),
    NOT_SPECIFIED("", null);

    private String name;
    private Indicator indicator;

    private EconomicStatus(String name, Indicator indicator) {
        this.name = name;
        this.indicator = indicator;
    }

    public static EconomicStatus from(String name) {
        EconomicStatus[] economicStatusValues = values();
        for (EconomicStatus economicStatus : economicStatusValues) {
            if (equalsIgnoreCase(economicStatus.name, name)) {
                return economicStatus;
            }
        }
        return NOT_SPECIFIED;
    }

    public Indicator indicator() {
        return indicator;
    }
}
