
package org.opensrp.connector.openmrs.constants;

/**
 * Mappings in OpenSRP for OpenMRS entities and properties
 */
public class OpenmrsConstants {

	public static final String SCHEDULER_TRACKER_SYNCER_SUBJECT = "OpenMRS Scheduler Tracker Syncer";
	public static final String ENROLLMENT_TRACK_UUID = "openmrsTrackUuid";


	public enum ScheduleTrackerConfig {
		openmrs_syncer_sync_by_last_update_enrollment,
		openmrs_syncer_sync_status,
		openmrs_syncer_sync_timestamp
	}
}