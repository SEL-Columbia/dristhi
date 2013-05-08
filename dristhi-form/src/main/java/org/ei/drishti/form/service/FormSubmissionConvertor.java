package org.ei.drishti.form.service;

import com.google.gson.Gson;
import org.ei.drishti.form.domain.FormInstance;
import org.ei.drishti.dto.form.FormSubmissionDTO;
import org.ei.drishti.form.domain.FormSubmission;

public class FormSubmissionConvertor {
    public static FormSubmissionDTO from(FormSubmission formSubmission) {
        return new FormSubmissionDTO(formSubmission.anmId(), formSubmission.instanceId(), formSubmission.entityId(), formSubmission.formName(),
                new Gson().toJson(formSubmission.instance()), String.valueOf(formSubmission.timestamp()));
    }

    public static FormSubmission toFormSubmission(FormSubmissionDTO formSubmissionDTO) {
        return new FormSubmission(formSubmissionDTO.anmId(), formSubmissionDTO.instanceId(), formSubmissionDTO.formName(),
                formSubmissionDTO.entityId(), new Gson().fromJson(formSubmissionDTO.instance(), FormInstance.class), Long.parseLong(formSubmissionDTO.timeStamp())
        );
    }
}
