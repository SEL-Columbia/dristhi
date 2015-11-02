package org.ei.drishti.service.reporting;

import org.apache.commons.lang3.StringUtils;
import org.ei.drishti.common.AllConstants;
import org.ei.drishti.common.domain.Indicator;
import org.ei.drishti.common.domain.ReportMonth;
import org.ei.drishti.common.domain.ReportingData;
import org.ei.drishti.domain.EligibleCouple;
import org.ei.drishti.domain.Location;
import org.ei.drishti.domain.Mother;
import org.ei.drishti.repository.AllEligibleCouples;
import org.ei.drishti.repository.AllMothers;
import org.ei.drishti.util.SafeMap;
import org.joda.time.LocalDate;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

import static java.lang.Integer.parseInt;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.ei.drishti.common.AllConstants.ANCCloseFields.*;
import static org.ei.drishti.common.AllConstants.ANCFormFields.*;
import static org.ei.drishti.common.AllConstants.CommonFormFields.*;
import static org.ei.drishti.common.AllConstants.DeliveryOutcomeFields.*;
import static org.ei.drishti.common.AllConstants.EntityCloseFormFields.*;
import static org.ei.drishti.common.AllConstants.Form.BOOLEAN_FALSE_VALUE;
import static org.ei.drishti.common.AllConstants.Form.BOOLEAN_TRUE_VALUE;
import static org.ei.drishti.common.AllConstants.PNCCloseFields.DEATH_DATE_FIELD_NAME;
import static org.ei.drishti.common.AllConstants.PNCCloseFields.DEATH_OF_MOTHER_VALUE;
import static org.ei.drishti.common.domain.Indicator.*;
import static org.ei.drishti.common.domain.ReportDataDeleteRequest.anmReportDataDeleteRequest;
import static org.ei.drishti.common.domain.ReportDataDeleteRequest.serviceProvidedDataDeleteRequest;
import static org.ei.drishti.common.domain.ReportingData.anmReportData;
import static org.ei.drishti.common.domain.ReportingData.serviceProvidedData;
import static org.joda.time.LocalDate.parse;

@Service
public class MotherReportingService {
    private static org.slf4j.Logger logger = LoggerFactory.getLogger(MotherReportingService.class.toString());
    public static final int NUMBER_OF_DAYS_IN_12_WEEKS = 84;

    public static final int NUMBER_OF_DAYS_IN_PNC_PERIOD = 42;
    private ReportingService reportingService;
    private AllMothers allMothers;
    private AllEligibleCouples allEligibleCouples;
    private final ReportMonth reportMonth;
    private final Map<String, Indicator> placeOfDeliveryToIndicator;


    @Autowired
    public MotherReportingService(ReportingService reportingService, AllMothers allMothers, AllEligibleCouples allEligibleCouples, ReportMonth reportMonth) {
        this.reportingService = reportingService;
        this.allMothers = allMothers;
        this.allEligibleCouples = allEligibleCouples;
        this.reportMonth = reportMonth;

        placeOfDeliveryToIndicator = new HashMap<>();
        placeOfDeliveryToIndicator.put(HOME_FIELD_VALUE, D_HOM);
        placeOfDeliveryToIndicator.put(SUBCENTER_SERVICE_PROVIDED_PLACE_VALUE, D_SC);
        placeOfDeliveryToIndicator.put(PHC_FIELD_VALUE, D_PHC);
        placeOfDeliveryToIndicator.put(CHC_FIELD_VALUE, D_CHC);
        placeOfDeliveryToIndicator.put(SDH_FIELD_VALUE, D_SDH);
        placeOfDeliveryToIndicator.put(DH_FIELD_VALUE, D_DH);
        placeOfDeliveryToIndicator.put(PRIVATE_FACILITY_FIELD_VALUE, D_PRI);
    }

    public void registerANC(SafeMap reportData) {
        Mother mother = allMothers.findByCaseId(reportData.get(MOTHER_ID));
        Location location = loadLocationFromEC(mother);
        reportToBoth(mother, ANC, reportData.get(REGISTRATION_DATE), reportData.get(SUBMISSION_DATE_FIELD_NAME), location);

        boolean isRegisteredWithinTwelveWeeks = !(parse(reportData.get(REGISTRATION_DATE)).minusDays(NUMBER_OF_DAYS_IN_12_WEEKS)
                .isAfter(parse(reportData.get(REFERENCE_DATE))));
        if (isRegisteredWithinTwelveWeeks) {
            reportToBoth(mother, ANC_BEFORE_12_WEEKS, reportData.get(REGISTRATION_DATE), reportData.get(SUBMISSION_DATE_FIELD_NAME), location);
        } else {
            reportToBoth(mother, ANC_AFTER_12_WEEKS, reportData.get(REGISTRATION_DATE), reportData.get(SUBMISSION_DATE_FIELD_NAME), location);
        }
    }

    public void ancVisit(SafeMap reportData) {
        Mother mother = allMothers.findByCaseId(reportData.get(ID));
        Location location = loadLocationFromEC(mother);
        reportANC4Visit(reportData, mother, location);
    }

    public void ttProvided(SafeMap reportData) {
        Mother mother = allMothers.findByCaseId(reportData.get(ID));
        Location location = loadLocationFromEC(mother);
        reportTTVisit(reportData.get(TT_DOSE_FIELD), reportData.get(TT_DATE_FIELD), reportData.get(SUBMISSION_DATE_FIELD_NAME), mother, location);
    }

    public void deliveryOutcome(SafeMap reportData) {
        Mother mother = allMothers.findByCaseId(reportData.get(AllConstants.CommonFormFields.ID));
        Location location = loadLocationFromEC(mother);
        reportPregnancyOutcome(reportData, mother, location);
        reportIfInstitutionalDelivery(reportData, mother, location);
        reportToBoth(mother, DELIVERY, reportData.get(REFERENCE_DATE), reportData.get(SUBMISSION_DATE_FIELD_NAME), location);
        reportMotherMortality(reportData, mother, location);
        reportPlaceOfDelivery(reportData, mother, location);
        reportCesareans(reportData, mother, location);
    }

    private void reportCesareans(SafeMap reportData, Mother mother, Location location) {
        if (CESAREAN_VALUE.equalsIgnoreCase(reportData.get(DELIVERY_TYPE))) {
            String submissionDate = reportData.get(SUBMISSION_DATE_FIELD_NAME);
            String referenceDate = reportData.get(REFERENCE_DATE);
            reportToBoth(mother, CESAREAN, referenceDate, submissionDate, location);
            Indicator cesareanFacilityIndicator =
                    PRIVATE_FACILITY_FIELD_VALUE.equalsIgnoreCase(reportData.get(DELIVERY_PLACE))
                            ? CESAREAN_PRIVATE_FACILITY
                            : CESAREAN_GOVERNMENT_FACILITY;
            reportToBoth(mother, cesareanFacilityIndicator, referenceDate, submissionDate, location);
        }
    }

    public void pncVisitHappened(SafeMap reportData) {
        Mother mother = allMothers.findByCaseId(reportData.get(ID));
        Location location = loadLocationFromEC(mother);
        String thirdVisitDate = mother.thirdPNCVisitDate();
        String visitDate = reportData.get(AllConstants.PNCVisitFormFields.VISIT_DATE_FIELD_NAME);
        if (thirdVisitDate != null && parse(visitDate).isEqual(parse(thirdVisitDate))) {
            reportToBoth(mother, PNC3, visitDate, reportData.get(SUBMISSION_DATE_FIELD_NAME), location);
        }
    }

    public void closeANC(SafeMap reportData) {
        Mother mother = allMothers.findByCaseId(reportData.get(ID));
        Location location = loadLocationFromEC(mother);
        String closeReason = reportData.get(CLOSE_REASON_FIELD_NAME);
        if (WRONG_ENTRY_VALUE.equalsIgnoreCase(closeReason)) {
            deleteReports(reportData.get(ID));
            return;
        }
        if (DEATH_OF_WOMAN_VALUE.equals(closeReason) &&
                BOOLEAN_TRUE_VALUE.equalsIgnoreCase(reportData.get(IS_MATERNAL_LEAVE_FIELD_NAME))) {
            reportDeath(mother, MMA, reportData.get(ANC_DEATH_DATE_FIELD_NAME), reportData.get(SUBMISSION_DATE_FIELD_NAME), location);
            return;
        }
        reportAbortion(reportData, mother, location);
    }

    public void closePNC(SafeMap reportData) {
        Mother mother = allMothers.findByCaseId(reportData.get(ID));
        Location location = loadLocationFromEC(mother);
        String closeReason = reportData.get(CLOSE_REASON_FIELD_NAME);
        if (WRONG_ENTRY_VALUE.equalsIgnoreCase(closeReason)) {
            deleteReports(reportData.get(ID));
            return;
        }
        if (DEATH_OF_MOTHER_VALUE.equals(reportData.get(CLOSE_REASON_FIELD_NAME))
                && BOOLEAN_TRUE_VALUE.equals(reportData.get(IS_MATERNAL_LEAVE_FIELD_NAME))
                && mother.dateOfDelivery().plusDays(NUMBER_OF_DAYS_IN_PNC_PERIOD)
                .isAfter(parse(reportData.get(DEATH_DATE_FIELD_NAME)))) {
            reportDeath(mother, MMP, reportData.get(DEATH_DATE_FIELD_NAME), reportData.get(SUBMISSION_DATE_FIELD_NAME), location);
            return;
        }
    }

    private Location loadLocationFromEC(Mother mother) {
        EligibleCouple couple = allEligibleCouples.findByCaseId(mother.ecCaseId());
        return new Location(couple.village(), couple.subCenter(), couple.phc());
    }

    private void reportIfInstitutionalDelivery(SafeMap reportData, Mother mother, Location location) {
        if (!HOME_FIELD_VALUE.equals(reportData.get(DELIVERY_PLACE))) {
            reportToBoth(mother, INSTITUTIONAL_DELIVERY, reportData.get(REFERENCE_DATE), reportData.get(SUBMISSION_DATE_FIELD_NAME), location);
        }
    }

    private void reportPregnancyOutcome(SafeMap reportData, Mother mother, Location location) {
        String submissionDate = reportData.get(SUBMISSION_DATE_FIELD_NAME);
        Indicator stateIndicator = LIVE_BIRTH_FIELD_VALUE.equals(reportData.get(DELIVERY_OUTCOME)) ? LIVE_BIRTH : STILL_BIRTH;
        reportToBoth(mother, stateIndicator, reportData.get(REFERENCE_DATE), submissionDate, location);

        String deliveryPlace = reportData.get(DELIVERY_PLACE);
        if (HOME_FIELD_VALUE.equalsIgnoreCase(deliveryPlace)
                || SUBCENTER_SERVICE_PROVIDED_PLACE_VALUE.equalsIgnoreCase(deliveryPlace)) {
            Indicator nrhmIndicator = LIVE_BIRTH_FIELD_VALUE.equals(reportData.get(DELIVERY_OUTCOME)) ? NRHM_LIVE_BIRTH : NRHM_STILL_BIRTH;
            reportToBoth(mother, nrhmIndicator, reportData.get(REFERENCE_DATE), submissionDate, location);
        }
    }

    private void reportMotherMortality(SafeMap reportData, Mother mother, Location location) {
        if (BOOLEAN_FALSE_VALUE.equals(reportData.get(DID_WOMAN_SURVIVE)) || BOOLEAN_FALSE_VALUE.equals(reportData.get(DID_MOTHER_SURVIVE))) {
            reportDeath(mother, MMD, reportData.get(REFERENCE_DATE), reportData.get(SUBMISSION_DATE_FIELD_NAME), location);
        }
    }

    private void reportPlaceOfDelivery(SafeMap reportData, Mother mother, Location location) {
        Indicator indicator = placeOfDeliveryToIndicator.get(reportData.get(DELIVERY_PLACE));
        if (indicator != null) {
            reportToBoth(mother, indicator, reportData.get(REFERENCE_DATE), reportData.get(SUBMISSION_DATE_FIELD_NAME), location);
        } else {
            logger.warn("Not reporting: Invalid place of delivery: " + reportData.get(DELIVERY_PLACE) + " for mother: " +
                    mother.caseId());
        }
    }

    private void reportDeath(Mother mother, Indicator indicator, String deathDate, String submissionDate, Location location) {
        reportToBoth(mother, indicator, deathDate, submissionDate, location);
        reportToBoth(mother, MOTHER_MORTALITY, deathDate, submissionDate, location);
    }

    private void reportAbortion(SafeMap reportData, Mother mother, Location location) {
        if (SPONTANEOUS_ABORTION_VALUE.equals(reportData.get(CLOSE_REASON_FIELD_NAME))) {
            reportToBoth(mother, SPONTANEOUS_ABORTION, reportData.get(CLOSE_SPONTANEOUS_ABORTION_DATE_FIELD_NAME), reportData.get(SUBMISSION_DATE_FIELD_NAME), location);
        }

        if (MTP_GREATER_THAN_12_WEEKS_FIELD_NAME.equals(reportData.get(CLOSE_MTP_TIME_FIELD_NAME))) {
            reportToBoth(mother, MTP_GREATER_THAN_12_WEEKS, reportData.get(CLOSE_MTP_DATE_FIELD_NAME), reportData.get(SUBMISSION_DATE_FIELD_NAME), location);
        }
        if (MTP_LESS_THAN_12_WEEKS_FIELD_NAME.equals(reportData.get(CLOSE_MTP_TIME_FIELD_NAME))) {
            reportToBoth(mother, MTP_LESS_THAN_12_WEEKS, reportData.get(CLOSE_MTP_DATE_FIELD_NAME), reportData.get(SUBMISSION_DATE_FIELD_NAME), location);
        }
    }

    private void reportANC4Visit(SafeMap reportData, Mother mother, Location location) {
        try {
            int visitNumber = parseInt(reportData.get(ANC_VISIT_NUMBER_FIELD));
            String ancVisitDate = getFourthAncVisitDate(mother.ancVisits());
            if ((visitNumber == 4 && StringUtils.equalsIgnoreCase(ancVisitDate, reportData.get(ANC_VISIT_DATE_FIELD)))
                    && (!parse(reportData.get(ANC_VISIT_DATE_FIELD)).minusWeeks(36).isBefore(mother.lmp()))) {
                reportToBoth(mother, ANC4, reportData.get(ANC_VISIT_DATE_FIELD), reportData.get(SUBMISSION_DATE_FIELD_NAME), location);
            }
        } catch (NumberFormatException e) {
            logger.warn("Not reporting ANC visit for mother: " + mother.ecCaseId() +
                    " as visit number is invalid, visit number:" + reportData.get(ANC_VISIT_NUMBER_FIELD));
        }
    }

    private String getFourthAncVisitDate(List<Map<String, String>> ancVisits) {
        Collections.sort(ancVisits, new Comparator<Map<String, String>>() {
            @Override
            public int compare(Map<String, String> ancVisit1, Map<String, String> ancVisit2) {
                return LocalDate.parse(ancVisit1.get(ANC_VISIT_DATE_FIELD)).compareTo(LocalDate.parse(ancVisit2.get(ANC_VISIT_DATE_FIELD)));
            }
        });
        return (ancVisits.size() >= 4) ? ancVisits.get(3).get("ancVisitDate") : null;
    }

    private void reportTTVisit(String ttDose, String ttDate, String submissionDate, Mother mother, Location location) {
        if (TT1_DOSE_VALUE.equalsIgnoreCase(ttDose)) {
            reportToBoth(mother, TT1, ttDate, submissionDate, location);
        } else if (TT2_DOSE_VALUE.equalsIgnoreCase(ttDose)) {
            reportToBoth(mother, TT2, ttDate, submissionDate, location);
            reportToBoth(mother, SUB_TT, ttDate, submissionDate, location);
        } else if (TT_BOOSTER_DOSE_VALUE.equalsIgnoreCase(ttDose)) {
            reportToBoth(mother, TTB, ttDate, submissionDate, location);
            reportToBoth(mother, SUB_TT, ttDate, submissionDate, location);
        }
    }

    public void reportToBoth(Mother mother, Indicator indicator, String date, String submissionDate, Location location) {
        if (!isBlank(date) && !reportMonth.areDatesBelongToSameReportingMonth(LocalDate.parse(date), LocalDate.parse(submissionDate)))
            return;
        report(mother, indicator, date, location);
    }

    private void report(Mother mother, Indicator indicator, String date, Location location) {
        ReportingData serviceProvided = serviceProvidedData(mother.anmIdentifier(), mother.thayiCardNumber(), indicator, date, location, mother.caseId());
      //  ReportingData serviceProvided = serviceProvidedData(mother.anmIdentifier(), indicator, date, location, mother.caseId());
        reportingService.sendReportData(serviceProvided);

        ReportingData anmReportData = anmReportData(mother.anmIdentifier(), mother.caseId(), indicator, date);
        reportingService.sendReportData(anmReportData);
    }

    public void pncRegistrationOA(SafeMap reportData) {
        Mother mother = allMothers.findByCaseId(reportData.get(AllConstants.ANCFormFields.MOTHER_ID));
        Location location = loadLocationFromEC(mother);
        reportPregnancyOutcome(reportData, mother, location);
        reportIfInstitutionalDelivery(reportData, mother, location);
        reportToBoth(mother, DELIVERY, reportData.get(REFERENCE_DATE), reportData.get(SUBMISSION_DATE_FIELD_NAME), location);
        reportPlaceOfDelivery(reportData, mother, location);
        reportCesareans(reportData, mother, location);
    }

    public void deleteReports(String motherCaseId) {
        reportingService.deleteReportData(serviceProvidedDataDeleteRequest(motherCaseId));
        reportingService.deleteReportData(anmReportDataDeleteRequest(motherCaseId));
    }
}
