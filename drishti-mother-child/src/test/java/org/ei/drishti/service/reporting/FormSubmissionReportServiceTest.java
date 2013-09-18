package org.ei.drishti.service.reporting;

import org.ei.drishti.domain.Location;
import org.ei.drishti.form.domain.FormSubmission;
import org.ei.drishti.repository.AllChildren;
import org.ei.drishti.repository.AllEligibleCouples;
import org.ei.drishti.repository.AllMothers;
import org.ei.drishti.service.reporting.rules.IRule;
import org.ei.drishti.util.SafeMap;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static java.util.Arrays.asList;
import static org.ei.drishti.util.FormSubmissionBuilder.create;
import static org.mockito.Mockito.*;
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
    @Mock
    private ILocationLoader locationLoader;
    @Mock
    private IRulesFactory rulesFactory;
    @Mock
    private IRule rule;
    @Mock
    private IReporterFactory reporterFactory;
    @Mock
    private IReportDefinitionLoader reportDefinitionLoader;
    @Mock
    private IReporter reporter;

    private FormSubmissionReportService service;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        service = new FormSubmissionReportService(locationLoader, rulesFactory, reporterFactory, reportDefinitionLoader);
    }

    @Test
    public void shouldReportIndicatorWhenAllRulesSucceed() throws Exception {
        FormSubmission submission = create()
                .withFormName("child_close")
                .withANMId("anm id 1")
                .withEntityId("child id 1")
                .addFormField("submissionDate", "2012-03-01")
                .addFormField("closeReason", "permanent_relocation")
                .build();
        Location location = new Location("bherya", "Sub Center", "PHC X");
        when(rulesFactory.ruleByName(any(String.class))).thenReturn(rule);
        when(rule.apply(any(FormSubmission.class), anyList(), any(ReferenceData.class))).thenReturn(true);
        when(reportDefinitionLoader.reportDefinition()).thenReturn(reportDefinitionForInfantLeft());
        when(reporterFactory.reporterFor("child")).thenReturn(reporter);
        when(locationLoader.loadLocationFor("child", "child id 1")).thenReturn(location);
        SafeMap reportData = new SafeMap().put("submissionDate", submission.getField("submissionDate"))
                .put("id", submission.entityId())
                .put("closeReason", submission.getField("closeReason"));

        service.reportFor(submission);

        verify(locationLoader).loadLocationFor("child", "child id 1");
        verify(reporter).report(submission.entityId(), "INFANT_LEFT", location, "2012-03-01", reportData);
    }

    @Test
    public void shouldReportQuantityWhenQuantityFieldIsSpecifiedInTheIndicatorDefinition() throws Exception {
        FormSubmission submission = create()
                .withFormName("eligible_couple")
                .withANMId("anm id 1")
                .withEntityId("ec id 1")
                .addFormField("submissionDate", "2012-03-01")
                .addFormField("numberOfCondomsSupplied", "10")
                .addFormField("familyPlanningMethodChangeDate", "2013-01-01")
                .build();
        Location location = new Location("bherya", "Sub Center", "PHC X");
        when(rulesFactory.ruleByName(any(String.class))).thenReturn(rule);
        when(rule.apply(any(FormSubmission.class), anyList(), any(ReferenceData.class))).thenReturn(true);
        when(reportDefinitionLoader.reportDefinition()).thenReturn(reportDefinitionWithCondomQuantity());
        when(reporterFactory.reporterFor("eligible_couple")).thenReturn(reporter);
        when(locationLoader.loadLocationFor("eligible_couple", "ec id 1")).thenReturn(location);
        SafeMap reportData = new SafeMap().put("submissionDate", submission.getField("submissionDate"))
                .put("id", submission.entityId())
                .put("numberOfCondomsSupplied", submission.getField("numberOfCondomsSupplied"))
                .put("familyPlanningMethodChangeDate", "2013-01-01")
                .put("quantity", submission.getField("numberOfCondomsSupplied"));

        service.reportFor(submission);

        verify(locationLoader).loadLocationFor("eligible_couple", "ec id 1");
        verify(reporter).report(submission.entityId(), "CONDOM_QTY", location, "2013-01-01", reportData);
    }

    @Test
    public void shouldNotReportWhenRulesAreNotSatisfied() throws Exception {
        FormSubmission submission = create()
                .withFormName("child_close")
                .withANMId("anm id 1")
                .withEntityId("child id 1")
                .addFormField("submissionDate", "2013-03-01")
                .addFormField("closeReason", "permanent_relocation")
                .build();
        when(reportDefinitionLoader.reportDefinition()).thenReturn(reportDefinitionForInfantLeft());
        when(rulesFactory.ruleByName(any(String.class))).thenReturn(rule);
        when(rule.apply(any(FormSubmission.class), anyList(), any(ReferenceData.class))).thenReturn(false);

        service.reportFor(submission);

        verifyZeroInteractions(reportingService);
        verifyZeroInteractions(locationLoader);
    }

    private ReportDefinition reportDefinitionForInfantLeft() {
        return new ReportDefinition(
                asList(
                        new FormIndicator("child_close",
                                asList(
                                        new ReportIndicator(
                                                "INFANT_LEFT",
                                                "child",
                                                null,
                                                "submissionDate",
                                                asList("id", "closeReason", "submissionDate"),
                                                new ReferenceData("child", "id", asList("dateOfBirth")),
                                                asList("AgeIsLessThanOneYearRule", "RelocationIsPermanentRule")
                                        )))));
    }

    private ReportDefinition reportDefinitionWithCondomQuantity() {
        return new ReportDefinition(
                asList(
                        new FormIndicator("eligible_couple",
                                asList(
                                        new ReportIndicator(
                                                "CONDOM_QTY",
                                                "eligible_couple",
                                                "numberOfCondomsSupplied",
                                                "familyPlanningMethodChangeDate",
                                                asList("id", "numberOfCondomsSupplied", "familyPlanningMethodChangeDate"),
                                                new ReferenceData("eligible_couple", "caseId", asList("currentMethod")),
                                                asList("CurrentFPMethodIsCondomRule")
                                        )))));
    }

}
