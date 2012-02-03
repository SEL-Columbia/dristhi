package org.ei.commcare.event;

import org.ei.commcare.domain.CommcareForm;
import org.ei.commcare.service.CommCareFormExportService;
import org.ei.commcare.util.Xml;
import org.motechproject.gateway.OutboundEventGateway;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class CommCareListener {
    private final CommCareFormExportService formExportService;
    private final OutboundEventGateway outboundEventGateway;

    public CommCareListener(CommCareFormExportService formExportService, OutboundEventGateway outboundEventGateway) {
        this.formExportService = formExportService;
        this.outboundEventGateway = outboundEventGateway;
    }

    public void fetchFromServer() throws IOException {
        List<CommcareForm> commcareForms = this.formExportService.fetchForms();
        CommcareForm form = commcareForms.get(0);

        Map<String, String> fieldsInXMLWeCareAbout = new Xml(form.content()).getValuesOfFieldsSpecifiedByPath(form.definition().mappings());
        outboundEventGateway.sendEventMessage(new CommCareFormEvent(form, fieldsInXMLWeCareAbout).toMotechEvent());
    }

}
