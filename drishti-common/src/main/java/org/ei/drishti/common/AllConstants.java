package org.ei.drishti.common;

import java.util.List;

import static java.util.Arrays.asList;

public class AllConstants {
    public static class Form {
        public static final String ENTITY_ID = "entityId";
        public static final String ANM_ID = "anmId";
        public static final String FORM_NAME = "formName";
        public static final String INSTANCE_ID = "instanceId";
        public static final String CLIENT_VERSION = "clientVersion";
        public static final String SERVER_VERSION = "serverVersion";
        public static final String EC_REGISTRATION = "ec_registration";
        public static final String FP_COMPLICATIONS = "fp_complications";
        public static final String FP_CHANGE = "fp_change";
        public static final String RENEW_FP_PRODUCT = "renew_fp_product";
        public static final String FP_FOLLOWUP_PRODUCT = "fp_followup";
        public static final String FP_REFERRAL_FOLLOWUP = "fp_referral_followup";
        public static final String EC_CLOSE = "ec_close";
        public static final String ANC_REGISTRATION = "anc_registration";
        public static final String ANC_REGISTRATION_OA = "anc_registration_oa";
        public static final String ANC_VISIT = "anc_visit";
        public static final String ANC_CLOSE = "anc_close";
        public static final String IFA = "ifa";
        public static final String HB_TEST = "hb_test";
        public static final String DELIVERY_OUTCOME = "delivery_outcome";
        public static final String PNC_REGISTRATION_OA = "pnc_registration_oa";
        public static final String PNC_CLOSE = "pnc_close";
        public static final String PNC_VISIT = "pnc_visit";
        public static final String CHILD_REGISTRATION_EC = "child_registration_ec";
        public static final String CHILD_REGISTRATION_OA = "child_registration_oa";
        public static final String CHILD_IMMUNIZATIONS = "child_immunizations";
        public static final String CHILD_ILLNESS = "child_illness";
        public static final String CHILD_CLOSE = "child_close";
        public static final String TT_BOOSTER = "tt_booster";
        public static final String TT_1 = "tt_1";
        public static final String TT_2 = "tt_2";
        public static final String BOOLEAN_TRUE_VALUE = "yes";
        public static final String BOOLEAN_FALSE_VALUE = "no";
        public static final String PNC_REGISTRATION_OA_SUB_FORM_NAME = "child_registration_oa";
        public static final String PNC_VISIT_CHILD_SUB_FORM_NAME = "child_pnc_visit";
        public static final String VITAMIN_A = "vitamin_a";

    }

    public static class Report {
        public static final int REPORTING_MONTH = 3;
        public static final int REPORTING_MONTH_START_DAY = 26;
        public static final double LOW_BIRTH_WEIGHT_THRESHOLD = 2.5;
        public static final int INFANT_MORTALITY_THRESHOLD_IN_YEARS = 1;
        public static final int CHILD_MORTALITY_THRESHOLD_IN_YEARS = 5;
        public static final int CHILD_EARLY_NEONATAL_MORTALITY_THRESHOLD_IN_DAYS = 7;
        public static final int CHILD_NEONATAL_MORTALITY_THRESHOLD_IN_DAYS = 28;
        public static final int CHILD_DIARRHEA_THRESHOLD_IN_YEARS = 5;
    }

    public static class ReportDataParameters {
        public static final String ANM_IDENTIFIER = "anmIdentifier";
        public static final String SERVICE_PROVIDED_DATA_TYPE = "serviceProvided";
        public static final String ANM_REPORT_DATA_TYPE = "anmReportData";
        public static final String SERVICE_PROVIDER_TYPE = "serviceProviderType";
        public static final String EXTERNAL_ID = "externalId";
        public static final String INDICATOR = "indicator";
        public static final String SERVICE_PROVIDED_DATE = "date";
        public static final String VILLAGE = "village";
        public static final String SUB_CENTER = "subCenter";
        public static final String PHC = "phc";
        public static final String QUANTITY = "quantity";
        public static final String SERVICE_PROVIDER_ANM = "ANM";
    }

    public static class ECRegistrationFields {
        public static final String CASTE = "caste";
        public static final String ECONOMIC_STATUS = "economicStatus";
    }

    public static class DeliveryOutcomeFields {
        public static final String DELIVERY_OUTCOME = "deliveryOutcome";
        public static final String DELIVERY_PLACE = "deliveryPlace";
        public static final String DID_WOMAN_SURVIVE = "didWomanSurvive";
        public static final String DID_MOTHER_SURVIVE = "didMotherSurvive";
        public static final String DID_BREAST_FEEDING_START = "didBreastfeedingStart";
        public static final String LIVE_BIRTH_FIELD_VALUE = "live_birth";
        public static final String HOME_FIELD_VALUE = "home";
        public static final String SC_FIELD_VALUE = "subcenter";
        public static final String PHC_FIELD_VALUE = "phc";
        public static final String CHC_FIELD_VALUE = "chc";
        public static final String SDH_FIELD_VALUE = "sdh";
        public static final String DH_FIELD_VALUE = "dh";
        public static final String PRIVATE_FACILITY_FIELD_VALUE = "private_facility";
        public static final String REFERENCE_DATE = "referenceDate";
        public static final String CHILD_REGISTRATION_SUB_FORM_NAME = "child_registration";
        public static final String STILL_BIRTH_VALUE = "still_birth";
        public static final String DELIVERY_TYPE = "deliveryType";
        public static final String CESAREAN_VALUE = "cesarean";

    }

    public static class ANCFormFields {
        public static final String MOTHER_ID = "motherId";
        public static final String REGISTRATION_DATE = "registrationDate";
        public static final String REFERENCE_DATE = "referenceDate";
        public static final String ANC_VISIT_DATE_FIELD = "ancVisitDate";
        public static final String ANC_VISIT_NUMBER_FIELD = "ancVisitNumber";
        public static final String TT1_DOSE_VALUE = "tt1";
        public static final String TT2_DOSE_VALUE = "tt2";
        public static final String TT_BOOSTER_DOSE_VALUE = "ttbooster";
        public static final String TT_DOSE_FIELD = "ttDose";
        public static final String TT_DATE_FIELD = "ttDate";
    }

    public static class HbTestFormFields {
        public static final String HB_TEST_DATE_FIELD = "hbTestDate";
        public static final String ANAEMIC_STATUS_FIELD = "anaemicStatus";
    }

    public static class PNCVisitFormFields {
        public static final String VISIT_DATE_FIELD_NAME = "pncVisitDate";
        public static final String VISIT_NUMBER_FIELD_NAME = "pncVisitNumber";
        public static final String URINE_STOOL_PROBLEMS = "urineStoolProblems";
    }

    public static class CommonFormFields {
        public static final String SUBMISSION_DATE_FIELD_NAME = "submissionDate";
        public static final String ID = "id";
    }

    public static class CommonChildFormFields {
        public static final String DIARRHEA_VALUE = "diarrhea";
        public static final String FEMALE_VALUE = "female";
        public static final String MALE_VALUE = "male";

    }

    public static class ChildRegistrationFormFields {
        public static final String BF_POSTBIRTH = "didBreastfeedingStart";
    }

    public static class ChildCloseFormFields {
        public static final String CLOSE_REASON_FIELD_NAME = "closeReason";
        public static final String DEATH_OF_CHILD_VALUE = "death_of_child";
        public static final String DATE_OF_DEATH_FIELD_NAME = "deathDate";
        public static final String DEATH_CAUSE = "deathCause";
        public static final String PERMANENT_RELOCATION_VALUE = "permanent_relocation";
    }

    public static class EntityCloseFormFields {
        public static final String CLOSE_REASON_FIELD_NAME = "closeReason";
        public static final String ANC_DEATH_DATE_FIELD_NAME = "maternalDeathDate";
        public static final String IS_MATERNAL_LEAVE_FIELD_NAME = "isMaternalDeath";
    }

    public static class ANCCloseFields {
        public static final String CLOSE_MTP_DATE_FIELD_NAME = "dateOfInducedAbortion";
        public static final String CLOSE_SPONTANEOUS_ABORTION_DATE_FIELD_NAME = "dateOfSpontaneousAbortion";
        public static final String CLOSE_MTP_TIME_FIELD_NAME = "timeOfInducedAbortion";
        public static final String MTP_GREATER_THAN_12_WEEKS_FIELD_NAME = "greater_12wks";
        public static final String MTP_LESS_THAN_12_WEEKS_FIELD_NAME = "less_12wks";
        public static final String DEATH_OF_WOMAN_VALUE = "death_of_woman";
        public static final String PERMANENT_RELOCATION_VALUE = "relocation_permanent";
        public static final String SPONTANEOUS_ABORTION_VALUE = "spontaneous_abortion";
    }

    public static class PNCCloseFields {
        public static final String DEATH_DATE_FIELD_NAME = "deathDate";
        public static final String DEATH_OF_MOTHER_VALUE = "death_of_mother";
    }

    public static class ECCloseFields {
        public static final String IS_EC_CLOSE_CONFIRMED_FIELD_NAME = "isECCloseConfirmed";
    }

    public static class FamilyPlanningFormFields {
        public static final String CURRENT_FP_METHOD_FIELD_NAME = "currentMethod";
        public static final String FP_METHOD_CHANGE_DATE_FIELD_NAME = "familyPlanningMethodChangeDate";
        public static final String DMPA_INJECTION_DATE_FIELD_NAME = "dmpaInjectionDate";
        public static final String DMPA_INJECTABLE_FP_METHOD_VALUE = "dmpa_injectable";
        public static final String OCP_REFILL_DATE_FIELD_NAME = "ocpRefillDate";
        public static final String NUMBER_OF_OCP_STRIPS_SUPPLIED_FIELD_NAME = "numberOfOCPDelivered";
        public static final String NUMBER_OF_CONDOMS_SUPPLIED_FIELD_NAME = "numberOfCondomsSupplied";
        public static final String FP_FOLLOWUP_DATE_FIELD_NAME = "fpFollowupDate";
        public static final String OCP_FP_METHOD_VALUE = "ocp";
        public static final String CONDOM_FP_METHOD_VALUE = "condom";
        public static final String FEMALE_STERILIZATION_FP_METHOD_VALUE = "female_sterilization";
        public static final String MALE_STERILIZATION_FP_METHOD_VALUE = "male_sterilization";
        public static final String COMPLICATION_DATE_FIELD_NAME = "complicationDate";
        public static final String NEEDS_FOLLOWUP_FIELD_NAME = "needsFollowup";
        public static final String NEEDS_REFERRAL_FOLLOWUP_FIELD_NAME = "needsReferralFollowup";
        public static final String IUD_FP_METHOD_VALUE = "iud";
        public static final String REFERRAL_FOLLOW_UP_DATE_FIELD_NAME = "referralFollowupDate";
        public static final String PREVIOUS_FP_METHOD_FIELD_NAME = "currentMethod";
        public static final String NEW_FP_METHOD_FIELD_NAME = "newMethod";
    }

    public static class ChildImmunizationFields {
        public static final String IMMUNIZATIONS_GIVEN_FIELD_NAME = "immunizationsGiven";
        public static final String PREVIOUS_IMMUNIZATIONS_FIELD_NAME = "previousImmunizations";
        public static final String IMMUNIZATION_DATE_FIELD_NAME = "immunizationDate";

        public static final String BCG_VALUE = "bcg";

        public static final String DPT_BOOSTER_1_VALUE = "dptbooster_1";
        public static final String DPT_BOOSTER_2_VALUE = "dptbooster_2";

        public static final String HEPATITIS_0_VALUE = "hepb_0";

        public static final String MEASLES_VALUE = "measles";
        public static final String MEASLES_BOOSTER_VALUE = "measlesbooster";

        public static final String OPV_0_VALUE = "opv_0";
        public static final String OPV_1_VALUE = "opv_1";
        public static final String OPV_2_VALUE = "opv_2";
        public static final String OPV_3_VALUE = "opv_3";
        public static final String OPV_BOOSTER_VALUE = "opvbooster";

        public static final String PENTAVALENT_1_VALUE = "pentavalent_1";
        public static final String PENTAVALENT_2_VALUE = "pentavalent_2";
        public static final String PENTAVALENT_3_VALUE = "pentavalent_3";

        public static final String MMR_VALUE = "mmr";
        public static final String JE_VALUE = "je";
    }

    public static class VitaminAFields {
        public static final String VITAMIN_A_DOSE = "vitaminADose";
        public static final String VITAMIN_A_DATE = "vitaminADate";
        public static final String VITAMIN_A_DOSE_1_VALUE = "1";
        public static final String VITAMIN_A_DOSE_2_VALUE = "2";
        public static final String VITAMIN_A_DOSE_5_VALUE = "5";
        public static final String VITAMIN_A_DOSE_9_VALUE = "9";
        public static final List<String> VITAMIN_A_DOSES_1_2_5_9 =
                asList(
                        VITAMIN_A_DOSE_1_VALUE,
                        VITAMIN_A_DOSE_2_VALUE,
                        VITAMIN_A_DOSE_5_VALUE,
                        VITAMIN_A_DOSE_9_VALUE);
    }

    public static class IFAFields {
        public static final String NUMBER_OF_IFA_TABLETS_GIVEN = "numberOfIFATabletsGiven";
        public static final String IFA_TABLETS_DATE = "ifaTabletsDate";
    }

    public static class ChildIllnessFields {
        public static final String CHILD_SIGNS = "childSigns";
        public static final String SICK_VISIT_DATE = "sickVisitDate";
        public static final String REPORT_CHILD_DISEASE = "reportChildDisease";
        public static final String REPORT_CHILD_DISEASE_DATE = "reportChildDiseaseDate";
        public static final String DIARRHEA_DEHYDRATION_VALUE = "diarrhea_dehydration";
    }

    public static class ReportBindTypes {
        public static final String CHILD_BIND_TYPE = "child";
        public static final String MOTHER_BIND_TYPE = "mother";
        public static final String ELIGIBLE_COUPLE_BIND_TYPE = "eligible_couple";
    }

    public static class ReportingRuleNames {
        public static final String AGE_LESS_THAN_ONE_YEAR = "AgeIsLessThanOneYearRule";
        public static final String RELOCATION_PERMANENT = "RelocationIsPermanentRule";
        public static final String CURRENT_FP_METHOD_IS_CONDOM = "CurrentFPMethodIsCondomRule";
    }
}
