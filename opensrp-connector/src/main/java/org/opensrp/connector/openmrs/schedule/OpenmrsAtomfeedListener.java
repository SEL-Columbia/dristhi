package org.opensrp.connector.openmrs.schedule;

import java.util.logging.Logger;

import org.motechproject.scheduler.domain.MotechEvent;
import org.motechproject.server.event.annotations.MotechListener;
import org.opensrp.connector.openmrs.EncounterAtomfeed;
import org.opensrp.connector.openmrs.PatientAtomfeed;
import org.opensrp.connector.openmrs.constants.OpenmrsConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OpenmrsAtomfeedListener {
	Logger log = Logger.getLogger(getClass().getName());
	private PatientAtomfeed patientAtomfeed;
	private EncounterAtomfeed encounterAtomfeed;

	@Autowired
	public OpenmrsAtomfeedListener(PatientAtomfeed patientAtomfeed, EncounterAtomfeed encounterAtomfeed) {
		this.patientAtomfeed = patientAtomfeed;
		this.encounterAtomfeed = encounterAtomfeed;
	}

	@MotechListener(subjects=OpenmrsConstants.SCHEDULER_OPENMRS_ATOMFEED_SYNCER_SUBJECT)
	public void syncAtomfeeds(MotechEvent event) {
		try{
			log.info("Running "+OpenmrsConstants.SCHEDULER_OPENMRS_ATOMFEED_SYNCER_SUBJECT);
			
			patientAtomfeed.processEvents();
			
			encounterAtomfeed.processEvents();
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
}
