package org.ei.commcare.listener.event;

import com.google.gson.Gson;
import org.ei.commcare.api.domain.CommcareForm;
import org.motechproject.model.MotechEvent;

import java.util.HashMap;
import java.util.Map;

public class CommCareFormEvent {
    public static final String EVENT_SUBJECT = "FORM_SUBMITTED_EVENT";
    public static final String FORM_NAME_PARAMETER = "FormName";
    public static final String FORM_DATA_PARAMETER = "FormData";

    private final CommcareForm form;
    private final Map<String, String> fieldsInXMLWeCareAbout;

    public CommCareFormEvent(CommcareForm form, Map<String, String> fieldsInXMLWeCareAbout) {
        this.form = form;
        this.fieldsInXMLWeCareAbout = fieldsInXMLWeCareAbout;
    }

    public MotechEvent toMotechEvent() {
        HashMap<String, Object> parameters = new HashMap<String, Object>();
        parameters.put(FORM_NAME_PARAMETER, form.definition().name());
        parameters.put(FORM_DATA_PARAMETER, new Gson().toJson(fieldsInXMLWeCareAbout));
        return new MotechEvent(EVENT_SUBJECT, parameters);
    }
}
