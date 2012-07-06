package org.ei.drishti.service;

import org.ei.drishti.common.domain.ReportingData;
import org.ei.drishti.domain.Location;
import org.ei.drishti.domain.Mother;
import org.ei.drishti.repository.AllMothers;
import org.ei.drishti.util.SafeMap;
import org.joda.time.LocalDate;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MotherReportingService {
    private ReportingService reportingService;
    private AllMothers allMothers;

    @Autowired
    public MotherReportingService(ReportingService reportingService, AllMothers allMothers) {
        this.reportingService = reportingService;
        this.allMothers = allMothers;
    }

    public void registerANC(SafeMap reportData) {
        boolean isNotWithin12WeeksOfLMP = DateUtil.today().minusDays(84).isAfter(LocalDate.parse(reportData.get("lmp")));
        String indicator = isNotWithin12WeeksOfLMP ? "ANC>12" : "ANC<12";

        ReportingData data = ReportingData.serviceProvidedData(reportData.get("anmIdentifier"), reportData.get("thaayiCardNumber"), indicator, DateUtil.today().toString(),
                new Location(reportData.get("village"), reportData.get("subCenter"), reportData.get("phc")));
        reportingService.sendReportData(data);
    }

    public void closeANC(SafeMap reportData) {
        if (!reportData.get("closeReason").equals("death")) {
            return;
        }

        Mother mother = allMothers.findByCaseId(reportData.get("caseId"));
        if (mother == null) {
            return;
        }

        ReportingData data = ReportingData.serviceProvidedData(mother.anmIdentifier(), mother.thaayiCardNo(), "MORT_M", DateUtil.today().toString(),
                new Location(mother.village(), mother.subCenter(), mother.phc()));

        reportingService.sendReportData(data);
    }
}
