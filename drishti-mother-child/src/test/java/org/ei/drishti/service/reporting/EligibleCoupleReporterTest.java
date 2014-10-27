package org.ei.drishti.service.reporting;

import org.ei.drishti.common.domain.Indicator;
import org.ei.drishti.domain.EligibleCouple;
import org.ei.drishti.domain.Location;
import org.ei.drishti.form.domain.FormSubmission;
import org.ei.drishti.repository.AllEligibleCouples;
import org.ei.drishti.util.SafeMap;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.ArrayList;

import static org.ei.drishti.util.FormSubmissionBuilder.create;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class EligibleCoupleReporterTest {
    @Mock
    private AllEligibleCouples allEligibleCouples;
    @Mock
    private ECReportingService ecReportingService;
    @Mock
    private EligibleCouple eligibleCouple;
    private IReporter reporter;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        this.reporter = new EligibleCoupleReporter(ecReportingService, allEligibleCouples);
    }

    @Test
    public void shouldCallECReporting() {
        FormSubmission submission = create()
                .withEntityId("ec id 1")
                .addFormField("familyPlanningMethodChangeDate", "2012-03-01")
                .addFormField("submissionDate", "2012-03-01")
                .build();
        ArrayList<String> fieldNames = new ArrayList<>();
        fieldNames.add("submissionDate");

        SafeMap reportData = new SafeMap(submission.getFields(fieldNames));
        Location location = new Location("village", "sc", "phc");
        when(allEligibleCouples.findByCaseId("ec id 1")).thenReturn(eligibleCouple);

        reporter.report(submission.entityId(), "OCP", location, "2012-03-01", reportData);

        verify(ecReportingService).reportIndicator(reportData, eligibleCouple, Indicator.from("OCP"), "2012-03-01", "2012-03-01");
    }
}
