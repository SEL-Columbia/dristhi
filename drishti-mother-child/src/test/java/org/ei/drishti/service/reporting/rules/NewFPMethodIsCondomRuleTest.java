package org.ei.drishti.service.reporting.rules;

import org.ei.drishti.form.domain.FormSubmission;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.ei.drishti.util.FormSubmissionBuilder.create;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.MockitoAnnotations.initMocks;


public class NewFPMethodIsCondomRuleTest {

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
                .addFormField("currentMethod", "condom")
                .addFormField("newMethod", "ocp")
                .build();

        NewFPMethodIsCondomRule newFPMethodIsCondomRule = new NewFPMethodIsCondomRule();

        boolean didRuleSucceed = newFPMethodIsCondomRule.apply(submission, null, null);

        assertFalse(didRuleSucceed);
    }

    @Test
    public void shouldReturnTrueIfCurrentFPMethodOfTheECIsCondom() {
        FormSubmission submission = create()
                .withFormName("ec_registration")
                .withANMId("anm id 1")
                .withEntityId("ec id 1")
                .addFormField("submissionDate", "2012-03-01")
                .addFormField("currentMethod", "ocp")
                .addFormField("newMethod", "condom")
                .build();
        ArrayList<String> formFields = new ArrayList<>();
        formFields.add("caseId");

        NewFPMethodIsCondomRule newFPMethodIsCondomRule = new NewFPMethodIsCondomRule();
        boolean didRuleSucceed = newFPMethodIsCondomRule.apply(submission, formFields, null);

        assertTrue(didRuleSucceed);
    }
}
