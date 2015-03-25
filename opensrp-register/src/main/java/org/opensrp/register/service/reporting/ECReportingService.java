package org.opensrp.register.service.reporting;

import org.opensrp.common.AllConstants;
import org.opensrp.common.domain.*;
import org.opensrp.register.domain.EligibleCouple;
import org.opensrp.util.SafeMap;
import org.joda.time.LocalDate;
import org.opensrp.register.repository.AllEligibleCouples;
import org.opensrp.service.reporting.ReportingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.opensrp.common.AllConstants.CommonFormFields.ID;
import static org.opensrp.common.AllConstants.CommonFormFields.SUBMISSION_DATE_FIELD_NAME;
import static org.opensrp.common.AllConstants.ECRegistrationFields.CASTE;
import static org.opensrp.common.AllConstants.ECRegistrationFields.ECONOMIC_STATUS;
import static org.opensrp.common.AllConstants.FamilyPlanningFormFields.*;
import static org.opensrp.common.domain.ReportDataDeleteRequest.anmReportDataDeleteRequest;
import static org.opensrp.common.domain.ReportDataDeleteRequest.serviceProvidedDataDeleteRequest;

@Service
public class ECReportingService {
    private ReportingService service;
    private AllEligibleCouples allEligibleCouples;
    private final ReportMonth reportMonth;

    @Autowired
    public ECReportingService(ReportingService service, AllEligibleCouples allEligibleCouples, ReportMonth reportMonth) {
        this.service = service;
        this.allEligibleCouples = allEligibleCouples;
        this.reportMonth = reportMonth;
    }

    public void registerEC(SafeMap reportData) {
        EligibleCouple couple = allEligibleCouples.findByCaseId(reportData.get(ID));
        reportIndicator(reportData, couple, Indicator.from(reportData.get(CURRENT_FP_METHOD_FIELD_NAME)),
                reportData.get(FP_METHOD_CHANGE_DATE_FIELD_NAME), reportData.get(SUBMISSION_DATE_FIELD_NAME));
        reportOCPCasteBasedIndicators(reportData, couple, reportData.get(CURRENT_FP_METHOD_FIELD_NAME));
        reportFemaleSterilizationEconomicStatusBasedIndicators(reportData, couple, reportData.get(CURRENT_FP_METHOD_FIELD_NAME));
    }

    public void fpChange(SafeMap reportData) {
        EligibleCouple couple = allEligibleCouples.findByCaseId(reportData.get(ID));
        reportIndicator(reportData, couple, Indicator.from(reportData.get(NEW_FP_METHOD_FIELD_NAME)),
                reportData.get(FP_METHOD_CHANGE_DATE_FIELD_NAME), reportData.get(SUBMISSION_DATE_FIELD_NAME));
        reportOCPCasteBasedIndicators(reportData, couple, reportData.get(NEW_FP_METHOD_FIELD_NAME));
        reportFemaleSterilizationEconomicStatusBasedIndicators(reportData, couple, reportData.get(NEW_FP_METHOD_FIELD_NAME));
    }

    private void reportOCPCasteBasedIndicators(SafeMap reportData, EligibleCouple ec, String fpMethod) {
        if (OCP_FP_METHOD_VALUE.equalsIgnoreCase(fpMethod)) {
            reportIndicator(reportData, ec, Caste.from(reportData.get(CASTE)).indicator(),
                    reportData.get(FP_METHOD_CHANGE_DATE_FIELD_NAME), reportData.get(SUBMISSION_DATE_FIELD_NAME));
        }
    }

    private void reportFemaleSterilizationEconomicStatusBasedIndicators(SafeMap reportData, EligibleCouple couple, String fpMethod) {
        if (FEMALE_STERILIZATION_FP_METHOD_VALUE.equalsIgnoreCase(fpMethod)) {
            reportIndicator(reportData, couple, EconomicStatus.from(reportData.get(ECONOMIC_STATUS)).indicator(),
                    reportData.get(FP_METHOD_CHANGE_DATE_FIELD_NAME), reportData.get(SUBMISSION_DATE_FIELD_NAME));
        }
    }

    public void reportIndicator(SafeMap reportData, EligibleCouple ec, Indicator indicator, String serviceProvidedDate,
                                String submissionDate) {
        if ((!isBlank(serviceProvidedDate) &&
                !reportMonth.areDatesBelongToSameReportingMonth(LocalDate.parse(serviceProvidedDate),
                        LocalDate.parse(submissionDate)))
                || indicator == null)
            return;

        String externalId = ec.ecNumber();
        if (externalId == null) {
            externalId = reportData.get(AllConstants.ReportDataParameters.EXTERNAL_ID);
        }

        ReportingData serviceProvidedData = ReportingData.serviceProvidedData(ec.anmIdentifier(), externalId,
                indicator, serviceProvidedDate, new Location(ec.village(), ec.subCenter(), ec.phc()), ec.caseId());
        ReportingData anmReportData = ReportingData.anmReportData(ec.anmIdentifier(), ec.caseId(), indicator,
                serviceProvidedDate);
        if (reportData.has(AllConstants.ReportDataParameters.QUANTITY)) {
            serviceProvidedData.withQuantity(reportData.get(AllConstants.ReportDataParameters.QUANTITY));
            anmReportData.withQuantity(reportData.get(AllConstants.ReportDataParameters.QUANTITY));
        }

        service.sendReportData(serviceProvidedData);
        service.sendReportData(anmReportData);
    }

    public void deleteReportsForEC(String ecCaseId) {
        service.deleteReportData(serviceProvidedDataDeleteRequest(ecCaseId));
        service.deleteReportData(anmReportDataDeleteRequest(ecCaseId));
    }
}
