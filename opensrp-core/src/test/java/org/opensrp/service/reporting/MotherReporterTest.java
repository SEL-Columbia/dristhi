package org.opensrp.service.reporting;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.opensrp.util.FormSubmissionBuilder.create;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.opensrp.common.domain.Indicator;
import org.opensrp.domain.Location;
import org.opensrp.domain.Mother;
import org.opensrp.form.domain.FormSubmission;
import org.opensrp.repository.AllMothers;
import org.opensrp.util.SafeMap;

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

        verify(motherReportingService).reportToBoth(mother, Indicator.from("MMA"), safeMap.get("submissionDate"),
                safeMap.get("submissionDate"), location);
    }
}
