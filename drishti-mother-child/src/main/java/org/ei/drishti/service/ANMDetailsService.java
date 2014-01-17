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
            List<ANMDetail> anmDetails = new ArrayList<>();
            for (ANMDTO anm : anms) {
                anmDetails.add(anmDetails(anm));
            }
            return new ANMDetails(anmDetails);
    }

    private ANMDetail anmDetails(ANMDTO anm) {
        int ecCount = allEligibleCouples.ecCountForANM(anm.identifier());
        int fpCount = allEligibleCouples.fpCountForANM(anm.identifier());
        int ancCount = allMothers.ancCountForANM(anm.identifier());
        int pncCount = allMothers.pncCountForANM(anm.identifier());
        int childCount = allChildren.childCountForANM(anm.identifier());
        return new ANMDetail(anm.identifier(), anm.name(), anm.location(), ecCount, fpCount, ancCount, pncCount, childCount);
    }
}
