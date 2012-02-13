package org.ei.commcare.api.event;

import org.ei.commcare.api.domain.CommcareForm;
import org.ei.commcare.api.service.CommCareFormImportService;
import org.motechproject.gateway.OutboundEventGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class CommCareListener {
    private final CommCareFormImportService formImportService;
    private final OutboundEventGateway outboundEventGateway;

    @Autowired
    public CommCareListener(CommCareFormImportService formImportService, OutboundEventGateway outboundEventGateway) {
        this.formImportService = formImportService;
        this.outboundEventGateway = outboundEventGateway;
    }

    public void fetchFromServer() throws Exception {
        List<CommcareForm> commcareForms = this.formImportService.fetchForms();

        for (CommcareForm form : commcareForms) {
            Map<String, String> fieldsInXMLWeCareAbout = form.content().getValuesOfFieldsSpecifiedByPath(form.definition().mappings());
            outboundEventGateway.sendEventMessage(new CommCareFormEvent(form, fieldsInXMLWeCareAbout).toMotechEvent());
        }
    }

}
