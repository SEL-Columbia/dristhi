package org.opensrp.repository.it;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.io.IOException;
import java.util.HashMap;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.scheduletracking.api.domain.EnrollmentStatus;
import org.motechproject.scheduletracking.api.domain.WindowName;
import org.opensrp.form.domain.FormSubmission;
import org.opensrp.form.service.FormSubmissionService;
import org.opensrp.scheduler.AlertCreationAction;
import org.opensrp.scheduler.HealthSchedulerService;
import org.opensrp.scheduler.MilestoneEvent;
import org.opensrp.util.Event;
import org.opensrp.util.ScheduleBuilder;
import org.opensrp.util.TestResourceLoader;

public class ActionCreationIntegrationTest extends TestResourceLoader{
    public ActionCreationIntegrationTest() throws IOException {
		super();
	}

	@Mock
    private HealthSchedulerService scheduler;
    
    

    private AlertCreationAction reminderAction;

    @Mock
	private FormSubmissionService formSubmissionService;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        
    }


}
