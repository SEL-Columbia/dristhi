package org.opensrp.service;

import java.util.ArrayList;
import java.util.List;

import org.opensrp.common.domain.ReportMonth;
import org.opensrp.domain.EligibleCouple;
import org.opensrp.domain.register.CondomRegisterEntry;
import org.opensrp.domain.register.FPRegister;
import org.opensrp.domain.register.FemaleSterilizationRegisterEntry;
import org.opensrp.domain.register.IUDRegisterEntry;
import org.opensrp.domain.register.MaleSterilizationRegisterEntry;
import org.opensrp.domain.register.OCPRegisterEntry;
import org.opensrp.mapper.FPRegisterMapper;
import org.opensrp.repository.AllEligibleCouples;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
