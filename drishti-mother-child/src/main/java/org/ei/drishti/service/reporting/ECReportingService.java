package org.ei.drishti.service.reporting;

import org.ei.drishti.common.domain.Indicator;
import org.ei.drishti.common.domain.ReportingData;
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

    public void fpMethodChanged(SafeMap reporting) {
        Indicator indicator = Indicator.from(reporting.get("currentMethod"));
        if (indicator == null){
            return;
        }

        ReportingData data = ReportingData.anmReportData(reporting.get("anmIdentifier"), reporting.get("ecNumber"),
                indicator, reporting.get("familyPlanningMethodChangeDate"));
        service.sendReportData(data);
    }
}
