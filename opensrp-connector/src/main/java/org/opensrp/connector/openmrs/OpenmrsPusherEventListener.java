package org.opensrp.connector.openmrs;

import static org.apache.commons.lang.exception.ExceptionUtils.getFullStackTrace;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

import org.motechproject.scheduler.domain.MotechEvent;
import org.motechproject.server.event.annotations.MotechListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class OpenmrsPusherEventListener {
    private static Logger logger = LoggerFactory.getLogger(OpenmrsPusherEventListener.class.toString());
    private OpenmrsService openmrsService;
    private String formids;
    private static final ReentrantLock lock = new ReentrantLock();
    private static final String previousFirtimeKey = "PRV_FIRE_TIME";

    @Autowired
    public OpenmrsPusherEventListener(OpenmrsService openmrsService, 
    		@Value("#{drishti['openmrs.opensrp-connector.accepted-form-ids']}") String formsToPush) {
		this.openmrsService = openmrsService;
		this.formids = formsToPush;
	}

    @MotechListener(subjects = OpenmrsPusherScheduler.SUBJECT)
    public void fetchReports(MotechEvent event) {
        if (!lock.tryLock()) {
            logger.warn("Not pushing data to openmrs. It is already in progress.");
            return;
        }
        try {
            logger.info("Pushing data to openmrs");

            String[] formsToPushArr = formids.split(",");
            Date prvFt = (Date) event.getParameters().get(previousFirtimeKey);
			long serverVersion = prvFt==null?0L:prvFt.getTime();
			
    		System.out.println(formids+":ARRRR:("+formsToPushArr.length+"):"+formsToPushArr);
    		List<String> resp = new ArrayList<>();
    		for (String fid : formsToPushArr) {
				resp.add(openmrsService.pushDataToOpenmrs(fid, serverVersion));
    		}
    		
			System.out.println(event.getParameters()+"::"+event.getEndTime());

    		Map<String, Object> map = event.getParameters();
			map.put(previousFirtimeKey, new Date());
			event.copy(OpenmrsPusherScheduler.SUBJECT, map);
			
    		System.out.println(resp);

            logger.info("Recieved responses back from openrms : "+resp);
        } catch (Exception e) {
            logger.error(MessageFormat.format("{0} occurred while pushing data to OpenMRS. Message: {1} with stack trace {2}",
                    e.toString(), e.getMessage(), getFullStackTrace(e)));
        } finally {
            lock.unlock();
        }
    }
}
