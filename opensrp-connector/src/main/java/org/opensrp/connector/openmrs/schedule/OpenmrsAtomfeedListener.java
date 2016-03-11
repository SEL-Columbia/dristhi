package org.opensrp.connector.openmrs.schedule;

import org.motechproject.scheduler.domain.MotechEvent;
import org.motechproject.server.event.annotations.MotechListener;
import org.opensrp.connector.openmrs.EncounterAtomfeed;
import org.opensrp.connector.openmrs.PatientAtomfeed;
import org.opensrp.connector.openmrs.constants.OpenmrsConstants;
import org.springframework.beans.factory.annotation.Autowired;

public class OpenmrsAtomfeedListener {
	
	private PatientAtomfeed patientAtomfeed;
	private EncounterAtomfeed encounterAtomfeed;

	@Autowired
	public OpenmrsAtomfeedListener(PatientAtomfeed patientAtomfeed, EncounterAtomfeed encounterAtomfeed) {
		this.patientAtomfeed = patientAtomfeed;
		this.encounterAtomfeed = encounterAtomfeed;
	}

	@MotechListener(subjects=OpenmrsConstants.SCHEDULER_TRACKER_SYNCER_SUBJECT)
	public void syncAtomfeeds(MotechEvent event) {
		patientAtomfeed.processEvents();;
		
		encounterAtomfeed.processEvents();
	}
}
