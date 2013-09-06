package org.ei.drishti.service;

import org.ei.drishti.common.domain.Indicator;
import org.ei.drishti.common.domain.ReportingData;
import org.ei.drishti.domain.Child;
import org.ei.drishti.domain.EligibleCouple;
import org.ei.drishti.domain.Location;
import org.ei.drishti.domain.Mother;
import org.ei.drishti.form.domain.FormSubmission;
import org.ei.drishti.repository.AllChildren;
import org.ei.drishti.repository.AllEligibleCouples;
import org.ei.drishti.repository.AllMothers;
import org.ei.drishti.service.reporting.FormSubmissionReportService;
import org.ei.drishti.service.reporting.ReportingService;
import org.junit.Before;
import org.junit.Ignore;
import org.mockito.Mock;

import static org.ei.drishti.util.FormSubmissionBuilder.create;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class FormSubmissionReportServiceTest {
    @Mock
    private ReportingService reportingService;
    @Mock
    private AllChildren allChildren;
    @Mock
    private AllMothers allMothers;
    @Mock
    private AllEligibleCouples allEligibleCouples;

    private FormSubmissionReportService service;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        service = new FormSubmissionReportService();
    }

    @Ignore
    public void shouldCreateActionsForReports() throws Exception {
        FormSubmission submission = create()
                .withFormName("child_close")
                .withANMId("anm id 1")
                .withEntityId("child id 1")
                .addFormField("submissionDate", "2012-03-01")
                .addFormField("closeReason", "permanent_relocation")
                .build();
        Child child = new Child("child id 1", "mother id 1", "opv", "2", "female").withDateOfBirth("2012-01-01");

        when(allMothers.findByCaseId("mother id 1")).thenReturn(new Mother("mother id 1", "EC-CASE-1", "TC 1"));
        when(allEligibleCouples.findByCaseId("EC-CASE-1")).thenReturn(new EligibleCouple().withLocation("bherya", "Sub Center", "PHC X"));
        when(allChildren.findByCaseId("child id 1")).thenReturn(child);

        service.reportFor(submission);

        verifyBothReportingCalls(Indicator.INFANT_LEFT, "2012-03-01");
    }

    private void verifyBothReportingCalls(Indicator indicator, String date) {
        ReportingData serviceProvidedData = ReportingData.serviceProvidedData("ANM X", "TC 1", indicator, date, new Location("bherya", "Sub Center", "PHC X"));
        ReportingData anmReportData = ReportingData.anmReportData("ANM X", "CASE X", indicator, date);
        verify(reportingService).sendReportData(serviceProvidedData);
        verify(reportingService).sendReportData(anmReportData);
    }
}
