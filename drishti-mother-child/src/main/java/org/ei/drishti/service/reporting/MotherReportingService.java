package org.ei.drishti.service.reporting;

import org.ei.drishti.common.AllConstants;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

import static org.ei.drishti.common.AllConstants.CommonCommCareFields.CASE_ID_COMMCARE_FIELD_NAME;
import static org.ei.drishti.common.AllConstants.CommonCommCareFields.THAYI_CARD_NUMBER_COMMCARE_FIELD_NAME;
import static org.ei.drishti.common.AllConstants.CommonCommRegisterMotherFields.*;
import static org.ei.drishti.common.AllConstants.DeliveryOutcomeCommCareFields.*;
import static org.ei.drishti.common.AllConstants.MetaCommCareFields.ANM_IDENTIFIER_COMMCARE_FIELD_NAME;
import static org.ei.drishti.common.domain.Indicator.*;
import static org.ei.drishti.common.domain.ReportingData.anmReportData;
import static org.ei.drishti.common.domain.ReportingData.serviceProvidedData;
import static org.motechproject.util.DateUtil.today;

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
        reportANCRegistration(reportData, village, subCenter, ANC);

        boolean isRegisteredWithinTwelveWeeks = !(LocalDate.parse(reportData.get(REGISTRATION_COMMCARE_FIELD_NAME)).minusDays(NUMBER_OF_DAYS_IN_12_WEEKS).isAfter(LocalDate.parse(reportData.get(LMP))));
        if (isRegisteredWithinTwelveWeeks) {
            reportANCRegistration(reportData, village, subCenter, ANC_BEFORE_12_WEEKS);
        }
    }

    private void reportANCRegistration(SafeMap reportData, String village, String subCenter, Indicator indicator) {
        ReportingData serviceProvidedData = serviceProvidedData(reportData.get(ANM_IDENTIFIER_COMMCARE_FIELD_NAME), reportData.get(THAYI_CARD_NUMBER_COMMCARE_FIELD_NAME), indicator, reportData.get(REGISTRATION_COMMCARE_FIELD_NAME), new Location(village, subCenter, reportData.get(PHC)));
        reportingService.sendReportData(serviceProvidedData);

        ReportingData anmReportData = anmReportData(reportData.get(ANM_IDENTIFIER_COMMCARE_FIELD_NAME), reportData.get(CASE_ID_COMMCARE_FIELD_NAME), indicator, reportData.get(REGISTRATION_COMMCARE_FIELD_NAME));
        reportingService.sendReportData(anmReportData);
    }

    public void closeANC(SafeMap reportData) {
        if (!reportData.get("closeReason").equals("death_of_woman")) {
            return;
        }

        Mother mother = allMothers.findByCaseId(reportData.get("caseId"));
        if (mother == null) {
            logMotherNotFound(reportData.get("caseId"));
            return;
        }

        ReportingData serviceProvided = serviceProvidedData(mother.anmIdentifier(), mother.thaayiCardNo(), MOTHER_MORTALITY, today().toString(),
                new Location(mother.village(), mother.subCenter(), mother.phc()));
        reportingService.sendReportData(serviceProvided);

        ReportingData anmReportData = anmReportData(mother.anmIdentifier(), mother.caseId(), MOTHER_MORTALITY, today().toString());
        reportingService.sendReportData(anmReportData);
    }

    public void ttVisitHasHappened(AnteNatalCareInformation ancInformation) {
        Mother mother = allMothers.findByCaseId(ancInformation.caseId());
        if (mother == null) {
            logMotherNotFound(ancInformation.caseId());
            return;
        }

        EligibleCouple couple = allEligibleCouples.findByCaseId(mother.ecCaseId());
        if (couple == null) {
            logECNotFound(mother);
            return;
        }

        ReportingData serviceProvided = serviceProvidedData(ancInformation.anmIdentifier(), mother.thaayiCardNo(), TT, ancInformation.visitDate().toString(),
                couple.location());
        reportingService.sendReportData(serviceProvided);

        ReportingData anmReportData = anmReportData(ancInformation.anmIdentifier(), mother.caseId(), TT, ancInformation.visitDate().toString());
        reportingService.sendReportData(anmReportData);
    }

    public void updatePregnancyOutcome(Map<String, String> reportData) {
        Mother mother = allMothers.findByCaseId(reportData.get(MOTHER_CASE_ID_COMMCARE_FIELD_NAME));
        if (mother == null) {
            logMotherNotFound(reportData.get(MOTHER_CASE_ID_COMMCARE_FIELD_NAME));
            return;
        }

        EligibleCouple couple = allEligibleCouples.findByCaseId(mother.ecCaseId());
        if (couple == null) {
            logECNotFound(mother);
            return;
        }

        Indicator indicator;
        if (LIVE_BIRTH_COMMCARE_FIELD_VALUE.equals(reportData.get(DELIVERY_OUTCOME_COMMCARE_FIELD_NAME)))
            indicator = LIVE_BIRTH;
        else
            indicator = STILL_BIRTH;

        ReportingData serviceProvided = serviceProvidedData(reportData.get(ANM_IDENTIFIER_COMMCARE_FIELD_NAME), mother.thaayiCardNo(), indicator, reportData.get(DATE_OF_DELIVERY_COMMCARE_FIELD_NAME), couple.location());
        reportingService.sendReportData(serviceProvided);

        ReportingData anmReportData = anmReportData(reportData.get(ANM_IDENTIFIER_COMMCARE_FIELD_NAME), mother.caseId(), indicator, reportData.get(DATE_OF_DELIVERY_COMMCARE_FIELD_NAME));
        reportingService.sendReportData(anmReportData);
    }

    private void logECNotFound(Mother mother) {
        logger.warn("EC Case not found for mother with CaseID " + mother.caseId() + " ec CaseID : " + mother.ecCaseId());
    }

    private void logMotherNotFound(String caseId) {
        logger.warn("Mother Case not found for mother with CaseID " + caseId);
    }
}
