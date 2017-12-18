package org.opensrp.connector.openmrs.schedule;

import java.util.concurrent.locks.ReentrantLock;

import org.opensrp.connector.openmrs.EncounterAtomfeed;
import org.opensrp.connector.openmrs.PatientAtomfeed;
import org.opensrp.connector.openmrs.constants.OpenmrsConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OpenmrsAtomfeedListener {
	
	Logger log = LoggerFactory.getLogger(getClass().getName());
	
	private static final ReentrantLock lock = new ReentrantLock();
	
	private PatientAtomfeed patientAtomfeed;
	
	private EncounterAtomfeed encounterAtomfeed;
	
	@Autowired
	public OpenmrsAtomfeedListener(PatientAtomfeed patientAtomfeed, EncounterAtomfeed encounterAtomfeed) {
		this.patientAtomfeed = patientAtomfeed;
		this.encounterAtomfeed = encounterAtomfeed;
	}
	
	public void syncAtomfeeds() {
		if (!lock.tryLock()) {
			log.warn("Not fetching atom feed records. It is already in progress.");
			return;
		}
		
		try {
			log.info("Running " + OpenmrsConstants.SCHEDULER_OPENMRS_ATOMFEED_SYNCER_SUBJECT);
			
			patientAtomfeed.processEvents();
			
			encounterAtomfeed.processEvents();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			lock.unlock();
		}
	}
}
