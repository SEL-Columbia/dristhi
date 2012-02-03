package org.ei.commcare.event;

import org.ei.commcare.CommcareFormBuilder;
import org.ei.commcare.domain.CommcareForm;
import org.ei.commcare.service.CommCareFormExportService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.gateway.OutboundEventGateway;
import org.motechproject.model.MotechEvent;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

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
    public void shouldSendAnEventWithSubjectAsTheNameOfTheFormDefinitionAndNoParametersWhenTheFieldsWeCareAboutAreEmpty() throws Exception {
        CommcareForm form = form().withName("FormName").withContent("<some_xml/>").build();
        when(formExportService.fetchForms()).thenReturn(Arrays.asList(form));

        listener.fetchFromServer();

        verify(outboundEventGateway).sendEventMessage(new MotechEvent("FormName", new HashMap<String, Object>()));
    }

    @Test
    public void shouldSendAnEventWithParametersWhichAreTheFieldsSpecifiedInTheFormDefinition() throws Exception {
        String content = "<data><Patient_Name>Abu</Patient_Name></data>";
        CommcareForm form = form().withName("FormName").withMapping("Patient", "/data/Patient_Name").withContent(content).build();
        when(formExportService.fetchForms()).thenReturn(Arrays.asList(form));

        listener.fetchFromServer();

        verify(outboundEventGateway).sendEventMessage(new MotechEvent("FormName", params("Patient", "Abu")));
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
        verify(outboundEventGateway).sendEventMessage(new MotechEvent("FormName", parameters));
    }

    @Test
    public void shouldSendOneEventForEachFormFound() throws Exception {
        String content1 = "<data><Patient_Name>Abu</Patient_Name></data>";
        CommcareForm form1 = form().withName("PatientForm").withMapping("Patient", "/data/Patient_Name").withContent(content1).build();

        String content2 = "<data><MermaidName>Ariel</MermaidName></data>";
        CommcareForm form2 = form().withName("MermaidForm").withMapping("Mermaid", "/data/MermaidName").withContent(content2).build();
        when(formExportService.fetchForms()).thenReturn(Arrays.asList(form1, form2));

        listener.fetchFromServer();

        verify(outboundEventGateway).sendEventMessage(new MotechEvent("PatientForm", params("Patient", "Abu")));
        verify(outboundEventGateway).sendEventMessage(new MotechEvent("MermaidForm", params("Mermaid", "Ariel")));
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
