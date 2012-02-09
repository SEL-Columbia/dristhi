package org.ei.commcare.listener.event;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.ei.commcare.CommcareFormBuilder;
import org.ei.commcare.listener.domain.CommcareForm;
import org.ei.commcare.listener.service.CommCareFormExportService;
import org.ei.commcare.listener.util.CommCareFormContent;
import org.hamcrest.Description;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.Mock;
import org.motechproject.gateway.OutboundEventGateway;
import org.motechproject.model.MotechEvent;

import java.lang.reflect.Type;
import java.util.Map;

import static java.util.Arrays.asList;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.when;

public class CommCareListenerTest {
    @Mock
    CommCareFormExportService formExportService;

    @Mock
    OutboundEventGateway outboundEventGateway;

    private CommCareListener listener;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        listener = new CommCareListener(formExportService, outboundEventGateway);
    }

    @Test
    public void shouldSendAnEventWithTheFormNameAsTheOnlyParameterWhenTheFieldsWeCareAboutAreEmpty() throws Exception {
        CommcareForm form = form().withName("FormName").withContent(new CommCareFormContent(asList("something"), asList("something-else"))).build();
        when(formExportService.fetchForms()).thenReturn(asList(form));

        listener.fetchFromServer();

        verify(outboundEventGateway).sendEventMessage(eventWhichMatches("FormName", "{}"));
    }

    @Test
    public void shouldSendAnEventWithFormNameAndFormDataAsParametersWhichAreTheFieldsSpecifiedInTheFormDefinition() throws Exception {
        CommCareFormContent content = new CommCareFormContent(asList("form.Patient_Name"), asList("Abu"));
        CommcareForm form = form().withName("FormName").withMapping("form.Patient_Name", "Patient").withContent(content).build();
        when(formExportService.fetchForms()).thenReturn(asList(form));

        listener.fetchFromServer();

        verify(outboundEventGateway).sendEventMessage(eventWhichMatches("FormName", "{\"Patient\" : \"Abu\"}"));
    }

    @Test
    public void shouldSendAnEventWithMultipleFieldsInFormDataWhenThereAreMultipleFieldsSpecified() throws Exception {
        CommCareFormContent content = new CommCareFormContent(asList("form.Patient_Name", "form.Patient_Age"), asList("Abu", "23"));

        CommcareForm form = form().withName("FormName").withContent(content)
                .withMapping("form.Patient_Name", "Patient")
                .withMapping("form.Patient_Age", "Age").build();
        when(formExportService.fetchForms()).thenReturn(asList(form));

        listener.fetchFromServer();

        verify(outboundEventGateway).sendEventMessage(eventWhichMatches("FormName", "{\"Patient\" : \"Abu\", \"Age\" : \"23\"}"));
    }

    @Test
    public void shouldSendOneEventForEachFormFound() throws Exception {
        CommCareFormContent content1 = new CommCareFormContent(asList("form.Patient_Name"), asList("Abu"));
        CommcareForm form1 = form().withName("PatientForm").withMapping("form.Patient_Name", "Patient").withContent(content1).build();

        CommCareFormContent content2 = new CommCareFormContent(asList("form.MermaidName"), asList("Ariel"));

        CommcareForm form2 = form().withName("MermaidForm").withMapping("form.MermaidName", "Mermaid").withContent(content2).build();
        when(formExportService.fetchForms()).thenReturn(asList(form1, form2));

        listener.fetchFromServer();

        verify(outboundEventGateway).sendEventMessage(eventWhichMatches("PatientForm", "{\"Patient\" : \"Abu\"}"));
        verify(outboundEventGateway).sendEventMessage(eventWhichMatches("MermaidForm", "{\"Mermaid\" : \"Ariel\"}"));
    }

    private CommcareFormBuilder form() {
        return new CommcareFormBuilder();
    }

    private MotechEvent eventWhichMatches(final String expectedFormName, final String expectedFormDataJson) {
        return argThat(new ArgumentMatcher<MotechEvent>() {
            @Override
            public boolean matches(Object actualEvent) {
                MotechEvent event = (MotechEvent) actualEvent;

                Type mapType = new TypeToken<Map<String, String>>() { }.getType();
                Map actualFormData = new Gson().fromJson(event.getParameters().get(CommCareFormEvent.FORM_DATA_PARAMETER).toString(), mapType);
                Map expectedFormData = new Gson().fromJson(expectedFormDataJson, mapType);

                return expectedFormName.equals(event.getParameters().get(CommCareFormEvent.FORM_NAME_PARAMETER)) &&
                        expectedFormData.equals(actualFormData);
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("FormName=" + expectedFormName + ", FormData=" + expectedFormDataJson);
            }
        });
    }
}
