
package org.opensrp.connector.openmrs.constants;

/**
 * Mappings in OpenSRP for OpenMRS entities and properties
 */
public class OpenmrsConstants {
	
	public static final String SCHEDULER_TRACKER_SYNCER_SUBJECT = "OpenMRS Scheduler Tracker Syncer";
	
	public static final String SCHEDULER_OPENMRS_ATOMFEED_SYNCER_SUBJECT = "OpenMRS Atomfeed Syncer";
	
	public static final String ENROLLMENT_TRACK_UUID = "openmrsTrackUuid";
	
	public static final String SCHEDULER_OPENMRS_DATA_PUSH_SUBJECT = "OpenMRS Data Pusher";
	
	public static final String ATOMFEED_URL = "ws/atomfeed";
	
	public static final String ATOMFEED_DATABASE_CONNECTOR = "atomfeedDatabaseConnector";
	
	public static final String SCHEDULER_OPENMRS_SYNC_VALIDATOR_SUBJECT = "OpenMRS Sync Validator";
	
	public enum SchedulerConfig {
		openmrs_syncer_sync_schedule_tracker_by_last_update_enrollment,
		openmrs_syncer_sync_client_by_date_updated, openmrs_syncer_sync_client_by_date_voided, 
		openmrs_syncer_sync_event_by_date_updated, openmrs_syncer_sync_event_by_date_voided, 
		openmrs_syncer_sync_status, openmrs_syncer_sync_timestamp, 
		openmrs_client_sync_validator_timestamp, openmrs_event_sync_validator_timestamp,
		openmrs_birth_reg_event_sync_validator_timestamp, openmrs_growth_mon_event_sync_validator_timestamp, openmrs_vaccine_event_sync_validator_timestamp
	}
}
