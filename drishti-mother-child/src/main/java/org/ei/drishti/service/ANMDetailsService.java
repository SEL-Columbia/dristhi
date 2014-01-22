package org.ei.drishti.service;

import org.ei.drishti.domain.ANMDetail;
import org.ei.drishti.domain.ANMDetails;
import org.ei.drishti.dto.ANMDTO;
import org.ei.drishti.repository.AllChildren;
import org.ei.drishti.repository.AllEligibleCouples;
import org.ei.drishti.repository.AllMothers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static ch.lambdaj.Lambda.collect;
import static ch.lambdaj.Lambda.on;

@Service
public class ANMDetailsService {
    private static Logger logger = LoggerFactory.getLogger(ANMDetailsService.class.toString());
    private final AllChildren allChildren;
    private final AllMothers allMothers;
    private final AllEligibleCouples allEligibleCouples;

    @Autowired
    public ANMDetailsService(AllEligibleCouples allEligibleCouples, AllMothers allMothers, AllChildren allChildren) {
        this.allChildren = allChildren;
        this.allMothers = allMothers;
        this.allEligibleCouples = allEligibleCouples;
    }

    public ANMDetails anmDetails(List<ANMDTO> anms) {
        List<String> anmIdentifiers = collect(anms, on(ANMDTO.class).identifier());
        Map<String, Integer> ecCount = allEligibleCouples.allOpenECs(anmIdentifiers);
        Map<String, Integer> fpCount = allEligibleCouples.fpCountForANM(anmIdentifiers);
        Map<String, Integer> ancCount = allMothers.allOpenMotherCount(anmIdentifiers);
        Map<String, Integer> pncCount = allMothers.allOpenPNCCount(anmIdentifiers);
        Map<String, Integer> childCount = allChildren.openChildCount(anmIdentifiers);
        List<ANMDetail> anmDetails = new ArrayList<>();
        for (ANMDTO anm : anms) {
            int ecCountForANM = ecCount.get(anm.identifier()) == null ? 0 : ecCount.get(anm.identifier());
            int fpCountForANM = fpCount.get(anm.identifier()) == null ? 0 : fpCount.get(anm.identifier());
            int ancCountForANM = ancCount.get(anm.identifier()) == null ? 0 : ancCount.get(anm.identifier());
            int pncCountForANM = pncCount.get(anm.identifier()) == null ? 0 : pncCount.get(anm.identifier());
            int childCountForANM = childCount.get(anm.identifier()) == null ? 0 : childCount.get(anm.identifier());
            anmDetails.add(new ANMDetail(anm.identifier(), anm.name(), anm.location(),
                    ecCountForANM, fpCountForANM, ancCountForANM, pncCountForANM, childCountForANM));
        }
        return new ANMDetails(anmDetails);
    }
}
