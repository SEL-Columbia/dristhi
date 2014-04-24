package org.ei.drishti.domain.register;

import org.codehaus.jackson.annotate.JsonProperty;

import java.util.List;
import java.util.Map;

public class OCPFPDetails extends RefillableFPDetails {
    @JsonProperty
    private String lmpDate;
    @JsonProperty
    private String uptResult;

    private OCPFPDetails() {
    }

    public OCPFPDetails(String fpAcceptanceDate, List<Map<String, String>> refills, String lmpDate, String uptResult) {
        super(fpAcceptanceDate, refills);
        this.lmpDate = lmpDate;
        this.uptResult = uptResult;
    }

    public OCPFPDetails(String fpAcceptanceDate, List<Map<String, String>> refills) {
        super(fpAcceptanceDate, refills);
    }

    public String lmpDate() {
        return lmpDate;
    }

    public String uptResult() {
        return uptResult;
    }
}
