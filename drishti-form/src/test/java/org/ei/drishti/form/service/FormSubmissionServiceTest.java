package org.ei.drishti.form.service;

import org.ei.drishti.dto.form.FormSubmissionDTO;
import org.ei.drishti.form.domain.FormSubmission;
import org.ei.drishti.form.repository.AllFormSubmissions;
import org.ei.drishti.form.repository.AllPoc_tableRepository;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.motechproject.util.DateUtil;

import java.util.List;

import static java.lang.String.valueOf;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class FormSubmissionServiceTest {
    @Mock
    private AllFormSubmissions allFormSubmissions;
private AllPoc_tableRepository allPoc_tableRepository;
    private FormSubmissionService formSubmissionService;
    private long serverVersion;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        formSubmissionService = new FormSubmissionService(allFormSubmissions,allPoc_tableRepository);
        LocalDate fakeDate = new LocalDate("2012-01-01");
        org.ei.drishti.common.util.DateUtil.fakeIt(fakeDate);
        serverVersion = fakeDate.toDateTimeAtStartOfDay().getMillis();
    }

    @Test
    public void shouldSortAllSubmissionsAndSaveEachOne() throws Exception {
        long baseTimeStamp = DateUtil.now().getMillis();
        FormSubmissionDTO earlierFormSubmissionDTO = new FormSubmissionDTO("anm id 1", "instance id 1", "entity id 1", "form name 1", null, valueOf(baseTimeStamp), "1","testvillage");
        FormSubmissionDTO laterFormSubmissionDTO = new FormSubmissionDTO("anm id 2", "instance id 2", "entity id 2", "form name 1", null, valueOf(baseTimeStamp + 1), "1","testvillage");
        FormSubmissionDTO veryLateFormSubmissionDTO = new FormSubmissionDTO("anm id 2", "instance id 3", "entity id 3", "form name 1", null, valueOf(baseTimeStamp + 2), "1","testvillage");
        FormSubmission earlierFormSubmission = new FormSubmission("anm id 1", "instance id 1", "form name 1", "entity id 1", baseTimeStamp, "1","testvillage", null, serverVersion);
        FormSubmission laterFormSubmission = new FormSubmission("anm id 2", "instance id 2", "form name 1", "entity id 2", baseTimeStamp + 1, "1","testvillage", null, serverVersion);
        FormSubmission veryLateFormSubmission = new FormSubmission("anm id 2", "instance id 3", "form name 1", "entity id 3", baseTimeStamp + 2, "1","testvillage", null, serverVersion);
        List<FormSubmissionDTO> formSubmissionsDTO = asList(laterFormSubmissionDTO, earlierFormSubmissionDTO, veryLateFormSubmissionDTO);
        when(allFormSubmissions.exists(anyString())).thenReturn(false);

        formSubmissionService.submit(formSubmissionsDTO);

        InOrder inOrder = inOrder(allFormSubmissions);
        inOrder.verify(allFormSubmissions).exists("instance id 1");
        inOrder.verify(allFormSubmissions).add(earlierFormSubmission);
        inOrder.verify(allFormSubmissions).exists("instance id 2");
        inOrder.verify(allFormSubmissions).add(laterFormSubmission);
        inOrder.verify(allFormSubmissions).exists("instance id 3");
        inOrder.verify(allFormSubmissions).add(veryLateFormSubmission);
        verifyNoMoreInteractions(allFormSubmissions);
    }

    @Test
    public void shouldNotDelegateFormSubmissionIfAlreadyExists() throws Exception {
        long baseTimeStamp = DateUtil.now().getMillis();
        FormSubmissionDTO firstFormSubmissionDTO = new FormSubmissionDTO("anm id 1", "instance id 1", "entity id 1", "form name 1", null, valueOf(baseTimeStamp), "1","testvillage");
        FormSubmissionDTO secondFormSubmissionDTO = new FormSubmissionDTO("anm id 2", "instance id 2", "entity id 2", "form name 1", null, valueOf(baseTimeStamp + 1), "1","testvillage");
        FormSubmission firstFormSubmission = new FormSubmission("anm id 1", "instance id 1", "form name 1", "entity id 1", baseTimeStamp, "1","testvillage", null, serverVersion);
        FormSubmission secondFormSubmission = new FormSubmission("anm id 2", "instance id 2", "form name 1", "entity id 2", baseTimeStamp + 1, "1","testvillage", null, serverVersion);
        when(allFormSubmissions.exists("instance id 1")).thenReturn(true);
        when(allFormSubmissions.exists("instance id 2")).thenReturn(false);

        formSubmissionService.submit(asList(firstFormSubmissionDTO, secondFormSubmissionDTO));

        InOrder inOrder = inOrder(allFormSubmissions);
        inOrder.verify(allFormSubmissions).exists("instance id 1");
        inOrder.verify(allFormSubmissions, times(0)).add(firstFormSubmission);
        inOrder.verify(allFormSubmissions).exists("instance id 2");
        inOrder.verify(allFormSubmissions).add(secondFormSubmission);
        verifyNoMoreInteractions(allFormSubmissions);
    }

    @Test
    public void shouldFetchFormSubmissionsByGiven() throws Exception {
        long baseTimeStamp = DateUtil.now().getMillis();
        FormSubmissionDTO firstFormSubmissionDTO = new FormSubmissionDTO("anm id 1", "instance id 1", "entity id 1", "form name 1", "", valueOf(baseTimeStamp), "1","testvillage").withServerVersion("0");
        FormSubmissionDTO secondFormSubmissionDTO = new FormSubmissionDTO("anm id 2", "instance id 2", "entity id 2", "form name 1", "", valueOf(baseTimeStamp + 1), "1","testvillage").withServerVersion("1");
        FormSubmission firstFormSubmission = new FormSubmission("anm id 1", "instance id 1", "form name 1", "entity id 1", baseTimeStamp, "1","testvillage", null, 0L);
        FormSubmission secondFormSubmission = new FormSubmission("anm id 2", "instance id 2", "form name 1", "entity id 2", baseTimeStamp + 1, "1","testvillage", null, 1L);
        when(allFormSubmissions.findByServerVersion(0L)).thenReturn(asList(firstFormSubmission, secondFormSubmission));

        List<FormSubmissionDTO> formSubmissionDTOs = formSubmissionService.fetch(0L);

        assertEquals(asList(firstFormSubmissionDTO, secondFormSubmissionDTO), formSubmissionDTOs);
    }

    @Test
    public void shouldFetchAllFormSubmissions() throws Exception {
        long baseTimeStamp = DateUtil.now().getMillis();
        FormSubmission firstFormSubmission = new FormSubmission("anm id 1", "instance id 1", "form name 1", "entity id 1", baseTimeStamp, "1","testvillage", null, 0L);
        FormSubmission secondFormSubmission = new FormSubmission("anm id 2", "instance id 2", "form name 1", "entity id 2", baseTimeStamp + 1, "1","testvillage", null, 1L);
        when(allFormSubmissions.allFormSubmissions(0, 2)).thenReturn(asList(firstFormSubmission, secondFormSubmission));

        List<FormSubmission> formSubmissions = formSubmissionService.getAllSubmissions(0L, 2);

        assertEquals(asList(firstFormSubmission, secondFormSubmission), formSubmissions);
    }
}
