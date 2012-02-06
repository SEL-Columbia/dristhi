package org.ei.commcare.listener.event;

import org.ei.commcare.listener.domain.CommcareForm;
import org.motechproject.model.MotechEvent;

import java.util.HashMap;
import java.util.Map;

public class CommCareFormEvent {
    public static final String EVENT_SUBJECT = "FORM_SUBMITTED_EVENT";
    public static final String FORM_NAME_PARAMETER = "FormName";

    private final CommcareForm form;
    private final Map<String, String> fieldsInXMLWeCareAbout;

    public CommCareFormEvent(CommcareForm form, Map<String, String> fieldsInXMLWeCareAbout) {
        this.form = form;
        this.fieldsInXMLWeCareAbout = fieldsInXMLWeCareAbout;
    }

    public MotechEvent toMotechEvent() {
        HashMap<String, Object> parameters = new HashMap<String, Object>(fieldsInXMLWeCareAbout);
        parameters.put(FORM_NAME_PARAMETER, form.definition().name());
        return new MotechEvent(EVENT_SUBJECT, parameters);
    }
}
