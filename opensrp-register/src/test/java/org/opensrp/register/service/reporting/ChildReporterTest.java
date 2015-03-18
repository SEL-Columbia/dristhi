package org.opensrp.register.service.reporting;

import org.opensrp.common.domain.Indicator;
import org.opensrp.register.domain.Child;
import org.opensrp.domain.Location;
import org.opensrp.form.domain.FormSubmission;
import org.opensrp.util.SafeMap;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import org.opensrp.register.repository.AllChildren;
import org.opensrp.register.service.reporting.ChildReporter;
import org.opensrp.register.service.reporting.ChildReportingService;
import org.opensrp.service.reporting.IReporter;
import  static org.opensrp.register.util.FormSubmissionBuilder.create;

public class ChildReporterTest {

    @Mock
    private AllChildren allChildren;
    @Mock
    private ChildReportingService childReportingService;
    @Mock
    private Child child;

    private IReporter reporter;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        this.reporter = new ChildReporter(childReportingService, allChildren);
    }

    @Test
    public void shouldCallChildReportingService() {
        FormSubmission submission = create()
                .withEntityId("child id 1")
                .addFormField("submissionDate", "2012-03-01")
                .build();
        Location location = new Location("village", "sc", "phc");
        when(allChildren.findByCaseId("child id 1")).thenReturn(child);
        SafeMap safeMap = new SafeMap().put("submissionDate", "2012-03-01");

        reporter.report(submission.entityId(), "INFANT_LEFT", location, "2012-03-01", safeMap);

        verify(childReportingService).reportToBoth(child, Indicator.from("INFANT_LEFT"), submission.getField("submissionDate"), submission.getField("submissionDate"), location);
    }
}
