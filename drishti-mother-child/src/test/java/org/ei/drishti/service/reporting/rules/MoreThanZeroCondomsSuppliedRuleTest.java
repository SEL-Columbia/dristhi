package org.ei.drishti.service.reporting.rules;

import org.ei.drishti.form.domain.FormSubmission;
import org.junit.Before;
import org.junit.Test;

import static org.ei.drishti.util.FormSubmissionBuilder.create;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class MoreThanZeroCondomsSuppliedRuleTest {

    private MoreThanZeroCondomsSuppliedRule rule;

    @Before
    public void setUp() throws Exception {
        rule = new MoreThanZeroCondomsSuppliedRule();
    }

    @Test
    public void shouldReturnFalseWhenNoCondomsAreSupplied() throws Exception {
        FormSubmission submission = create()
                .withFormName("renew_fp_product")
                .withANMId("anm id 1")
                .withEntityId("ec id 1")
                .addFormField("submissionDate", "2012-03-01")
                .addFormField("currentMethod", "condom")
                .addFormField("numberOfCondomsSupplied", null)
                .build();
        assertFalse(rule.apply(submission, null, null));

        submission = create()
                .withFormName("renew_fp_product")
                .withANMId("anm id 1")
                .withEntityId("ec id 1")
                .addFormField("submissionDate", "2012-03-01")
                .addFormField("currentMethod", "condom")
                .addFormField("numberOfCondomsSupplied", "")
                .build();

        assertFalse(rule.apply(submission, null, null));
    }

    @Test
    public void shouldReturnFalseWhenZeroCondomsAreSupplied() throws Exception {
        FormSubmission submission = create()
                .withFormName("renew_fp_product")
                .withANMId("anm id 1")
                .withEntityId("ec id 1")
                .addFormField("submissionDate", "2012-03-01")
                .addFormField("currentMethod", "condom")
                .addFormField("numberOfCondomsSupplied", "0")
                .build();

        assertFalse(rule.apply(submission, null, null));
    }

    @Test
    public void shouldReturnTrueWhenMoreThanZeroCondomsAreSupplied() throws Exception {
        FormSubmission submission = create()
                .withFormName("renew_fp_product")
                .withANMId("anm id 1")
                .withEntityId("ec id 1")
                .addFormField("submissionDate", "2012-03-01")
                .addFormField("currentMethod", "condom")
                .addFormField("numberOfCondomsSupplied", "1")
                .build();

        assertTrue(rule.apply(submission, null, null));
    }
}
