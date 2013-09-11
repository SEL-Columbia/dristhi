package org.ei.drishti.service.reporting;

import org.ei.drishti.common.domain.Indicator;
import org.ei.drishti.domain.Child;
import org.ei.drishti.domain.Location;
import org.ei.drishti.form.domain.FormSubmission;
import org.ei.drishti.repository.AllChildren;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static org.ei.drishti.util.FormSubmissionBuilder.create;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

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

        reporter.report(submission, "INFANT_LEFT", location);

        verify(childReportingService).reportToBoth(child, Indicator.from("INFANT_LEFT"), submission.getField("submissionDate"), location);
        reporter.report(submission, "INFANT_LEFT", location);
    }
}
