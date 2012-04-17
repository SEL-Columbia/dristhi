package org.ei.drishti.contract;

public class ChildImmunizationUpdationRequest {
    String caseId;
    String anmIdentifier;
    String immunizationsProvided;

    public ChildImmunizationUpdationRequest(String caseId, String anmIdentifier, String immunizationsProvided) {
        this.caseId = caseId;
        this.anmIdentifier = anmIdentifier;
        this.immunizationsProvided = immunizationsProvided;
    }

    public boolean isImmunizationProvided(String checkForThisImmunization) {
        return (" " + immunizationsProvided + " ").contains(" " + checkForThisImmunization + " ");
    }

    public String anmIdentifier() {
        return anmIdentifier;
    }

    public String caseId() {
        return caseId;
    }
}
