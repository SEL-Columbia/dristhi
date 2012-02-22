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
        throw new RuntimeException("Unsupported.");
    }

    public void ancCareHasBeenProvided(AnteNatalCareInformation ancInformation) {
        throw new RuntimeException("Unsupported.");
    }

    public void updateANCOutcome(AnteNatalCareOutcomeInformation careOutcomeInformation) {
        throw new RuntimeException("Unsupported.");
    }

    public void closeANCCase(AnteNatalCareCloseInformation closeInformation) {
        throw new RuntimeException("Unsupported.");
    }
}
