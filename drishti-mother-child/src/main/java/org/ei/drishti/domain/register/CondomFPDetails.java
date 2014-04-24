package org.ei.drishti.domain.register;

import java.util.List;
import java.util.Map;

public class CondomFPDetails extends RefillableFPDetails {

    private CondomFPDetails() {
    }

    public CondomFPDetails(String fpAcceptanceDate, List<Map<String, String>> refills) {
        super(fpAcceptanceDate, refills);
    }
}
