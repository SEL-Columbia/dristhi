package org.opensrp.common.domain;

import static org.apache.commons.lang.StringUtils.equalsIgnoreCase;

public enum Caste {
    SC("sc", Indicator.FP_OCP_SC),
    ST("st", Indicator.FP_OCP_ST),
    OTHERS("c_others", Indicator.FP_OCP_CASTE_OTHERS),
    NOT_SPECIFIED("", null);

    private String name;
    private Indicator indicator;

    private Caste(String name, Indicator indicator) {
        this.name = name;
        this.indicator = indicator;
    }

    public static Caste from(String name) {
        Caste[] castes = values();
        for (Caste caste : castes) {
            if (equalsIgnoreCase(caste.name, name)) {
                return caste;
            }
        }
        return NOT_SPECIFIED;
    }

    public Indicator indicator() {
        return indicator;
    }
}
