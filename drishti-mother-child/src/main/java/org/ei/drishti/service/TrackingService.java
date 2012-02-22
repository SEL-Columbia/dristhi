package org.ei.drishti.service;

import org.ei.drishti.contract.AnteNatalCareCloseInformation;
import org.ei.drishti.contract.AnteNatalCareEnrollmentInformation;
import org.ei.drishti.contract.AnteNatalCareInformation;
import org.ei.drishti.contract.AnteNatalCareOutcomeInformation;
import org.ei.drishti.repository.AllMothers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TrackingService {
    private final MCTSService service;
    private final AllMothers mothers;

    @Autowired
    public TrackingService(MCTSService service, AllMothers mothers) {
        this.service = service;
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
