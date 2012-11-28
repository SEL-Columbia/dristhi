package org.ei.drishti.service.reporting;

import org.ei.drishti.common.domain.Indicator;
import org.ei.drishti.common.domain.ReportingData;
import org.ei.drishti.contract.AnteNatalCareInformation;
import org.ei.drishti.domain.EligibleCouple;
import org.ei.drishti.domain.Location;
import org.ei.drishti.domain.Mother;
import org.ei.drishti.repository.AllEligibleCouples;
import org.ei.drishti.repository.AllMothers;
import org.ei.drishti.util.SafeMap;
import org.joda.time.LocalDate;
import org.motechproject.util.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static org.ei.drishti.common.domain.Indicator.*;

@Service
public class MotherReportingService {
    public static final int NUMBER_OF_DAYS_IN_12_WEEKS = 84;
    private ReportingService reportingService;
    private AllMothers allMothers;
    private AllEligibleCouples allEligibleCouples;
    private static Logger logger = LoggerFactory.getLogger(ChildReportingService.class.toString());


    @Autowired
    public MotherReportingService(ReportingService reportingService, AllMothers allMothers, AllEligibleCouples allEligibleCouples) {
        this.reportingService = reportingService;
        this.allMothers = allMothers;
        this.allEligibleCouples = allEligibleCouples;
    }

    public void registerANC(SafeMap reportData, String village, String subCenter) {
        boolean isNotWithin12WeeksOfLMP = DateUtil.today().minusDays(NUMBER_OF_DAYS_IN_12_WEEKS).isAfter(LocalDate.parse(reportData.get("lmp")));
        Indicator indicator = isNotWithin12WeeksOfLMP ? Indicator.ANC_AFTER_12_WEEKS : Indicator.ANC_BEFORE_12_WEEKS;

        ReportingData serviceProvidedData = ReportingData.serviceProvidedData(reportData.get("anmIdentifier"), reportData.get("thaayiCardNumber"), indicator, DateUtil.today().toString(),
                new Location(village, subCenter, reportData.get("phc")));
        reportingService.sendReportData(serviceProvidedData);

        ReportingData anmReportData = ReportingData.anmReportData(reportData.get("anmIdentifier"), reportData.get("caseId"), indicator, DateUtil.today().toString());
        reportingService.sendReportData(anmReportData);
    }

    public void closeANC(SafeMap reportData) {
        if (!reportData.get("closeReason").equals("death_of_woman")) {
            return;
        }

        Mother mother = allMothers.findByCaseId(reportData.get("caseId"));
        if (mother == null) {
            logger.warn("Mother Case not found for mother with CaseID " + reportData.get("caseId"));
            return;
        }

        ReportingData serviceProvided = ReportingData.serviceProvidedData(mother.anmIdentifier(), mother.thaayiCardNo(), MOTHER_MORTALITY, DateUtil.today().toString(),
                new Location(mother.village(), mother.subCenter(), mother.phc()));
        reportingService.sendReportData(serviceProvided);

        ReportingData anmReportData = ReportingData.anmReportData(mother.anmIdentifier(), mother.caseId(), MOTHER_MORTALITY, DateUtil.today().toString());
        reportingService.sendReportData(anmReportData);
    }

    public void ttVisitHasHappened(AnteNatalCareInformation ancInformation) {
        Mother mother = allMothers.findByCaseId(ancInformation.caseId());
        if (mother == null) {
            logger.warn("Mother Case not found for mother with CaseID " + ancInformation.caseId());
            return;
        }

        EligibleCouple couple = allEligibleCouples.findByCaseId(mother.ecCaseId());
        if (couple == null) {
            logger.warn("EC Case not found for mother with CaseID " + mother.caseId() + " ec CaseID : " + mother.ecCaseId());
            return;
        }

        ReportingData serviceProvided = ReportingData.serviceProvidedData(ancInformation.anmIdentifier(), mother.thaayiCardNo(), TT, ancInformation.visitDate().toString(),
                couple.location());
        reportingService.sendReportData(serviceProvided);

        ReportingData anmReportData = ReportingData.anmReportData(ancInformation.anmIdentifier(), mother.caseId(), TT, ancInformation.visitDate().toString());
        reportingService.sendReportData(anmReportData);
    }
}
