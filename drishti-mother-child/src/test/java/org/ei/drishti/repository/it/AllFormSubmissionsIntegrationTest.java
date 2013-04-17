package org.ei.drishti.repository.it;

import org.ei.drishti.domain.form.FormSubmission;
import org.ei.drishti.repository.AllFormSubmissions;
import org.ei.drishti.util.FormSubmissionBuilder;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static java.util.Arrays.asList;
import static junit.framework.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:test-applicationContext-drishti.xml")
public class AllFormSubmissionsIntegrationTest {
    @Autowired
    private AllFormSubmissions formSubmissions;

    @Before
    public void setUp() throws Exception {
        formSubmissions.removeAll();
    }

    @Test
    public void shouldFetchFormSubmissionsBasedOnANMIDAndTimeStamp() throws Exception {
        long baseTimeStamp = DateUtil.now().getMillis();
        FormSubmission firstFormSubmission = FormSubmissionBuilder.create().withANMId("ANM 1").addFormField("field 1", "value 1").withTimeStamp(baseTimeStamp).build();
        formSubmissions.add(firstFormSubmission);

        FormSubmission secondFormSubmission = FormSubmissionBuilder.create().withANMId("ANM 1").withInstanceId("instance id 2").withEntityId("entity id 2").addFormField("field 1", "value 2").withTimeStamp(baseTimeStamp + 1).build();
        formSubmissions.add(secondFormSubmission);

        FormSubmission thirdFormSubmission = FormSubmissionBuilder.create().withANMId("ANM 1").withInstanceId("instance id 3").withEntityId("entity id 3").addFormField("field 1", "value 3").withTimeStamp(baseTimeStamp + 2).build();
        formSubmissions.add(thirdFormSubmission);

        assertEquals(asList(firstFormSubmission, secondFormSubmission, thirdFormSubmission), formSubmissions.findByANMIDAndTimeStamp("ANM 1", 0L));
        assertEquals(asList(secondFormSubmission, thirdFormSubmission), formSubmissions.findByANMIDAndTimeStamp("ANM 1", firstFormSubmission.timestamp()));
        assertEquals(asList(thirdFormSubmission), formSubmissions.findByANMIDAndTimeStamp("ANM 1", secondFormSubmission.timestamp()));

        assertEquals(0, formSubmissions.findByANMIDAndTimeStamp("ANM 1", thirdFormSubmission.timestamp()).size());
    }
}
