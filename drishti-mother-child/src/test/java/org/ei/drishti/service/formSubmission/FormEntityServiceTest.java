package org.ei.drishti.service.formSubmission;

import com.google.gson.Gson;
import org.ei.drishti.domain.FormExportToken;
import org.ei.drishti.form.domain.FormSubmission;
import org.ei.drishti.repository.AllFormExportTokens;
import org.ei.drishti.service.formSubmission.handler.FormSubmissionRouter;
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

public class FormEntityServiceTest {
    @Mock
    private ZiggyService ziggyService;
    @Mock
    private FormSubmissionRouter formSubmissionRouter;
    @Mock
    private AllFormExportTokens allFormExportTokens;

    private FormEntityService submissionService;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        submissionService = new FormEntityService(ziggyService, allFormExportTokens);
    }

    @Test
    public void shouldSortAllSubmissionsAndSaveEachOne() throws Exception {
        long baseTimeStamp = DateUtil.now().getMillis();
        String paramsForEarlierFormSubmission = new Gson().toJson(create(ANM_ID, "anm id 1").put(INSTANCE_ID, "instance id 1").put(ENTITY_ID, "entity id 1").put(FORM_NAME, "form name 1").put(TIME_STAMP, String.valueOf(baseTimeStamp)).put(SERVER_VERSION, String.valueOf(1L)).map());
        String paramsForLaterFormSubmission = new Gson().toJson(create(ANM_ID, "anm id 2").put(INSTANCE_ID, "instance id 2").put(ENTITY_ID, "entity id 2").put(FORM_NAME, "form name 1").put(TIME_STAMP, String.valueOf(baseTimeStamp + 1)).put(SERVER_VERSION, String.valueOf(2L)).map());
        String paramsForVeryLateFormSubmission = new Gson().toJson(create(ANM_ID, "anm id 2").put(INSTANCE_ID, "instance id 3").put(ENTITY_ID, "entity id 3").put(FORM_NAME, "form name 1").put(TIME_STAMP, String.valueOf(baseTimeStamp + 2)).put(SERVER_VERSION, String.valueOf(3L)).map());
        FormSubmission earlierFormSubmission = FormSubmissionBuilder.create().withANMId("anm id 1").addFormField("field 1", "value 1").withTimeStamp(baseTimeStamp).withServerVersion(1L).build();
        FormSubmission laterFormSubmission = FormSubmissionBuilder.create().withANMId("anm id 2").withInstanceId("instance id 2").withEntityId("entity id 2").addFormField("field 1", "value 2").withTimeStamp(baseTimeStamp + 1).withServerVersion(2L).build();
        FormSubmission veryLateFormSubmission = FormSubmissionBuilder.create().withANMId("anm id 2").withInstanceId("instance id 3").withEntityId("entity id 3").addFormField("field 1", "value 3").withTimeStamp(baseTimeStamp + 2).withServerVersion(3L).build();
        List<FormSubmission> formSubmissions = asList(laterFormSubmission, earlierFormSubmission, veryLateFormSubmission);
        FormExportToken formExportToken = new FormExportToken(0L);
        when(allFormExportTokens.getAll()).thenReturn(asList(formExportToken));

        submissionService.process(formSubmissions);

        InOrder inOrder = inOrder(ziggyService, allFormExportTokens);
        inOrder.verify(ziggyService).saveForm(paramsForEarlierFormSubmission, new Gson().toJson(earlierFormSubmission.instance()));
        inOrder.verify(allFormExportTokens).update(formExportToken.withVersion(1L));
        inOrder.verify(ziggyService).saveForm(paramsForLaterFormSubmission, new Gson().toJson(laterFormSubmission.instance()));
        inOrder.verify(allFormExportTokens).update(formExportToken.withVersion(2L));
        inOrder.verify(ziggyService).saveForm(paramsForVeryLateFormSubmission, new Gson().toJson(veryLateFormSubmission.instance()));
        inOrder.verify(allFormExportTokens).update(formExportToken.withVersion(3L));
        verifyNoMoreInteractions(ziggyService);
    }
}
