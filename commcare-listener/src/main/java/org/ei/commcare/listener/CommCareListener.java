package org.ei.commcare.listener;

import org.ei.commcare.api.domain.CommcareFormInstance;
import org.ei.commcare.listener.event.CommCareFormEvent;
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
        List<CommcareFormInstance> commcareFormInstances = this.formImportService.fetchForms();

        for (CommcareFormInstance formInstance : commcareFormInstances) {
            Map<String, String> fieldsWeCareAbout = formInstance.content();
            outboundEventGateway.sendEventMessage(new CommCareFormEvent(formInstance, fieldsWeCareAbout).toMotechEvent());
        }
    }
}
