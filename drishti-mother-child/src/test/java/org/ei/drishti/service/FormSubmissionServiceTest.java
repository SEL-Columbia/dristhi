package org.ei.drishti.service;

import com.google.gson.Gson;
import org.ei.drishti.dto.form.FormSubmission;
import org.ei.drishti.service.formSubmissionHandler.FormSubmissionRouter;
import org.ei.drishti.util.FormSubmissionBuilder;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.motechproject.util.DateUtil;

import java.util.List;

import static java.util.Arrays.asList;
import static org.ei.drishti.common.AllConstants.Form.*;
import static org.ei.drishti.util.EasyMap.create;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class FormSubmissionServiceTest {
    @Mock
    private DFLService dflService;
    @Mock
    private FormSubmissionRouter formSubmissionRouter;

    private FormSubmissionService submissionService;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        submissionService = new FormSubmissionService(dflService, formSubmissionRouter);
    }

    @Test
    public void shouldSortAllSubmissionsAndSaveEachOne() throws Exception {
        long baseTimeStamp = DateUtil.now().getMillis();
        String paramsForEarlierFormSubmission = new Gson().toJson(create(ANM_ID, "anm id 1").put(INSTANCE_ID, "instance id 1").put(ENTITY_ID, "entity id 1").put(FORM_NAME, "form name 1").put(TIME_STAMP, String.valueOf(baseTimeStamp)).map());
        String paramsForLaterFormSubmission = new Gson().toJson(create(ANM_ID, "anm id 2").put(INSTANCE_ID, "instance id 2").put(ENTITY_ID, "entity id 2").put(FORM_NAME, "form name 1").put(TIME_STAMP, String.valueOf(baseTimeStamp + 1)).map());
        String paramsForVeryLateFormSubmission = new Gson().toJson(create(ANM_ID, "anm id 2").put(INSTANCE_ID, "instance id 3").put(ENTITY_ID, "entity id 3").put(FORM_NAME, "form name 1").put(TIME_STAMP, String.valueOf(baseTimeStamp + 2)).map());
        FormSubmission earlierFormSubmission = FormSubmissionBuilder.create().withANMId("anm id 1").addFormField("field 1", "value 1").withTimeStamp(String.valueOf(baseTimeStamp)).build();
        FormSubmission laterFormSubmission = FormSubmissionBuilder.create().withANMId("anm id 2").withInstanceId("instance id 2").withEntityId("entity id 2").addFormField("field 1", "value 2").withTimeStamp(String.valueOf(baseTimeStamp + 1)).build();
        FormSubmission veryLateFormSubmission = FormSubmissionBuilder.create().withANMId("anm id 2").withInstanceId("instance id 3").withEntityId("entity id 3").addFormField("field 1", "value 3").withTimeStamp(String.valueOf(baseTimeStamp + 2)).build();
        List<FormSubmission> formSubmissions = asList(laterFormSubmission, earlierFormSubmission, veryLateFormSubmission);

        submissionService.processSubmissions(formSubmissions);

        InOrder inOrder = inOrder(dflService);
        inOrder.verify(dflService).saveForm(paramsForEarlierFormSubmission, new Gson().toJson(earlierFormSubmission.instance()));
        inOrder.verify(dflService).saveForm(paramsForLaterFormSubmission, new Gson().toJson(laterFormSubmission.instance()));
        inOrder.verify(dflService).saveForm(paramsForVeryLateFormSubmission, new Gson().toJson(veryLateFormSubmission.instance()));
        verifyNoMoreInteractions(dflService);
    }

    @Test
    public void shouldDelegateFormSubmissionHandlerAfterSave() throws Exception {
        long baseTimeStamp = DateUtil.now().getMillis();
        FormSubmission formSubmission = new FormSubmission("anm id 1", "instance id 1", "entity id 1", "form name", null, String.valueOf(baseTimeStamp));

        submissionService.processSubmissions(asList(formSubmission));

        verify(formSubmissionRouter).route(formSubmission);
    }
}
