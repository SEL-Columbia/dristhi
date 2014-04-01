package org.ei.drishti.domain.register;

import java.util.List;
import java.util.Map;

public class FPDetails {
    private String fpAcceptanceDate;
    private List<Map<String, String>> refills;

    public FPDetails(String fpAcceptanceDate, List<Map<String, String>> refills) {
        this.fpAcceptanceDate = fpAcceptanceDate;
        this.refills = refills;
    }

    public String fpAcceptanceDate() {
        return fpAcceptanceDate;
    }

    public List<Map<String, String>> refills() {
        return refills;
    }
}
