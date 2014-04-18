package org.ei.drishti.domain.register;

import java.util.List;
import java.util.Map;

public class OCPFPDetails extends RefillableFPDetails {
    private String lmpDate;
    private String uptResult;

    public OCPFPDetails(String fpAcceptanceDate, List<Map<String, String>> refills, String lmpDate, String uptResult) {
        super(fpAcceptanceDate, refills);
        this.lmpDate = lmpDate;
        this.uptResult = uptResult;
    }

    public String lmpDate() {
        return lmpDate;
    }

    public String uptResult() {
        return uptResult;
    }
}
