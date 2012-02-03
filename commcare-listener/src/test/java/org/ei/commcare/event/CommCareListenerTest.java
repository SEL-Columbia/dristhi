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
        CommcareForm form = form().withName("FormName").build();
        when(formExportService.fetchForms()).thenReturn(Arrays.asList(form));

        listener.fetchFromServer();

        verify(outboundEventGateway).sendEventMessage(new MotechEvent("FormName"));
    }

    @Test
    public void shouldSendAnEventWithParametersWhichAreTheFieldsSpecifiedInTheFormDefinition() throws Exception {
        CommcareForm form = form().withName("FormName").withMappings("/root/x", "abc").withContent("<root><x>Hello</x></root>").build();
        when(formExportService.fetchForms()).thenReturn(Arrays.asList(form));

        listener.fetchFromServer();

        HashMap<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("abc", "Hello");
        verify(outboundEventGateway).sendEventMessage(new MotechEvent("FormName", parameters));
    }

    private CommcareFormBuilder form() {
        return new CommcareFormBuilder();
    }
}
