
package org.opensrp.connector.openmrs.constants;

import org.ict4h.atomfeed.client.AtomFeedProperties;

/**
 * Mappings in OpenSRP for OpenMRS entities and properties
 */
public class OpenmrsConstants {

	public static final String SCHEDULER_TRACKER_SYNCER_SUBJECT = "OpenMRS Scheduler Tracker Syncer";
	public static final String SCHEDULER_OPENMRS_ATOMFEED_SYNCER_SUBJECT = "OpenMRS Atomfeed Syncer";
	public static final String ENROLLMENT_TRACK_UUID = "openmrsTrackUuid";
	
	public static final String ATOMFEED_URL = "ws/atomfeed";
    public static final String ATOMFEED_DATABASE_CONNECTOR = "atomfeedDatabaseConnector";


	public enum ScheduleTrackerConfig {
		openmrs_syncer_sync_by_last_update_enrollment,
		openmrs_syncer_sync_status,
		openmrs_syncer_sync_timestamp
	}
	
	public static AtomFeedProperties DEFUALT_ATOM_FEED_PROPERTIES = new AtomFeedProperties();
}