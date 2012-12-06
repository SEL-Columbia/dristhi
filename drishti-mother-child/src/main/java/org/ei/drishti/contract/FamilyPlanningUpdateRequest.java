package org.ei.drishti.contract;

import org.apache.commons.lang.builder.ToStringBuilder;

public class FamilyPlanningUpdateRequest {
    private String caseId;
    private String anmIdentifier;
    private String fpUpdate;
    private String currentMethod;
    private String familyPlanningMethodChangeDate;

    public FamilyPlanningUpdateRequest(String caseId, String anmIdentifier) {
        this.caseId = caseId;
        this.anmIdentifier = anmIdentifier;
    }

    public String caseId() {
        return caseId;
    }

    public String anmIdentifier() {
        return anmIdentifier;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    public String fpUpdate() {
        return fpUpdate;
    }

    public String currentMethod() {
        return currentMethod;
    }

    public String familyPlanningMethodChangeDate() {
        return familyPlanningMethodChangeDate;
    }

    public FamilyPlanningUpdateRequest withFpUpdate(String familyPlanningUpdate) {
        fpUpdate = familyPlanningUpdate;
        return this;
    }

    public FamilyPlanningUpdateRequest withCurrentMethod(String currentFPMethod) {
        currentMethod = currentFPMethod;
        return this;
    }

    public FamilyPlanningUpdateRequest withFPStartDate(String fpStartDate) {
        familyPlanningMethodChangeDate = fpStartDate;
        return this;
    }
}
