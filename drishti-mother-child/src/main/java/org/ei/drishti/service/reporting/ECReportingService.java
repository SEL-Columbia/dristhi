package org.ei.drishti.service.reporting;

import org.ei.drishti.common.domain.Indicator;
import org.ei.drishti.common.domain.ReportingData;
import org.ei.drishti.domain.EligibleCouple;
import org.ei.drishti.domain.Location;
import org.ei.drishti.repository.AllEligibleCouples;
import org.ei.drishti.util.SafeMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static org.ei.drishti.common.AllConstants.FamilyPlanningFormFields.CURRENT_FP_METHOD_FIELD_NAME;
import static org.ei.drishti.common.AllConstants.FamilyPlanningFormFields.FP_METHOD_CHANGE_DATE_FIELD_NAME;
import static org.ei.drishti.common.AllConstants.CommonFormFields.ID;

@Service
public class ECReportingService {
    private ReportingService service;
    private AllEligibleCouples allEligibleCouples;

    @Autowired
    public ECReportingService(ReportingService service, AllEligibleCouples allEligibleCouples) {
        this.service = service;
        this.allEligibleCouples = allEligibleCouples;
    }

    public void registerEC(SafeMap reportData) {
        EligibleCouple couple = allEligibleCouples.findByCaseId(reportData.get(ID));
        reportFPMethod(reportData, couple);
    }

    public void fpChange(SafeMap reportData) {
        EligibleCouple couple = allEligibleCouples.findByCaseId(reportData.get(ID));
        reportFPMethod(reportData, couple);
    }

    private void reportFPMethod(SafeMap reportData, EligibleCouple ec) {
        Indicator indicator = Indicator.from(reportData.get(CURRENT_FP_METHOD_FIELD_NAME));
        if (indicator == null) {
            return;
        }

        ReportingData serviceProvidedData = ReportingData.serviceProvidedData(ec.anmIdentifier(), ec.ecNumber(),
                indicator, reportData.get(FP_METHOD_CHANGE_DATE_FIELD_NAME), new Location(ec.village(), ec.subCenter(), ec.phc()));
        ReportingData anmReportData = ReportingData.anmReportData(ec.anmIdentifier(), reportData.get(ID),
                indicator, reportData.get(FP_METHOD_CHANGE_DATE_FIELD_NAME));

        service.sendReportData(serviceProvidedData);
        service.sendReportData(anmReportData);
    }
}
