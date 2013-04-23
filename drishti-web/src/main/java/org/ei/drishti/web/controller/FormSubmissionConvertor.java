package org.ei.drishti.web.controller;

import com.google.gson.Gson;
import org.ei.drishti.domain.form.FormInstance;
import org.ei.drishti.dto.form.FormSubmission;

public class FormSubmissionConvertor {
    public static FormSubmission from(org.ei.drishti.domain.form.FormSubmission formSubmission) {
        return new FormSubmission(formSubmission.anmId(), formSubmission.instanceId(), formSubmission.entityId(), formSubmission.formName(),
                new Gson().toJson(formSubmission.instance()), String.valueOf(formSubmission.timestamp()));
    }

    public static org.ei.drishti.domain.form.FormSubmission toFormSubmission(FormSubmission formSubmission) {
        return new org.ei.drishti.domain.form.FormSubmission(formSubmission.anmId(), formSubmission.instanceId(), formSubmission.formName(),
                formSubmission.entityId(), new Gson().fromJson(formSubmission.instance(), FormInstance.class), Long.parseLong(formSubmission.timeStamp())
        );
    }
}
