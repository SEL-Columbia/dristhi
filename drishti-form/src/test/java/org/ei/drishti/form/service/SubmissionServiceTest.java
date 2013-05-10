package org.ei.drishti.form.service;

import org.ei.drishti.dto.form.FormSubmissionDTO;
import org.ei.drishti.form.domain.FormSubmission;
import org.ei.drishti.form.repository.AllSubmissions;
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

public class SubmissionServiceTest {
    @Mock
    private AllSubmissions allSubmissions;

    private SubmissionService submissionService;
    private long serverVersion;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        submissionService = new SubmissionService(allSubmissions);
        LocalDate fakeDate = new LocalDate("2012-01-01");
        org.ei.drishti.common.util.DateUtil.fakeIt(fakeDate);
        serverVersion = fakeDate.toDateTimeAtStartOfDay().getMillis();
    }

    @Test
    public void shouldSortAllSubmissionsAndSaveEachOne() throws Exception {
        long baseTimeStamp = DateUtil.now().getMillis();
        FormSubmissionDTO earlierFormSubmissionDTO = new FormSubmissionDTO("anm id 1", "instance id 1", "entity id 1", "form name 1", null, valueOf(baseTimeStamp));
        FormSubmissionDTO laterFormSubmissionDTO = new FormSubmissionDTO("anm id 2", "instance id 2", "entity id 2", "form name 1", null, valueOf(baseTimeStamp + 1));
        FormSubmissionDTO veryLateFormSubmissionDTO = new FormSubmissionDTO("anm id 2", "instance id 3", "entity id 3", "form name 1", null, valueOf(baseTimeStamp + 2));
        FormSubmission earlierFormSubmission = new FormSubmission("anm id 1", "instance id 1", "form name 1", "entity id 1", null, baseTimeStamp, serverVersion);
        FormSubmission laterFormSubmission = new FormSubmission("anm id 2", "instance id 2", "form name 1", "entity id 2", null, baseTimeStamp + 1, serverVersion);
        FormSubmission veryLateFormSubmission = new FormSubmission("anm id 2", "instance id 3", "form name 1", "entity id 3", null, baseTimeStamp + 2, serverVersion);
        List<FormSubmissionDTO> formSubmissionsDTO = asList(laterFormSubmissionDTO, earlierFormSubmissionDTO, veryLateFormSubmissionDTO);
        when(allSubmissions.exists(anyString())).thenReturn(false);

        submissionService.submit(formSubmissionsDTO);

        InOrder inOrder = inOrder(allSubmissions);
        inOrder.verify(allSubmissions).exists("instance id 1");
        inOrder.verify(allSubmissions).add(earlierFormSubmission);
        inOrder.verify(allSubmissions).exists("instance id 2");
        inOrder.verify(allSubmissions).add(laterFormSubmission);
        inOrder.verify(allSubmissions).exists("instance id 3");
        inOrder.verify(allSubmissions).add(veryLateFormSubmission);
        verifyNoMoreInteractions(allSubmissions);
    }

    @Test
    public void shouldNotDelegateFormSubmissionIfAlreadyExists() throws Exception {
        long baseTimeStamp = DateUtil.now().getMillis();
        FormSubmissionDTO firstFormSubmissionDTO = new FormSubmissionDTO("anm id 1", "instance id 1", "entity id 1", "form name 1", null, valueOf(baseTimeStamp));
        FormSubmissionDTO secondFormSubmissionDTO = new FormSubmissionDTO("anm id 2", "instance id 2", "entity id 2", "form name 1", null, valueOf(baseTimeStamp + 1));
        FormSubmission firstFormSubmission = new FormSubmission("anm id 1", "instance id 1", "form name 1", "entity id 1", null, baseTimeStamp, serverVersion);
        FormSubmission secondFormSubmission = new FormSubmission("anm id 2", "instance id 2", "form name 1", "entity id 2", null, baseTimeStamp + 1, serverVersion);
        when(allSubmissions.exists("instance id 1")).thenReturn(true);
        when(allSubmissions.exists("instance id 2")).thenReturn(false);

        submissionService.submit(asList(firstFormSubmissionDTO, secondFormSubmissionDTO));

        InOrder inOrder = inOrder(allSubmissions);
        inOrder.verify(allSubmissions).exists("instance id 1");
        inOrder.verify(allSubmissions, times(0)).add(firstFormSubmission);
        inOrder.verify(allSubmissions).exists("instance id 2");
        inOrder.verify(allSubmissions).add(secondFormSubmission);
        verifyNoMoreInteractions(allSubmissions);
    }

    @Test
    public void shouldFetchFormSubmissionsByGiven() throws Exception {
        long baseTimeStamp = DateUtil.now().getMillis();
        FormSubmissionDTO firstFormSubmissionDTO = new FormSubmissionDTO("anm id 1", "instance id 1", "entity id 1", "form name 1", "", valueOf(baseTimeStamp)).withServerVersion("0");
        FormSubmissionDTO secondFormSubmissionDTO = new FormSubmissionDTO("anm id 2", "instance id 2", "entity id 2", "form name 1", "", valueOf(baseTimeStamp + 1)).withServerVersion("1");
        FormSubmission firstFormSubmission = new FormSubmission("anm id 1", "instance id 1", "form name 1", "entity id 1", null, baseTimeStamp, 0L);
        FormSubmission secondFormSubmission = new FormSubmission("anm id 2", "instance id 2", "form name 1", "entity id 2", null, baseTimeStamp + 1, 1L);
        when(allSubmissions.findByServerVersion(0L)).thenReturn(asList(firstFormSubmission, secondFormSubmission));

        List<FormSubmissionDTO> formSubmissionDTOs = submissionService.fetch(0L);

        assertEquals(asList(firstFormSubmissionDTO, secondFormSubmissionDTO), formSubmissionDTOs);
    }
}
