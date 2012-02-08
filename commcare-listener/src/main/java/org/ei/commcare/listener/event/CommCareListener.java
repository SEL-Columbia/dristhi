package org.ei.commcare.listener.event;

import org.ei.commcare.listener.domain.CommcareForm;
import org.ei.commcare.listener.service.CommCareFormExportService;
import org.motechproject.gateway.OutboundEventGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class CommCareListener {
    private final CommCareFormExportService formExportService;
    private final OutboundEventGateway outboundEventGateway;

    @Autowired
    public CommCareListener(CommCareFormExportService formExportService, OutboundEventGateway outboundEventGateway) {
        this.formExportService = formExportService;
        this.outboundEventGateway = outboundEventGateway;
    }

    public void fetchFromServer() throws Exception {
        List<CommcareForm> commcareForms = this.formExportService.fetchForms();

        for (CommcareForm form : commcareForms) {
            Map<String, String> fieldsInXMLWeCareAbout = form.content().getValuesOfFieldsSpecifiedByPath(form.definition().mappings());
            outboundEventGateway.sendEventMessage(new CommCareFormEvent(form, fieldsInXMLWeCareAbout).toMotechEvent());
        }
    }

}
