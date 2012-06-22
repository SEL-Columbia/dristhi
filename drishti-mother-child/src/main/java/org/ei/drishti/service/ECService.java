package org.ei.drishti.service;

import org.ei.drishti.contract.EligibleCoupleCloseRequest;
import org.ei.drishti.contract.EligibleCoupleRegistrationRequest;
import org.ei.drishti.domain.EligibleCouple;
import org.ei.drishti.repository.AllEligibleCouples;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ECService {
    private AllEligibleCouples allEligibleCouples;
    private ActionService actionService;

    @Autowired
    public ECService(AllEligibleCouples allEligibleCouples, ActionService actionService) {
        this.allEligibleCouples = allEligibleCouples;
        this.actionService = actionService;
    }

    public void registerEligibleCouple(EligibleCoupleRegistrationRequest request) {
        EligibleCouple couple = new EligibleCouple(request.caseId(), request.ecNumber())
                .withCouple(request.wife(), request.husband()).withANMIdentifier(request.anmIdentifier())
                .withLocation(request.village(), request.subCenter(), request.phc());

        allEligibleCouples.register(couple);

        actionService.registerEligibleCouple(request.caseId(), request.ecNumber(), request.wife(), request.husband(),
                request.anmIdentifier(), request.village(), request.subCenter(), request.phc());
    }

    public void closeEligibleCouple(EligibleCoupleCloseRequest request) {
        allEligibleCouples.close(request.caseId());
        actionService.closeEligibleCouple(request.caseId(), request.anmIdentifier());
    }
}
