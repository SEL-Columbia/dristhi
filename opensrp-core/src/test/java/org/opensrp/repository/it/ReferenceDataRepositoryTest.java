package org.opensrp.repository.it;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.opensrp.common.util.EasyMap.mapOf;
import static org.opensrp.util.FormSubmissionBuilder.create;

import java.util.Collections;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opensrp.domain.Child;
import org.opensrp.form.domain.FormSubmission;
import org.opensrp.repository.AllChildren;
import org.opensrp.service.reporting.ReferenceData;
import org.opensrp.service.reporting.rules.ReferenceDataRepository;
import org.opensrp.util.SafeMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:test-applicationContext-opensrp.xml")
public class ReferenceDataRepositoryTest {

    @Autowired
    private ReferenceDataRepository referenceDataRepository;

    @Autowired
    private AllChildren children;

    @Before
    public void setUp() throws Exception {
        children.removeAll();
    }

    @Test
    public void shouldReturnNullWhenThereIsNoEntity() {
        FormSubmission submission = create()
                .withFormName("ec_registration")
                .withANMId("anm id 1")
                .withEntityId("ec id 1")
                .addFormField("submissionDate", "2012-03-01")
                .build();

        SafeMap referenceData = referenceDataRepository.getReferenceData(submission, new ReferenceData("eligible_couple", "id", Collections.<String>emptyList()));

        assertTrue(referenceData.isEmpty());
    }

    @Test
    public void shouldReturnNullValueForReferenceFieldsFromEntityWhenThereIsAFieldButNoValue() {
        FormSubmission submission = create()
                .withFormName("child_close")
                .withANMId("anm id 1")
                .withEntityId("child id 1")
                .addFormField("submissionDate", "2012-03-01")
                .addFormField("closeReason", "permanent_relocation")
                .build();

        Child child = new Child("child id 1", "MOTHER-CASE-1", "bcg", "3", "male")
                .withAnm("anm id 1").withDetails(mapOf("key", "value"));
        children.add(child);

        SafeMap referenceData = referenceDataRepository.getReferenceData(submission,
                new ReferenceData("child", "id", asList("dateOfBirth")));

        assertNull(referenceData.get("dateOfBirth"));
    }


    @Test
    public void shouldReturnReferenceFieldsWithValueFromEntityWhenThereIsFieldWithValue() {
        FormSubmission submission = create()
                .withFormName("child_close")
                .withANMId("anm id 1")
                .withEntityId("child id 1")
                .addFormField("submissionDate", "2012-03-01")
                .addFormField("closeReason", "permanent_relocation")
                .build();

        Child child = new Child("child id 1", "MOTHER-CASE-1", "bcg", "3", "male")
                .withAnm("anm id 1")
                .withDateOfBirth("2013-01-01")
                .withLocation("bherya","subcenter","phc");
        children.add(child);

        SafeMap referenceData = referenceDataRepository.getReferenceData(
                submission,
                new ReferenceData("child", "id", asList("dateOfBirth", "village")));

        assertEquals("2013-01-01",referenceData.get("dateOfBirth"));
        assertEquals("bherya",referenceData.get("village"));
    }

    @Test
    public void shouldReturnReferenceFieldsWithValueFromDetailsWhenThereIsFieldWithValue() {
        FormSubmission submission = create()
                .withFormName("child_close")
                .withANMId("anm id 1")
                .withEntityId("child id 1")
                .addFormField("submissionDate", "2012-03-01")
                .addFormField("closeReason", "permanent_relocation")
                .build();

        Child child = new Child("child id 1", "MOTHER-CASE-1", "bcg", "3", "male")
                .withLocation("bherya", "sc", "phc")
                .withDetails(mapOf("immunizationDate", "2013-01-01"));
        children.add(child);

        SafeMap referenceData = referenceDataRepository.getReferenceData(
                submission,
                new ReferenceData("child", "id", asList("immunizationDate", "village")));

        assertEquals("2013-01-01",referenceData.get("immunizationDate"));
        assertEquals("bherya",referenceData.get("village"));
    }

    @Test
    public void shouldReturnNullWhenThereIsNoField() {
        FormSubmission submission = create()
                .withFormName("child_close")
                .withANMId("anm id 1")
                .withEntityId("child id 1")
                .addFormField("submissionDate", "2012-03-01")
                .addFormField("closeReason", "permanent_relocation")
                .build();

        Child child = new Child("child id 1", "MOTHER-CASE-1", "bcg", "3", "male")
                .withLocation("bherya", "sc", "phc")
                .withDetails(mapOf("immunizationDate", "2013-01-01"));
        children.add(child);

        SafeMap referenceData = referenceDataRepository.getReferenceData(
                submission,
                new ReferenceData("child", "id", asList("fieldNotPresent", "village")));

        assertEquals(null,referenceData.get("fieldNotPresent"));
        assertEquals("bherya",referenceData.get("village"));
    }
}