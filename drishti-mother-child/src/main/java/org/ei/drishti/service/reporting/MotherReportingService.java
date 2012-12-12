package org.ei.drishti.service.reporting;

import org.ei.drishti.common.domain.Indicator;
import org.ei.drishti.common.domain.ReportingData;
import org.ei.drishti.contract.AnteNatalCareInformation;
import org.ei.drishti.domain.Location;
import org.ei.drishti.domain.Mother;
import org.ei.drishti.repository.AllMothers;
import org.ei.drishti.util.SafeMap;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

import static org.ei.drishti.common.AllConstants.ANCCloseCommCareFields.*;
import static org.ei.drishti.common.AllConstants.CommonCommCareFields.CASE_ID_COMMCARE_FIELD_NAME;
import static org.ei.drishti.common.AllConstants.CommonCommRegisterMotherFields.LMP;
import static org.ei.drishti.common.AllConstants.CommonCommRegisterMotherFields.REGISTRATION_COMMCARE_FIELD_NAME;
import static org.ei.drishti.common.AllConstants.DeliveryOutcomeCommCareFields.*;
import static org.ei.drishti.common.domain.Indicator.*;
import static org.ei.drishti.common.domain.ReportingData.anmReportData;
import static org.ei.drishti.common.domain.ReportingData.serviceProvidedData;
import static org.motechproject.util.DateUtil.today;

@Service
public class MotherReportingService {
    public static final int NUMBER_OF_DAYS_IN_12_WEEKS = 84;
    private ReportingService reportingService;
    private AllMothers allMothers;
    private static Logger logger = LoggerFactory.getLogger(ChildReportingService.class);


    @Autowired
    public MotherReportingService(ReportingService reportingService, AllMothers allMothers) {
        this.reportingService = reportingService;
        this.allMothers = allMothers;
    }

    public void registerANC(SafeMap reportData) {
        Mother mother = allMothers.findByCaseId(reportData.get(CASE_ID_COMMCARE_FIELD_NAME));
        reportToBoth(mother, ANC, reportData.get(REGISTRATION_COMMCARE_FIELD_NAME));

        boolean isRegisteredWithinTwelveWeeks = !(LocalDate.parse(reportData.get(REGISTRATION_COMMCARE_FIELD_NAME)).minusDays(NUMBER_OF_DAYS_IN_12_WEEKS).isAfter(LocalDate.parse(reportData.get(LMP))));
        if (isRegisteredWithinTwelveWeeks) {
            reportToBoth(mother, ANC_BEFORE_12_WEEKS, reportData.get(REGISTRATION_COMMCARE_FIELD_NAME));
        }
    }

    public void closeANC(SafeMap reportData) {
        Mother mother = allMothers.findByCaseId(reportData.get(CASE_ID_COMMCARE_FIELD_NAME));

        if (reportData.get(CLOSE_REASON_COMMCARE_FIELD_NAME).equals(DEATH_OF_MOTHER_COMMCARE_VALUE)) {
            reportToBoth(mother, MOTHER_MORTALITY, today().toString());
        }

        if ("greater_12wks".equals(reportData.get(CLOSE_MTP_TIME_COMMCARE_FIELD_NAME))) {
            reportToBoth(mother, MTP_GREATER_THAN_12_WEEKS, reportData.get(CLOSE_MTP_DATE_COMMCARE_FIELD_NAME));
        }
        if ("less_12wks".equals(reportData.get(CLOSE_MTP_TIME_COMMCARE_FIELD_NAME))) {
            reportToBoth(mother, MTP_LESS_THAN_12_WEEKS, reportData.get(CLOSE_MTP_DATE_COMMCARE_FIELD_NAME));
        }
    }

    public void ttVisitHasHappened(AnteNatalCareInformation ancInformation) {
        Mother mother = allMothers.findByCaseId(ancInformation.caseId());
        if (mother == null) {
            logMotherNotFound(ancInformation.caseId());
            return;
        }

        reportToBoth(mother, TT, ancInformation.visitDate().toString());
    }

    public void updatePregnancyOutcome(Map<String, String> reportData) {
        Mother mother = allMothers.findByCaseId(reportData.get(MOTHER_CASE_ID_COMMCARE_FIELD_NAME));
        Indicator indicator;

        if (LIVE_BIRTH_COMMCARE_FIELD_VALUE.equals(reportData.get(DELIVERY_OUTCOME_COMMCARE_FIELD_NAME)))
            indicator = LIVE_BIRTH;
        else
            indicator = STILL_BIRTH;
        reportToBoth(mother, indicator, reportData.get(DATE_OF_DELIVERY_COMMCARE_FIELD_NAME));
    }

    private void reportToBoth(Mother mother, Indicator indicator, String date) {
        ReportingData serviceProvided = serviceProvidedData(mother.anmIdentifier(), mother.thaayiCardNo(), indicator, date,
                new Location(mother.village(), mother.subCenter(), mother.phc()));
        reportingService.sendReportData(serviceProvided);

        ReportingData anmReportData = anmReportData(mother.anmIdentifier(), mother.caseId(), indicator, date);
        reportingService.sendReportData(anmReportData);
    }

    private void logMotherNotFound(String caseId) {
        logger.warn("Mother Case not found for mother with CaseID " + caseId);
    }
}
