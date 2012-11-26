package org.ei.drishti.service.reporting;

import org.ei.drishti.common.domain.Indicator;
import org.ei.drishti.common.domain.ReportingData;
import org.ei.drishti.domain.Location;
import org.ei.drishti.util.SafeMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ECReportingService {
    private ReportingService service;

    @Autowired
    public ECReportingService(ReportingService service) {
        this.service = service;
    }

    public void fpMethodChangedWithECRegistrationDetails(SafeMap reporting, String village, String subCenter, String phc) {
        report(reporting, reporting.get("ecNumber"), village, subCenter, phc);
    }

    public void fpMethodChangedWithUpdatedECDetails(SafeMap reporting, String ecNumber, String village, String subCenter, String phc) {
        if (!"change_fp_product".equals(reporting.get("fpUpdate"))) {
            return;
        }

        report(reporting, ecNumber, village, subCenter, phc);
    }

    private void report(SafeMap reporting, String ecNumber, String village, String subCenter, String phc) {
        Indicator indicator = Indicator.from(reporting.get("currentMethod"));
        if (indicator == null) {
            return;
        }

        ReportingData serviceProvidedData = ReportingData.serviceProvidedData(reporting.get("anmIdentifier"), ecNumber,
                indicator, reporting.get("familyPlanningMethodChangeDate"), new Location(village, subCenter, phc));
        service.sendReportData(serviceProvidedData);

        ReportingData anmReportData = ReportingData.anmReportData(reporting.get("anmIdentifier"), reporting.get("caseId"),
                indicator, reporting.get("familyPlanningMethodChangeDate"));
        service.sendReportData(anmReportData);
    }
}
