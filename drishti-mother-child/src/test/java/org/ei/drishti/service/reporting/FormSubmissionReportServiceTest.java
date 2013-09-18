package org.ei.drishti.service.reporting;

import com.google.gson.Gson;
import org.ei.drishti.domain.Child;
import org.ei.drishti.domain.EligibleCouple;
import org.ei.drishti.domain.Location;
import org.ei.drishti.domain.Mother;
import org.ei.drishti.form.domain.FormSubmission;
import org.ei.drishti.repository.AllChildren;
import org.ei.drishti.repository.AllEligibleCouples;
import org.ei.drishti.repository.AllMothers;
import org.ei.drishti.service.reporting.rules.IRule;
import org.ei.drishti.util.SafeMap;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

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
    public void shouldCreateActionsForReports() throws Exception {
        FormSubmission submission = create()
                .withFormName("child_close")
                .withANMId("anm id 1")
                .withEntityId("child id 1")
                .addFormField("submissionDate", "2012-03-01")
                .addFormField("numberOfCondoms", "10")
                .addFormField("closeReason", "permanent_relocation")
                .build();
        Child child = new Child("child id 1", "mother id 1", "opv", "2", "female")
                .withDateOfBirth("2012-01-01")
                .withLocation("bherya", "Sub Center", "PHC X");
        Location location = new Location("bherya", "Sub Center", "PHC X");

        when(allMothers.findByCaseId("mother id 1")).thenReturn(new Mother("mother id 1", "EC-CASE-1", "TC 1"));
        when(allEligibleCouples.findByCaseId("EC-CASE-1")).thenReturn(new EligibleCouple().withLocation("bherya", "Sub Center", "PHC X"));
        when(allChildren.findByCaseId("child id 1")).thenReturn(child);
        when(rulesFactory.ruleByName(any(String.class))).thenReturn(rule);
        when(rule.apply(any(FormSubmission.class), anyList(), any(ReferenceData.class))).thenReturn(true);
        when(reportDefinitionLoader.reportDefinition()).thenReturn(reportDefinitionFromJson());
        when(reporterFactory.reporterFor("child")).thenReturn(reporter);
        when(locationLoader.loadLocationFor("child", "child id 1")).thenReturn(location);
        SafeMap reportData = new SafeMap().put("submissionDate", submission.getField("submissionDate"))
                .put("id", submission.entityId())
                .put("closeReason", submission.getField("closeReason"))
                .put("numberOfCondoms", "10")
                .put("quantity", submission.getField("numberOfCondoms"));

        service.reportFor(submission);

        verify(locationLoader).loadLocationFor("child", "child id 1");
        verify(reporter).report(submission.entityId(), "INFANT_LEFT", location, reportData);
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
        Child child = new Child("child id 1", "mother id 1", "opv", "2", "female").withDateOfBirth("2012-01-01");

        when(allMothers.findByCaseId("mother id 1")).thenReturn(new Mother("mother id 1", "EC-CASE-1", "TC 1"));
        when(allEligibleCouples.findByCaseId("EC-CASE-1")).thenReturn(new EligibleCouple().withLocation("bherya", "Sub Center", "PHC X"));
        when(reportDefinitionLoader.reportDefinition()).thenReturn(reportDefinitionFromJson());
        when(allChildren.findByCaseId("child id 1")).thenReturn(child);
        when(rulesFactory.ruleByName(any(String.class))).thenReturn(rule);
        when(rule.apply(any(FormSubmission.class), anyList(), any(ReferenceData.class))).thenReturn(false);

        service.reportFor(submission);

        verifyZeroInteractions(reportingService);
        verifyZeroInteractions(locationLoader);
    }

    //#TODO: Create object with these values instead of parsing from JSON
    private ReportDefinition reportDefinitionFromJson() {
        String indicatorJson = "{\n" +
                "    \"formIndicators\": [\n" +
                "        {\n" +
                "            \"form\": \"child_close\",\n" +
                "            \"indicators\": [\n" +
                "                {\n" +
                "                    \"indicator\": \"INFANT_LEFT\",\n" +
                "                    \"quantityField\": \"numberOfCondoms\",\n" +
                "                    \"formFields\": [\n" +
                "                        \"id\",\n" +
                "                        \"closeReason\",\n" +
                "                        \"numberOfCondoms\",\n" +
                "                        \"submissionDate\"\n" +
                "                    ],\n" +
                "                    \"referenceData\": {\n" +
                "                        \"type\": \"child\",\n" +
                "                        \"idField\": \"id\",\n" +
                "                        \"fields\": [\n" +
                "                            \"dateOfBirth\"\n" +
                "                        ]\n" +
                "                    },\n" +
                "                    \"reportWhen\": [\n" +
                "                        \"AgeIsLessThanOneYearRule\",\n" +
                "                        \"RelocationIsPermanentRule\"\n" +
                "                    ],\n" +
                "                    \"bindType\": \"child\"\n" +
                "                }\n" +
                "            ]\n" +
                "        }\n" +
                "    ]\n" +
                "}\n";
        return new Gson().fromJson(indicatorJson, ReportDefinition.class);
    }

}
