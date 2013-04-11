package org.ei.drishti.service;

import com.google.gson.Gson;
import org.ei.drishti.dto.FormSubmission;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.motechproject.util.DateUtil;

import java.util.List;

import static java.util.Arrays.asList;
import static org.ei.drishti.common.AllConstants.Form.*;
import static org.ei.drishti.util.EasyMap.create;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.MockitoAnnotations.initMocks;

public class FormSubmissionServiceTest {
    @Mock
    private DFLService dflService;

    private FormSubmissionService submissionService;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        submissionService = new FormSubmissionService(dflService);
    }

    @Test
    public void shouldSortAllSubmissionsAndSaveEachOne() throws Exception {
        long baseTimeStamp = DateUtil.now().getMillis();
        String paramsForEarlierFormSubmission = new Gson().toJson(create(ANM_ID, "anm id 1").put(INSTANCE_ID, "instance id 1").put(ENTITY_ID, "entity id 1").put(FORM_NAME, "form name").put(TIME_STAMP, String.valueOf(baseTimeStamp)));
        String paramsForLaterFormSubmission = new Gson().toJson(create(ANM_ID, "anm id 2").put(INSTANCE_ID, "instance id 2").put(ENTITY_ID, "entity id 2").put(FORM_NAME, "form name").put(TIME_STAMP, String.valueOf(baseTimeStamp + 1)));
        String paramsForVeryLateFormSubmission = new Gson().toJson(create(ANM_ID, "anm id 2").put(INSTANCE_ID, "instance id 3").put(ENTITY_ID, "entity id 3").put(FORM_NAME, "form name").put(TIME_STAMP, String.valueOf(baseTimeStamp + 2)));
        FormSubmission earlierFormSubmission = new FormSubmission("anm id 1", "instance id 1", "entity id 1", "form name", "form instance 1", baseTimeStamp);
        FormSubmission laterFormSubmission = new FormSubmission("anm id 2", "instance id 2", "entity id 2", "form name", "form instance 2", baseTimeStamp + 1);
        FormSubmission veryLateFormSubmission = new FormSubmission("anm id 2", "instance id 3", "entity id 3", "form name", "form instance 3", baseTimeStamp + 2);
        List<FormSubmission> formSubmissions = asList(laterFormSubmission, earlierFormSubmission, veryLateFormSubmission);

        submissionService.processSubmissions(formSubmissions);

        InOrder inOrder = inOrder(dflService);
        inOrder.verify(dflService).saveForm(paramsForEarlierFormSubmission, "form instance 1");
        inOrder.verify(dflService).saveForm(paramsForLaterFormSubmission, "form instance 2");
        inOrder.verify(dflService).saveForm(paramsForVeryLateFormSubmission, "form instance 3");
        verifyNoMoreInteractions(dflService);
    }
}
