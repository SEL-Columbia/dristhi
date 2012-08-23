package org.ei.drishti.service.reporting;

import org.ei.drishti.common.domain.Indicator;
import org.ei.drishti.common.domain.ReportingData;
import org.ei.drishti.domain.Location;
import org.ei.drishti.domain.Mother;
import org.ei.drishti.repository.AllMothers;
import org.ei.drishti.util.SafeMap;
import org.joda.time.LocalDate;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static org.ei.drishti.common.domain.Indicator.*;

@Service
public class MotherReportingService {
    private ReportingService reportingService;
    private AllMothers allMothers;

    @Autowired
    public MotherReportingService(ReportingService reportingService, AllMothers allMothers) {
        this.reportingService = reportingService;
        this.allMothers = allMothers;
    }

    public void registerANC(SafeMap reportData, String village, String subCenter) {
        boolean isNotWithin12WeeksOfLMP = DateUtil.today().minusDays(84).isAfter(LocalDate.parse(reportData.get("lmp")));
        Indicator indicator = isNotWithin12WeeksOfLMP ? Indicator.ANC_AFTER_12_WEEKS : Indicator.ANC_BEFORE_12_WEEKS;

        ReportingData data = ReportingData.serviceProvidedData(reportData.get("anmIdentifier"), reportData.get("thaayiCardNumber"), indicator, DateUtil.today().toString(),
                new Location(village, subCenter, reportData.get("phc")));
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

        ReportingData data = ReportingData.serviceProvidedData(mother.anmIdentifier(), mother.thaayiCardNo(), MOTHER_MORTALITY, DateUtil.today().toString(),
                new Location(mother.village(), mother.subCenter(), mother.phc()));

        reportingService.sendReportData(data);
    }

}
