package org.ei.drishti.service.reporting;

import org.ei.drishti.common.domain.Indicator;
import org.ei.drishti.common.domain.ReportingData;
import org.ei.drishti.domain.EligibleCouple;
import org.ei.drishti.domain.Location;
import org.ei.drishti.repository.AllEligibleCouples;
import org.ei.drishti.util.SafeMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static org.ei.drishti.common.AllConstants.CommonCommCareFields.CASE_ID_COMMCARE_FIELD_NAME;
import static org.ei.drishti.common.AllConstants.FamilyPlanningCommCareFields.*;

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
        EligibleCouple couple = allEligibleCouples.findByCaseId(reportData.get(CASE_ID_COMMCARE_FIELD_NAME));
        reportFPMethod(reportData, couple);
    }

    public void updateFamilyPlanningMethod(SafeMap reportData) {
        if (!FP_METHOD_CHANGED_COMMCARE_FIELD_VALUE.equals(reportData.get(FP_UPDATE_COMMCARE_FIELD_NAME))) {
            return;
        }

        EligibleCouple couple = allEligibleCouples.findByCaseId(reportData.get(CASE_ID_COMMCARE_FIELD_NAME));
        reportFPMethod(reportData, couple);
    }

    private void reportFPMethod(SafeMap reportData, EligibleCouple ec) {
        Indicator indicator = Indicator.from(reportData.get(CURRENT_FP_METHOD_COMMCARE_FIELD_NAME));
        if (indicator == null) {
            return;
        }

        ReportingData serviceProvidedData = ReportingData.serviceProvidedData(ec.anmIdentifier(), ec.ecNumber(),
                indicator, reportData.get(CURRENT_FP_METHOD_CHANGE_DATE_COMMCARE_FIELD_NAME), new Location(ec.village(), ec.subCenter(), ec.phc()));
        service.sendReportData(serviceProvidedData);

        ReportingData anmReportData = ReportingData.anmReportData(ec.anmIdentifier(), reportData.get(CASE_ID_COMMCARE_FIELD_NAME),
                indicator, reportData.get(CURRENT_FP_METHOD_CHANGE_DATE_COMMCARE_FIELD_NAME));
        service.sendReportData(anmReportData);
    }
}
