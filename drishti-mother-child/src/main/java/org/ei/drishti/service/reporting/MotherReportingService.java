package org.ei.drishti.service.reporting;

import org.ei.drishti.common.domain.Indicator;
import org.ei.drishti.common.domain.ReportingData;
import org.ei.drishti.domain.Location;
import org.ei.drishti.domain.Mother;
import org.ei.drishti.repository.AllMothers;
import org.ei.drishti.util.SafeMap;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static java.lang.Integer.parseInt;
import static org.ei.drishti.common.AllConstants.ANCCloseCommCareFields.*;
import static org.ei.drishti.common.AllConstants.ANCVisitCommCareFields.*;
import static org.ei.drishti.common.AllConstants.CaseCloseCommCareFields.*;
import static org.ei.drishti.common.AllConstants.CommonCommCareFields.*;
import static org.ei.drishti.common.AllConstants.CommonCommRegisterMotherFields.LMP;
import static org.ei.drishti.common.AllConstants.CommonCommRegisterMotherFields.REGISTRATION_COMMCARE_FIELD_NAME;
import static org.ei.drishti.common.AllConstants.DeliveryOutcomeCommCareFields.*;
import static org.ei.drishti.common.AllConstants.PNCCloseCommCareFields.DEATH_OF_MOTHER_COMMCARE_VALUE;
import static org.ei.drishti.common.domain.Indicator.*;
import static org.ei.drishti.common.domain.ReportingData.anmReportData;
import static org.ei.drishti.common.domain.ReportingData.serviceProvidedData;
import static org.joda.time.LocalDate.parse;

@Service
public class MotherReportingService {
    private static org.slf4j.Logger logger = LoggerFactory.getLogger(MotherReportingService.class.toString());

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

        boolean isRegisteredWithinTwelveWeeks = !(parse(reportData.get(REGISTRATION_COMMCARE_FIELD_NAME)).minusDays(NUMBER_OF_DAYS_IN_12_WEEKS).isAfter(parse(reportData.get(LMP))));
        if (isRegisteredWithinTwelveWeeks) {
            reportToBoth(mother, ANC_BEFORE_12_WEEKS, reportData.get(REGISTRATION_COMMCARE_FIELD_NAME));
        }
    }

    public void ancHasBeenProvided(SafeMap reportData) {
        Mother mother = allMothers.findByCaseId(reportData.get(CASE_ID_COMMCARE_FIELD_NAME));

        reportTTVisit(reportData.get(TT_DOSE_COMMCARE_FIELD), reportData.get(VISIT_DATE_COMMCARE_FIELD), mother);
        reportANC4Visit(reportData, mother);
    }

    public void subsetOfANCHasBeenProvided(SafeMap reportData) {
        Mother mother = allMothers.findByCaseId(reportData.get(CASE_ID_COMMCARE_FIELD_NAME));

        reportTTVisit(reportData.get(TT_DOSE_COMMCARE_FIELD), reportData.get(TT_DATE_COMMCARE_FIELD), mother);
    }

    public void updatePregnancyOutcome(SafeMap reportData) {
        Mother mother = allMothers.findByCaseId(reportData.get(MOTHER_CASE_ID_COMMCARE_FIELD_NAME));
        Indicator indicator = LIVE_BIRTH_COMMCARE_FIELD_VALUE.equals(reportData.get(DELIVERY_OUTCOME_COMMCARE_FIELD_NAME)) ? LIVE_BIRTH : STILL_BIRTH;
        reportToBoth(mother, indicator, reportData.get(DATE_OF_DELIVERY_COMMCARE_FIELD_NAME));

        if (!HOME_COMMCARE_FIELD_VALUE.equals(reportData.get(PLACE_OF_DELIVERY_COMMCARE_FIELD_NAME))) {
            reportToBoth(mother, INSTITUTIONAL_DELIVERY, reportData.get(DATE_OF_DELIVERY_COMMCARE_FIELD_NAME));
        }

        reportToBoth(mother, DELIVERY, reportData.get(DATE_OF_DELIVERY_COMMCARE_FIELD_NAME));

        if (BOOLEAN_FALSE_COMMCARE_VALUE.equals(reportData.get(MOTHER_SURVIVED_COMMCARE_FIELD_NAME)) ||
                BOOLEAN_FALSE_COMMCARE_VALUE.equals(reportData.get(WOMAN_SURVIVED_COMMCARE_FIELD_NAME))) {
            reportDeath(mother, MMD, reportData.get(DATE_OF_DELIVERY_COMMCARE_FIELD_NAME));
        }
    }

    public void closeANC(SafeMap reportData) {
        Mother mother = allMothers.findByCaseId(reportData.get(CASE_ID_COMMCARE_FIELD_NAME));

        if (DEATH_OF_WOMAN_COMMCARE_VALUE.equals(reportData.get(CLOSE_REASON_COMMCARE_FIELD_NAME)) &&
                BOOLEAN_TRUE_COMMCARE_VALUE.equalsIgnoreCase(reportData.get(IS_MATERNAL_LEAVE_COMMCARE_FIELD_NAME))) {
            reportDeath(mother, MMA, reportData.get(DEATH_DATE_COMMCARE_FIELD_NAME));
        } else {
            reportAbortion(reportData, mother);
        }
    }

    public void closePNC(SafeMap reportData) {
        Mother mother = allMothers.findByCaseId(reportData.get(CASE_ID_COMMCARE_FIELD_NAME));

        if (DEATH_OF_MOTHER_COMMCARE_VALUE.equals(reportData.get(CLOSE_REASON_COMMCARE_FIELD_NAME))
                && BOOLEAN_TRUE_COMMCARE_VALUE.equals(reportData.get(IS_MATERNAL_LEAVE_COMMCARE_FIELD_NAME))
                && mother.dateOfDelivery().plusDays(42).isAfter(parse(reportData.get(DEATH_DATE_COMMCARE_FIELD_NAME)))) {
            reportDeath(mother, MMP, reportData.get(DEATH_DATE_COMMCARE_FIELD_NAME));
        }
    }

    private void reportDeath(Mother mother, Indicator indicator, String date) {
        reportToBoth(mother, indicator, date);
        reportToBoth(mother, MOTHER_MORTALITY, date);
    }

    private void reportAbortion(SafeMap reportData, Mother mother) {
        if (SPONTANEOUS_ABORTION_COMMCARE_VALUE.equals(reportData.get(CLOSE_REASON_COMMCARE_FIELD_NAME))) {
            reportToBoth(mother, SPONTANEOUS_ABORTION, reportData.get(CLOSE_SPONTANEOUS_ABORTION_DATE_COMMCARE_FIELD_NAME));
        }

        if (MTP_GREATER_THAN_12_WEEKS_FIELD_NAME.equals(reportData.get(CLOSE_MTP_TIME_COMMCARE_FIELD_NAME))) {
            reportToBoth(mother, MTP_GREATER_THAN_12_WEEKS, reportData.get(CLOSE_MTP_DATE_COMMCARE_FIELD_NAME));
        }
        if (MTP_LESS_THAN_12_WEEKS_FIELD_NAME.equals(reportData.get(CLOSE_MTP_TIME_COMMCARE_FIELD_NAME))) {
            reportToBoth(mother, MTP_LESS_THAN_12_WEEKS, reportData.get(CLOSE_MTP_DATE_COMMCARE_FIELD_NAME));
        }
    }

    private void reportANC4Visit(SafeMap reportData, Mother mother) {
        int visitNumber;
        try {
            visitNumber = parseInt(reportData.get(VISIT_NUMBER_COMMCARE_FIELD));
            if ((visitNumber == ANC4_VISIT_NUMBER_COMMCARE_VALUE)
                    && (!parse(reportData.get(VISIT_DATE_COMMCARE_FIELD)).minusWeeks(36).isBefore(mother.lmp()))) {
                reportToBoth(mother, ANC4, reportData.get(VISIT_DATE_COMMCARE_FIELD));
            }
        } catch (NumberFormatException e) {
            logger.warn("Not reporting ANC visit for mother: " + mother.caseId() + " as visit number is invalid, visit number:" + reportData.get(VISIT_NUMBER_COMMCARE_FIELD));
        }
    }

    private void reportTTVisit(String ttDose, String ttDate, Mother mother) {
        if (TT1_DOSE_COMMCARE_VALUE.equalsIgnoreCase(ttDose)) {
            reportToBoth(mother, TT1, ttDate);
        } else if (TT2_DOSE_COMMCARE_VALUE.equalsIgnoreCase(ttDose)) {
            reportToBoth(mother, TT2, ttDate);
            reportToBoth(mother, SUB_TT, ttDate);
        } else if (TT_BOOSTER_DOSE_COMMCARE_VALUE.equalsIgnoreCase(ttDose)) {
            reportToBoth(mother, TTB, ttDate);
            reportToBoth(mother, SUB_TT, ttDate);
        }
    }

    private void reportToBoth(Mother mother, Indicator indicator, String date) {
        ReportingData serviceProvided = serviceProvidedData(mother.anmIdentifier(), mother.thaayiCardNo(), indicator, date,
                new Location(mother.village(), mother.subCenter(), mother.phc()));
        reportingService.sendReportData(serviceProvided);

        ReportingData anmReportData = anmReportData(mother.anmIdentifier(), mother.caseId(), indicator, date);
        reportingService.sendReportData(anmReportData);
    }
}
