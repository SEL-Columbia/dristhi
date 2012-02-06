package org.ei.commcare.listener.event;

import org.ei.commcare.CommcareFormBuilder;
import org.ei.commcare.listener.domain.CommcareForm;
import org.ei.commcare.listener.service.CommCareFormExportService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.gateway.OutboundEventGateway;
import org.motechproject.model.MotechEvent;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.ei.commcare.listener.event.CommCareFormEvent.FORM_NAME_PARAMETER;
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
        CommcareForm form = form().withName("FormName").withContent("<some_xml/>").build();
        when(formExportService.fetchForms()).thenReturn(Arrays.asList(form));

        listener.fetchFromServer();

        verify(outboundEventGateway).sendEventMessage(event("FormName", new HashMap<String, Object>()));
    }

    @Test
    public void shouldSendAnEventWithParametersWhichAreTheFieldsSpecifiedInTheFormDefinition() throws Exception {
        String content = "<data><Patient_Name>Abu</Patient_Name></data>";
        CommcareForm form = form().withName("FormName").withMapping("Patient", "/data/Patient_Name").withContent(content).build();
        when(formExportService.fetchForms()).thenReturn(Arrays.asList(form));

        listener.fetchFromServer();

        verify(outboundEventGateway).sendEventMessage(event("FormName", params("Patient", "Abu")));
    }

    @Test
    public void shouldSendAnEventWithMultipleParametersWhenThereAreMultipleFieldsSpecified() throws Exception {
        String content = "<data><Patient><Name>Abu</Name><Details><Age>23</Age></Details></Patient></data>";

        CommcareForm form = form().withName("FormName").withContent(content)
                .withMapping("Patient", "/data/Patient/Name")
                .withMapping("Age", "/data/Patient/Details/Age").build();
        when(formExportService.fetchForms()).thenReturn(Arrays.asList(form));

        listener.fetchFromServer();

        Map<String, Object> parameters = params("Patient", "Abu");
        parameters.put("Age", "23");
        verify(outboundEventGateway).sendEventMessage(event("FormName", parameters));
    }

    @Test
    public void shouldSendOneEventForEachFormFound() throws Exception {
        String content1 = "<data><Patient_Name>Abu</Patient_Name></data>";
        CommcareForm form1 = form().withName("PatientForm").withMapping("Patient", "/data/Patient_Name").withContent(content1).build();

        String content2 = "<data><MermaidName>Ariel</MermaidName></data>";
        CommcareForm form2 = form().withName("MermaidForm").withMapping("Mermaid", "/data/MermaidName").withContent(content2).build();
        when(formExportService.fetchForms()).thenReturn(Arrays.asList(form1, form2));

        listener.fetchFromServer();

        verify(outboundEventGateway).sendEventMessage(event("PatientForm", params("Patient", "Abu")));
        verify(outboundEventGateway).sendEventMessage(event("MermaidForm", params("Mermaid", "Ariel")));
    }

    private MotechEvent event(String formName, Map<String, Object> otherParams) {
        otherParams.put(FORM_NAME_PARAMETER, formName);
        return new MotechEvent(CommCareFormEvent.EVENT_SUBJECT, otherParams);
    }

    private Map<String, Object> params(String key, String value) {
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put(key, value);
        return map;
    }

    private CommcareFormBuilder form() {
        return new CommcareFormBuilder();
    }
}
