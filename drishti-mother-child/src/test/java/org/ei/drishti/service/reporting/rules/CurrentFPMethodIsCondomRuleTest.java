package org.ei.drishti.service.reporting.rules;

import org.ei.drishti.domain.EligibleCouple;
import org.ei.drishti.form.domain.FormSubmission;
import org.ei.drishti.repository.AllEligibleCouples;
import org.ei.drishti.service.reporting.ReferenceData;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.ArrayList;

import static org.ei.drishti.util.FormSubmissionBuilder.create;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;


public class CurrentFPMethodIsCondomRuleTest {

    @Mock
    private AllEligibleCouples allEligibleCouples;
    @Mock
    private EligibleCouple eligibleCouple;

    @Before
    public void setUp() {
        initMocks(this);
    }

    @Test
    public void shouldReturnFalseWhenCurrentFPMethodOfECIsNotCondom() {
        FormSubmission submission = create()
                .withFormName("ec_registration")
                .withANMId("anm id 1")
                .withEntityId("ec id 1")
                .addFormField("submissionDate", "2012-03-01")
                .build();

        CurrentFPMethodIsCondomRule currentFPMethodIsCondomRule =  new CurrentFPMethodIsCondomRule(allEligibleCouples);
        when(allEligibleCouples.findByCaseId("ec id 1")).thenReturn(eligibleCouple);
        when(eligibleCouple.currentMethod()).thenReturn("ocp");

        boolean didRuleSucceed = currentFPMethodIsCondomRule.apply(submission, null, null);

        assertFalse(didRuleSucceed);
    }

    @Test
    public void shouldReturnTrueIfCurrentFPMethodOfTheECIsCondom() {
        FormSubmission submission = create()
                .withFormName("ec_registration")
                .withANMId("anm id 1")
                .withEntityId("ec id 1")
                .addFormField("submissionDate", "2012-03-01")
                .build();
        ArrayList<String> formFields = new ArrayList<>();
        formFields.add("caseId");
        ArrayList<String> referenceFields = new ArrayList<>();
        ReferenceData referenceData = new ReferenceData("eligible_couple", "caseId", referenceFields);
        CurrentFPMethodIsCondomRule currentFPMethodIsCondomRule = new CurrentFPMethodIsCondomRule(allEligibleCouples);

        when(allEligibleCouples.findByCaseId("ec id 1")).thenReturn(eligibleCouple);
        when(eligibleCouple.currentMethod()).thenReturn("condom");

        boolean didRuleSucceed = currentFPMethodIsCondomRule.apply(submission, formFields, referenceData);

        assertTrue(didRuleSucceed);
    }
}
