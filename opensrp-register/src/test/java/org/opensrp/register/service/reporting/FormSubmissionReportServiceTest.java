package org.opensrp.register.service.reporting;

import org.opensrp.common.util.EasyMap;
import org.opensrp.domain.Location;
import org.opensrp.form.domain.FormSubmission;
import org.opensrp.util.SafeMap;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.Map;

import static java.util.Arrays.asList;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;
import org.opensrp.register.repository.AllChildren;
import org.opensrp.register.repository.AllEligibleCouples;
import org.opensrp.register.repository.AllMothers;
import org.opensrp.service.reporting.FormIndicator;
import org.opensrp.service.reporting.FormSubmissionReportService;
import org.opensrp.service.reporting.ILocationLoader;
import org.opensrp.service.reporting.IReportDefinitionLoader;
import org.opensrp.service.reporting.IReporter;
import org.opensrp.service.reporting.IReporterFactory;
import org.opensrp.service.reporting.IRulesFactory;
import org.opensrp.service.reporting.ReferenceData;
import org.opensrp.service.reporting.ReportDefinition;
import org.opensrp.service.reporting.ReportIndicator;
import org.opensrp.service.reporting.ReportingService;
import org.opensrp.service.reporting.rules.IReferenceDataRepository;
import org.opensrp.service.reporting.rules.IRule;
import static org.opensrp.register.util.FormSubmissionBuilder.create;

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
    @Mock
    private IReferenceDataRepository referenceDataRepository;

    private FormSubmissionReportService service;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        service = new FormSubmissionReportService(locationLoader, rulesFactory, reporterFactory, referenceDataRepository, reportDefinitionLoader);
    }

    @Test
    public void shouldLoadReportDefinitionOnlyWhenItIsNull() throws Exception {
        FormSubmission submission = create()
                .withFormName("new_form")
                .withANMId("anm id 1")
                .withEntityId("child id 1")
                .addFormField("submissionDate", "2012-03-01")
                .build();
        when(reportDefinitionLoader.load()).thenReturn(reportDefinition());

        service.reportFor(submission);
        service.reportFor(submission);

        verify(reportDefinitionLoader, times(1)).load();
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
        when(reportDefinitionLoader.load()).thenReturn(reportDefinition());
        when(referenceDataRepository.getReferenceData(any(FormSubmission.class), any(ReferenceData.class))).thenReturn(new SafeMap());
        when(reporterFactory.reporterFor("child")).thenReturn(reporter);
        when(locationLoader.loadLocationFor("child", "child id 1")).thenReturn(location);
        when(rule.apply(any(SafeMap.class))).thenReturn(true);
        SafeMap reportData = new SafeMap().put("submissionDate", submission.getField("submissionDate"))
                .put("id", submission.entityId())
                .put("closeReason", submission.getField("closeReason"));

        service.reportFor(submission);

        Map<String, String> expectedReportFields = EasyMap
                .create("id", "child id 1")
                .put("closeReason", "permanent_relocation")
                .put("submissionDate", "2012-03-01")
                .put("serviceProvidedDate", "2012-03-01")
                .map();
        verify(rule, times(2)).apply(new SafeMap(expectedReportFields));
        verify(locationLoader).loadLocationFor("child", "child id 1");
        verify(reporter).report(submission.entityId(), "INFANT_LEFT", location, "2012-03-01", reportData);
    }

    @Test
    public void shouldReportWithSpecifiedEntityIdWhenFieldIsSpecified() throws Exception {
        FormSubmission submission = create()
                .withFormName("anc_registration")
                .withANMId("anm id 1")
                .withEntityId("ec id 1")
                .addFormField("motherId", "mother id 1")
                .addFormField("submissionDate", "2012-03-01")
                .build();
        Location location = new Location("bherya", "Sub Center", "PHC X");
        when(rulesFactory.ruleByName(any(String.class))).thenReturn(rule);
        when(reportDefinitionLoader.load()).thenReturn(reportDefinitionWithReportEntityFieldSpecified());
        when(referenceDataRepository.getReferenceData(any(FormSubmission.class), any(ReferenceData.class))).thenReturn(new SafeMap());
        when(reporterFactory.reporterFor("eligible_couple")).thenReturn(reporter);
        when(locationLoader.loadLocationFor("eligible_couple", "mother id 1")).thenReturn(location);
        when(rule.apply(any(SafeMap.class))).thenReturn(true);
        SafeMap reportData = new SafeMap()
                .put("id", submission.entityId())
                .put("submissionDate", submission.getField("submissionDate"))
                .put("motherId", submission.getField("motherId"));

        service.reportFor(submission);

        verify(locationLoader).loadLocationFor("eligible_couple", "mother id 1");
        verify(reporter).report("mother id 1", "NRHM_JSY_REG", location, "2012-03-01", reportData);
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
        when(rule.apply(any(SafeMap.class))).thenReturn(true);
        when(reportDefinitionLoader.load()).thenReturn(reportDefinitionWithCondomQuantity());
        when(referenceDataRepository.getReferenceData(any(FormSubmission.class), any(ReferenceData.class)))
                .thenReturn(new SafeMap());
        when(reporterFactory.reporterFor("eligible_couple")).thenReturn(reporter);
        when(locationLoader.loadLocationFor("eligible_couple", "ec id 1")).thenReturn(location);
        SafeMap reportData = new SafeMap().put("submissionDate", submission.getField("submissionDate"))
                .put("id", submission.entityId())
                .put("numberOfCondomsSupplied", submission.getField("numberOfCondomsSupplied"))
                .put("familyPlanningMethodChangeDate", "2013-01-01")
                .put("quantity", submission.getField("numberOfCondomsSupplied"));

        service.reportFor(submission);

        Map<String, String> expectedReportFields = EasyMap
                .create("id", "ec id 1")
                .put("numberOfCondomsSupplied", "10")
                .put("familyPlanningMethodChangeDate", "2013-01-01")
                .put("serviceProvidedDate", "2013-01-01")
                .map();
        verify(rule).apply(new SafeMap(expectedReportFields));
        verify(locationLoader).loadLocationFor("eligible_couple", "ec id 1");
        verify(reporter).report(submission.entityId(), "CONDOM_QTY", location, "2013-01-01", reportData);
    }

    @Test
    public void shouldReportEWhenExternalIdFieldIsThereInTheIndicatorDefinition() throws Exception {
        FormSubmission submission = create()
                .withFormName("postpartum_family_planning")
                .withANMId("anm id 1")
                .withEntityId("mother id 1")
                .addFormField("ecId", "ec id 1")
                .addFormField("submissionDate", "2012-03-01")
                .addFormField("numberOfCondomsSupplied", "10")
                .addFormField("familyPlanningMethodChangeDate", "2013-01-01")
                .addFormField("thayiCardNumber", "thayi card no 1")
                .build();
        Location location = new Location("bherya", "Sub Center", "PHC X");
        SafeMap reportData = new SafeMap().put("submissionDate", submission.getField("submissionDate"))
                .put("id", submission.entityId())
                .put("externalId", "thayi card no 1")
                .put("ecId", "ec id 1")
                .put("thayiCardNumber", "thayi card no 1")
                .put("externalId", submission.getField("thayiCardNumber"));

        Map<String, String> expectedReportFields = EasyMap
                .create("id", "mother id 1")
                .put("serviceProvidedDate", "2012-03-01")
                .put("externalId", "thayi card no 1")
                .put("thayiCardNumber", "thayi card no 1")
                .put("ecId", "ec id 1")
                .map();

        when(rulesFactory.ruleByName(any(String.class))).thenReturn(rule);
        when(rule.apply(any(SafeMap.class))).thenReturn(true);
        when(reportDefinitionLoader.load()).thenReturn(reportDefinitionWithExternalId());
        when(referenceDataRepository.getReferenceData(any(FormSubmission.class), any(ReferenceData.class)))
                .thenReturn(new SafeMap());
        when(reporterFactory.reporterFor("eligible_couple")).thenReturn(reporter);
        when(locationLoader.loadLocationFor("eligible_couple", "ec id 1")).thenReturn(location);

        service.reportFor(submission);

        verify(rule).apply(new SafeMap(expectedReportFields));
        verify(locationLoader).loadLocationFor("eligible_couple", "ec id 1");
        verify(reporter).report("ec id 1", "CONDOM_QTY", location, "2012-03-01", reportData);
    }

    @Test
    public void shouldReportWhenReferenceDataDefinitionIsNotSpecified() throws Exception {
        FormSubmission submission = create()
                .withFormName("child_close")
                .withANMId("anm id 1")
                .withEntityId("child id 1")
                .addFormField("deathDate", "2012-01-01")
                .addFormField("submissionDate", "2012-03-01")
                .addFormField("closeReason", "permanent_relocation")
                .build();
        Location location = new Location("bherya", "Sub Center", "PHC X");
        when(rulesFactory.ruleByName(any(String.class))).thenReturn(rule);
        when(reportDefinitionLoader.load()).thenReturn(reportDefinitionWithoutReferenceDataDefinition());
        when(reporterFactory.reporterFor("child")).thenReturn(reporter);
        when(locationLoader.loadLocationFor("child", "child id 1")).thenReturn(location);
        when(rule.apply(any(SafeMap.class))).thenReturn(true);
        SafeMap reportData = new SafeMap().put("submissionDate", submission.getField("submissionDate"))
                .put("id", submission.entityId())
                .put("closeReason", submission.getField("closeReason"));

        service.reportFor(submission);

        Map<String, String> expectedReportFields = EasyMap
                .create("id", "child id 1")
                .put("closeReason", "permanent_relocation")
                .put("submissionDate", "2012-03-01")
                .put("serviceProvidedDate", "2012-01-01")
                .map();

        verify(rule, times(2)).apply(new SafeMap(expectedReportFields));
        verify(referenceDataRepository, times(0)).getReferenceData(any(FormSubmission.class), any(ReferenceData.class));
        verify(locationLoader).loadLocationFor("child", "child id 1");
        verify(reporter).report(submission.entityId(), "INFANT_LEFT", location, "2012-01-01", reportData);
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

        when(reportDefinitionLoader.load()).thenReturn(reportDefinition());
        when(rulesFactory.ruleByName(any(String.class))).thenReturn(rule);
        when(referenceDataRepository.getReferenceData(any(FormSubmission.class), any(ReferenceData.class))).thenReturn(new SafeMap());
        when(rule.apply(any(SafeMap.class))).thenReturn(false);

        service.reportFor(submission);

        verifyZeroInteractions(reportingService);
        verifyZeroInteractions(locationLoader);
    }

    @Test
    public void shouldReportServiceProvidedPlaceWhenServiceProvidedPlaceFieldIsSpecifiedInTheIndicatorDefinition() throws Exception {
        FormSubmission submission = create()
                .withFormName("anc_registration")
                .withANMId("anm id 1")
                .withEntityId("ec id 1")
                .addFormField("motherId", "mother id 1")
                .addFormField("submissionDate", "2012-03-01")
                .addFormField("registrationPlace", "phc")
                .build();
        Location location = new Location("bherya", "Sub Center", "PHC X");
        when(rulesFactory.ruleByName(any(String.class))).thenReturn(rule);
        when(reportDefinitionLoader.load()).thenReturn(reportDefinitionWithServiceProvidedPlace());
        when(referenceDataRepository.getReferenceData(any(FormSubmission.class), any(ReferenceData.class))).thenReturn(new SafeMap());
        when(reporterFactory.reporterFor("eligible_couple")).thenReturn(reporter);
        when(locationLoader.loadLocationFor("eligible_couple", "mother id 1")).thenReturn(location);
        when(rule.apply(any(SafeMap.class))).thenReturn(true);
        SafeMap reportData = new SafeMap()
                .put("id", submission.entityId())
                .put("submissionDate", submission.getField("submissionDate"))
                .put("motherId", submission.getField("motherId"))
                .put("serviceProvidedPlace", submission.getField("registrationPlace"));

        service.reportFor(submission);

        verify(locationLoader).loadLocationFor("eligible_couple", "mother id 1");
        verify(reporter).report("mother id 1", "NRHM_JSY_REG", location, "2012-03-01", reportData);
    }

    private ReportDefinition reportDefinitionWithServiceProvidedPlace() {
        return new ReportDefinition(
                asList(
                        new FormIndicator("anc_registration",
                                asList(
                                        new ReportIndicator(
                                                "NRHM_JSY_REG",
                                                "eligible_couple",
                                                null,
                                                null,
                                                asList("id", "motherId"),
                                                new ReferenceData("eligible_couple", "id", null),
                                                asList("IsJsyBeneficiaryRule", "ServiceProvidedAtSubCenterRule"),
                                                "registrationPlace", null).withReportEntityIdField("motherId")
                                )
                        )
                )
        );
    }

    private ReportDefinition reportDefinition() {
        return new ReportDefinition(
                asList(
                        new FormIndicator("child_close",
                                asList(
                                        new ReportIndicator(
                                                "INFANT_LEFT",
                                                "child",
                                                null,
                                                null,
                                                asList("id", "closeReason", "submissionDate"),
                                                new ReferenceData("child", "id", asList("dateOfBirth")),
                                                asList("IsChildLessThanOneYearOldRule", "RelocationIsPermanentRule"),
                                                null, null)
                                )
                        )
                )
        );
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
                                                asList("CurrentFPMethodIsCondomRule"),
                                                null, null)
                                )
                        )
                )
        );
    }

    private ReportDefinition reportDefinitionWithExternalId() {
        return new ReportDefinition(
                asList(
                        new FormIndicator("postpartum_family_planning",
                                asList(
                                        new ReportIndicator(
                                                "CONDOM_QTY",
                                                "eligible_couple",
                                                null,
                                                null,
                                                asList("id", "thayiCardNumber", "ecId"),
                                                null,
                                                asList("CurrentFPMethodIsCondomRule"),
                                                null, "thayiCardNumber").withReportEntityIdField("ecId")
                                )
                        )
                )
        );
    }

    private ReportDefinition reportDefinitionWithReportEntityFieldSpecified() {
        return new ReportDefinition(
                asList(
                        new FormIndicator("anc_registration",
                                asList(
                                        new ReportIndicator(
                                                "NRHM_JSY_REG",
                                                "eligible_couple",
                                                null,
                                                null,
                                                asList("id", "motherId"),
                                                new ReferenceData("eligible_couple", "id", null),
                                                asList("IsJsyBeneficiaryRule", "ServiceProvidedAtSubCenterRule"),
                                                null, null).withReportEntityIdField("motherId")
                                )
                        )
                )
        );
    }


    private ReportDefinition reportDefinitionWithoutReferenceDataDefinition() {
        return new ReportDefinition(
                asList(
                        new FormIndicator("child_close",
                                asList(
                                        new ReportIndicator(
                                                "INFANT_LEFT",
                                                "child",
                                                null,
                                                "deathDate",
                                                asList("id", "closeReason", "submissionDate"),
                                                null,
                                                asList("IsChildLessThanOneYearOldRule", "RelocationIsPermanentRule"),
                                                null, null)
                                )
                        )
                )
        );
    }
}
