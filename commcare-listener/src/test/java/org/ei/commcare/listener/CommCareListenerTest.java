package org.ei.commcare.listener;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.ei.commcare.api.domain.CommCareFormContent;
import org.ei.commcare.api.domain.CommcareFormInstance;
import org.ei.commcare.listener.event.CommCareFormEvent;
import org.ei.commcare.api.service.CommCareFormImportService;
import org.hamcrest.Description;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.Mock;
import org.motechproject.gateway.OutboundEventGateway;
import org.motechproject.model.MotechEvent;
import org.powermock.api.mockito.PowerMockito;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static org.ei.commcare.api.domain.CommCareFormContent.FORM_ID_FIELD;
import static org.ei.commcare.listener.event.CommCareFormEvent.FORM_DATA_PARAMETER;
import static org.ei.commcare.listener.event.CommCareFormEvent.FORM_NAME_PARAMETER;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class CommCareListenerTest {
    @Mock
    CommCareFormImportService formImportService;

    @Mock
    OutboundEventGateway outboundEventGateway;

    public static final String FORM_ID = "FORM-ID-1";
    private CommCareListener listener;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        listener = new CommCareListener(formImportService, outboundEventGateway);
    }

    @Test
    public void shouldSendAnEventWithTheFormNameAsTheOnlyParameterWhenTheFieldsWeCareAboutAreEmpty() throws Exception {
        CommcareFormInstance formInstance = form().withName("FormName").withContent(content(asList("something"), asList("something-else"))).build();
        PowerMockito.when(formImportService.fetchForms()).thenReturn(asList(formInstance));

        listener.fetchFromServer();

        verify(outboundEventGateway).sendEventMessage(eventWhichMatches("FormName", "{}"));
    }

    @Test
    public void shouldSendAnEventWithFormNameAndFormDataAsParametersWhichAreTheFieldsSpecifiedInTheFormDefinition() throws Exception {
        CommCareFormContent content = content(asList("formInstance.Patient_Name"), asList("Abu"));
        CommcareFormInstance formInstance = form().withName("FormName").withMapping("formInstance.Patient_Name", "Patient").withContent(content).build();
        PowerMockito.when(formImportService.fetchForms()).thenReturn(asList(formInstance));

        listener.fetchFromServer();

        verify(outboundEventGateway).sendEventMessage(eventWhichMatches("FormName", "{\"Patient\" : \"Abu\"}"));
    }

    @Test
    public void shouldSendAnEventWithMultipleFieldsInFormDataWhenThereAreMultipleFieldsSpecified() throws Exception {
        CommCareFormContent content = content(asList("formInstance.Patient_Name", "formInstance.Patient_Age"), asList("Abu", "23"));

        CommcareFormInstance formInstance = form().withName("FormName").withContent(content)
                .withMapping("formInstance.Patient_Name", "Patient")
                .withMapping("formInstance.Patient_Age", "Age").build();
        PowerMockito.when(formImportService.fetchForms()).thenReturn(asList(formInstance));

        listener.fetchFromServer();

        verify(outboundEventGateway).sendEventMessage(eventWhichMatches("FormName", "{\"Patient\" : \"Abu\", \"Age\" : \"23\"}"));
    }

    @Test
    public void shouldSendOneEventForEachFormFound() throws Exception {
        CommCareFormContent content1 = content(asList("form.Patient_Name"), asList("Abu"));
        CommcareFormInstance formInstance1 = form().withName("PatientForm").withMapping("form.Patient_Name", "Patient").withContent(content1).build();

        CommCareFormContent content2 = content(asList("form.MermaidName"), asList("Ariel"));

        CommcareFormInstance formInstance2 = form().withName("MermaidForm").withMapping("form.MermaidName", "Mermaid").withContent(content2).build();
        PowerMockito.when(formImportService.fetchForms()).thenReturn(asList(formInstance1, formInstance2));

        listener.fetchFromServer();

        verify(outboundEventGateway).sendEventMessage(eventWhichMatches("PatientForm", "{\"Patient\" : \"Abu\"}"));
        verify(outboundEventGateway).sendEventMessage(eventWhichMatches("MermaidForm", "{\"Mermaid\" : \"Ariel\"}"));
    }

    private CommCareFormContent content(List<String> headers, List<String> values) {
        ArrayList<String> headersWithID = new ArrayList<String>(headers);
        headersWithID.add(FORM_ID_FIELD);

        ArrayList<String> valuesWithID = new ArrayList<String>(values);
        valuesWithID.add(FORM_ID);

        return new CommCareFormContent(headersWithID, valuesWithID);
    }

    private CommCareFormBuilder form() {
        return new CommCareFormBuilder();
    }

    private MotechEvent eventWhichMatches(final String expectedFormName, final String expectedFormDataJson) {
        return argThat(new ArgumentMatcher<MotechEvent>() {
            @Override
            public boolean matches(Object actualEvent) {
                MotechEvent event = (MotechEvent) actualEvent;

                Type mapType = new TypeToken<Map<String, String>>() { }.getType();
                Map actualFormData = new Gson().fromJson(event.getParameters().get(FORM_DATA_PARAMETER).toString(), mapType);
                Map expectedFormData = new Gson().fromJson(expectedFormDataJson, mapType);

                return expectedFormName.equals(event.getParameters().get(FORM_NAME_PARAMETER)) &&
                        expectedFormData.equals(actualFormData) &&
                        FORM_ID.equals(event.getParameters().get(CommCareFormEvent.FORM_ID_PARAMETER));
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("FormName=" + expectedFormName + ", FormData=" + expectedFormDataJson + ", FormID=" + FORM_ID);
            }
        });
    }
}
