package org.ei.drishti.service.reporting;

import org.ei.drishti.common.domain.Indicator;
import org.ei.drishti.common.domain.ReportingData;
import org.ei.drishti.domain.Location;
import org.ei.drishti.domain.Mother;
import org.ei.drishti.repository.AllMothers;
import org.ei.drishti.util.SafeMap;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static org.ei.drishti.common.AllConstants.ANCCloseCommCareFields.*;
import static org.ei.drishti.common.AllConstants.ANCVisitCommCareFields.VISIT_DATE_COMMCARE_FIELD;
import static org.ei.drishti.common.AllConstants.ANCVisitCommCareFields.WAS_TT_SHOT_PROVIDED;
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

        if (DEATH_OF_MOTHER_COMMCARE_VALUE.equals(reportData.get(CLOSE_REASON_COMMCARE_FIELD_NAME))) {
            reportToBoth(mother, MOTHER_MORTALITY, today().toString());
        }

        if(SPONTANEOUS_ABORTION_COMMCARE_VALUE.equals(reportData.get(CLOSE_REASON_COMMCARE_FIELD_NAME))) {
            reportToBoth(mother, SPONTANEOUS_ABORTION, reportData.get(CLOSE_SPONTANEOUS_ABORTION_DATE_COMMCARE_FIELD_NAME));
        }

        if ("greater_12wks".equals(reportData.get(CLOSE_MTP_TIME_COMMCARE_FIELD_NAME))) {
            reportToBoth(mother, MTP_GREATER_THAN_12_WEEKS, reportData.get(CLOSE_MTP_DATE_COMMCARE_FIELD_NAME));
        }
        if ("less_12wks".equals(reportData.get(CLOSE_MTP_TIME_COMMCARE_FIELD_NAME))) {
            reportToBoth(mother, MTP_LESS_THAN_12_WEEKS, reportData.get(CLOSE_MTP_DATE_COMMCARE_FIELD_NAME));
        }
    }

    public void ancHasBeenProvided(SafeMap reportData) {
        Mother mother = allMothers.findByCaseId(reportData.get(CASE_ID_COMMCARE_FIELD_NAME));

        reportTTVisit(reportData, mother);
    }

    public void updatePregnancyOutcome(SafeMap reportData) {
        Mother mother = allMothers.findByCaseId(reportData.get(MOTHER_CASE_ID_COMMCARE_FIELD_NAME));
        Indicator indicator = LIVE_BIRTH_COMMCARE_FIELD_VALUE.equals(reportData.get(DELIVERY_OUTCOME_COMMCARE_FIELD_NAME)) ? LIVE_BIRTH : STILL_BIRTH;
        reportToBoth(mother, indicator, reportData.get(DATE_OF_DELIVERY_COMMCARE_FIELD_NAME));
    }

    private void reportToBoth(Mother mother, Indicator indicator, String date) {
        ReportingData serviceProvided = serviceProvidedData(mother.anmIdentifier(), mother.thaayiCardNo(), indicator, date,
                new Location(mother.village(), mother.subCenter(), mother.phc()));
        reportingService.sendReportData(serviceProvided);

        ReportingData anmReportData = anmReportData(mother.anmIdentifier(), mother.caseId(), indicator, date);
        reportingService.sendReportData(anmReportData);
    }

    private void reportTTVisit(SafeMap reportData, Mother mother) {
        if (Boolean.parseBoolean(reportData.get(WAS_TT_SHOT_PROVIDED))) {
            reportToBoth(mother, TT, reportData.get(VISIT_DATE_COMMCARE_FIELD));
        }
    }
}
