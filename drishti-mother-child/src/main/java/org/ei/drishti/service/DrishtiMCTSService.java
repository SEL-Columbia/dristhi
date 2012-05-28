package org.ei.drishti.service;

import org.ei.drishti.contract.*;
import org.ei.drishti.repository.AllMothers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DrishtiMCTSService {
    private final MCTSSMSService mctsSMSService;
    private final AllMothers mothers;

    @Autowired
    public DrishtiMCTSService(MCTSSMSService mctsSMSService, AllMothers mothers) {
        this.mctsSMSService = mctsSMSService;
        this.mothers = mothers;
    }

    public void registerANCCase(AnteNatalCareEnrollmentInformation enrollmentInformation) {
    }

    public void ancCareHasBeenProvided(AnteNatalCareInformation ancInformation) {
    }

    public void updateANCOutcome(AnteNatalCareOutcomeInformation careOutcomeInformation) {
    }

    public void closeANCCase(AnteNatalCareCloseInformation closeInformation) {
    }

    public void registerChild(ChildRegistrationInformation childInformation) {
    }

    public void registerChild(ChildRegistrationRequest childRegistrationRequest) {
    }

    public void updateChildImmunization(ChildImmunizationUpdationRequest updationRequest) {
    }

    public void closeChildCase(ChildCloseRequest childCloseRequest) {
    }
}
