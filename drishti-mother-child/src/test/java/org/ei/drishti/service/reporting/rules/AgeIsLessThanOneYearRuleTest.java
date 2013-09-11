package org.ei.drishti.service.reporting.rules;

import org.ei.drishti.domain.Child;
import org.ei.drishti.form.domain.FormSubmission;
import org.ei.drishti.repository.AllChildren;
import org.ei.drishti.service.reporting.ReferenceData;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.ArrayList;

import static junit.framework.Assert.assertTrue;
import static org.ei.drishti.util.FormSubmissionBuilder.create;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class AgeIsLessThanOneYearRuleTest {

    private AgeIsLessThanOneYearRule ageIsLessThanOneYearRule;

    @Mock
    private AllChildren allChildren;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        ageIsLessThanOneYearRule = new AgeIsLessThanOneYearRule(allChildren);
    }

    @Test
    public void shouldReturnTrueIfAgeIsLessThanOneYear(){
        FormSubmission submission = create()
                .withFormName("child_close")
                .withANMId("anm id 1")
                .withEntityId("child id 1")
                .addFormField("submissionDate", "2012-03-01")
                .addFormField("closeReason", "permanent_relocation")
                .build();
        ArrayList<String> formFields = new ArrayList<>();
        formFields.add("id");
        formFields.add("closeReason");
        formFields.add("submissionDate");
        ArrayList<String> referenceFields = new ArrayList<>();
        referenceFields.add("dateOfBirth");
        ReferenceData referenceData = new ReferenceData("child", "id", referenceFields);

        Child child = new Child("child id 1", "mother id 1", "opv", "2", "female").withDateOfBirth("2012-01-01");

        when(allChildren.findByCaseId("child id 1")).thenReturn(child);

        boolean didRuleApply = ageIsLessThanOneYearRule.apply(submission, formFields, referenceData);

        assertTrue(didRuleApply);
    }

    @Test
    public void shouldReturnFalseIfAgeIsNotLessThanOneYear(){
        FormSubmission submission = create()
                .withFormName("child_close")
                .withANMId("anm id 1")
                .withEntityId("child id 1")
                .addFormField("submissionDate", "2012-03-01")
                .addFormField("closeReason", "permanent_relocation")
                .build();

        ArrayList<String> formFields = new ArrayList<>();
        formFields.add("id");
        formFields.add("closeReason");
        formFields.add("submissionDate");
        ArrayList<String> referenceFields = new ArrayList<>();
        referenceFields.add("dateOfBirth");
        ReferenceData referenceData = new ReferenceData("child", "id", referenceFields);
        Child child = new Child("child id 1", "mother id 1", "opv", "2", "female").withDateOfBirth("2011-01-01");
 
        when(allChildren.findByCaseId("child id 1")).thenReturn(child);

        boolean didRuleApply = ageIsLessThanOneYearRule.apply(submission, formFields, referenceData);

        assertFalse(didRuleApply);
    }
}
