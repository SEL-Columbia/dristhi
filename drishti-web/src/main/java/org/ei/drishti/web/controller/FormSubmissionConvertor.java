package org.ei.drishti.web.controller;

import com.google.gson.Gson;
import org.ei.drishti.domain.form.FormInstance;
import org.ei.drishti.dto.form.FormSubmissionDTO;

public class FormSubmissionConvertor {
    public static FormSubmissionDTO from(org.ei.drishti.domain.form.FormSubmission formSubmission) {
        return new FormSubmissionDTO(formSubmission.anmId(), formSubmission.instanceId(), formSubmission.entityId(), formSubmission.formName(),
                new Gson().toJson(formSubmission.instance()), String.valueOf(formSubmission.timestamp()));
    }

    public static org.ei.drishti.domain.form.FormSubmission toFormSubmission(FormSubmissionDTO formSubmissionDTO) {
        return new org.ei.drishti.domain.form.FormSubmission(formSubmissionDTO.anmId(), formSubmissionDTO.instanceId(), formSubmissionDTO.formName(),
                formSubmissionDTO.entityId(), new Gson().fromJson(formSubmissionDTO.instance(), FormInstance.class), Long.parseLong(formSubmissionDTO.timeStamp())
        );
    }
}
