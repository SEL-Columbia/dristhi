package org.ei.drishti.domain.register;

import java.util.List;
import java.util.Map;

public class OCPFPDetails extends RefillableFPDetails {

    public OCPFPDetails(String fpAcceptanceDate, List<Map<String, String>> refills) {
        super(fpAcceptanceDate, refills);
    }
}
