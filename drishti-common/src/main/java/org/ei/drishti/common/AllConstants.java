package org.ei.drishti.common;

public class AllConstants {
    public static class Report {
        public static final String REPORT_EXTRA_MAPS_KEY_NAME = "reporting";
        public static final String SERVICE_PROVIDED_DATA_TYPE = "serviceProvided";
        public static final String ANM_REPORT_DATA_TYPE = "anmReportData";
    }

    public static class MetaCommCareFields {
        public static final String ANM_IDENTIFIER_COMMCARE_FIELD_NAME = "anmIdentifier";
    }

    public static class DeliveryOutcomeCommCareFields {
        public static final String MOTHER_CASE_ID_COMMCARE_FIELD_NAME = "motherCaseId";
        public static final String DATE_OF_DELIVERY_COMMCARE_FIELD_NAME = "dateOfDelivery";
        public static final String DELIVERY_OUTCOME_COMMCARE_FIELD_NAME = "pregnancyOutcome";
        public static final String LIVE_BIRTH_COMMCARE_FIELD_VALUE = "live_birth";
    }

    public static class CommonCommCareFields {
        public static final String CASE_ID_COMMCARE_FIELD_NAME = "caseId";
        public static final String SUBMISSION_DATE_COMMCARE_FIELD_NAME = "submissionDate";
    }

    public static class ChildCloseCommCareFields {
        public static final String CLOSE_REASON_COMMCARE_FIELD_NAME = "closeReason";
        public static final String DEATH_OF_CHILD_COMMCARE_VALUE = "death_of_child";
    }


    public static class ChildImmunizationCommCareValues {
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
