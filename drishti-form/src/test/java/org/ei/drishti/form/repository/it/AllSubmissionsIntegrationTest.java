package org.ei.drishti.form.repository.it;

import org.ei.drishti.form.domain.FormSubmission;
import org.ei.drishti.form.repository.AllSubmissions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static java.util.Arrays.asList;
import static junit.framework.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:applicationContext-drishti-form.xml")
public class AllSubmissionsIntegrationTest {
    @Autowired
    private AllSubmissions formSubmissions;

    @Before
    public void setUp() throws Exception {
        formSubmissions.removeAll();
    }

    @Test
    public void shouldCheckIfFormSubmissionExistsByInstanceId() throws Exception {
        FormSubmission formSubmission = new FormSubmission("anm id 1", "instance id 1", "form name 1", "entity id 1", null, 1L);
        formSubmissions.add(formSubmission);

        assertTrue(formSubmissions.exists("instance id 1"));
        assertFalse(formSubmissions.exists("Invalid Instance Id"));
    }

    @Test
    public void shouldFetchAllFormSubmissionsAfterServerVersion() throws Exception {
        long baseTimeStamp = DateUtil.now().getMillis();

        FormSubmission firstFormSubmission = new FormSubmission("anm id 1", "instance id 1", "form name 1", "entity id 1", null, 0L).withServerVersion(baseTimeStamp);
        formSubmissions.add(firstFormSubmission);

        FormSubmission secondFormSubmission = new FormSubmission("anm id 2", "instance id 2", "form name 1", "entity id 2", null, 1L).withServerVersion(baseTimeStamp + 1);
        formSubmissions.add(secondFormSubmission);

        FormSubmission thirdFormSubmission = new FormSubmission("anm id 3", "instance id 3", "form name 1", "entity id 3", null, 2L).withServerVersion(baseTimeStamp + 2);
        formSubmissions.add(thirdFormSubmission);

        assertEquals(asList(firstFormSubmission, secondFormSubmission, thirdFormSubmission), formSubmissions.findByServerVersion(0L));
        assertEquals(asList(secondFormSubmission, thirdFormSubmission), formSubmissions.findByServerVersion(firstFormSubmission.serverVersion()));
        assertEquals(asList(thirdFormSubmission), formSubmissions.findByServerVersion(secondFormSubmission.serverVersion()));
        assertEquals(0, formSubmissions.findByServerVersion(thirdFormSubmission.serverVersion()).size());
    }
}
