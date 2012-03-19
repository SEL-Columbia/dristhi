package org.ei.commcare.listener.event;

import com.google.gson.Gson;
import org.ei.commcare.api.domain.CommcareFormInstance;
import org.motechproject.model.MotechEvent;

import java.util.HashMap;
import java.util.Map;

public class CommCareFormEvent {
    public static final String EVENT_SUBJECT = "FORM_SUBMITTED_EVENT";
    public static final String FORM_NAME_PARAMETER = "FormName";
    public static final String FORM_DATA_PARAMETER = "FormData";
    public static final String FORM_ID_PARAMETER = "FormID";

    private final CommcareFormInstance formInstance;
    private final Map<String, String> fieldsWeCareAbout;

    public CommCareFormEvent(CommcareFormInstance formInstance, Map<String, String> fieldsWeCareAbout) {
        this.formInstance = formInstance;
        this.fieldsWeCareAbout = fieldsWeCareAbout;
    }

    public MotechEvent toMotechEvent() {
        HashMap<String, Object> parameters = new HashMap<String, Object>();
        parameters.put(FORM_NAME_PARAMETER, formInstance.definition().name());
        parameters.put(FORM_DATA_PARAMETER, new Gson().toJson(fieldsWeCareAbout));
        parameters.put(FORM_ID_PARAMETER, formInstance.formId());
        return new MotechEvent(EVENT_SUBJECT, parameters);
    }
}
