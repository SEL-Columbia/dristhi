package org.ei.drishti.common;

public class AllConstants {
    public static final String DETAILS_EXTRA_DATA_KEY_NAME = "details";

    public static class Form {
        public static final String ENTITY_ID = "entityId";
        public static final String ID = "id";
        public static final String ANM_ID = "anmId";
        public static final String FORM_NAME = "formName";
        public static final String INSTANCE_ID = "instanceId";
        public static final String TIME_STAMP = "timeStamp";
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
        public static final String METHOD_STILL_THE_SAME = "method_still_the_same";
    }

    public static class Report {
        public static final int REPORTING_MONTH = 3;
        public static final int REPORTING_MONTH_START_DAY = 26;
        public static final String REPORT_EXTRA_DATA_KEY_NAME = "reporting";
        public static final String SERVICE_PROVIDED_DATA_TYPE = "serviceProvided";
        public static final String ANM_REPORT_DATA_TYPE = "anmReportData";
        public static final double LOW_BIRTH_WEIGHT_THRESHOLD = 2.5;
        public static final int INFANT_MORTALITY_THRESHOLD_IN_YEARS = 1;
        public static final int CHILD_MORTALITY_THRESHOLD_IN_YEARS = 5;
        public static final int CHILD_EARLY_NEONATAL_MORTALITY_THRESHOLD_IN_DAYS = 7;
        public static final int CHILD_NEONATAL_MORTALITY_THRESHOLD_IN_DAYS = 28;
    }

    public static class DeliveryOutcomeCommCareFields {
        public static final String MOTHER_CASE_ID_COMMCARE_FIELD_NAME = "motherCaseId";
        public static final String DATE_OF_DELIVERY_COMMCARE_FIELD_NAME = "dateOfDelivery";
        public static final String DELIVERY_OUTCOME_COMMCARE_FIELD_NAME = "pregnancyOutcome";
        public static final String PLACE_OF_DELIVERY_COMMCARE_FIELD_NAME = "placeOfDelivery";
        public static final String MOTHER_SURVIVED_COMMCARE_FIELD_NAME = "motherSurvived";
        public static final String WOMAN_SURVIVED_COMMCARE_FIELD_NAME = "womanSurvived";
        public static final String LIVE_BIRTH_COMMCARE_FIELD_VALUE = "live_birth";
        public static final String HOME_COMMCARE_FIELD_VALUE = "home";
        public static final String SC_COMMCARE_FIELD_VALUE = "subcenter";
        public static final String PHC_COMMCARE_FIELD_VALUE = "phc";
        public static final String CHC_COMMCARE_FIELD_VALUE = "chc";
        public static final String SDH_COMMCARE_FIELD_VALUE = "sdh";
        public static final String DH_COMMCARE_FIELD_VALUE = "dh";
        public static final String PRIVATE_FACILITY_COMMCARE_FIELD_VALUE = "private_facility";
        public static final String PRIVATE_FACILITY2_COMMCARE_FIELD_VALUE = "private_facility2";

    }

    public static class ANCFormFields {
        public static final String MOTHER_ID = "motherId";
        public static final String REGISTRATION_DATE = "registrationDate";
        public static final String REFERENCE_DATE = "referenceDate";
        public static final String ANC_VISIT_DATE_FIELD = "ancVisitDate";
        public static final String ANC_VISIT_NUMBER_FIELD = "ancVisitNumber";
    }

    public static class ANCVisitCommCareFields {
        public static final String VISIT_DATE_COMMCARE_FIELD = "visitDate";
        public static final String VISIT_NUMBER_COMMCARE_FIELD = "visitNumber";
        public static final int ANC4_VISIT_NUMBER_COMMCARE_VALUE = 4;
        public static final String TT1_DOSE_COMMCARE_VALUE = "tt1";
        public static final String TT2_DOSE_COMMCARE_VALUE = "tt2";
        public static final String TT_BOOSTER_DOSE_COMMCARE_VALUE = "ttbooster";
        public static final String TT_DOSE_COMMCARE_FIELD = "ttDose";
        public static final String TT_DATE_COMMCARE_FIELD = "ttDate";
    }

    public static class CommonCommCareFields {
        public static final String CASE_ID_COMMCARE_FIELD_NAME = "caseId";
        public static final String SUBMISSION_DATE_COMMCARE_FIELD_NAME = "submissionDate";
        public static final String HIGH_PRIORITY_COMMCARE_FIELD_NAME = "isHighPriority";
        public static final String BOOLEAN_TRUE_COMMCARE_VALUE = "yes";
        public static final String BOOLEAN_FALSE_COMMCARE_VALUE = "no";
    }

    public static class ChildBirthCommCareFields {
        public static final String BIRTH_WEIGHT_COMMCARE_FIELD_NAME = "childWeight";
        public static final String BLOOD_GROUP_COMMCARE_FIELD_NAME = "childBloodGroup";
        public static final String BF_POSTBIRTH_COMMCARE_FIELD_NAME = "bfPostBirth";
    }

    public static class CommonCommRegisterMotherFields {
        public static final String REGISTRATION_COMMCARE_FIELD_NAME = "registrationDate";
        public static final String LMP = "lmp";
        public static final String PHC = "phc";
    }

    public static class ChildCloseCommCareFields {
        public static final String CLOSE_REASON_COMMCARE_FIELD_NAME = "closeReason";
        public static final String DEATH_OF_CHILD_COMMCARE_VALUE = "death_of_child";
        public static final String DATE_OF_DEATH_COMMCARE_FIELD_NAME = "diedOn";
    }

    public static class CaseCloseCommCareFields {
        public static final String CLOSE_REASON_COMMCARE_FIELD_NAME = "closeReason";
        public static final String DEATH_DATE_COMMCARE_FIELD_NAME = "diedOn";
        public static final String ANC_DEATH_DATE_FIELD_NAME = "maternalDeathDate";
        public static final String IS_MATERNAL_LEAVE_COMMCARE_FIELD_NAME = "isMaternalDeath";
    }

    public static class ANCCloseCommCareFields {
        public static final String CLOSE_MTP_DATE_COMMCARE_FIELD_NAME = "dateOfInducedAbortion";
        public static final String CLOSE_SPONTANEOUS_ABORTION_DATE_COMMCARE_FIELD_NAME = "dateOfSpontaneousAbortion";
        public static final String CLOSE_MTP_TIME_COMMCARE_FIELD_NAME = "timeOfInducedAbortion";
        public static final String MTP_GREATER_THAN_12_WEEKS_FIELD_NAME = "greater_12wks";
        public static final String MTP_LESS_THAN_12_WEEKS_FIELD_NAME = "less_12wks";
        public static final String DEATH_OF_WOMAN_COMMCARE_VALUE = "death_of_woman";
        public static final String PERMANENT_RELOCATION_COMMCARE_VALUE = "relocation_permanent";
        public static final String SPONTANEOUS_ABORTION_COMMCARE_VALUE = "spontaneous_abortion";
    }

    public static class ECCloseFields {
        public static final String IS_EC_CLOSE_CONFIRMED_FIELD_NAME = "isECCloseConfirmed";
    }

    public static class PNCCloseCommCareFields {
        public static final String DEATH_OF_MOTHER_COMMCARE_VALUE = "death_of_mother";
        public static final String PERMANENT_RELOCATION_COMMCARE_VALUE = "permanent_relocation";
        public static final String END_OF_PP_PERIOD_COMMCARE_VALUE = "end_of_pp_period";
    }

    public static class FamilyPlanningCommCareFields {
        public static final String CURRENT_FP_METHOD_FIELD_NAME = "currentMethod";
        public static final String FP_METHOD_CHANGE_DATE_FIELD_NAME = "familyPlanningMethodChangeDate";
        public static final String NO_FP_METHOD_COMMCARE_FIELD_VALUE = "none";
        public static final String FP_UPDATE_COMMCARE_FIELD_NAME = "fpUpdate";
        public static final String IS_FP_METHOD_SAME_COMMCARE_FIELD_NAME = "isMethodSame";
        public static final String FP_METHOD_CHANGED_COMMCARE_FIELD_VALUE = "change_fp_product";
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
    }

    public static class ChangeFamilyPlanningMethodCommCareFields {
        public static final String PREVIOUS_FP_METHOD_FIELD_NAME = "currentMethod";
        public static final String NEW_FP_METHOD_FIELD_NAME = "newMethod";
    }

    public static class ChildImmunizationCommCareFields {
        public static final String IMMUNIZATIONS_PROVIDED_COMMCARE_FIELD_NAME = "immunizationsProvided";
        public static final String IMMUNIZATIONS_PROVIDED_DATE_COMMCARE_FIELD_NAME = "immunizationsProvidedDate";
        public static final String VITAMIN_A_DOSE_COMMCARE_FIELD_NAME = "vitaminADose";
        public static final String BCG_COMMCARE_VALUE = "bcg";

        public static final String DPT_1_COMMCARE_VALUE = "dpt_1";
        public static final String DPT_2_COMMCARE_VALUE = "dpt_2";
        public static final String DPT_3_COMMCARE_VALUE = "dpt_3";
        public static final String DPT_BOOSTER_1_COMMCARE_VALUE = "dptbooster_1";
        public static final String DPT_BOOSTER_2_COMMCARE_VALUE = "dptbooster_2";

        public static final String HEPATITIS_0_COMMCARE_VALUE = "hepb_0";
        public static final String HEPATITIS_1_COMMCARE_VALUE = "hepb_1";
        public static final String HEPATITIS_2_COMMCARE_VALUE = "hepb_2";
        public static final String HEPATITIS_3_COMMCARE_VALUE = "hepb_3";

        public static final String MEASLES_COMMCARE_VALUE = "measles";
        public static final String MEASLES_BOOSTER_COMMCARE_VALUE = "measlesbooster";

        public static final String OPV_0_COMMCARE_VALUE = "opv_0";
        public static final String OPV_1_COMMCARE_VALUE = "opv_1";
        public static final String OPV_2_COMMCARE_VALUE = "opv_2";
        public static final String OPV_3_COMMCARE_VALUE = "opv_3";
        public static final String OPV_BOOSTER_COMMCARE_VALUE = "opvbooster";
    }
}
