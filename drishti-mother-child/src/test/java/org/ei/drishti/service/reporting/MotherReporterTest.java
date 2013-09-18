package org.ei.drishti.service.reporting;

import org.ei.drishti.common.domain.Indicator;
import org.ei.drishti.domain.Location;
import org.ei.drishti.domain.Mother;
import org.ei.drishti.form.domain.FormSubmission;
import org.ei.drishti.repository.AllMothers;
import org.ei.drishti.util.SafeMap;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static org.ei.drishti.util.FormSubmissionBuilder.create;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class MotherReporterTest {

    private IReporter motherReporter;

    @Mock
    private MotherReportingService motherReportingService;
    @Mock
    private AllMothers allMothers;
    @Mock
    private Mother mother;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        motherReporter = new MotherReporter(motherReportingService, allMothers);
    }

    @Test
    public void shouldCallMotherReporting() {
        FormSubmission submission = create()
                .withEntityId("mother id 1")
                .build();
        Location location = new Location("village", "sc", "phc");
        SafeMap safeMap = new SafeMap().put("submissionDate", "2012-03-01");

        when(allMothers.findByCaseId("mother id 1")).thenReturn(mother);

        motherReporter.report(submission.entityId(), "MMA", location, "2012-03-01", safeMap);

        verify(motherReportingService).reportToBoth(mother, Indicator.from("MMA"), safeMap.get("submissionDate"), location);
    }
}
