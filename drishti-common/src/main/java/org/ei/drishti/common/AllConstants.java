package org.ei.drishti.common;

public class AllConstants {
    public static final String DETAILS_EXTRA_DATA_KEY_NAME = "details";

    public static class Report {
        public static final int REPORTING_MONTH = 3;
        public static final int REPORTING_DAY = 26;
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
        public static final String LIVE_BIRTH_COMMCARE_FIELD_VALUE = "live_birth";
        public static final String HOME_COMMCARE_FIELD_VALUE = "home";
    }

    public static class ANCVisitCommCareFields {
        public static final String VISIT_DATE_COMMCARE_FIELD = "visitDate";
        public static final String WAS_TT_SHOT_PROVIDED = "ttShotProvided";
    }

    public static class CommonCommCareFields {
        public static final String CASE_ID_COMMCARE_FIELD_NAME = "caseId";
        public static final String SUBMISSION_DATE_COMMCARE_FIELD_NAME = "submissionDate";
        public static final String HIGH_PRIORITY_COMMCARE_FIELD_NAME = "isHighPriority";
        public static final String BOOLEAN_TRUE_COMMCARE_VALUE = "Yes";
    }

    public static class ChildBirthCommCareFields{
        public static final String BIRTH_WEIGHT_COMMCARE_FIELD_NAME = "childWeight";
        public static final String BLOOD_GROUP_COMMCARE_FIELD_NAME = "childBloodGroup";
        public static final String BF_POSTBIRTH_COMMCARE_FIELD_NAME = "bfPostBirth";
        public static final String YES_BF_POSTBIRTH_COMMCARE_FIELD_NAME = "yes";
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

    public static class ANCCloseCommCareFields {
        public static final String CLOSE_MTP_DATE_COMMCARE_FIELD_NAME = "dateInduced";
        public static final String CLOSE_SPONTANEOUS_ABORTION_DATE_COMMCARE_FIELD_NAME = "dateSpontaneous";
        public static final String CLOSE_MTP_TIME_COMMCARE_FIELD_NAME = "mtpTime";
        public static final String CLOSE_REASON_COMMCARE_FIELD_NAME = "closeReason";
        public static final String DEATH_OF_MOTHER_COMMCARE_VALUE = "death_of_woman";
        public static final String SPONTANEOUS_ABORTION_COMMCARE_VALUE = "spontaneous_abortion";
    }

    public static class FamilyPlanningCommCareFields {
        public static final String CURRENT_FP_METHOD_COMMCARE_FIELD_NAME = "currentMethod";
        public static final String CURRENT_FP_METHOD_CHANGE_DATE_COMMCARE_FIELD_NAME = "familyPlanningMethodChangeDate";
        public static final String NO_FP_METHOD_COMMCARE_FIELD_VALUE = "none";
        public static final String FP_UPDATE_COMMCARE_FIELD_NAME = "fpUpdate";
        public static final String IS_FP_METHOD_SAME_COMMCARE_FIELD_NAME = "isMethodSame";
        public static final String FP_METHOD_CHANGED_COMMCARE_FIELD_VALUE = "change_fp_product";
    }

    public static class ChildImmunizationCommCareFields {
        public static final String IMMUNIZATIONS_PROVIDED_COMMCARE_FIELD_NAME = "immunizationsProvided";
        public static final String IMMUNIZATIONS_PROVIDED_DATE_COMMCARE_FIELD_NAME = "immunizationsProvidedDate";
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
