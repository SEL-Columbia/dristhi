package org.ei.drishti.service;

import org.ei.drishti.common.domain.ReportMonth;
import org.ei.drishti.domain.EligibleCouple;
import org.ei.drishti.domain.register.*;
import org.ei.drishti.mapper.FPRegisterMapper;
import org.ei.drishti.repository.AllEligibleCouples;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class FPRegisterService {
    private final AllEligibleCouples allEligibleCouples;
    private FPRegisterMapper fpRegisterMapper;
    private ReportMonth reportMonth;

    @Autowired
    public FPRegisterService(AllEligibleCouples allEligibleCouples, FPRegisterMapper fpRegisterMapper, ReportMonth reportMonth) {
        this.allEligibleCouples = allEligibleCouples;
        this.fpRegisterMapper = fpRegisterMapper;
        this.reportMonth = reportMonth;
    }

    public FPRegister getRegisterForANM(String anmIdentifier) {
        ArrayList<IUDRegisterEntry> iudRegisterEntries = new ArrayList<>();
        ArrayList<CondomRegisterEntry> condomRegisterEntries = new ArrayList<>();
        ArrayList<OCPRegisterEntry> ocpRegisterEntries = new ArrayList<>();
        ArrayList<MaleSterilizationRegisterEntry> maleSterilizationRegisterEntries = new ArrayList<>();
        ArrayList<FemaleSterilizationRegisterEntry> femaleSterilizationRegisterEntries = new ArrayList<>();

        List<EligibleCouple> ecs = allEligibleCouples.allOpenECsForANM(anmIdentifier);

        for (EligibleCouple ec : ecs) {
            if (ec.iudFPDetails() != null) {
                iudRegisterEntries.addAll(fpRegisterMapper.mapToIUDRegisterEntries(ec));
            }
            if (ec.condomFPDetails() != null) {
                condomRegisterEntries.addAll(fpRegisterMapper.mapToCondomRegisterEntries(ec));
            }
            if (ec.ocpFPDetails() != null) {
                ocpRegisterEntries.addAll(fpRegisterMapper.mapToOCPRegisterEntries(ec));
            }
            if (ec.maleSterilizationFPDetails() != null) {
                maleSterilizationRegisterEntries.addAll(fpRegisterMapper.mapToMaleSterilizationRegistryEntries(ec));
            }
            if (ec.femaleSterilizationFPDetails() != null) {
                femaleSterilizationRegisterEntries.addAll(fpRegisterMapper.mapToFemaleSterilizationRegistryEntries(ec));
            }
        }
        return new FPRegister(iudRegisterEntries, condomRegisterEntries, ocpRegisterEntries,
                maleSterilizationRegisterEntries, femaleSterilizationRegisterEntries, reportMonth.reportingYear());
    }
}
