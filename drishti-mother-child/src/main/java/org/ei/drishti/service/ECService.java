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

    @Autowired
    public ECService(AllEligibleCouples allEligibleCouples) {
        this.allEligibleCouples = allEligibleCouples;
    }

    public void registerEligibleCouple(EligibleCoupleRegistrationRequest request) {
        EligibleCouple couple = new EligibleCouple(request.caseId(), request.ecNumber()).withCouple(request.wife(), request.husband()).withANMIdentifier(request.anmIdentifier());
        allEligibleCouples.register(couple);
    }

    public void closeEligibleCouple(EligibleCoupleCloseRequest request) {
        allEligibleCouples.close(request.caseId());
    }
}
