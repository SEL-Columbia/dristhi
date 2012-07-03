package org.ei.drishti.service;

import org.ei.drishti.common.domain.ReportingData;
import org.ei.drishti.domain.Location;
import org.ei.drishti.util.SafeMap;
import org.joda.time.LocalDate;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class MotherReportingService {
    private ReportingService reportingService;

    @Autowired
    public MotherReportingService(ReportingService reportingService) {
        this.reportingService = reportingService;
    }

    public void registerANC(Map<String, String> reportData) {
        SafeMap safeReportingData = new SafeMap(reportData);
        boolean isNotWithin12WeeksOfLMP = DateUtil.today().minusDays(84).isAfter(LocalDate.parse(safeReportingData.get("lmp")));
        String indicator = isNotWithin12WeeksOfLMP ? "ANC>12" : "ANC<12";

        ReportingData data = ReportingData.serviceProvidedData(safeReportingData.get("anmIdentifier"), safeReportingData.get("thaayiCardNumber"), indicator, DateUtil.today().toString(), new Location(safeReportingData.get("village"),
                safeReportingData.get("subCenter"), safeReportingData.get("phc")));
        reportingService.sendReportData(data);
    }
}
