package org.ei.drishti.form.service;

import com.google.gson.Gson;
import org.ei.drishti.dto.form.FormSubmissionDTO;
import org.ei.drishti.form.domain.FormInstance;
import org.ei.drishti.form.domain.FormSubmission;

import static java.lang.Long.parseLong;
import static java.lang.String.valueOf;

public class FormSubmissionConvertor {
    public static FormSubmissionDTO from(FormSubmission formSubmission) {
        return new FormSubmissionDTO(formSubmission.anmId(), formSubmission.instanceId(), formSubmission.entityId(), formSubmission.formName(),
                new Gson().toJson(formSubmission.instance()), valueOf(formSubmission.clientVersion()), formSubmission.formDataDefinitionVersion())
                .withServerVersion(valueOf(formSubmission.serverVersion()));
    }

    public static FormSubmission toFormSubmission(FormSubmissionDTO formSubmissionDTO) {
        return new FormSubmission(formSubmissionDTO.anmId(), formSubmissionDTO.instanceId(), formSubmissionDTO.formName(), formSubmissionDTO.entityId(),
                formSubmissionDTO.formDataDefinitionVersion(), parseLong(formSubmissionDTO.clientVersion()), new Gson().fromJson(formSubmissionDTO.instance(), FormInstance.class));
    }

    public static FormSubmission toFormSubmissionWithVersion(FormSubmissionDTO formSubmissionDTO) {
        return new FormSubmission(formSubmissionDTO.anmId(), formSubmissionDTO.instanceId(), formSubmissionDTO.formName(),
                formSubmissionDTO.entityId(), parseLong(formSubmissionDTO.clientVersion()), formSubmissionDTO.formDataDefinitionVersion(), new Gson().fromJson(formSubmissionDTO.instance(), FormInstance.class),
                parseLong(formSubmissionDTO.serverVersion()));
    }
}
