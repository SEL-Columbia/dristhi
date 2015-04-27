package org.opensrp.service.reporting;

import static java.util.Arrays.asList;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.opensrp.util.FormSubmissionBuilder.create;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.opensrp.common.util.EasyMap;
import org.opensrp.form.domain.FormSubmission;
import org.opensrp.service.reporting.rules.IReferenceDataRepository;
import org.opensrp.service.reporting.rules.IRule;
import org.opensrp.util.SafeMap;

public class MCTSReportServiceTest {
    @Mock
    private IRulesFactory rulesFactory;
    @Mock
    private IRule rule;
    @Mock
    private IMCTSReportDefinitionLoader reportDefinitionLoader;
    @Mock
    private MCTSReporter reporter;
    @Mock
    private IReferenceDataRepository referenceDataRepository;

    private MCTSReportService service;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        service = new MCTSReportService(rulesFactory, referenceDataRepository,
                reportDefinitionLoader, reporter);
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
                .withFormName("anc_visit")
                .withANMId("anm id 1")
                .withEntityId("mother id 1")
                .addFormField("submissionDate", "2012-03-02")
                .addFormField("ancVisitDate", "2012-03-01")
                .addFormField("ancVisitNumber", "1")
                .build();
        when(rulesFactory.ruleByName(any(String.class))).thenReturn(rule);
        when(reportDefinitionLoader.load()).thenReturn(reportDefinition());
        when(referenceDataRepository
                .getReferenceData(any(FormSubmission.class), any(ReferenceData.class)))
                .thenReturn(
                        new SafeMap(EasyMap
                                .create("thayiCardNumber", "thayi card 1")
                                .put("registrationDate", "2012-02-01").map())
                );
        when(rule.apply(any(SafeMap.class))).thenReturn(true);

        service.reportFor(submission);

        verify(reporter).report("mother id 1", "thayi card 1", "ANC1", "2012-02-01", "2012-03-01");
    }

    @Test
    public void shouldAddReferenceFieldsToReportData() throws Exception {
        FormSubmission submission = create()
                .withFormName("anc_visit")
                .withANMId("anm id 1")
                .withEntityId("mother id 1")
                .addFormField("submissionDate", "2012-03-02")
                .addFormField("ancVisitDate", "2012-03-01")
                .addFormField("ancVisitNumber", "1")
                .build();
        when(rulesFactory.ruleByName(any(String.class))).thenReturn(rule);
        when(reportDefinitionLoader.load()).thenReturn(reportDefinition());
        when(referenceDataRepository
                .getReferenceData(any(FormSubmission.class), any(ReferenceData.class)))
                .thenReturn(
                        new SafeMap(EasyMap
                                .create("thayiCardNumber", "thayi card 1")
                                .put("registrationDate", "2012-02-01").map())
                );

        service.reportFor(submission);

        verify(rule).apply(new SafeMap(EasyMap
                .create("thayiCardNumber", "thayi card 1")
                .put("id", "mother id 1")
                .put("registrationDate", "2012-02-01")
                .put("ancVisitNumber", "1")
                .put("submissionDate", "2012-03-02")
                .map()));
    }

    private MCTSReportDefinition reportDefinition() {
        return new MCTSReportDefinition(
                asList(
                        new MCTSFormIndicator("anc_visit",
                                asList(new MCTSReportIndicator(
                                        "ANC1", "mother", "ancVisitDate",
                                        asList("id", "ancVisitNumber"),
                                        new ReferenceData("mother", "id",
                                                asList("thayiCardNumber", "registrationDate")),
                                        asList("IsChildLessThanOneYearOldRule")
                                ))
                        )
                )
        );
    }
}
