package org.opensrp.common;


public class AllConstants {
	public static final String OPENSRP_FORM_DATABASE_CONNECTOR = "opensrpFormDatabaseConnector";
    public static final String OPENSRP_DATABASE_CONNECTOR = "opensrpDatabaseConnector";
    public static final String OPENSRP_MCTS_DATABASE_CONNECTOR = "opensrpMCTSDatabaseConnector";
    public static final String SPACE = " ";
    public static final String BOOLEAN_TRUE_VALUE = "true";
    public static final String BOOLEAN_FALSE_VALUE = "false";
    public static final String AUTO_CLOSE_PNC_CLOSE_REASON = "Auto Close PNC";
    public static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd";
    public static final String EMPTY_STRING = "";
    public static final String OPENSRP_ERRORTRACE_DATABASE="opensrpErrorTraceDatabaseConnector";

    public static class Form {
        public static final String ENTITY_ID = "entityId";
        public static final String ANM_ID = "anmId";
        public static final String FORM_NAME = "formName";
        public static final String INSTANCE_ID = "instanceId";
        public static final String CLIENT_VERSION = "clientVersion";
        public static final String SERVER_VERSION = "serverVersion";
    }
    public static class HTTP {
        public static final String ACCESS_CONTROL_ALLOW_ORIGIN_HEADER = "Access-Control-Allow-Origin";
        public static final String WWW_AUTHENTICATE_HEADER = "www-authenticate";
    }

	public static class OpenSRPEvent{
		public static final String FORM_SUBMISSION = "FORM_SUBMISSION";
	}

	public enum Config {
		FORM_ENTITY_PARSER_LAST_SYNCED_FORM_SUBMISSION
	}

	public static final String FORM_SCHEDULE_SUBJECT = "FORM-SCHEDULE";
	
	public static class Report {
        public static final int FIRST_REPORT_MONTH_OF_YEAR = 3;
        public static final int REPORTING_MONTH_START_DAY = 26;
        public static final int REPORTING_MONTH_END_DAY = 25;
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
        public static final String DRISTHI_ENTITY_ID = "dristhiEntityId";
        public static final String VILLAGE = "village";
        public static final String SUB_CENTER = "subCenter";
        public static final String PHC = "phc";
        public static final String QUANTITY = "quantity";
        public static final String SERVICE_PROVIDER_ANM = "ANM";
    }

}
