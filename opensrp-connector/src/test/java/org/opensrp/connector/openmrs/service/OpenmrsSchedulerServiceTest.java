package org.opensrp.connector.openmrs.service;

import static org.opensrp.dto.AlertStatus.normal;
import static org.opensrp.dto.BeneficiaryType.mother;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.model.Time;
import org.motechproject.scheduletracking.api.domain.Enrollment;
import org.motechproject.scheduletracking.api.domain.EnrollmentStatus;
import org.motechproject.scheduletracking.api.domain.Schedule;
import org.opensrp.dto.ActionData;
import org.opensrp.scheduler.Action;

public class OpenmrsSchedulerServiceTest extends TestResourceLoader{
	public OpenmrsSchedulerServiceTest() throws IOException {
		super();
	}

	OpenmrsSchedulerService ss;
	OpenmrsUserService us;

	SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd");
	
	@Before
	public void setup() throws IOException{
		openmrsOpenmrsUrl = "http://localhost:8181/openmrs";
		openmrsUsername = "admin";
		openmrsPassword = "Admin321";
		ss = new OpenmrsSchedulerService(openmrsOpenmrsUrl, openmrsUsername, openmrsPassword);
		us = new OpenmrsUserService(openmrsOpenmrsUrl, openmrsUsername, openmrsPassword);
		ss.setUserService(us);;
	}
	
	@Test
	public void testTrack() throws JSONException, ParseException {
		Enrollment e = new Enrollment("abc2831e-1e0d-4707-877b-22961e01e753", new Schedule("Boosters"), "REMINDER", 
				new DateTime(2012, 1, 1, 0, 0), new DateTime(2012, 1, 1, 0, 0), new Time(23,8), EnrollmentStatus.ACTIVE, null);
		List<Action> alertActions = new ArrayList<Action>();
		alertActions.add(new Action("abc2831e-1e0d-4707-877b-22961e01e753", "admin", alert("Boosters", "REMINDER")));
		alertActions.add(new Action("abc2831e-1e0d-4707-877b-22961e01e753", "admin", ActionData.markAlertAsClosed("REMINDER", "12-12-2015")));
		//ss.createTrack(e, alertActions);
	}
	
	private ActionData alert() {
        return ActionData.createAlert(mother, "Ante Natal Care - Normal", "ANC 1", normal, DateTime.now(), DateTime.now().plusDays(3));
    }

    private ActionData alert(String schedule, String milestone) {
        return ActionData.createAlert(mother, schedule, milestone, normal, DateTime.now(), DateTime.now().plusDays(3));
    }
}
