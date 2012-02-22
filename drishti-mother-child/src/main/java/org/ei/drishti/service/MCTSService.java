package org.ei.drishti.service;

import org.ei.drishti.contract.AnteNatalCareCloseInformation;
import org.ei.drishti.contract.AnteNatalCareEnrollmentInformation;
import org.ei.drishti.contract.AnteNatalCareInformation;
import org.ei.drishti.contract.AnteNatalCareOutcomeInformation;
import org.ei.drishti.repository.AllMothers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MCTSService {
    private final MCTSSMSService mctsSMSService;
    private final AllMothers mothers;

    @Autowired
    public MCTSService(MCTSSMSService mctsSMSService, AllMothers mothers) {
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
}
